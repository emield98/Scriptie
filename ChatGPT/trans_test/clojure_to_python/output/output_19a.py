def parse_input(lines):
    patterns = lines[0].split(",")
    designs = lines[2:]  # skip the blank line at index 1
    return patterns, designs

def can_form(patterns, design):
    n = len(design)
    pattern_set = set(patterns)
    dp = [False] * (n + 1)
    dp[0] = True
    for i in range(1, n + 1):
        for j in range(max(0, i - 10), i):
            if dp[j] and design[j:i] in pattern_set:
                dp[i] = True
                break
    return dp[n]

def part(lines):
    patterns, designs = parse_input(lines)
    return sum(1 for design in designs if can_form(patterns, design))

def main():
    with open('input.txt') as f:
        lines = f.read().splitlines()
    print(part(lines))

if __name__ == '__main__':
    main()
