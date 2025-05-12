def safe_report(nums):
    deltas = [b - a for a, b in zip(nums, nums[1:])]
    signs = set((d > 0) - (d < 0) for d in deltas)
    return (signs == {1} or signs == {-1}) and all(1 <= abs(d) <= 3 for d in deltas)

def part(lines):
    reports = [list(map(int, line.split())) for line in lines]
    return sum(1 for report in reports if safe_report(report))

def main():
    with open('input.txt') as f:
        lines = f.read().splitlines()
    print(part(lines))

if __name__ == '__main__':
    main()
