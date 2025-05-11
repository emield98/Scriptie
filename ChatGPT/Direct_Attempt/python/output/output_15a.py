# ChatGPT-generated solution will go here.
def main():
    with open("input.txt") as f:
        lines = [line.rstrip('\n') for line in f]

    grid = []
    moves = ""
    reading_grid = True

    for line in lines:
        if reading_grid:
            if line.strip() == "":
                reading_grid = False
            else:
                grid.append(list(line))
        else:
            moves += line.strip()

    rows = len(grid)
    cols = len(grid[0])
    directions = {'^': (-1, 0), 'v': (1, 0), '<': (0, -1), '>': (0, 1)}

    # Locate robot and boxes
    for r in range(rows):
        for c in range(cols):
            if grid[r][c] == '@':
                robot_pos = (r, c)
                grid[r][c] = '.'

    for move in moves:
        dr, dc = directions[move]
        nr, nc = robot_pos[0] + dr, robot_pos[1] + dc

        if not (0 <= nr < rows and 0 <= nc < cols):
            continue

        target = grid[nr][nc]
        if target == '.':
            robot_pos = (nr, nc)
        elif target == 'O':
            nnr, nnc = nr + dr, nc + dc
            if 0 <= nnr < rows and 0 <= nnc < cols and grid[nnr][nnc] == '.':
                grid[nnr][nnc] = 'O'
                grid[nr][nc] = '.'
                robot_pos = (nr, nc)

    total = 0
    for r in range(rows):
        for c in range(cols):
            if grid[r][c] == 'O':
                total += r * 100 + c

    print(total)

if __name__ == "__main__":
    main()
