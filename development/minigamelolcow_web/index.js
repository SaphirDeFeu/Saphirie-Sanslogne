const child_proc = require("child_process");
const express = require("express");
const fs = require("fs");
const path = require("path");

const PORT = process.argv[3];
const HOST = process.argv[2];
let app;
let NEED_APPROVAL;
let FINISHED;
if (require.main === module) {
  app = express();
  NEED_APPROVAL = 0;
  FINISHED = 0;
}

async function main() {
  const _p = path.join(__dirname, "../--ignore--web");
  if (!fs.existsSync(_p)) fs.mkdirSync(_p);

  // Check the amount of js files with prefix svr: in ./server
  calculateApprovalNecessity(path.join(__dirname, "server"));

  app.use(express.static("public"));
  app.use((req, res, next) => {
    const time = new Date(Date.now()).toLocaleString("fr");
    const ip = req.socket.remoteAddress || req.headers["x-forwarded-for"];
    console.log(`[REQ] [${time}] ${ip} ${req.method} ${req.url}`);

    next();
  });

  if (NEED_APPROVAL == 0) declareFinished();
  importDynamics(path.join(__dirname, "server"));
}

/**
 * @param {"get"|"post"} method
 * @param {string} path
 * @param {RequestHandler<ParamsDictionary, any, any, qs.ParsedQs, Record<string, any>>[]} handlers
 */
function setRoute(method, path, ...handlers) {
  console.log("setting route", path);
  switch (method) {
    case "get": {
      app.get(path, handlers);
      break;
    }
    case "post": {
      app.post(path, handlers);
      break;
    }
  }
}

function declareFinished() {
  FINISHED += 1;

  if (FINISHED >= NEED_APPROVAL) {
    app.listen(PORT, HOST, () => {
      console.log(`Listening on ${HOST}:${PORT}`);
    });
  }

  return FINISHED;
}

function calculateApprovalNecessity(root) {
  const entries = fs.readdirSync(root);

  for (const entry of entries) {
    const _p = path.join(root, entry);
    if (fs.lstatSync(_p).isDirectory()) {
      calculateApprovalNecessity(_p);
    } else if (fs.lstatSync(_p).isFile()) {
      if (
        entry.substring(0, 4) == "svr." &&
        entry.substring(entry.length - 3) == ".js"
      ) NEED_APPROVAL++;
    }
  }
  console.log(NEED_APPROVAL);

  return NEED_APPROVAL;
}

async function importDynamics(root) {
  const entries = fs.readdirSync(root);

  for (const entry of entries) {
    const _p = path.join(root, entry);
    if (fs.lstatSync(_p).isDirectory(_p)) {
      importDynamics(_p);
      continue;
    }
    try {
      if (
        entry.substring(0, 4) == "svr." &&
        entry.substring(entry.length - 3) == ".js"
      ) {
        await import("file://" + _p);
      }
    } catch (e) {
      console.error(e);
    }
  }
}

// Basically the main function call
if (require.main === module) {
  main();
}

module.exports = {
  setRoute,
  declareFinished,
};
