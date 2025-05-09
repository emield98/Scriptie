# ChatGPT-generated solution will go here.
from math import gcd
from functools import reduce

def extended_gcd(a, b):
    if b == 0:
        return (1, 0, a)
    else:
        x1, y1, d = extended_gcd(b, a % b)
        x, y = y1, x1 - (a // b) * y1
        return x, y, d

def solve_diophantine(a1, b1, c1, a2, b2, c2):
    det = a1 * b2 - a2 * b1
    if det == 0:
        return None  # No unique solution
    x = (c1 * b2 - c2 * b1)
    y = (a1 * c2 - a2 * c1)
    if x % det != 0 or y % det != 0:
        return None
    return x // det, y // det

def find_min_cost(ax, ay, bx, by, px, py):
    x, y, d = extended_gcd(ax, bx)
    dx = px
    dy = py
    det = ax * by - ay * bx
    if det == 0:
        return None
    # Find one solution (a0, b0)
    a1 = ax
    b1 = bx
    c1 = dx
    a2 = ay
    b2 = by
    c2 = dy
    sol = solve_diophantine(a1, b1, c1, a2, b2, c2)
    if sol is None:
        return None
    b0, a0 = sol  # because of cross multiplication
    # Generate all integer solutions
    # general solution:
    # a = a0 + k * (bx // g)
    # b = b0 - k * (ax // g)
    g = gcd(ax * by - ay * bx, ay * bx - ax * by)
    # we search for integer solutions a, b >= 0 with minimum cost a*3 + b
    # brute-force over a reasonable range around a0, b0
    min_cost = None
    for k in range(-1000000, 1000000):
        a = a0 + k * (bx // gcd(ax, bx))
        b = b0 - k * (ax // gcd(ax, bx))
        if a >= 0 and b >= 0:
            cost = a * 3 + b
            if min_cost is None or cost < min_cost:
                min_cost = cost
    return min_cost

def extract_values(line):
    parts = line.split(",")
    x = int(parts[0].split("+")[1] if '+' in parts[0] else parts[0].split("=")[1])
    y = int(parts[1].split("+")[1] if '+' in parts[1] else parts[1].split("=")[1])
    return x, y

def main():
    machines = []
    with open("input.txt") as f:
        lines = [line.strip() for line in f if line.strip()]
        for i in range(0, len(lines), 3):
            ax, ay = extract_values(lines[i])
            bx, by = extract_values(lines[i+1])
            px, py = extract_values(lines[i+2])
            px += 10**13
            py += 10**13
            machines.append(((ax, ay), (bx, by), (px, py)))

    total_cost = 0
    for (ax, ay), (bx, by), (px, py) in machines:
        result = find_min_cost(ax, ay, bx, by, px, py)
        if result is not None:
            total_cost += result

    print(total_cost)

if __name__ == "__main__":
    main()
