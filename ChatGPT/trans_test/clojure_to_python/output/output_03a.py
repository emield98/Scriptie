import re

def part(lines):
    input_str = ''.join(lines)
    pattern = r"mul\((\d{1,3}),(\d{1,3})\)"
    return sum(int(a) * int(b) for a, b in re.findall(pattern, input_str))

def main():
    with open('input.txt') as f:
        lines = f.read().splitlines()
    print(part(lines))

if __name__ == '__main__':
    main()
