def mix(secret, val):
    return secret ^ val

def prune(secret):
    return secret % 16777216

def next_secret(secret):
    secret = mix(secret, secret * 64)
    secret = prune(secret)
    secret = mix(secret, secret // 32)
    secret = prune(secret)
    secret = mix(secret, secret * 2048)
    return prune(secret)

def nth_secret(start, n):
    for _ in range(n):
        start = next_secret(start)
    return start

def part(lines):
    return sum(nth_secret(int(line), 2000) for line in lines)

def main():
    with open('input.txt') as f:
        lines = f.read().splitlines()
    print(part(lines))

if __name__ == '__main__':
    main()
