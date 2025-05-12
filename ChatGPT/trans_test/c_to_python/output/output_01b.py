from collections import Counter

def main():
    left = []
    right = []

    with open('input.txt', 'r') as f:
        for line in f:
            l, r = map(int, line.strip().split())
            left.append(l)
            right.append(r)

    right_counts = Counter(right)

    similarity_score = sum(x * right_counts[x] for x in left)
    print(similarity_score)

if __name__ == "__main__":
    main()
