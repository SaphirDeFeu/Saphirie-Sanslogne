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
 * Checks the existence of a file named --ignore-web/[domain]
 * @param {string} domain a single file name (no slashes) that represents the website domain
 *
 * @return {boolean} function exit value
 */
function exists(domain) {
  if (typeof domain !== typeof "") return "domain is not string";

  if (illegal_regex.test(domain)) return "invalid domain name";

  // We know the parent folder exists because it's been created during setup
  const path = join(__dirname, "../--ignore--web", domain);

  return fs.existsSync(path);
}

/**
 * Reads JSON data from --ignore-web/[domain]
 * @param {string} domain a single file name (no slashes) that represents the website domain
 *
 * @return {[string, number]} function exit value or object (return[1] is 0 when function returns an object, and 1 when it returns an exit value)
 */
function read(domain) {
  if (typeof domain !== typeof "") return ["domain is not string", 1];

  if (illegal_regex.test(domain)) return ["invalid domain name", 1];

  const path = join(__dirname, "../--ignore--web", domain);

  const _d = fs.readFileSync(path, "utf-8");
  return [_d, 0];
}

module.exports = {
  write,
  read,
  exists,
};
