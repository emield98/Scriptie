# ChatGPT-generated solution will go here.
def main():
    with open('input.txt') as f:
        left = []
        right = []
        for line in f:
            if line.strip():
                l, r = map(int, line.strip().split())
                left.append(l)
                right.append(r)

    left.sort()
    right.sort()
    total_distance = sum(abs(l - r) for l, r in zip(left, right))
    print(total_distance)

if __name__ == '__main__':
    main()
