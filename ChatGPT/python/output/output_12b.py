# ChatGPT-generated solution will go here.
from collections import deque

def main():
    with open('input.txt') as f:
        grid = [list(line.strip()) for line in f if line.strip()]

    rows, cols = len(grid), len(grid[0])
    visited = [[False] * cols for _ in range(rows)]
    total_price = 0

    def bfs(sr, sc):
        plant = grid[sr][sc]
        queue = deque()
        queue.append((sr, sc))
        visited[sr][sc] = True
        region = [(sr, sc)]
        while queue:
            r, c = queue.popleft()
            for dr, dc in [(-1,0), (1,0), (0,-1), (0,1)]:
                nr, nc = r + dr, c + dc
                if 0 <= nr < rows and 0 <= nc < cols:
                    if not visited[nr][nc] and grid[nr][nc] == plant:
                        visited[nr][nc] = True
                        queue.append((nr, nc))
                        region.append((nr, nc))
        return region

    def count_sides(region_set):
        sides = 0
        for r, c in region_set:
            for dr, dc in [(-1,0), (1,0), (0,-1), (0,1)]:
                nr, nc = r + dr, c + dc
                if not (0 <= nr < rows and 0 <= nc < cols) or (nr, nc) not in region_set:
                    sides += 1
        # Each straight segment of fence counts as one side.
        # The number of straight segments is the number of connected edge runs.
        edge_map = [[0]*cols for _ in range(rows)]
        for r, c in region_set:
            for i, (dr, dc) in enumerate([(-1,0), (1,0), (0,-1), (0,1)]):
                nr, nc = r + dr, c + dc
                if not (0 <= nr < rows and 0 <= nc < cols) or (nr, nc) not in region_set:
                    edge_map[r][c] |= 1 << i

        seen_edges = set()
        for r in range(rows):
            for c in range(cols):
                val = edge_map[r][c]
                if val == 0:
                    continue
                for dr, dc in [(-1,0), (1,0), (0,-1), (0,1)]:
                    seg = []
                    nr, nc = r, c
                    while (0 <= nr < rows and 0 <= nc < cols and edge_map[nr][nc]):
                        seg.append((nr, nc))
                        edge_map[nr][nc] = 0
                        nr += dr
                        nc += dc
                    if seg:
                        seen_edges.add(tuple(seg))
        return len(seen_edges)

    for r in range(rows):
        for c in range(cols):
            if not visited[r][c]:
                region = bfs(r, c)
                area = len(region)
                region_set = set(region)
                sides = count_sides(region_set)
                total_price += area * sides

    print(total_price)

if __name__ == '__main__':
    main()
