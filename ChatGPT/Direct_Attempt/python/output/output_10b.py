# ChatGPT-generated solution will go here.
from functools import lru_cache

def main():
    with open('input.txt') as f:
        grid = [list(map(int, line.strip())) for line in f if line.strip()]

    rows, cols = len(grid), len(grid[0])
    total_rating = 0

    @lru_cache(maxsize=None)
    def dfs(r, c):
        if grid[r][c] == 9:
            return 1
        count = 0
        for dr, dc in [(-1,0), (1,0), (0,-1), (0,1)]:
            nr, nc = r + dr, c + dc
            if 0 <= nr < rows and 0 <= nc < cols:
                if grid[nr][nc] == grid[r][c] + 1:
                    count += dfs(nr, nc)
        return count

    for r in range(rows):
        for c in range(cols):
            if grid[r][c] == 0:
                total_rating += dfs(r, c)

    print(total_rating)

if __name__ == '__main__':
    main()
