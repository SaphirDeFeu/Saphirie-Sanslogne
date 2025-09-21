// This file is used exclusively to count the amount of directories present in this folder.
// So as to know whether or not the folder will be downloaded correctly by GitHub API calls.

const fs = require("fs");
const { join } = require("path");

const IGNORE_LIST = [
  "node_modules",
  "package-lock.json",
];

const MAX_ALLOWED = 60; // Unauthenticated GitHub API calls allow up to 60 requests per hour (60 directories) per IP.

function countDirectories(root) {
  let total = 1;

  const data = fs.readdirSync(root);

  for (const entry of data) {
    if (IGNORE_LIST.includes(entry)) continue;

    const path = join(root, entry);
    console.log(path, "is dir?:", fs.lstatSync(path).isDirectory());

    if (fs.lstatSync(path).isDirectory()) {
      total += countDirectories(path);
    }
  }

  return total;
}

const amount = countDirectories(__dirname);
console.log(amount);

if (amount > MAX_ALLOWED) {
  console.log("Limite dépassée.");
} else {
  console.log("OK");
}
