# ChatGPT-generated solution will go here.
def main():
    with open('input.txt') as f:
        grid = [line.strip() for line in f if line.strip()]

    rows, cols = len(grid), len(grid[0])
    patterns = [
        # Pattern 1: diagonals from top-left to bottom-right (MAS top-left, MAS bottom-right)
        [(0, 0), (1, 1), (2, 2), (0, 2), (1, 1), (2, 0)],
        # Pattern 2: diagonals from top-right to bottom-left (SAM top-right, SAM bottom-left)
        [(0, 2), (1, 1), (2, 0), (0, 0), (1, 1), (2, 2)],
        # Pattern 3: MAS bottom-left, SAM top-right
        [(2, 0), (1, 1), (0, 2), (0, 0), (1, 1), (2, 2)],
        # Pattern 4: SAM bottom-right, MAS top-left
        [(2, 2), (1, 1), (0, 0), (0, 2), (1, 1), (2, 0)],
        # Pattern 5: both MAS
        [(0, 0), (1, 1), (2, 2), (2, 0), (1, 1), (0, 2)],
        # Pattern 6: both SAM
        [(2, 0), (1, 1), (0, 2), (2, 2), (1, 1), (0, 0)],
    ]

    count = 0
    for r in range(rows - 2):
        for c in range(cols - 2):
            for pattern in patterns:
                chars = [grid[r + dr][c + dc] for dr, dc in pattern]
                if (chars[:3] == ['M', 'A', 'S'] or chars[:3] == ['S', 'A', 'M']) and \
                   (chars[3:] == ['M', 'A', 'S'] or chars[3:] == ['S', 'A', 'M']):
                    count += 1
                    break
    print(count)

if __name__ == '__main__':
    main()
