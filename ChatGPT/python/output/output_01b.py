# ChatGPT-generated solution will go here.
from collections import Counter

def main():
    with open('input.txt') as f:
        left = []
        right = []
        for line in f:
            if line.strip():
                l, r = map(int, line.strip().split())
                left.append(l)
                right.append(r)

    right_counts = Counter(right)
    similarity_score = sum(x * right_counts[x] for x in left)
    print(similarity_score)

if __name__ == '__main__':
    main()
