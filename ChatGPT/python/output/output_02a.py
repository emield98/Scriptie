# ChatGPT-generated solution will go here.
def is_safe(report):
    diffs = [b - a for a, b in zip(report, report[1:])]
    if all(d > 0 and 1 <= d <= 3 for d in diffs):
        return True
    if all(d < 0 and -3 <= d <= -1 for d in diffs):
        return True
    return False

def main():
    safe_count = 0
    with open('input.txt') as f:
        for line in f:
            if line.strip():
                levels = list(map(int, line.strip().split()))
                if is_safe(levels):
                    safe_count += 1
    print(safe_count)

if __name__ == '__main__':
    main()
