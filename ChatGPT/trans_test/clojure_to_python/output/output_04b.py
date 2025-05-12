def get_char(grid, r, c):
    if 0 <= r < len(grid) and 0 <= c < len(grid[0]):
        return grid[r][c]
    return None

def valid_xmas(grid, r, c):
    nw = [get_char(grid, r - 1, c - 1), get_char(grid, r, c), get_char(grid, r + 1, c + 1)]
    ne = [get_char(grid, r - 1, c + 1), get_char(grid, r, c), get_char(grid, r + 1, c - 1)]

    def valid_mas(triplet):
        return triplet == ['M', 'A', 'S'] or triplet == ['S', 'A', 'M']

    return all(ch is not None for ch in nw) and all(ch is not None for ch in ne) and valid_mas(nw) and valid_mas(ne)

def part(lines):
    grid = [list(line) for line in lines]
    rows, cols = len(grid), len(grid[0])
    return sum(1 for r in range(1, rows - 1) for c in range(1, cols - 1) if valid_xmas(grid, r, c))

def main():
    with open('input.txt') as f:
        lines = f.read().splitlines()
    print(part(lines))

if __name__ == '__main__':
    main()
