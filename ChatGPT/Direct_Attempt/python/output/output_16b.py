# ChatGPT-generated solution will go here.
import heapq
from collections import defaultdict, deque

def main():
    with open('input.txt') as f:
        grid = [list(line.strip()) for line in f if line.strip()]

    rows, cols = len(grid), len(grid[0])

    for r in range(rows):
        for c in range(cols):
            if grid[r][c] == 'S':
                start = (r, c)
            elif grid[r][c] == 'E':
                end = (r, c)

    drc = [(0,1), (1,0), (0,-1), (-1,0)]
    dir_names = range(4)

    # Dijkstra: (cost, r, c, dir)
    heap = []
    visited = dict()
    parents = defaultdict(list)

    for d in dir_names:
        heapq.heappush(heap, (1000, start[0], start[1], d))

    min_cost_to_end = None

    while heap:
        cost, r, c, d = heapq.heappop(heap)
        key = (r, c, d)
        if key in visited:
            continue
        visited[key] = cost
        if (r, c) == end:
            if min_cost_to_end is None:
                min_cost_to_end = cost
            elif cost > min_cost_to_end:
                break
        # Move forward
        dr, dc = drc[d]
        nr, nc = r + dr, c + dc
        if 0 <= nr < rows and 0 <= nc < cols and grid[nr][nc] != '#':
            if (nr, nc, d) not in visited:
                heapq.heappush(heap, (cost + 1, nr, nc, d))
                parents[(nr, nc, d)].append((r, c, d))
        # Rotate left/right
        for nd in [(d + 1) % 4, (d - 1) % 4]:
            if (r, c, nd) not in visited:
                heapq.heappush(heap, (cost + 1000, r, c, nd))
                parents[(r, c, nd)].append((r, c, d))

    # Trace back from all end states with min cost
    frontier = deque()
    on_path = set()
    for d in dir_names:
        key = (end[0], end[1], d)
        if visited.get(key) == min_cost_to_end:
            frontier.append(key)
            on_path.add((end[0], end[1]))

    while frontier:
        r, c, d = frontier.popleft()
        for pr, pc, pd in parents.get((r, c, d), []):
            if (pr, pc) not in on_path:
                on_path.add((pr, pc))
                frontier.append((pr, pc, pd))

    print(len(on_path))

if __name__ == "__main__":
    main()
