# ChatGPT-generated solution will go here.
def main():
    with open('input.txt') as f:
        grid = [list(line.strip()) for line in f if line.strip()]

    rows, cols = len(grid), len(grid[0])
    directions = ['^', '>', 'v', '<']
    deltas = [(-1, 0), (0, 1), (1, 0), (0, -1)]
    turn_right = {0: 1, 1: 2, 2: 3, 3: 0}

    # Find the guard's starting position and direction
    for r in range(rows):
        for c in range(cols):
            if grid[r][c] in directions:
                start_pos = (r, c)
                dir_idx = directions.index(grid[r][c])
                break

    def simulate(grid, start_pos, dir_idx):
        visited = set()
        seen_states = set()
        pos = start_pos
        visited.add(pos)
        while True:
            state = (pos, dir_idx)
            if state in seen_states:
                return True, visited  # loop detected
            seen_states.add(state)
            dr, dc = deltas[dir_idx]
            nr, nc = pos[0] + dr, pos[1] + dc
            if not (0 <= nr < rows and 0 <= nc < cols):
                return False, visited  # exited grid
            if grid[nr][nc] == '#':
                dir_idx = turn_right[dir_idx]
            else:
                pos = (nr, nc)
                visited.add(pos)

    # Simulate without any modification
    _, original_path = simulate(grid, start_pos, dir_idx)

    # Try placing a new obstruction on every empty cell (except start)
    count = 0
    for r in range(rows):
        for c in range(cols):
            if (r, c) != start_pos and grid[r][c] == '.':
                grid[r][c] = '#'
                loops, _ = simulate(grid, start_pos, dir_idx)
                if loops:
                    count += 1
                grid[r][c] = '.'
    print(count)

if __name__ == '__main__':
    main()
