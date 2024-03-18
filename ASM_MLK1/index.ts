const data = new TextDecoder("utf-8").decode(Deno.readFileSync("./source.s"));

const lines = data.split("\n");
const result: number[] = [];

if (lines[lines.length - 1] == "") {
  lines.pop();
}

for (const line of lines) {
  const [instruction, ...values] = line.split(" ");
  result.push(parseInstruction(instruction));

  if (values.length > 0) {
    values.forEach((e) => {
      const integer = parseInt(e);
      if (!Number.isNaN(integer)) {
        result.push(integer);
      } else {
        if (instruction == "mov") {
          const regNumber = getRegNum(e);
          result.push(regNumber);
        }
      }
    });
  }
}

function parseInstruction(instruction: string): number {
  switch (instruction) {
    case "nop":
      return 0;
    case "ret":
      return 1;
    case "jmp":
      return 2;
    case "mov":
      return 3;
    default:
      return -1;
  }
}

function getRegNum(register: string): number {
  switch (register) {
    case "eax":
      return 0;
    case "ebx":
      return 1;
    case "ecx":
      return 2;
    case "edx":
      return 3;
    default:
      return -1;
  }
}

const text =
  `This program requires ${result.length} individual entries (${Math.ceil(
    result.length / 16
  )} ROM cartridges)\n\n` + result.join("\n");
Deno.writeFileSync("./output.txt", new TextEncoder().encode(text));
