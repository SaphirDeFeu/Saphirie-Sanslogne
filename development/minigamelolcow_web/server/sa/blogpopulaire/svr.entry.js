const { write, read } = require("../../../data");
const { setRoute, declareFinished } = require("../../../index");

setRoute("get", "/sa/blogpopulaire/test", (req, res) => {
  res.status(200).send("HALLO GUYS!!!!");
});

declareFinished();
