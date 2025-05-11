# ChatGPT-generated solution will go here.
def main():
    with open('input.txt') as f:
        line = f.read().strip()

    blocks = []
    file_id = 0
    i = 0
    while i < len(line):
        length = int(line[i])
        is_file = (i % 2 == 0)
        if is_file:
            blocks.extend([str(file_id)] * length)
            file_id += 1
        else:
            blocks.extend(['.'] * length)
        i += 1

    # Compact the files
    i = 0
    while i < len(blocks):
        if blocks[i] == '.':
            j = i + 1
            while j < len(blocks) and blocks[j] == '.':
                j += 1
            if j == len(blocks):
                break
            blocks[i] = blocks[j]
            blocks[j] = '.'
            i = max(i - 1, 0)
        else:
            i += 1

    # Compute checksum
    checksum = 0
    for idx, val in enumerate(blocks):
        if val != '.':
            checksum += idx * int(val)
    print(checksum)

if __name__ == '__main__':
    main()
