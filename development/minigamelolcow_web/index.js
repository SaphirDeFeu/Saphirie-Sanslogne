const child_proc = require("child_process");

console.log(process.argv);
const proc = child_proc.exec("npm i", (err, stdout, stderr) => {
  if (err) console.log(err);
  if (stderr) console.log(stderr);
  console.log(stdout);
});

proc.on("exit", main);

function main() {
  const PORT = process.argv[3];
  const HOST = process.argv[2];

  console.log(HOST + ":" + PORT);
}
