# ChatGPT-generated solution will go here.
import re

def main():
    with open('input.txt') as f:
        data = f.read()
    
    pattern = re.compile(r'\bmul\((\d{1,3}),(\d{1,3})\)')
    total = sum(int(a) * int(b) for a, b in pattern.findall(data))
    print(total)

if __name__ == '__main__':
    main()
