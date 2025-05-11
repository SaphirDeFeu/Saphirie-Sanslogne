const express = require("express");

const app = express();

app.use("/", express.static("./public/"));

app.listen(5071, () => {
  console.log("Server listening");
});
