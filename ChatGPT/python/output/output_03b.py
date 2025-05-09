# ChatGPT-generated solution will go here.
import re

def main():
    with open('input.txt') as f:
        data = f.read()

    token_pattern = re.compile(r'do\(\)|don\'t\(\)|\bmul\((\d{1,3}),(\d{1,3})\)')
    enabled = True
    total = 0

    for match in token_pattern.finditer(data):
        text = match.group(0)
        if text == "do()":
            enabled = True
        elif text == "don't()":
            enabled = False
        else:
            if enabled:
                a, b = match.groups()
                total += int(a) * int(b)
    print(total)

if __name__ == '__main__':
    main()
