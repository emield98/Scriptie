def main():
    left = []
    right = []

    with open('input.txt', 'r') as f:
        for line in f:
            l, r = map(int, line.strip().split())
            left.append(l)
            right.append(r)

    left.sort()
    right.sort()

    total_distance = sum(abs(l - r) for l, r in zip(left, right))
    print(total_distance)

if __name__ == "__main__":
    main()
