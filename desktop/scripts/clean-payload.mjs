import { rm } from "node:fs/promises";
import path from "node:path";
import { fileURLToPath } from "node:url";

const __filename = fileURLToPath(import.meta.url);
const scriptsDir = path.dirname(__filename);
const desktopDir = path.resolve(scriptsDir, "..");

const pathsToRemove = [
  path.join(desktopDir, "payload-dist"),
  path.join(desktopDir, "src-tauri", "resources", "payload"),
];

for (const item of pathsToRemove) {
  console.log(`Removing ${item}`);
  await rm(item, { recursive: true, force: true });
}

console.log("Payload output cleaned.");