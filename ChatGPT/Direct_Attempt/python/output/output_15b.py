# ChatGPT-generated solution will go here.
def main():
    with open("input.txt") as f:
        lines = [line.rstrip('\n') for line in f]

    raw_map = []
    moves = ""
    reading_map = True

    for line in lines:
        if reading_map:
            if line.strip() == "":
                reading_map = False
            else:
                raw_map.append(line)
        else:
            moves += line.strip()

    orig_rows = len(raw_map)
    orig_cols = len(raw_map[0])
    rows = orig_rows
    cols = orig_cols * 2

    grid = [[''] * cols for _ in range(rows)]
    for r in range(rows):
        c_out = 0
        for c in range(orig_cols):
            ch = raw_map[r][c]
            if ch == '#':
                grid[r][c_out] = '#'
                grid[r][c_out + 1] = '#'
            elif ch == '.':
                grid[r][c_out] = '.'
                grid[r][c_out + 1] = '.'
            elif ch == 'O':
                grid[r][c_out] = '['
                grid[r][c_out + 1] = ']'
            elif ch == '@':
                grid[r][c_out] = '@'
                grid[r][c_out + 1] = '.'
            c_out += 2

    # Locate robot
    for r in range(rows):
        for c in range(cols - 1):
            if grid[r][c] == '@':
                robot_r, robot_c = r, c
                grid[r][c] = '.'

    directions = {'^': (-1, 0), 'v': (1, 0), '<': (0, -1), '>': (0, 1)}

    for move in moves:
        dr, dc = directions[move]
        nr, nc = robot_r + dr, robot_c + dc

        if not (0 <= nr < rows and 0 <= nc < cols - 1):
            continue

        tgt1, tgt2 = grid[nr][nc], grid[nr][nc + 1]
        if tgt1 == '.' and tgt2 == '.':
            robot_r, robot_c = nr, nc
        elif tgt1 == '[' and tgt2 == ']':
            nnr, nnc = nr + dr, nc + dc
            if 0 <= nnr < rows and 0 <= nnc < cols - 1:
                after1, after2 = grid[nnr][nnc], grid[nnr][nnc + 1]
                if after1 == '.' and after2 == '.':
                    grid[nnr][nnc] = '['
                    grid[nnr][nnc + 1] = ']'
                    grid[nr][nc] = '.'
                    grid[nr][nc + 1] = '.'
                    robot_r, robot_c = nr, nc

    # Compute GPS sum
    total = 0
    for r in range(rows):
        for c in range(cols - 1):
            if grid[r][c] == '[' and grid[r][c + 1] == ']':
                total += r * 100 + c

    print(total)

if __name__ == "__main__":
    main()
