# ChatGPT-generated solution will go here.
def main():
    with open('input.txt') as f:
        grid = [line.strip() for line in f if line.strip()]
    
    word = "XMAS"
    rows, cols = len(grid), len(grid[0])
    directions = [(-1, -1), (-1, 0), (-1, 1),
                  (0, -1),          (0, 1),
                  (1, -1),  (1, 0),  (1, 1)]

    def in_bounds(r, c):
        return 0 <= r < rows and 0 <= c < cols

    count = 0
    for r in range(rows):
        for c in range(cols):
            for dr, dc in directions:
                if all(in_bounds(r + dr * i, c + dc * i) and grid[r + dr * i][c + dc * i] == word[i] for i in range(4)):
                    count += 1
    print(count)

if __name__ == '__main__':
    main()
