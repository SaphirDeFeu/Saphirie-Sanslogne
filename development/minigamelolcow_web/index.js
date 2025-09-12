const child_proc = require("child_process");
const express = require("express");

const PORT = process.argv[3];
const HOST = process.argv[2];
const app = express();

app.use(express.static("public"));
app.use((req, res, next) => {
  const time = new Date(Date.now()).toLocaleString("fr");
  const ip = req.socket.remoteAddress || req.headers["x-forwarded-for"];
  console.log(`[REQ] [${time}] ${ip} ${req.method} ${req.url}`);

  next();
});

app.listen(PORT, HOST, () => {
  console.log(`Listening on ${HOST}:${PORT}`);
});
