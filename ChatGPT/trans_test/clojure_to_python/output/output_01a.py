def part(lines):
    pairs = [list(map(int, line.split())) for line in lines]
    left, right = zip(*pairs)
    sorted_left = sorted(left)
    sorted_right = sorted(right)
    return sum(abs(a - b) for a, b in zip(sorted_left, sorted_right))

def main():
    with open('input.txt') as f:
        lines = f.read().splitlines()
    print(part(lines))

if __name__ == '__main__':
    main()
