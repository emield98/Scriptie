def part(lines):
    grid = [list(line) for line in lines]
    directions = [(0, 1), (1, 0), (1, 1), (-1, 1),
                  (0, -1), (-1, 0), (-1, -1), (1, -1)]
    target = ['X', 'M', 'A', 'S']
    rows, cols = len(grid), len(grid[0])

    def in_bounds(r, c):
        return 0 <= r < rows and 0 <= c < cols

    def match_word(r, c, dr, dc):
        for i, ch in enumerate(target):
            nr, nc = r + dr * i, c + dc * i
            if not in_bounds(nr, nc) or grid[nr][nc] != ch:
                return False
        return True

    count = 0
    for r in range(rows):
        for c in range(cols):
            for dr, dc in directions:
                if match_word(r, c, dr, dc):
                    count += 1
    return count

def main():
    with open('input.txt') as f:
        lines = f.read().splitlines()
    print(part(lines))

if __name__ == '__main__':
    main()
