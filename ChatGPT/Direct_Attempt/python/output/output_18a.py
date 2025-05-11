# ChatGPT-generated solution will go here.
from collections import deque

def main():
    with open("input.txt") as f:
        lines = [line.strip() for line in f if line.strip()]

    # Read the first 1024 byte coordinates
    blocked = set()
    for line in lines[:1024]:
        x, y = map(int, line.split(','))
        blocked.add((x, y))

    max_coord = 70
    start = (0, 0)
    end = (max_coord, max_coord)

    # BFS to find shortest path
    queue = deque([(start[0], start[1], 0)])  # x, y, steps
    visited = set()
    visited.add(start)

    directions = [(-1, 0), (1, 0), (0, -1), (0, 1)]

    while queue:
        x, y, steps = queue.popleft()
        if (x, y) == end:
            print(steps)
            return
        for dx, dy in directions:
            nx, ny = x + dx, y + dy
            if (
                0 <= nx <= max_coord and
                0 <= ny <= max_coord and
                (nx, ny) not in blocked and
                (nx, ny) not in visited
            ):
                visited.add((nx, ny))
                queue.append((nx, ny, steps + 1))

    print("No path found")

if __name__ == "__main__":
    main()
