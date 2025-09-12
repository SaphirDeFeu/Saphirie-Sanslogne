const child_proc = require("child_process");
const express = require("express");

const PORT = process.argv[3];
const HOST = process.argv[2];

console.log(HOST + ":" + PORT);
