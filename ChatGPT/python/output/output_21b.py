# ChatGPT-generated solution will go here.
from collections import deque

def main():
    with open("input.txt") as f:
        codes = [line.strip() for line in f if line.strip()]

    # Numeric keypad layout
    num_pad = [
        "789",
        "456",
        "123",
        " 0A"
    ]
    num_positions = {}
    for i, row in enumerate(num_pad):
        for j, c in enumerate(row):
            if c != ' ':
                num_positions[c] = (i, j)

    # Directional keypad layout
    dir_pad = [
        " ^A",
        "<v>"
    ]
    dir_positions = {}
    for i, row in enumerate(dir_pad):
        for j, c in enumerate(row):
            if c != ' ':
                dir_positions[c] = (i, j)

    # Valid directions
    dirs = [(-1, 0, '^'), (1, 0, 'v'), (0, -1, '<'), (0, 1, '>')]

    def bfs_keypad(start, target_seq, pad, pad_pos):
        queue = deque([(pad_pos[start], 0, 0)])  # (pos, index in target_seq, total cost)
        visited = set()
        while queue:
            (r, c), idx, cost = queue.popleft()
            if (r, c, idx) in visited:
                continue
            visited.add((r, c, idx))
            if idx == len(target_seq):
                return cost
            for dr, dc, mv in dirs:
                nr, nc = r + dr, c + dc
                if 0 <= nr < len(pad) and 0 <= nc < len(pad[nr]) and pad[nr][nc] != ' ':
                    queue.append(((nr, nc), idx, cost + 1))
            if pad[r][c] == target_seq[idx]:
                queue.append(((r, c), idx + 1, cost + 1))
        return float('inf')

    def simulate(code):
        # Start at A on numeric pad
        sequence = list(code)
        presses = bfs_keypad('A', sequence, num_pad, num_positions)
        # Each robot layer wraps the sequence in keypad moves + 'A' presses
        for _ in range(25):
            move_seq = []
            for ch in sequence:
                move_seq.extend([ch, 'A'])
            sequence = move_seq
            presses = bfs_keypad('A', sequence, dir_pad, dir_positions)
        return presses

    total = 0
    for code in codes:
        numeric_part = int(code.rstrip('A').lstrip('0') or '0')
        complexity = simulate(code) * numeric_part
        total += complexity

    print(total)

if __name__ == "__main__":
    main()
