import heapq

def parse_coord(line):
    x, y = map(int, line.strip().split(","))
    return (x, y)

def parse_input(lines):
    return [parse_coord(line) for line in lines if line.strip()]

def neighbors(x, y):
    return [(x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1)]

def in_bounds(x, y):
    return 0 <= x <= 70 and 0 <= y <= 70

def bfs(start, goal, blocked):
    heap = [(0, start)]
    visited = {start}

    while heap:
        cost, (x, y) = heapq.heappop(heap)
        if (x, y) == goal:
            return True
        for nx, ny in neighbors(x, y):
            if in_bounds(nx, ny) and (nx, ny) not in blocked and (nx, ny) not in visited:
                visited.add((nx, ny))
                heapq.heappush(heap, (cost + 1, (nx, ny)))
    return False

def part(lines):
    coords = parse_input(lines)
    fallen = set()
    for x, y in coords:
        fallen.add((x, y))
        if not bfs((0, 0), (70, 70), fallen):
            return f"{x},{y}"
    return None

def main():
    with open('input.txt') as f:
        lines = f.read().splitlines()
    print(part(lines))

if __name__ == '__main__':
    main()
