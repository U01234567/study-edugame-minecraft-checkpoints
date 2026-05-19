import { spawnSync } from "node:child_process";
import {
  access,
  chmod,
  cp,
  mkdir,
  readdir,
  readFile,
  rm,
  writeFile,
} from "node:fs/promises";
import fs from "node:fs";
import path from "node:path";
import crypto from "node:crypto";
import { fileURLToPath } from "node:url";

const __filename = fileURLToPath(import.meta.url);
const scriptsDir = path.dirname(__filename);
const desktopDir = path.resolve(scriptsDir, "..");
const repoRoot = path.resolve(desktopDir, "..");

const args = parseArgs(process.argv.slice(2));

const target = args.target ?? defaultTarget();
const runtimeJrePath = args.jre ?? targetRuntimeJavaEnv(target) ?? process.env.JAVA_RUNTIME_HOME ?? process.env.JAVA_HOME;
const buildJavaHome = args["build-java"] ?? process.env.BUILD_JAVA_HOME ?? process.env.JAVA_HOME ?? runtimeJrePath;
const buildLabel = args["build-label"] ?? "release";
const supportedTargets = new Set(["win-x64", "mac-arm64", "mac-x64"]);

if (!supportedTargets.has(target)) {
  fail(`Unsupported target "${target}". Use one of: ${[...supportedTargets].join(", ")}`);
}

if (!runtimeJrePath) {
  fail("No participant Java runtime was provided. Set a target-specific Java runtime env var, JAVA_RUNTIME_HOME, JAVA_HOME, or pass --jre <path>.");
}

if (!buildJavaHome) {
  fail("No build Java runtime was provided. Set BUILD_JAVA_HOME, JAVA_HOME, or pass --build-java <path>.");
}

const modsCustomDir = path.join(repoRoot, "mods", "custom");
const worldDir = path.join(repoRoot, "world");
const externalLibsDir = path.join(repoRoot, "mods", "external", "libs");

const payloadRoot = path.join(desktopDir, "payload-dist", target, "payload");
const bundledPayload = path.join(desktopDir, "src-tauri", "resources", "payload");

const gameDir = path.join(payloadRoot, "game");
const runtimeDir = path.join(payloadRoot, "runtime");
const licensesDir = path.join(payloadRoot, "licenses");
const launchDir = path.join(gameDir, "launch");
const depsDir = path.join(launchDir, "deps");
const portableLoomCacheDir = path.join(launchDir, "loom-cache");
const portableAssetsDir = path.join(launchDir, "assets");

const studyWorldSaveName = "experiment-world";
const studyUsername = "Explorer16";
const studyUuid = "00000000-0000-0000-0000-00000000000f";
const devLaunchMainClass = "net.fabricmc.devlaunchinjector.Main";
const knotClientMainClass = "net.fabricmc.loader.impl.launch.knot.KnotClient";

await assertExists(modsCustomDir, "Fabric mod folder");
await assertExists(worldDir, "Study world folder");
await assertExists(externalLibsDir, "External runtime libs folder");
await assertExists(runtimeJrePath, "Participant Java runtime/JDK folder");
await assertExists(buildJavaHome, "Build Java runtime/JDK folder");

console.log(`Preparing release payload for ${target}`);
console.log(`Repository: ${repoRoot}`);
console.log(`Participant Java runtime: ${runtimeJrePath}`);
console.log(`Build Java runtime: ${buildJavaHome}`);

await rm(payloadRoot, { recursive: true, force: true });
await mkdir(gameDir, { recursive: true });
await mkdir(runtimeDir, { recursive: true });
await mkdir(licensesDir, { recursive: true });
await mkdir(launchDir, { recursive: true });
await mkdir(depsDir, { recursive: true });
await mkdir(portableLoomCacheDir, { recursive: true });

await runGradleBuild();

const buildSource = path.join(modsCustomDir, "build");
const runSource = path.join(modsCustomDir, "run");

await assertExists(buildSource, "Generated Gradle build folder");
await assertExists(runSource, "Prepared Minecraft run folder");

console.log("Copying packaged runtime files...");

const buildDest = path.join(gameDir, "build");
const runDest = path.join(gameDir, "run");

await copyDirectoryIfPresent(path.join(buildSource, "classes"), path.join(buildDest, "classes"));
await copyDirectoryIfPresent(path.join(buildSource, "resources"), path.join(buildDest, "resources"));
await copyDirectoryIfPresent(path.join(buildSource, "libs"), path.join(buildDest, "libs"));
await copyDirectoryIfPresent(path.join(buildSource, "study-launch"), path.join(buildDest, "study-launch"));

