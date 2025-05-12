import heapq

def parse_coord(line):
    x, y = map(int, line.strip().split(","))
    return (x, y)

def parse_input(lines):
    return set(parse_coord(line) for line in lines if line.strip())  # auto-limits by 1024 later via data

def neighbors(x, y):
    return [(x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1)]

def in_bounds(x, y):
    return 0 <= x <= 70 and 0 <= y <= 70

def bfs(start, goal, blocked):
    heap = [(0, start)]
    dist = {start: 0}

    while heap:
        cost, pos = heapq.heappop(heap)
        if pos == goal:
            return cost
        for nx, ny in neighbors(*pos):
            if in_bounds(nx, ny) and (nx, ny) not in blocked:
                new_cost = cost + 1
                if (nx, ny) not in dist or new_cost < dist[(nx, ny)]:
                    dist[(nx, ny)] = new_cost
                    heapq.heappush(heap, (new_cost, (nx, ny)))
    return None

def part(lines):
    blocked = set(list(parse_input(lines))[:1024])
    return bfs((0, 0), (70, 70), blocked)

def main():
    with open('input.txt') as f:
        lines = f.read().splitlines()
    print(part(lines))

if __name__ == '__main__':
    main()
