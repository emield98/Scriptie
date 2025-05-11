# ChatGPT-generated solution will go here.
def parse_input():
    with open("input.txt") as f:
        blocks = f.read().strip().split("\n\n")

    def to_heights(block, is_lock):
        lines = block.splitlines()
        W = len(lines[0])
        H = len(lines)
        heights = []
        for col in range(W):
            h = 0
            if is_lock:
                for row in range(H):
                    if lines[row][col] == "#":
                        h += 1
                    else:
                        break
            else:
                for row in range(H - 1, -1, -1):
                    if lines[row][col] == "#":
                        h += 1
                    else:
                        break
            heights.append(h)
        return heights

    locks, keys = [], []
    for block in blocks:
        lines = block.strip().splitlines()
        if lines[0].startswith("#"):
            locks.append(to_heights(block, True))
        else:
            keys.append(to_heights(block, False))
    return locks, keys

def count_fitting_pairs(locks, keys):
    count = 0
    for l in locks:
        for k in keys:
            if all(l[i] + k[i] <= len(l) for i in range(len(l))):
                count += 1
    return count

def main():
    locks, keys = parse_input()
    print(count_fitting_pairs(locks, keys))

if __name__ == "__main__":
    main()
