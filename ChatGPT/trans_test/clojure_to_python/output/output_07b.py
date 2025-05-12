from itertools import product

def parse_line(line):
    lhs, rhs = line.split(": ")
    target = int(lhs)
    nums = list(map(int, rhs.split()))
    return target, nums

def all_operator_combos(n):
    return product(['+', '*', '||'], repeat=n)

def apply_op(a, b, op):
    if op == '+':
        return a + b
    elif op == '*':
        return a * b
    elif op == '||':
        return int(f"{a}{b}")

def eval_left_to_right(nums, ops):
    result = nums[0]
    for op, n in zip(ops, nums[1:]):
        result = apply_op(result, n, op)
    return result

def valid_equation(target, nums):
    for ops in all_operator_combos(len(nums) - 1):
        if eval_left_to_right(nums, ops) == target:
            return True
    return False

def part(lines):
    total = 0
    for line in lines:
        target, nums = parse_line(line)
        if valid_equation(target, nums):
            total += target
    return total

def main():
    with open('input.txt') as f:
        lines = f.read().splitlines()
    print(part(lines))

if __name__ == '__main__':
    main()
