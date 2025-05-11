# ChatGPT-generated solution will go here.
from collections import deque

def main():
    with open("input.txt") as f:
        grid = [list(line.strip()) for line in f if line.strip()]

    height = len(grid)
    width = len(grid[0])
    dirs = [(-1,0),(1,0),(0,-1),(0,1)]

    def in_bounds(x, y):
        return 0 <= x < height and 0 <= y < width

    # Find start and end
    for i in range(height):
        for j in range(width):
            if grid[i][j] == 'S':
                start = (i, j)
            if grid[i][j] == 'E':
                end = (i, j)

    # Standard BFS for shortest path without cheating
    def bfs():
        queue = deque([(start[0], start[1], 0)])
        visited = set([start])
        while queue:
            x, y, dist = queue.popleft()
            if (x, y) == end:
                return dist
            for dx, dy in dirs:
                nx, ny = x + dx, y + dy
                if in_bounds(nx, ny) and grid[nx][ny] in ('.', 'S', 'E') and (nx, ny) not in visited:
                    visited.add((nx, ny))
                    queue.append((nx, ny, dist + 1))
        return float('inf')

    normal_shortest = bfs()

    # Try all cheat positions: any two-tile path through walls where both ends are track
    cheat_saves = []

    for i in range(height):
        for j in range(width):
            for dx1, dy1 in dirs:
                x1, y1 = i + dx1, j + dy1
                x2, y2 = i + 2*dx1, j + 2*dy1
                if in_bounds(x2, y2):
                    if grid[i][j] in ('.', 'S') and grid[x1][y1] == '#' and grid[x2][y2] in ('.', 'E'):
                        # Try cheating: treat two wall tiles as walkable
                        grid[x1][y1] = '.'
                        cheat_path_len = bfs()
                        grid[x1][y1] = '#'
                        save = normal_shortest - cheat_path_len
                        if save >= 100:
                            cheat_saves.append(((i,j), (x2,y2), save))

    print(len(cheat_saves))

if __name__ == "__main__":
    main()
