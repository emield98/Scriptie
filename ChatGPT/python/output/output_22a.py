# ChatGPT-generated solution will go here.
def next_secret(secret):
    secret ^= secret * 64
    secret %= 16777216
    secret ^= secret // 32
    secret %= 16777216
    secret ^= secret * 2048
    secret %= 16777216
    return secret

def main():
    with open("input.txt") as f:
        secrets = [int(line.strip()) for line in f if line.strip()]

    total = 0
    for secret in secrets:
        for _ in range(2000):
            secret = next_secret(secret)
        total += secret

    print(total)

if __name__ == "__main__":
    main()
