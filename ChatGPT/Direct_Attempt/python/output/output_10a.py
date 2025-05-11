# ChatGPT-generated solution will go here.
from collections import deque

def main():
    with open('input.txt') as f:
        grid = [list(map(int, line.strip())) for line in f if line.strip()]

    rows, cols = len(grid), len(grid[0])
    total_score = 0

    def bfs(r, c):
        visited = set()
        queue = deque()
        queue.append((r, c, 0))
        visited.add((r, c))
        nines = set()

        while queue:
            x, y, h = queue.popleft()
            if grid[x][y] == 9:
                nines.add((x, y))
                continue
            for dx, dy in [(-1,0), (1,0), (0,-1), (0,1)]:
                nx, ny = x + dx, y + dy
                if 0 <= nx < rows and 0 <= ny < cols:
                    if (nx, ny) not in visited and grid[nx][ny] == grid[x][y] + 1:
                        visited.add((nx, ny))
                        queue.append((nx, ny, grid[nx][ny]))
        return len(nines)

    for r in range(rows):
        for c in range(cols):
            if grid[r][c] == 0:
                total_score += bfs(r, c)

    print(total_score)

if __name__ == '__main__':
    main()
