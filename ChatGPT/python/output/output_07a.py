# ChatGPT-generated solution will go here.
from itertools import product

def eval_expr(nums, ops):
    result = nums[0]
    for i, op in enumerate(ops):
        if op == '+':
            result += nums[i + 1]
        else:
            result *= nums[i + 1]
    return result

def main():
    total = 0
    with open('input.txt') as f:
        for line in f:
            if not line.strip():
                continue
            left, right = line.strip().split(':')
            target = int(left.strip())
            nums = list(map(int, right.strip().split()))
            n = len(nums)
            for ops in product(['+', '*'], repeat=n-1):
                if eval_expr(nums, ops) == target:
                    total += target
                    break
    print(total)

if __name__ == '__main__':
    main()
