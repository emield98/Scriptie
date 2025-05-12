from collections import Counter

def part(lines):
    pairs = [list(map(int, line.split())) for line in lines]
    left = [a for a, _ in pairs]
    right = [b for _, b in pairs]
    freq_right = Counter(right)
    return sum(a * freq_right.get(a, 0) for a in left)

def main():
    with open('input.txt') as f:
        lines = f.read().splitlines()
    print(part(lines))

if __name__ == '__main__':
    main()
