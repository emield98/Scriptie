# ChatGPT-generated solution will go here.
def main():
    width, height = 101, 103
    time = 100
    robots = []

    with open('input.txt') as f:
        for line in f:
            if not line.strip():
                continue
            parts = line.strip().split()
            px, py = map(int, parts[0][2:].split(','))
            vx, vy = map(int, parts[1][2:].split(','))
            robots.append((px, py, vx, vy))

    positions = {}
    for px, py, vx, vy in robots:
        x = (px + vx * time) % width
        y = (py + vy * time) % height
        positions[(x, y)] = positions.get((x, y), 0) + 1

    mid_x = width // 2
    mid_y = height // 2
    quadrants = [0, 0, 0, 0]  # Q1, Q2, Q3, Q4

    for (x, y), count in positions.items():
        if x == mid_x or y == mid_y:
            continue
        if x < mid_x and y < mid_y:
            quadrants[0] += count  # top-left
        elif x >= mid_x and y < mid_y:
            quadrants[1] += count  # top-right
        elif x < mid_x and y >= mid_y:
            quadrants[2] += count  # bottom-left
        else:
            quadrants[3] += count  # bottom-right

    safety_factor = 1
    for q in quadrants:
        safety_factor *= q

    print(safety_factor)

if __name__ == '__main__':
    main()
