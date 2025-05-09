with open("input.txt", "r") as file:
    disk_map = file.read().strip()

# Parse the disk map into segments
disk_segments = []
for i in range(0, len(disk_map), 2):
    file_length = int(disk_map[i])
    free_length = int(disk_map[i + 1]) if i + 1 < len(disk_map) else 0
    disk_segments.append(("file", file_length))
    if free_length > 0:
        disk_segments.append(("free", free_length))

# Build disk representation
blocks = []
file_id = 0
for segment, length in disk_segments:
    if segment == "file":
        blocks.extend([file_id] * length)
        file_id += 1
    else:
        blocks.extend(["."] * length)

# Compact the disk
left_walker, right_walker = 0, len(blocks) - 1
while left_walker < right_walker:
    while left_walker < len(blocks) and blocks[left_walker] != ".":
        left_walker += 1
    while right_walker >= 0 and blocks[right_walker] == ".":
        right_walker -= 1
    if left_walker < right_walker:
        blocks[left_walker], blocks[right_walker] = blocks[right_walker], blocks[left_walker]

# Calculate checksum
checksum = 0
for i in range(len(blocks)):
    if blocks[i] != ".":
        checksum += i * blocks[i]

print(checksum)

# n = number of blocks in the disk (length of the blocks array / length of input file)

# Time Complexity:
# Building Representation:    O(n)
# Compacting Process:         O(n)
# Checksum Calculation:       O(n)
# Total:                      O(n)

# Space Complexity:
# Blocks Array:               O(n)
# Auxiliary Variables:        O(1)
# Total:                      O(n)

