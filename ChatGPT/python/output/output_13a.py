# ChatGPT-generated solution will go here.
def parse_input():
    machines = []
    with open("input.txt") as f:
        lines = [line.strip() for line in f if line.strip()]
        for i in range(0, len(lines), 3):
            ax, ay = map(int, lines[i].split(":")[1].split(","))
            bx, by = map(int, lines[i+1].split(":")[1].split(","))
            px, py = map(int, lines[i+2].split(":")[1].split(","))
            machines.append(((ax, ay), (bx, by), (px, py)))
    return machines

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
            machines.append(((ax, ay), (bx, by), (px, py)))

    max_presses = 100
    total_cost = 0
    for (ax, ay), (bx, by), (px, py) in machines:
        min_cost = float('inf')
        for a in range(max_presses + 1):
            for b in range(max_presses + 1):
                x = a * ax + b * bx
                y = a * ay + b * by
                if x == px and y == py:
                    cost = a * 3 + b * 1
                    if cost < min_cost:
                        min_cost = cost
        if min_cost != float('inf'):
            total_cost += min_cost

    print(total_cost)

if __name__ == "__main__":
    main()
