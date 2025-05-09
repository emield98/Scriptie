# ChatGPT-generated solution will go here.
from collections import deque

def main():
    with open("input.txt") as f:
        grid = [list(line.strip()) for line in f if line.strip()]

    height, width = len(grid), len(grid[0])
    dirs = [(-1,0), (1,0), (0,-1), (0,1)]

    def in_bounds(x, y):
        return 0 <= x < height and 0 <= y < width

    def bfs(grid_mod):
        queue = deque([(start[0], start[1], 0)])
        visited = set([(start[0], start[1])])
        while queue:
            x, y, d = queue.popleft()
            if (x, y) == end:
                return d
            for dx, dy in dirs:
                nx, ny = x + dx, y + dy
                if in_bounds(nx, ny) and grid_mod[nx][ny] in ('.', 'E') and (nx, ny) not in visited:
                    visited.add((nx, ny))
                    queue.append((nx, ny, d + 1))
        return float('inf')

    for i in range(height):
        for j in range(width):
            if grid[i][j] == 'S':
                start = (i, j)
            if grid[i][j] == 'E':
                end = (i, j)

    base_cost = bfs(grid)

    cheat_set = set()
    count = 0

    for i in range(height):
        for j in range(width):
            if grid[i][j] != '.':
                continue
            for dx, dy in dirs:
                path = []
                for k in range(1, 21 + 1):
                    nx, ny = i + k * dx, j + k * dy
                    if not in_bounds(nx, ny):
                        break
                    path.append((nx, ny))
                    if grid[nx][ny] == '.':
                        # simulate cheat from (i,j) to (nx,ny)
                        grid_copy = [row[:] for row in grid]
                        for px, py in path[:-1]:
                            grid_copy[px][py] = '.'
                        cheat_id = (i, j, nx, ny)
                        if cheat_id in cheat_set:
                            continue
                        cheat_set.add(cheat_id)
                        new_cost = bfs(grid_copy)
                        save = base_cost - new_cost
                        if save >= 100:
                            count += 1
                        break

    print(count)

if __name__ == "__main__":
    main()
