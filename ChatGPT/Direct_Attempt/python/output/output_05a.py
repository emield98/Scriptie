# ChatGPT-generated solution will go here.
def main():
    with open('input.txt') as f:
        lines = [line.strip() for line in f if line.strip()]

    rules = set()
    updates = []
    parsing_rules = True

    for line in lines:
        if '|' in line:
            a, b = map(int, line.split('|'))
            rules.add((a, b))
        else:
            updates.append(list(map(int, line.split(','))))

    total = 0
    for update in updates:
        index_map = {page: i for i, page in enumerate(update)}
        valid = True
        for a, b in rules:
            if a in index_map and b in index_map:
                if index_map[a] >= index_map[b]:
                    valid = False
                    break
        if valid:
            mid = len(update) // 2
            total += update[mid]
    print(total)

if __name__ == '__main__':
    main()
