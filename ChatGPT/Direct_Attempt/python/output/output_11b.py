# ChatGPT-generated solution will go here.
from collections import Counter

def transform_counts(counts):
    next_counts = Counter()
    for num, cnt in counts.items():
        if num == 0:
            next_counts[1] += cnt
        elif len(str(num)) % 2 == 0:
            s = str(num)
            left = int(s[:len(s)//2])
            right = int(s[len(s)//2:])
            next_counts[left] += cnt
            next_counts[right] += cnt
        else:
            next_counts[num * 2024] += cnt
    return next_counts

def main():
    with open('input.txt') as f:
        stones = list(map(int, f.read().strip().split()))
    counts = Counter(stones)

    for _ in range(75):
        counts = transform_counts(counts)

    print(sum(counts.values()))

if __name__ == '__main__':
    main()
