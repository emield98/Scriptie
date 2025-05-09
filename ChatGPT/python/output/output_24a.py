# ChatGPT-generated solution will go here.
import re
from collections import defaultdict, deque

def main():
    with open("input.txt") as f:
        lines = [line.strip() for line in f if line.strip()]

    wire_values = {}
    gates = []

    # Parse input
    for line in lines:
        if ':' in line:
            wire, val = line.split(':')
            wire_values[wire.strip()] = int(val.strip())
        else:
            m = re.match(r"(\w+) (AND|OR|XOR) (\w+) -> (\w+)", line)
            if m:
                in1, op, in2, out = m.groups()
                gates.append((in1, op, in2, out))

    # Continue processing gates until no changes occur
    pending = gates[:]
    while pending:
        next_round = []
        for in1, op, in2, out in pending:
            if in1 in wire_values and in2 in wire_values:
                a, b = wire_values[in1], wire_values[in2]
                if op == "AND":
                    wire_values[out] = a & b
                elif op == "OR":
                    wire_values[out] = a | b
                elif op == "XOR":
                    wire_values[out] = a ^ b
            else:
                next_round.append((in1, op, in2, out))
        if len(next_round) == len(pending):
            break  # no progress, avoid infinite loop
        pending = next_round

    # Gather all wires starting with 'z' and compute output value
    z_wires = sorted([k for k in wire_values if k.startswith('z')])
    binary_str = ''.join(str(wire_values[z]) for z in z_wires[::-1])
    print(int(binary_str, 2))

if __name__ == "__main__":
    main()