await copyRuntimeRunFolder(runSource, runDest);

console.log("Creating portable Java launch argfile...");
const portableLaunch = await createPortableLaunchFiles(buildSource, launchDir, depsDir);

console.log("Copying Java runtime...");
const copiedJavaLayout = await copyJavaRuntime(runtimeJrePath, path.join(runtimeDir, "jre"));

console.log("Copying license/provenance files...");
await copyFileIfPresent(path.join(repoRoot, "LICENSE"), path.join(licensesDir, "LICENSE"));
await copyFileIfPresent(
  path.join(repoRoot, "mods", "external", "README.md"),
  path.join(licensesDir, "mods-external-README.md")
);

const manifest = {
  schemaVersion: 3,
  appName: "Minecraft Study",
  buildLabel,
  target,
  generatedAtUtc: new Date().toISOString(),
  internalModId: "study-checkpoints",
  game: {
    runDirectory: "game/run",
    buildDirectory: "game/build",
    worldSaveName: studyWorldSaveName,
  },
  runtime: {
    javaDirectory: "runtime/jre",
    javaExecutable: copiedJavaLayout.relativeJavaExecutable,
    javaLayout: copiedJavaLayout.layout,
    pruneSummary: copiedJavaLayout.pruneSummary,
  },
  launch: {
    directJavaLaunchReady: true,
    mode: "portable-fabric-dev-launch",
    argFile: portableLaunch.relativeArgFile,
    launchConfig: portableLaunch.relativeLaunchConfig,
    dependencyCount: portableLaunch.dependencyCount,
    assets: portableLaunch.assets,
    note: "Release payload uses bundled Java and a complete portable Fabric dev-launch command. No Gradle/source tree is used at runtime.",
  },
};

const manifestPath = path.join(payloadRoot, "manifest.json");
await writeFile(manifestPath, `${JSON.stringify(manifest, null, 2)}\n`, "utf8");
await assertExists(manifestPath, "Payload manifest");

const payloadFileCount = await countFiles(payloadRoot);

if (payloadFileCount === 0) {
  fail(`Payload folder exists but contains no files: ${payloadRoot}`);
}

console.log("Refreshing Tauri bundled payload folder...");
await rm(bundledPayload, { recursive: true, force: true });
await mkdir(path.dirname(bundledPayload), { recursive: true });
await cp(payloadRoot, bundledPayload, { recursive: true });

console.log("");
console.log("Release payload prepared:");
console.log(`  ${payloadRoot}`);
console.log("");
console.log("Tauri resource copy prepared:");
console.log(`  ${bundledPayload}`);
console.log("");
console.log(`Payload file count: ${payloadFileCount}`);

function parseArgs(rawArgs) {
  const parsed = {};

  for (let index = 0; index < rawArgs.length; index += 1) {
    const item = rawArgs[index];

    if (!item.startsWith("--")) {
      continue;
    }

    const withoutPrefix = item.slice(2);
    const inlineSplit = withoutPrefix.indexOf("=");

    if (inlineSplit >= 0) {
      parsed[withoutPrefix.slice(0, inlineSplit)] = withoutPrefix.slice(inlineSplit + 1);
      continue;
    }

    const key = withoutPrefix;
    const next = rawArgs[index + 1];

    if (!next || next.startsWith("--")) {
      parsed[key] = "true";
      continue;
    }

    parsed[key] = next;
    index += 1;
  }

  return parsed;
}

function defaultTarget() {
  if (process.platform === "win32" && process.arch === "x64") {
    return "win-x64";
  }

  if (process.platform === "darwin" && process.arch === "arm64") {
    return "mac-arm64";
  }

  if (process.platform === "darwin" && process.arch === "x64") {
    return "mac-x64";
  }

  return "win-x64";
}

function targetRuntimeJavaEnv(targetName) {
  const envByTarget = {
    "win-x64": process.env.JAVA_RUNTIME_WIN_X64,
    "mac-arm64": process.env.JAVA_RUNTIME_MAC_ARM64,
    "mac-x64": process.env.JAVA_RUNTIME_MAC_X64,
  };

  return envByTarget[targetName] || null;
}

