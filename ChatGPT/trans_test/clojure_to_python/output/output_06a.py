def part(lines):
    grid = [list(line) for line in lines]
    deltas = {'^': (-1, 0), '>': (0, 1), 'v': (1, 0), '<': (0, -1)}
    turn_right = {'^': '>', '>': 'v', 'v': '<', '<': '^'}
    rows, cols = len(grid), len(grid[0])

    def in_bounds(r, c):
        return 0 <= r < rows and 0 <= c < cols

    def blocked(r, c):
        return not in_bounds(r, c) or grid[r][c] == '#'

    def find_guard():
        for r in range(rows):
            for c in range(cols):
                ch = grid[r][c]
                if ch in deltas:
                    return (r, c), ch
        return None, None

    def move(r, c, dir):
        dr, dc = deltas[dir]
        return r + dr, c + dc

    (r, c), dir = find_guard()
    visited = {(r, c)}

    while True:
        nr, nc = move(r, c, dir)
        if not in_bounds(nr, nc):
            return len(visited)
        if blocked(nr, nc):
            dir = turn_right[dir]
        else:
            r, c = nr, nc
            visited.add((r, c))

def main():
    with open('input.txt') as f:
        lines = f.read().splitlines()
    print(part(lines))

if __name__ == '__main__':
    main()
