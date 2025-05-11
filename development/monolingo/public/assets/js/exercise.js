// CHOICE WRONG
function CH_X(ex, choice) {
  const elem = document.getElementById(ex);
  const button = elem.querySelector("div.multiple").children.item(choice);

  button.setAttribute("style", "background-color: red");
}

function CH_V(ex, choice) {
  const elem = document.getElementById(ex);
  const button = elem.querySelector("div.multiple").children.item(choice);

  button.setAttribute("style", "background-color: green");

  elem.querySelector("div.explain").classList.remove(
    "hidden",
  );
  elem.querySelector("div.question").classList.add("hidden");

  const windowYSize = window.innerHeight;
  window.scroll({
    top: windowYSize,
    behavior: "smooth",
  });
}