async function runGradleBuild() {
  console.log("Building Fabric mod and preparing runtime game folder...");

  const gradlewBat = path.join(modsCustomDir, "gradlew.bat");
  const gradlewUnix = path.join(modsCustomDir, "gradlew");
  const isWindows = process.platform === "win32";

  let command;
  let commandArgs;

  if (isWindows) {
    await assertExists(gradlewBat, "Gradle Windows launcher");

    command = "cmd.exe";
    commandArgs = [
      "/c",
      "gradlew.bat",
      "clean",
      "build",
      "copyStudyWorld",
      "copyExternalRuntimeMods",
      "writeStudyRunClientClasspathArgFile",
    ];
  } else {
    await assertExists(gradlewUnix, "Gradle launcher");

    try {
      await chmod(gradlewUnix, 0o755);
    } catch {
      // Filesystems such as some USB drives may ignore chmod. The later process start will fail if this matters.
    }

    command = "./gradlew";
    commandArgs = [
      "clean",
      "build",
      "copyStudyWorld",
      "copyExternalRuntimeMods",
      "writeStudyRunClientClasspathArgFile",
    ];
  }

  const result = spawnSync(command, commandArgs, {
    cwd: modsCustomDir,
    stdio: "inherit",
    shell: false,
    env: {
      ...process.env,
      JAVA_HOME: buildJavaHome,
    },
  });

  if (result.error) {
    fail(`Gradle could not be started: ${result.error.message}`);
  }

  if (result.status !== 0) {
    fail(`Gradle failed with exit code ${result.status}`);
  }
}

async function copyRuntimeRunFolder(source, destination) {
  await rm(destination, { recursive: true, force: true });
  await mkdir(destination, { recursive: true });

  const keepNames = new Set([
    "config",
    "data",
    "mods",
    "resourcepacks",
    "resources",
    "saves",
    "shaderpacks",
    "options.txt",
    "servers.dat",
  ]);

  const entries = await readdir(source, { withFileTypes: true });

  for (const entry of entries) {
    if (!keepNames.has(entry.name)) {
      continue;
    }

    const from = path.join(source, entry.name);
    const to = path.join(destination, entry.name);

    if (entry.isDirectory()) {
      await cp(from, to, { recursive: true });
    } else if (entry.isFile()) {
      await mkdir(path.dirname(to), { recursive: true });
      await cp(from, to);
    }
  }

  await assertExists(path.join(destination, "saves", studyWorldSaveName), "Packaged study world");
  await assertExists(path.join(destination, "mods"), "Packaged runtime mods folder");
}

async function createPortableLaunchFiles(buildSource, launchDir, depsDir) {
  const sourceArgFile = path.join(buildSource, "study-launch", "runClient-classpath.args");
  const sourceLaunchCfg = path.join(modsCustomDir, ".gradle", "loom-cache", "launch.cfg");
  const sourceLog4j = path.join(modsCustomDir, ".gradle", "loom-cache", "log4j.xml");

  await assertExists(sourceArgFile, "Study runClient classpath argfile");
  await assertExists(sourceLaunchCfg, "Fabric Loom launch config");
  await assertExists(sourceLog4j, "Fabric Loom log4j config");

  const copiedPaths = new Map();
  const portableLaunchCfg = await createPortableLaunchConfig(sourceLaunchCfg, sourceLog4j, launchDir, depsDir, copiedPaths);
  const javaArgTokens = await rewriteJavaArgFile(sourceArgFile, depsDir, copiedPaths);

  validateJavaArgTokens(javaArgTokens, sourceArgFile);

  const portableTokens = [
    "-Xshare:off",
    `-Dfabric.dli.config=${portableLaunchCfg.relativeFromRun}`,
    "-Dfabric.dli.env=client",
    `-Dfabric.dli.main=${knotClientMainClass}`,
    "--sun-misc-unsafe-memory-access=allow",
    "--enable-native-access=ALL-UNNAMED",
    ...javaArgTokens,
    devLaunchMainClass,
    "--username",
    studyUsername,
    "--uuid",
    studyUuid,
    "--quickPlaySingleplayer",
    studyWorldSaveName,
  ];

  const portableArgFile = path.join(launchDir, "runClient-portable.args");
  const portableContents = portableTokens
    .filter((token) => token !== "")
    .map(quoteArgFileToken)
    .join("\n") + "\n";

  await writeFile(portableArgFile, portableContents, "utf8");

  return {
    relativeArgFile: "game/launch/runClient-portable.args",
    relativeLaunchConfig: "game/launch/loom-cache/launch.cfg",
    dependencyCount: copiedPaths.size,
    assets: portableLaunchCfg.assets,
  };
}

