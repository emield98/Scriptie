# ChatGPT-generated solution will go here.
from collections import deque

def is_reachable(blocked, max_coord=70):
    start = (0, 0)
    end = (max_coord, max_coord)
    if start in blocked or end in blocked:
        return False

    queue = deque([start])
    visited = set([start])
    directions = [(-1,0),(1,0),(0,-1),(0,1)]

    while queue:
        x, y = queue.popleft()
        if (x, y) == end:
            return True
        for dx, dy in directions:
            nx, ny = x + dx, y + dy
            if (0 <= nx <= max_coord and 0 <= ny <= max_coord and
                (nx, ny) not in blocked and (nx, ny) not in visited):
                visited.add((nx, ny))
                queue.append((nx, ny))
    return False

def main():
    with open("input.txt") as f:
        coords = [tuple(map(int, line.strip().split(','))) for line in f if line.strip()]

    blocked = set()
    for x, y in coords:
        blocked.add((x, y))
        if not is_reachable(blocked):
            print(f"{x},{y}")
            return

if __name__ == "__main__":
    main()
