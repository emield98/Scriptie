def parse_grid(lines):
    return [[int(ch) for ch in line] for line in lines]

def neighbors(r, c, rows, cols):
    for dr, dc in [(-1, 0), (1, 0), (0, -1), (0, 1)]:
        nr, nc = r + dr, c + dc
        if 0 <= nr < rows and 0 <= nc < cols:
            yield nr, nc

def count_paths(grid, start):
    rows, cols = len(grid), len(grid[0])
    memo = {}

    def dfs(r, c, h):
        if h == 9:
            return 1
        if (r, c, h) in memo:
            return memo[(r, c, h)]
        total = 0
        for nr, nc in neighbors(r, c, rows, cols):
            if grid[nr][nc] == h + 1:
                total += dfs(nr, nc, h + 1)
        memo[(r, c, h)] = total
        return total

    r, c = start
    return dfs(r, c, 0)

def part(lines):
    grid = parse_grid(lines)
    rows, cols = len(grid), len(grid[0])
    trailheads = [(r, c) for r in range(rows) for c in range(cols) if grid[r][c] == 0]
    return sum(count_paths(grid, start) for start in trailheads)

def main():
    with open('input.txt') as f:
        lines = f.read().splitlines()
    print(part(lines))

if __name__ == '__main__':
    main()