async function createPortableLaunchConfig(sourceLaunchCfg, sourceLog4j, launchDir, depsDir, copiedPaths) {
  const raw = await readFile(sourceLaunchCfg, "utf8");

  if (!raw.trim()) {
    fail(`Fabric Loom launch config is empty: ${sourceLaunchCfg}`);
  }

  await mkdir(portableLoomCacheDir, { recursive: true });
  await cp(sourceLog4j, path.join(portableLoomCacheDir, "log4j.xml"));

  const lines = raw.split(/\r?\n/);
  const rewritten = [];
  let section = null;
  let previousClientArg = null;
  let sawAssetsDir = false;
  let assetsSource = null;
  let assetIndexName = null;

  for (const line of lines) {
    if (line.trim() === "") {
      rewritten.push("");
      continue;
    }

    const isIndented = /^\s/.test(line);

    if (!isIndented) {
      section = line.trim();
      previousClientArg = null;
      rewritten.push(section);
      continue;
    }

    const indent = line.match(/^\s*/)?.[0] ?? "\t";
    const value = line.trim();

    if (section === "commonProperties") {
      const equalsIndex = value.indexOf("=");

      if (equalsIndex >= 0) {
        const key = value.slice(0, equalsIndex);
        const rawValue = value.slice(equalsIndex + 1);

        if (key === "log4j.configurationFile") {
          rewritten.push(`${indent}${key}=../launch/loom-cache/log4j.xml`);
        } else {
          const rewrittenValue = await rewritePathishValue(rawValue, depsDir, copiedPaths);
          rewritten.push(`${indent}${key}=${rewrittenValue}`);
        }
      } else {
        rewritten.push(`${indent}${await rewritePathishValue(value, depsDir, copiedPaths)}`);
      }

      continue;
    }

    if (section === "clientArgs") {
      const inlineAssetIndex = value.match(/^--assetIndex=(.+)$/);
      if (inlineAssetIndex) {
        assetIndexName = normaliseAssetIndexName(inlineAssetIndex[1]);
        rewritten.push(`${indent}${value}`);
        previousClientArg = null;
        continue;
      }

      const inlineAssetsDir = value.match(/^--assetsDir=(.+)$/);
      if (inlineAssetsDir) {
        assetsSource = path.resolve(inlineAssetsDir[1]);
        await assertExists(assetsSource, "Fabric Loom downloaded assets folder");
        rewritten.push(`${indent}--assetsDir=../launch/assets`);
        sawAssetsDir = true;
        previousClientArg = null;
        continue;
      }

      if (previousClientArg === "--assetIndex") {
        assetIndexName = normaliseAssetIndexName(value);
        rewritten.push(`${indent}${value}`);
        previousClientArg = null;
        continue;
      }

      if (previousClientArg === "--assetsDir") {
        assetsSource = path.resolve(value);
        await assertExists(assetsSource, "Fabric Loom downloaded assets folder");
        rewritten.push(`${indent}../launch/assets`);
        sawAssetsDir = true;
        previousClientArg = null;
        continue;
      }

      rewritten.push(`${indent}${await rewritePathishValue(value, depsDir, copiedPaths)}`);
      previousClientArg = value.startsWith("--") ? value : null;
      continue;
    }

    rewritten.push(`${indent}${await rewritePathishValue(value, depsDir, copiedPaths)}`);
  }

  if (!sawAssetsDir || !assetsSource) {
    fail(`Fabric Loom launch config did not contain a client --assetsDir entry: ${sourceLaunchCfg}`);
  }

  if (!assetIndexName) {
    fail(`Fabric Loom launch config did not contain a client --assetIndex entry: ${sourceLaunchCfg}`);
  }

  const assets = await copyPrunedAssets(assetsSource, portableAssetsDir, assetIndexName);

  const portableLaunchCfg = path.join(portableLoomCacheDir, "launch.cfg");
  await writeFile(portableLaunchCfg, `${rewritten.join("\n").trimEnd()}\n`, "utf8");

  return {
    relativeFromRun: "../launch/loom-cache/launch.cfg",
    assets,
  };
}

async function rewriteJavaArgFile(sourceArgFile, depsDir, copiedPaths) {
  const raw = await readFile(sourceArgFile, "utf8");
  const tokens = tokenizeJavaArgFile(raw);

  if (tokens.length === 0) {
    fail(`Study runClient argfile is empty: ${sourceArgFile}`);
  }

  const rewritten = [];

  for (let index = 0; index < tokens.length; index += 1) {
    const token = tokens[index];
    const previous = rewritten[rewritten.length - 1];
    const lowerPrevious = String(previous || "").toLowerCase();

    if (isClasspathValueForPreviousArg(lowerPrevious)) {
      rewritten.push(await rewriteClasspathToken(token, depsDir, copiedPaths));
      continue;
    }

    if (isGameDirValueForPreviousArg(lowerPrevious)) {
      rewritten.push(".");
      continue;
    }

    rewritten.push(await rewriteGenericToken(token, depsDir, copiedPaths));
  }

  return rewritten;
}

