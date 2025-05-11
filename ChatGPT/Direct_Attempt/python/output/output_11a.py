# ChatGPT-generated solution will go here.
def main():
    with open('input.txt') as f:
        stones = list(map(int, f.read().strip().split()))

    for _ in range(25):
        next_stones = []
        for num in stones:
            if num == 0:
                next_stones.append(1)
            elif len(str(num)) % 2 == 0:
                s = str(num)
                left = int(s[:len(s)//2])
                right = int(s[len(s)//2:])
                next_stones.extend([left, right])
            else:
                next_stones.append(num * 2024)
        stones = next_stones

    print(len(stones))

if __name__ == '__main__':
    main()
