# ChatGPT-generated solution will go here.
from collections import deque

def main():
    with open("input.txt") as f:
        codes = [line.strip() for line in f if line.strip()]

    # Define the numeric keypad layout
    num_keypad = [
        "789",
        "456",
        "123",
        " 0A"
    ]
    num_pos = {}
    for i, row in enumerate(num_keypad):
        for j, ch in enumerate(row):
            if ch != ' ':
                num_pos[ch] = (i, j)

    # Define the directional keypad layout
    dir_keypad = [
        " ^A",
        "<v>"
    ]
    dir_pos = {}
    for i, row in enumerate(dir_keypad):
        for j, ch in enumerate(row):
            if ch != ' ':
                dir_pos[ch] = (i, j)

    def neighbors(kp, pos):
        i, j = pos
        for move, (di, dj) in zip("^v<>", [(-1,0),(1,0),(0,-1),(0,1)]):
            ni, nj = i + di, j + dj
            if 0 <= ni < len(kp) and 0 <= nj < len(kp[ni]) and kp[ni][nj] != ' ':
                yield move, (ni, nj)

    # BFS to find shortest sequence from start_pos to press target_seq on keypad
    def bfs(kp, start_pos, targets):
        queue = deque([(start_pos, 0, 0)])  # (position, idx in targets, cost)
        visited = set()
        while queue:
            pos, idx, cost = queue.popleft()
            if (pos, idx) in visited:
                continue
            visited.add((pos, idx))
            if idx == len(targets):
                return cost
            # Try moves
            for move, new_pos in neighbors(kp, pos):
                queue.append((new_pos, idx, cost + 1))
            # Try press A if pointing at correct key
            i, j = pos
            if kp[i][j] == targets[idx]:
                queue.append((pos, idx + 1, cost + 1))
        return float('inf')

    # BFS between two positions on a given keypad
    def bfs_between(kp, start, end):
        queue = deque([(start, 0)])
        visited = set()
        while queue:
            pos, cost = queue.popleft()
            if pos in visited:
                continue
            visited.add(pos)
            if pos == end:
                return cost
            for _, new_pos in neighbors(kp, pos):
                queue.append((new_pos, cost + 1))
        return float('inf')

    # Precompute cost between any two numeric keypad buttons
    num_keys = list(num_pos.keys())
    num_dist = {}
    for a in num_keys:
        for b in num_keys:
            d = bfs_between(num_keypad, num_pos[a], num_pos[b])
            num_dist[(a, b)] = d + 1  # include press A

    # BFS to simulate second robot issuing instructions to first robot
    def simulate_second_robot(seq):
        # Start at 'A' in top-right
        start = dir_pos['A']
        return bfs(num_keypad, num_pos['A'], seq)

    # BFS to simulate first robot issuing instructions to numeric keypad
    def simulate_first_robot(seq):
        return bfs(dir_keypad, dir_pos['A'], seq)

    total = 0
    for code in codes:
        # Construct sequence of button presses
        path = []
        cur = 'A'
        for ch in code:
            path.append(ch)
            cur = ch
        # Cost from second robot to type into first
        second_cost = bfs(num_keypad, num_pos['A'], path)
        # Cost from first robot to type into numeric
        first_cost = bfs(dir_keypad, dir_pos['A'], path)
        total_cost = bfs(num_keypad, num_pos['A'], path)
        seq_cost = bfs(dir_keypad, dir_pos['A'], path)
        presses = bfs(dir_keypad, dir_pos['A'], path)
        code_num = int(code.rstrip('A').lstrip('0') or '0')
        total += presses * code_num

    print(total)

if __name__ == "__main__":
    main()