function validateJavaArgTokens(tokens, sourceArgFile) {
  const lowerTokens = tokens.map((token) => token.toLowerCase());
  const hasClasspath = lowerTokens.includes("-cp")
    || lowerTokens.includes("-classpath")
    || lowerTokens.includes("--class-path");

  if (!hasClasspath) {
    fail(`Study runClient argfile does not contain a Java classpath: ${sourceArgFile}`);
  }

  const classpathIndex = lowerTokens.findIndex((token) => isClasspathValueForPreviousArg(token));
  const classpathValue = classpathIndex >= 0 ? tokens[classpathIndex + 1] : "";

  if (!classpathValue || !classpathValue.includes("dev-launch-injector")) {
    fail(`Study runClient classpath does not contain dev-launch-injector: ${sourceArgFile}`);
  }

  if (!classpathValue || !classpathValue.includes("fabric-loader")) {
    fail(`Study runClient classpath does not contain fabric-loader: ${sourceArgFile}`);
  }
}

function tokenizeJavaArgFile(raw) {
  const tokens = [];
  let current = "";
  let quote = null;
  let escaped = false;

  for (const char of raw) {
    if (escaped) {
      current += char;
      escaped = false;
      continue;
    }

    if (char === "\\") {
      current += char;
      continue;
    }

    if (quote) {
      if (char === quote) {
        quote = null;
      } else {
        current += char;
      }
      continue;
    }

    if (char === "'" || char === '"') {
      quote = char;
      continue;
    }

    if (/\s/.test(char)) {
      if (current.length > 0) {
        tokens.push(current);
        current = "";
      }
      continue;
    }

    current += char;
  }

  if (current.length > 0) {
    tokens.push(current);
  }

  return tokens;
}

function isClasspathValueForPreviousArg(previous) {
  return previous === "-cp" || previous === "-classpath" || previous === "--class-path";
}

function isGameDirValueForPreviousArg(previous) {
  return previous === "--gamedir";
}

async function rewriteClasspathToken(token, depsDir, copiedPaths) {
  const sourceDelimiter = sourceClasspathDelimiter();
  const targetDelimiter = targetClasspathDelimiter();
  const parts = token.split(sourceDelimiter);
  const rewrittenParts = [];

  for (const part of parts) {
    if (!part) {
      continue;
    }

    const rewritten = await copyExternalPathIfPresent(part, depsDir, copiedPaths);
    rewrittenParts.push(rewritten ?? part);
  }

  return rewrittenParts.join(targetDelimiter);
}

async function rewriteGenericToken(token, depsDir, copiedPaths) {
  const gameDirInline = token.match(/^(--gameDir=)(.+)$/i);
  if (gameDirInline) {
    return `${gameDirInline[1]}.`;
  }

  const propertyPath = token.match(/^(-D[^=]+=)(.+)$/);
  if (propertyPath) {
    const rewrittenValue = await rewritePathishValue(propertyPath[2], depsDir, copiedPaths);
    return `${propertyPath[1]}${rewrittenValue}`;
  }

  return await rewritePathishValue(token, depsDir, copiedPaths);
}

async function rewritePathishValue(value, depsDir, copiedPaths) {
  const copied = await copyExternalPathIfPresent(value, depsDir, copiedPaths);
  return copied ?? value;
}

async function copyExternalPathIfPresent(candidate, depsDir, copiedPaths) {
  if (!looksAbsolute(candidate)) {
    return null;
  }

  const normalized = path.resolve(candidate);

  if (!(await exists(normalized))) {
    return null;
  }

  const existing = copiedPaths.get(normalized);
  if (existing) {
    return existing;
  }

  const basename = safeBasename(path.basename(normalized) || "dependency");
  const hash = crypto.createHash("sha1").update(normalized).digest("hex").slice(0, 10);
  const destination = path.join(depsDir, `${hash}-${basename}`);

  await cp(normalized, destination, { recursive: true });

  const relative = pathRelativePortable(path.join(gameDir, "run"), destination);
  copiedPaths.set(normalized, relative);
  return relative;
}

function looksAbsolute(value) {
  return path.isAbsolute(value) || /^[A-Za-z]:[\\/]/.test(value);
}

function sourceClasspathDelimiter() {
  return path.delimiter;
}

function targetClasspathDelimiter() {
  return target.startsWith("win-") ? ";" : ":";
}

function pathRelativePortable(from, to) {
  return path.relative(from, to).replaceAll(path.sep, "/");
}

function safeBasename(value) {
  return value.replace(/[^a-zA-Z0-9._-]+/g, "_");
}

