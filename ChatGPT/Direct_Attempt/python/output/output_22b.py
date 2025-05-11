# ChatGPT-generated solution will go here.
import itertools
from collections import defaultdict

MOD = 16777216

def next_secret(secret):
    secret ^= secret * 64
    secret %= MOD
    secret ^= secret // 32
    secret %= MOD
    secret ^= secret * 2048
    secret %= MOD
    return secret

def get_prices(initial_secret, count=2001):
    prices = []
    secret = initial_secret
    for _ in range(count):
        prices.append(secret % 10)
        secret = next_secret(secret)
    return prices

def main():
    with open("input.txt") as f:
        secrets = [int(line.strip()) for line in f if line.strip()]

    buyer_changes = []
    buyer_prices = []
    for secret in secrets:
        prices = get_prices(secret)
        changes = [b - a for a, b in zip(prices, prices[1:])]
        buyer_prices.append(prices[1:])
        buyer_changes.append(changes)

    best_total = 0

    for pattern in itertools.product(range(-9, 10), repeat=4):
        total = 0
        for changes, prices in zip(buyer_changes, buyer_prices):
            for i in range(len(changes) - 3):
                if tuple(changes[i:i+4]) == pattern:
                    total += prices[i+3]
                    break
        best_total = max(best_total, total)

    print(best_total)

if __name__ == "__main__":
    main()
