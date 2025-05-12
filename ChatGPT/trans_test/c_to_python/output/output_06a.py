def main():
    directions = [(-1, 0), (0, 1), (1, 0), (0, -1)]
    grid = []
    x = y = dir = 0

    with open("input.txt", "r") as f:
        for i, line in enumerate(f):
            line = line.rstrip('\n')
            grid.append(list(line))
            for j, char in enumerate(line):
                if char == '^':
                    x, y, dir = i, j, 0
                    grid[i][j] = '.'

    rows = len(grid)
    cols = max(len(row) for row in grid)
    visited = [[False] * cols for _ in range(rows)]

    visited_count = 0
    while 0 <= x < rows and 0 <= y < len(grid[x]):
        if not visited[x][y]:
            visited[x][y] = True
            visited_count += 1

        dx, dy = directions[dir]
        next_x = x + dx
        next_y = y + dy

        if (0 <= next_x < rows and 0 <= next_y < len(grid[next_x]) 
            and grid[next_x][next_y] == '#'):
            dir = (dir + 1) % 4
        else:
            x, y = next_x, next_y

    print(visited_count)

if __name__ == "__main__":
    main()