function quoteArgFileToken(token) {
  if (/^[A-Za-z0-9_./:@%+=,;\\-]+$/.test(token)) {
    return token;
  }

  return `"${token.replaceAll("\\", "\\\\").replaceAll('"', '\\"')}"`;
}

function normaliseAssetIndexName(value) {
  const trimmed = String(value || "").trim().replace(/^['"]|['"]$/g, "");
  return trimmed.endsWith(".json") ? trimmed.slice(0, -5) : trimmed;
}

async function copyPrunedAssets(assetsSource, destination, assetIndexName) {
  const sourceIndexPath = path.join(assetsSource, "indexes", `${assetIndexName}.json`);
  await assertExists(sourceIndexPath, `Minecraft asset index ${assetIndexName}`);

  const sourceIndex = await readJsonFile(sourceIndexPath, `Minecraft asset index ${assetIndexName}`);

  if (!sourceIndex || typeof sourceIndex !== "object" || !sourceIndex.objects || typeof sourceIndex.objects !== "object") {
    fail(`Minecraft asset index has no objects map: ${sourceIndexPath}`);
  }

  const keptObjects = {};
  const sourceObjects = Object.entries(sourceIndex.objects);
  let keptBytes = 0;
  let skippedBytes = 0;
  let skippedAudioCount = 0;
  let skippedLanguageCount = 0;
  let copiedObjectCount = 0;
  const copiedHashes = new Set();

  await rm(destination, { recursive: true, force: true });
  await mkdir(path.join(destination, "indexes"), { recursive: true });
  await mkdir(path.join(destination, "objects"), { recursive: true });

  for (const [logicalName, objectInfo] of sourceObjects) {
    if (!objectInfo || typeof objectInfo.hash !== "string") {
      fail(`Asset index entry is missing a hash for ${logicalName}`);
    }

    const size = Number(objectInfo.size || 0);

    if (!shouldKeepAsset(logicalName)) {
      skippedBytes += size;
      if (isAudioAsset(logicalName)) {
        skippedAudioCount += 1;
      } else if (isNonEnglishLanguageAsset(logicalName)) {
        skippedLanguageCount += 1;
      }
      continue;
    }

    keptObjects[logicalName] = objectInfo;
    keptBytes += size;

    if (!copiedHashes.has(objectInfo.hash)) {
      await copyAssetObject(assetsSource, destination, objectInfo.hash, logicalName);
      copiedHashes.add(objectInfo.hash);
      copiedObjectCount += 1;
    }
  }

  const trimmedIndex = {
    ...sourceIndex,
    objects: keptObjects,
  };

  const destinationIndexPath = path.join(destination, "indexes", `${assetIndexName}.json`);
  await writeFile(destinationIndexPath, `${JSON.stringify(trimmedIndex, null, 2)}\n`, "utf8");

  const summary = {
    assetIndex: assetIndexName,
    sourceObjectCount: sourceObjects.length,
    keptObjectCount: Object.keys(keptObjects).length,
    copiedObjectCount,
    skippedObjectCount: sourceObjects.length - Object.keys(keptObjects).length,
    keptBytes,
    skippedBytes,
    skippedAudioCount,
    skippedLanguageCount,
    policy: "keeps indexed non-audio assets and English/default language assets only",
  };

  console.log("Pruned Minecraft assets:");
  console.log(`  asset index: ${assetIndexName}`);
  console.log(`  kept: ${summary.keptObjectCount} indexed entries (${formatBytes(keptBytes)})`);
  console.log(`  skipped: ${summary.skippedObjectCount} indexed entries (${formatBytes(skippedBytes)})`);
  console.log(`  skipped audio/music entries: ${skippedAudioCount}`);
  console.log(`  skipped non-English language entries: ${skippedLanguageCount}`);

  return summary;
}

function shouldKeepAsset(logicalName) {
  if (isAudioAsset(logicalName)) {
    return false;
  }

  if (isNonEnglishLanguageAsset(logicalName)) {
    return false;
  }

  return true;
}

function isAudioAsset(logicalName) {
  return logicalName === "minecraft/sounds.json" || logicalName.startsWith("minecraft/sounds/");
}

function isNonEnglishLanguageAsset(logicalName) {
  if (logicalName.startsWith("minecraft/lang/")) {
    return logicalName !== "minecraft/lang/en_us.json";
  }

  if (logicalName.startsWith("realms/lang/")) {
    return logicalName !== "realms/lang/en_us.json";
  }

  return false;
}

async function copyAssetObject(assetsSource, destination, hash, logicalName) {
  const objectShard = hash.slice(0, 2);
  const sourceObject = path.join(assetsSource, "objects", objectShard, hash);
  const destinationObject = path.join(destination, "objects", objectShard, hash);

  await assertExists(sourceObject, `Minecraft asset object for ${logicalName}`);
  await mkdir(path.dirname(destinationObject), { recursive: true });
  await cp(sourceObject, destinationObject);
}

async function readJsonFile(filePath, label) {
  const raw = await readFile(filePath, "utf8");

  try {
    return JSON.parse(raw);
  } catch (error) {
    fail(`Could not parse ${label}: ${error.message}`);
  }
}

function formatBytes(bytes) {
  const mib = bytes / 1024 / 1024;
  return `${mib.toFixed(2)} MiB`;
}

async function copyJavaRuntimePruned(source, destination) {
  const summary = {
    copiedFileCount: 0,
    skippedFileCount: 0,
    copiedBytes: 0,
    skippedBytes: 0,
  };

  await copyDirectoryFiltered(source, destination, "", shouldCopyJavaRuntimeEntry, summary);

  console.log("Pruned Java runtime copy:");
  console.log(`  copied: ${summary.copiedFileCount} files (${formatBytes(summary.copiedBytes)})`);
  console.log(`  skipped: ${summary.skippedFileCount} files (${formatBytes(summary.skippedBytes)})`);

  return summary;
}

async function copyDirectoryFiltered(source, destination, relativeDir, shouldCopy, summary) {
  await mkdir(destination, { recursive: true });

  const entries = await readdir(source, { withFileTypes: true });

  for (const entry of entries) {
    const relativePath = relativeDir ? `${relativeDir}/${entry.name}` : entry.name;
    const sourcePath = path.join(source, entry.name);
    const destinationPath = path.join(destination, entry.name);

    if (!shouldCopy(relativePath, entry)) {
      await addSkippedEntryToSummary(sourcePath, entry, summary);
      continue;
    }

    if (entry.isDirectory()) {
      await copyDirectoryFiltered(sourcePath, destinationPath, relativePath, shouldCopy, summary);
    } else if (entry.isFile()) {
      await mkdir(path.dirname(destinationPath), { recursive: true });
      await cp(sourcePath, destinationPath);
      const stat = await fs.promises.stat(sourcePath);
      summary.copiedFileCount += 1;
      summary.copiedBytes += stat.size;
    } else if (entry.isSymbolicLink()) {
      await mkdir(path.dirname(destinationPath), { recursive: true });
      await cp(sourcePath, destinationPath, { dereference: false });
      summary.copiedFileCount += 1;
    }
  }
}

async function addSkippedEntryToSummary(sourcePath, entry, summary) {
  if (entry.isFile()) {
    const stat = await fs.promises.stat(sourcePath);
    summary.skippedFileCount += 1;
    summary.skippedBytes += stat.size;
    return;
  }

  if (!entry.isDirectory()) {
    summary.skippedFileCount += 1;
    return;
  }

  const entries = await readdir(sourcePath, { withFileTypes: true });
  for (const child of entries) {
    await addSkippedEntryToSummary(path.join(sourcePath, child.name), child, summary);
  }
}

function shouldCopyJavaRuntimeEntry(relativePath, entry) {
  const normalized = relativePath.replaceAll("\\", "/").toLowerCase();
  const basename = path.posix.basename(normalized);

  if (isPrunedJavaRuntimeDirectory(normalized, entry)) {
    return false;
  }

  if (basename === "src.zip" || /^classes.*\.jsa$/.test(basename)) {
    return false;
  }

  if (basename.endsWith(".pdb") || basename.endsWith(".map") || basename.endsWith(".diz")) {
    return false;
  }

  if (normalized.startsWith("bin/") || normalized.includes("/bin/")) {
    return shouldKeepJavaBinEntry(basename, entry);
  }

  return true;
}

function isPrunedJavaRuntimeDirectory(normalized, entry) {
  if (!entry.isDirectory()) {
    return false;
  }

  const prunedDirectoryNames = new Set([
    "demo",
    "demos",
    "include",
    "jmods",
    "man",
    "sample",
    "samples",
  ]);

  return normalized
    .split("/")
    .some((part) => prunedDirectoryNames.has(part));
}

function shouldKeepJavaBinEntry(basename, entry) {
  if (!entry.isFile()) {
    return true;
  }

  if (basename.endsWith(".dll") || basename.endsWith(".dylib") || basename.endsWith(".so")) {
    return true;
  }

  const alwaysKeep = new Set([
    "java",
    "java.exe",
    "javaw",
    "javaw.exe",
    "jspawnhelper",
  ]);

  if (alwaysKeep.has(basename)) {
    return true;
  }

  const developmentTools = new Set([
    "jar",
    "jar.exe",
    "jarsigner",
    "jarsigner.exe",
    "javac",
    "javac.exe",
    "javadoc",
    "javadoc.exe",
    "javap",
    "javap.exe",
    "jcmd",
    "jcmd.exe",
    "jconsole",
    "jconsole.exe",
    "jdb",
    "jdb.exe",
    "jdeprscan",
    "jdeprscan.exe",
    "jdeps",
    "jdeps.exe",
    "jfr",
    "jfr.exe",
    "jhsdb",
    "jhsdb.exe",
    "jimage",
    "jimage.exe",
    "jinfo",
    "jinfo.exe",
    "jlink",
    "jlink.exe",
    "jmap",
    "jmap.exe",
    "jmod",
    "jmod.exe",
    "jpackage",
    "jpackage.exe",
    "jps",
    "jps.exe",
    "jrunscript",
    "jrunscript.exe",
    "jshell",
    "jshell.exe",
    "jstack",
    "jstack.exe",
    "jstat",
    "jstat.exe",
    "jstatd",
    "jstatd.exe",
    "keytool",
    "keytool.exe",
    "rmiregistry",
    "rmiregistry.exe",
    "serialver",
    "serialver.exe",
  ]);

  return !developmentTools.has(basename);
}

async function copyJavaRuntime(source, destination) {
  const normalizedSource = path.resolve(source);

  const rootBinJava = await firstExisting([
    path.join(normalizedSource, "bin", "java.exe"),
    path.join(normalizedSource, "bin", "java"),
  ]);

  const macBundleJava = await firstExisting([
    path.join(normalizedSource, "Contents", "Home", "bin", "java"),
  ]);

  if (!rootBinJava && !macBundleJava) {
    fail([
      "Could not find a Java executable in the provided runtime.",
      `Provided path: ${normalizedSource}`,
      "Expected one of:",
      "  bin/java.exe",
      "  bin/java",
      "  Contents/Home/bin/java",
    ].join("\n"));
  }

  await rm(destination, { recursive: true, force: true });
  await mkdir(path.dirname(destination), { recursive: true });

  const pruneSummary = await copyJavaRuntimePruned(normalizedSource, destination);

  if (rootBinJava) {
    const javaBinaryName = target.startsWith("win-") ? "java.exe" : "java";
    const relative = `runtime/jre/bin/${javaBinaryName}`;
    await makeExecutableIfPresent(path.join(destination, "bin", javaBinaryName));
    return {
      layout: "java-home",
      relativeJavaExecutable: relative,
      pruneSummary,
    };
  }

  await makeExecutableIfPresent(path.join(destination, "Contents", "Home", "bin", "java"));
  return {
    layout: "macos-jdk-bundle",
    relativeJavaExecutable: "runtime/jre/Contents/Home/bin/java",
    pruneSummary,
  };
}

async function makeExecutableIfPresent(filePath) {
  if (!(await exists(filePath))) {
    return;
  }

  try {
    await chmod(filePath, 0o755);
  } catch {
    // Build verification will catch non-executable Java binaries.
  }
}

async function copyDirectoryIfPresent(source, destination) {
  if (!(await exists(source))) {
    return;
  }

  await rm(destination, { recursive: true, force: true });
  await mkdir(path.dirname(destination), { recursive: true });
  await cp(source, destination, { recursive: true });
}

async function copyFileIfPresent(source, destination) {
  if (!(await exists(source))) {
    return;
  }

  await mkdir(path.dirname(destination), { recursive: true });
  await cp(source, destination);
}

async function assertExists(itemPath, label) {
  if (!(await exists(itemPath))) {
    fail(`${label} not found: ${itemPath}`);
  }
}

async function exists(itemPath) {
  try {
    await access(itemPath, fs.constants.F_OK);
    return true;
  } catch {
    return false;
  }
}

async function firstExisting(candidates) {
  for (const candidate of candidates) {
    if (await exists(candidate)) {
      return candidate;
    }
  }

  return null;
}

async function countFiles(root) {
  if (!(await exists(root))) {
    return 0;
  }

  const entries = await readdir(root, { withFileTypes: true });
  let total = 0;

  for (const entry of entries) {
    const entryPath = path.join(root, entry.name);

    if (entry.isDirectory()) {
      total += await countFiles(entryPath);
    } else if (entry.isFile()) {
      total += 1;
    }
  }

  return total;
}

function fail(message) {
  console.error(message);
  process.exit(1);
}