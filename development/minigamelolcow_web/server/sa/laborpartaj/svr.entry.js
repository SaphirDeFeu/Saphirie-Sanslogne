const { write, read, exists } = require("../../../data");
const { setRoute, declareFinished } = require("../../../index");

const DOMAIN = "laborpartaj.sa";

let LaborpartajData = {
  /**
   * @type {Array<{lastname: string, firstname: string, street: string, city: string, country: string}>}
   */
  registrations: [],
};

if (!exists(DOMAIN)) {
  write(DOMAIN, LaborpartajData);
}

setRoute("post", "/sa/laborpartaj/register", async (req, res) => {
  const body = await req.body;

  const file = read(DOMAIN);
  if (file[1] == 1) return;

  const data = JSON.parse(file[0]);

  if (typeof data === typeof LaborpartajData) {
    data.registrations.push(body);

    write(DOMAIN, data);

    res.status(200).send(
      "Votre adhésion aux listes engagées a bien été prise en compte!",
    );
  } else {
    write(DOMAIN, LaborpartajData);

    res.status(200).send("Une erreur est parvenue. Ré-essayez.");
  }
});

setRoute("get", "/sa/laborpartaj/adherents", async (req, res) => {
  const file = read(DOMAIN);
  if (file[1] == 1) return;

  const data = JSON.parse(file[0]);

  const total = data.registrations.length;
  const sanslogniens = data.registrations.filter((item) => {
    return item.city.includes("Sanslogne");
  }).length;

  // Format: [total, sanslogniens]
  res.status(200).send([total, sanslogniens]);
});

declareFinished();
