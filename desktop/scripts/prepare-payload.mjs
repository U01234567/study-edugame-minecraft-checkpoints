import { spawnSync } from "node:child_process";
import {
  access,
  chmod,
  cp,
  mkdir,
  readdir,
  readFile,
  rm,
  stat,
  writeFile,
} from "node:fs/promises";
import fs from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const __filename = fileURLToPath(import.meta.url);
const scriptsDir = path.dirname(__filename);
const desktopDir = path.resolve(scriptsDir, "..");
const repoRoot = path.resolve(desktopDir, "..");

const args = parseArgs(process.argv.slice(2));

const target = args.target ?? defaultTarget();
const jrePath = args.jre ?? process.env.JAVA_HOME;
const buildLabel = args["build-label"] ?? "local-dev";

const supportedTargets = new Set(["win-x64", "mac-arm64"]);

if (!supportedTargets.has(target)) {
  fail(`Unsupported target "${target}". Use one of: ${[...supportedTargets].join(", ")}`);
}

if (!jrePath) {
  fail("No Java runtime was provided. Set JAVA_HOME or pass --jre <path>.");
}

const modsCustomDir = path.join(repoRoot, "mods", "custom");
const worldDir = path.join(repoRoot, "world");
const externalLibsDir = path.join(repoRoot, "mods", "external", "libs");

const payloadRoot = path.join(desktopDir, "payload-dist", target, "payload");
const bundledPayload = path.join(desktopDir, "src-tauri", "resources", "payload");

const gameDir = path.join(payloadRoot, "game");
const runtimeDir = path.join(payloadRoot, "runtime");
const licensesDir = path.join(payloadRoot, "licenses");

await assertExists(modsCustomDir, "Fabric mod folder");
await assertExists(worldDir, "Study world folder");
await assertExists(externalLibsDir, "External runtime libs folder");
await assertExists(jrePath, "Java runtime/JDK folder");

console.log(`Preparing payload for ${target}`);
console.log(`Repository: ${repoRoot}`);
console.log(`Java runtime: ${jrePath}`);

await rm(payloadRoot, { recursive: true, force: true });
await mkdir(gameDir, { recursive: true });
await mkdir(runtimeDir, { recursive: true });
await mkdir(licensesDir, { recursive: true });

await runGradleBuild();

const buildSource = path.join(modsCustomDir, "build");
const runSource = path.join(modsCustomDir, "run");

await assertExists(buildSource, "Generated Gradle build folder");
await assertExists(runSource, "Prepared Minecraft run folder");

console.log("Copying runtime build essentials...");

const buildDest = path.join(gameDir, "build");

await copyDirectoryIfPresent(
  path.join(buildSource, "classes"),
  path.join(buildDest, "classes")
);

await copyDirectoryIfPresent(
  path.join(buildSource, "resources"),
  path.join(buildDest, "resources")
);

await copyDirectoryIfPresent(
  path.join(buildSource, "libs"),
  path.join(buildDest, "libs")
);

await copyDirectoryIfPresent(
  path.join(buildSource, "loom-cache", "argFiles"),
  path.join(buildDest, "loom-cache", "argFiles")
);

console.log("Copying prepared run essentials...");

const runDest = path.join(gameDir, "run");

await copyDirectoryIfPresent(
  path.join(runSource, "mods"),
  path.join(runDest, "mods")
);

await copyDirectoryIfPresent(
  path.join(runSource, "saves", "experiment-world"),
  path.join(runDest, "saves", "experiment-world")
);

await copyDirectoryIfPresent(
  path.join(runSource, "config"),
  path.join(runDest, "config")
);

await copyDirectoryIfPresent(
  path.join(runSource, "resourcepacks"),
  path.join(runDest, "resourcepacks")
);

await copyDirectoryIfPresent(
  path.join(runSource, "shaderpacks"),
  path.join(runDest, "shaderpacks")
);

await copyFileIfPresent(
  path.join(runSource, "options.txt"),
  path.join(runDest, "options.txt")
);

await copyFileIfPresent(
  path.join(runSource, "servers.dat"),
  path.join(runDest, "servers.dat")
);

console.log("Copying Java runtime...");

const copiedJavaLayout = await copyJavaRuntime(jrePath, path.join(runtimeDir, "jre"));

console.log("Copying license/provenance files...");

await copyFileIfPresent(
  path.join(repoRoot, "LICENSE"),
  path.join(licensesDir, "LICENSE")
);

await copyFileIfPresent(
  path.join(repoRoot, "mods", "external", "README.md"),
  path.join(licensesDir, "mods-external-README.md")
);

const manifest = {
  schemaVersion: 1,
  appName: "Minecraft Study",
  buildLabel,
  target,
  generatedAtUtc: new Date().toISOString(),
  internalModId: "study-checkpoints",
  game: {
    runDirectory: "game/run",
    buildDirectory: "game/build",
    worldSaveName: "experiment-world",
  },
  runtime: {
    javaDirectory: "runtime/jre",
    javaExecutable: copiedJavaLayout.relativeJavaExecutable,
    javaLayout: copiedJavaLayout.layout,
  },
  launch: {
    directJavaLaunchReady: false,
    mode: "prepared-payload",
    note: "Payload contains runtime essentials. Direct no-Gradle launch command still needs verification.",
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
console.log("Payload prepared:");
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
    const inline = withoutPrefix.split("=");

    if (inline.length === 2) {
      parsed[inline[0]] = inline[1];
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

  return "win-x64";
}

async function runGradleBuild() {
  console.log("Building Fabric mod and preparing dev run folder...");

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
      "build",
      "copyStudyWorld",
      "copyExternalRuntimeMods",
    ];
  } else {
    await assertExists(gradlewUnix, "Gradle launcher");

    try {
      await chmod(gradlewUnix, 0o755);
    } catch {
      // Non-fatal. The file may already be executable or the filesystem may ignore chmod.
    }

    command = "./gradlew";
    commandArgs = [
      "build",
      "copyStudyWorld",
      "copyExternalRuntimeMods",
    ];
  }

  const result = spawnSync(command, commandArgs, {
    cwd: modsCustomDir,
    stdio: "inherit",
    shell: false,
    env: {
      ...process.env,
      JAVA_HOME: jrePath,
    },
  });

  if (result.error) {
    fail(`Gradle could not be started: ${result.error.message}`);
  }

  if (result.status !== 0) {
    fail(`Gradle failed with exit code ${result.status}`);
  }
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
    fail(
      [
        "Could not find a Java executable in the provided runtime.",
        `Provided path: ${normalizedSource}`,
        "Expected one of:",
        "  bin/java.exe",
        "  bin/java",
        "  Contents/Home/bin/java",
      ].join("\n")
    );
  }

  await rm(destination, { recursive: true, force: true });
  await mkdir(path.dirname(destination), { recursive: true });
  await cp(normalizedSource, destination, { recursive: true });

  if (rootBinJava) {
    return {
      layout: "java-home",
      relativeJavaExecutable: process.platform === "win32"
        ? "runtime/jre/bin/java.exe"
        : "runtime/jre/bin/java",
    };
  }

  return {
    layout: "macos-jdk-bundle",
    relativeJavaExecutable: "runtime/jre/Contents/Home/bin/java",
  };
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