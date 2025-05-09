# ChatGPT-generated solution will go here.
def main():
    with open('input.txt') as f:
        grid = [list(line.strip()) for line in f if line.strip()]

    rows, cols = len(grid), len(grid[0])

    directions = {
        '^': (-1, 0),
        '>': (0, 1),
        'v': (1, 0),
        '<': (0, -1),
    }
    turn_right = {
        '^': '>',
        '>': 'v',
        'v': '<',
        '<': '^',
    }

    # Find starting position and direction
    for r in range(rows):
        for c in range(cols):
            if grid[r][c] in directions:
                pos = (r, c)
                dir = grid[r][c]
                break

    visited = set()
    visited.add(pos)

    while True:
        dr, dc = directions[dir]
        nr, nc = pos[0] + dr, pos[1] + dc
        if 0 <= nr < rows and 0 <= nc < cols and grid[nr][nc] != '#':
            pos = (nr, nc)
            visited.add(pos)
        else:
            dir = turn_right[dir]
        # Exit condition: out of bounds in the next move
        dr, dc = directions[dir]
        next_r, next_c = pos[0] + dr, pos[1] + dc
        if not (0 <= next_r < rows and 0 <= next_c < cols):
            break
    print(len(visited))

if __name__ == '__main__':
    main()
