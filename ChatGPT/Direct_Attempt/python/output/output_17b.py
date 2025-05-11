# ChatGPT-generated solution will go here.
def run_program(a_value, program):
    reg = {'A': a_value, 'B': 0, 'C': 0}
    ip = 0
    output = []

    def combo_value(op):
        if op <= 3:
            return op
        elif op == 4:
            return reg['A']
        elif op == 5:
            return reg['B']
        elif op == 6:
            return reg['C']
        else:
            return 0  # operand 7 is invalid, but assume 0 for safety

    while ip < len(program):
        opcode = program[ip]
        if ip + 1 >= len(program):
            break
        operand = program[ip + 1]

        if opcode == 0:  # adv
            denom = 2 ** combo_value(operand)
            reg['A'] = reg['A'] // denom
        elif opcode == 1:  # bxl
            reg['B'] ^= operand
        elif opcode == 2:  # bst
            reg['B'] = combo_value(operand) % 8
        elif opcode == 3:  # jnz
            if reg['A'] != 0:
                ip = operand
                continue
        elif opcode == 4:  # bxc
            reg['B'] ^= reg['C']
        elif opcode == 5:  # out
            output.append(combo_value(operand) % 8)
        elif opcode == 6:  # bdv
            denom = 2 ** combo_value(operand)
            reg['B'] = reg['A'] // denom
        elif opcode == 7:  # cdv
            denom = 2 ** combo_value(operand)
            reg['C'] = reg['A'] // denom

        ip += 2

    return output

def main():
    with open("input.txt") as f:
        lines = [line.strip() for line in f if line.strip()]

    program = []
    for line in lines:
        if line.startswith("Program:"):
            program = list(map(int, line.split(":")[1].split(",")))

    target = program
    i = 1
    while True:
        result = run_program(i, program)
        if result == target:
            print(i)
            return
        i += 1

if __name__ == "__main__":
    main()
