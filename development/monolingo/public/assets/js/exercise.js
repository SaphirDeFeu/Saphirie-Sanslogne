// CHOICE WRONG
function CH_X(ex, choice) {
  const elem = document.getElementById(ex);
  const button = elem.querySelector("div.multiple").children.item(choice);

  button.setAttribute("style", "border: 2px solid red");
}

function CH_V(ex, choice) {
  const elem = document.getElementById(ex);
  const button = elem.querySelector("div.multiple").children.item(choice);

  button.setAttribute("style", "border: 2px solid green");

  elem.querySelector("div.explain").classList.remove(
    "hidden",
  );
  elem.querySelector("div.question").classList.add("hidden");
}

function CHECK(ex) {
  const elem = document.getElementById(ex);
  const explain = elem.querySelector("div.explain");

  const input = elem.querySelector("input");
  const possibilities = explain.textContent.toLowerCase().split("/");

  if (possibilities.includes(input.value.toLowerCase())) {
    input.setAttribute("style", "border: 2px solid green");

    const windowYSize = window.innerHeight;
    window.scrollBy({
      top: windowYSize,
      behavior: "smooth",
    });
  } else {
    input.setAttribute("style", "border: 2px solid red");
  }

  explain.classList.remove("hidden");
}

document.addEventListener("DOMContentLoaded", () => {
  window.scrollTo({
    top: 0,
    behavior: "smooth",
  });
});

/*
const windowYSize = window.innerHeight;
window.scrollBy({
  top: windowYSize,
  behavior: "smooth",
});
*/
