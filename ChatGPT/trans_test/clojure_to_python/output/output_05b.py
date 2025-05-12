from collections import defaultdict, deque

def parse_input(lines):
    rule_lines = []
    update_lines = []
    for line in lines:
        if '|' in line:
            rule_lines.append(line)
        elif line.strip():
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

def topological_sort(nodes, edges):
    adj = defaultdict(set)
    incoming = defaultdict(set)

    for a, b in edges:
        adj[a].add(b)
        incoming[b].add(a)

    no_incoming = sorted(n for n in nodes if n not in incoming)
    result = []
    queue = deque(no_incoming)

    while queue:
        n = queue.popleft()
        result.append(n)
        for m in adj[n]:
            incoming[m].discard(n)
            if not incoming[m]:
                del incoming[m]
                queue.append(m)

    return result if len(result) == len(nodes) else None

def middle_page(update):
    return update[len(update) // 2]

def part(lines):
    rules, updates = parse_input(lines)
    total = 0
    for update in updates:
        if not valid_update(rules, update):
            relevant_rules = [(a, b) for a, b in rules if a in update and b in update]
            sorted_update = topological_sort(set(update), relevant_rules)
            if sorted_update:
                total += middle_page(sorted_update)
    return total

def main():
    with open('input.txt') as f:
        lines = f.read().splitlines()
    print(part(lines))

if __name__ == '__main__':
    main()
