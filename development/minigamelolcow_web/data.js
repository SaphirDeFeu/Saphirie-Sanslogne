const fs = require("fs");
const { join } = require("path");

const illegal_regex = /[\\/:*?"<>|]/g;

/**
 * Writes JSON data to --ignore--web/[domain]
 * @param {string} domain a single file name (no slashes) that represents the website domain
 * @param {object} data data to be stored (must be a JSON object)
 *
 * @return {string} function exit value
 */
function write(domain, data) {
  if (typeof domain !== typeof "") return "domain is not string";
  if (typeof data !== typeof {}) return "data is not object";

  if (illegal_regex.test(domain)) return "invalid domain name";

  // We know the parent folder exists because it's been created during setup
  const path = join(__dirname, "../--ignore--web", domain);

  const _d = JSON.stringify(data, null, 2);
  fs.writeFileSync(path, _d, "utf-8");
  return "success";
}

/**
 * Reads JSON data from --ignore-web/[domain]
 * @param {string} domain a single file name (no slashes) that represents the website domain
 *
 * @return {string} function exit value
 */
function read(domain) {
  if (typeof domain !== typeof "") return "domain is not string";

  if (illegal_regex.test(domain)) return "invalid domain name";

  const path = join(__dirname, "../--ignore--web", domain);

  const _d = fs.readFileSync(path, "utf-8");
  return JSON.parse(_d);
}

module.exports = {
  write,
  read,
};
