def parse_input(lines):
    patterns = lines[0].split(",")
    designs = lines[2:]  # skip the blank line
    return patterns, designs

def count_ways(patterns, design):
    n = len(design)
    pattern_set = set(patterns)
    dp = [0] * (n + 1)
    dp[0] = 1
    for i in range(1, n + 1):
        for j in range(max(0, i - 10), i):
            if design[j:i] in pattern_set:
                dp[i] += dp[j]
    return dp[n]

def part(lines):
    patterns, designs = parse_input(lines)
    return sum(count_ways(patterns, design) for design in designs)

def main():
    with open('input.txt') as f:
        lines = f.read().splitlines()
    print(part(lines))

if __name__ == '__main__':
    main()
