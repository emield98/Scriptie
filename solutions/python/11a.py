from collections import deque

NUM_BLINKS = 25

def count_digits(n):
    digits = 1
    while n >= 10:
        n //= 10
        digits += 1
    return digits

def update_arrangement(arrangement):
    new_arrangement = deque()
    while arrangement:
        stone = arrangement.popleft()
        if stone == 0:
            new_arrangement.append(1)
        else:
            digits = count_digits(stone)
            if digits % 2 == 0:
                half = digits // 2
                divisor = 10 ** half
                left = stone // divisor
                right = stone % divisor
                new_arrangement.append(left)
                new_arrangement.append(right)
            else:
                new_arrangement.append(stone * 2024)
    return new_arrangement

if __name__ == "__main__":
    with open("input.txt") as f:
        stones = deque(map(int, f.read().split()))

    for _ in range(NUM_BLINKS):
        stones = update_arrangement(stones)

    print(len(stones))
