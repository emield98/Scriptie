# ChatGPT-generated solution will go here.
def main():
    with open('input.txt') as f:
        line = f.read().strip()

    blocks = []
    file_id = 0
    i = 0
    file_ranges = {}
    pos = 0

    while i < len(line):
        length = int(line[i])
        is_file = (i % 2 == 0)
        if is_file:
            blocks.extend([str(file_id)] * length)
            file_ranges[file_id] = (pos, length)
            file_id += 1
        else:
            blocks.extend(['.'] * length)
        pos += length
        i += 1

    total_len = len(blocks)

    # Find all free spans
    def get_free_spans():
        spans = []
        i = 0
        while i < total_len:
            if blocks[i] == '.':
                start = i
                while i < total_len and blocks[i] == '.':
                    i += 1
                spans.append((start, i - start))
            else:
                i += 1
        return spans

    # Move files in decreasing file_id order
    for fid in sorted(file_ranges.keys(), reverse=True):
        start, length = file_ranges[fid]
        # Only consider spans strictly to the left of the current file
        free_spans = get_free_spans()
        for fstart, flen in free_spans:
            if flen >= length and fstart < start:
                # Move file
                blocks[fstart:fstart + length] = [str(fid)] * length
                for i in range(start, start + length):
                    blocks[i] = '.'
                file_ranges[fid] = (fstart, length)
                break

    # Compute checksum
    checksum = 0
    for idx, val in enumerate(blocks):
        if val != '.':
            checksum += idx * int(val)
    print(checksum)

if __name__ == '__main__':
    main()
