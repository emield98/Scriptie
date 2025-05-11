# ChatGPT-generated solution will go here.
def main():
    robots = []
    with open('input.txt') as f:
        for line in f:
            if not line.strip():
                continue
            parts = line.strip().split()
            px, py = map(int, parts[0][2:].split(','))
            vx, vy = map(int, parts[1][2:].split(','))
            robots.append((px, py, vx, vy))

    def get_bounds(positions):
        xs = [x for x, y in positions]
        ys = [y for x, y in positions]
        return min(xs), max(xs), min(ys), max(ys)

    def simulate(t):
        return [(px + vx * t, py + vy * t) for (px, py, vx, vy) in robots]

    prev_area = float('inf')
    time = 0

    while True:
        positions = simulate(time)
        min_x, max_x, min_y, max_y = get_bounds(positions)
        area = (max_x - min_x) * (max_y - min_y)
        if area > prev_area:
            print(time - 1)
            return
        prev_area = area
        time += 1

if __name__ == '__main__':
    main()
