# ChatGPT-generated solution will go here.
import re
import itertools
from collections import defaultdict, deque

def parse_input():
    with open("input.txt") as f:
        blocks = f.read().split("\n\n")
    initial_vals = {}
    gates = []
    for line in blocks[0].splitlines():
        k, v = line.split(":")
        initial_vals[k.strip()] = int(v.strip())
    for line in blocks[1].splitlines():
        m = re.match(r"(\w+) (AND|OR|XOR) (\w+) -> (\w+)", line)
        if m:
            in1, op, in2, out = m.groups()
            gates.append((in1, op, in2, out))
    return initial_vals, gates

def simulate(initial, gates):
    wires = dict(initial)
    gates = gates[:]
    progress = True
    while progress and gates:
        next_gates = []
        progress = False
        for in1, op, in2, out in gates:
            if in1 in wires and in2 in wires:
                a, b = wires[in1], wires[in2]
                if op == "AND":
                    wires[out] = a & b
                elif op == "OR":
                    wires[out] = a | b
                elif op == "XOR":
                    wires[out] = a ^ b
                progress = True
            else:
                next_gates.append((in1, op, in2, out))
        gates = next_gates
    return wires

def extract_bits(wires, prefix):
    bits = []
    for i in range(128):
        k = f"{prefix}{i:02}"
        if k in wires:
            bits.append(wires[k])
    return bits

def bin_to_int(bits):
    return sum(b << i for i, b in enumerate(bits))

def generate_addition_testcases(nbits=8):
    cases = []
    for x in [3, 7, 42, 100]:
        for y in [5, 11, 50, 200]:
            if x < 2**nbits and y < 2**nbits:
                cases.append((x, y))
    return cases

def encode_inputs(x, y, nbits):
    inputs = {}
    for i in range(nbits):
        inputs[f"x{i:02}"] = (x >> i) & 1
        inputs[f"y{i:02}"] = (y >> i) & 1
    return inputs

def expected_sum(x, y, nbits):
    s = x + y
    return [(s >> i) & 1 for i in range(nbits + 1)]

def find_swaps(gates, initial_vals, nbits=8):
    # Simulate without swaps
    testcases = generate_addition_testcases(nbits)
    wrong = set()
    for x, y in testcases:
        inputs = encode_inputs(x, y, nbits)
        wires = {**initial_vals, **inputs}
        output = simulate(wires, gates)
        got = extract_bits(output, "z")
        want = expected_sum(x, y, nbits)
        for i, (g, w) in enumerate(zip(got, want)):
            if g != w:
                wrong.add(f"z{i:02}")

    # Find all candidate z wires
    z_targets = [out for _, _, _, out in gates if out.startswith("z")]
    candidates = list(set(z_targets))
    wire_to_gate = {out: (in1, op, in2, out) for in1, op, in2, out in gates}

    # Try all combinations of 4 pairs (8 wires)
    for wireset in itertools.combinations(candidates, 8):
        for pairs in itertools.permutations(wireset, 8):
            valid = True
            swapped_gates = gates[:]
            mapping = {}
            for i in range(0, 8, 2):
                a, b = pairs[i], pairs[i+1]
                ga, gb = wire_to_gate[a], wire_to_gate[b]
                swapped_gates = [g for g in swapped_gates if g != ga and g != gb]
                swapped_gates.append((ga[0], ga[1], ga[2], b))
                swapped_gates.append((gb[0], gb[1], gb[2], a))

            all_ok = True
            for x, y in testcases:
                inputs = encode_inputs(x, y, nbits)
                wires = {**initial_vals, **inputs}
                output = simulate(wires, swapped_gates)
                got = extract_bits(output, "z")
                want = expected_sum(x, y, nbits)
                if got[:len(want)] != want:
                    all_ok = False
                    break
            if all_ok:
                return ",".join(sorted(pairs[:8]))
    return "Not found"

def main():
    initial_vals, gates = parse_input()
    print(find_swaps(gates, initial_vals, nbits=8))

if __name__ == "__main__":
    main()
