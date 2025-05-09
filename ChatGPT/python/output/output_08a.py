# ChatGPT-generated solution will go here.
def main():
    with open('input.txt') as f:
        grid = [line.strip() for line in f if line.strip()]

    rows, cols = len(grid), len(grid[0])
    antennas = {}

    # Collect antenna positions by frequency
    for r in range(rows):
        for c in range(cols):
            ch = grid[r][c]
            if ch != '.':
                antennas.setdefault(ch, []).append((r, c))

    antinodes = set()

    for freq, positions in antennas.items():
        n = len(positions)
        for i in range(n):
            r1, c1 = positions[i]
            for j in range(n):
                if i == j:
                    continue
                r2, c2 = positions[j]
                # Check if (r2, c2) is twice as far from the midpoint as (r1, c1)
                mid_r = 2 * r2 - r1
                mid_c = 2 * c2 - c1
                if 0 <= mid_r < rows and 0 <= mid_c < cols:
                    antinodes.add((mid_r, mid_c))

    print(len(antinodes))

if __name__ == '__main__':
    main()
