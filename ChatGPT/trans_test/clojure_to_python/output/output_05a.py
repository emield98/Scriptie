def parse_input(lines):
    rule_lines = []
    update_lines = []
    in_rules = True
    for line in lines:
        if '|' in line:
            rule_lines.append(line)
        elif line.strip():
            in_rules = False
            update_lines.append(line)
    rules = [tuple(map(int, line.split('|'))) for line in rule_lines]
    updates = [list(map(int, line.split(','))) for line in update_lines]
    return rules, updates

def valid_update(rules, update):
    idx = {val: i for i, val in enumerate(update)}
    for a, b in rules:
        if a in idx and b in idx and not idx[a] < idx[b]:
            return False
    return True

def middle_page(update):
    return update[len(update) // 2]

def part(lines):
    rules, updates = parse_input(lines)
    return sum(middle_page(update) for update in updates if valid_update(rules, update))

def main():
    with open('input.txt') as f:
        lines = f.read().splitlines()
    print(part(lines))

if __name__ == '__main__':
    main()
