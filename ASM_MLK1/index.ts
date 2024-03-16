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
      result.push(parseInt(e));
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
    default:
      return -1;
  }
}

const text =
  `This program requires ${result.length} individual entries (${Math.ceil(
    result.length / 16
  )} ROM cartridges)\n\n` + result.join("\n");
Deno.writeFileSync("./output.txt", new TextEncoder().encode(text));
