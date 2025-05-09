from collections import defaultdict

with open("input.txt", "r") as file:
    lines = []
    for line in file:
        lines.append(line.strip())

valid_antenna_chars = set("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")
# we could use .isalnum() meaning is alphanumeric but for clarity, we dont.

# Collect antenna positions and frequencies
antennas = []
for y, line in enumerate(lines):
    for x, char in enumerate(line):
        if char in valid_antenna_chars:
            antennas.append((x, y, char))

# Group antennas by frequency
frequency_map = defaultdict(list)
for x, y, freq in antennas:
    frequency_map[freq].append((x, y))

unique_antinodes = set() # set has unique elements only
for freq, positions in frequency_map.items():
    # Skip frequencies with only one antenna
    if len(positions) < 2:
        continue

    n = len(positions)
    for i in range(n):
        for j in range(i + 1, n):
            x1, y1 = positions[i]
            x2, y2 = positions[j]

            step_x = x2 - x1
            step_y = y2 - y1

            antinode1 = (x1 - step_x, y1 - step_y)
            antinode2 = (x2 + step_x, y2 + step_y)

            # Add to unique locations if valid
            if 0 <= antinode1[0] < len(lines[0]) and 0 <= antinode1[1] < len(lines):
                unique_antinodes.add(antinode1)
            if 0 <= antinode2[0] < len(lines[0]) and 0 <= antinode2[1] < len(lines):
                unique_antinodes.add(antinode2)

print(len(unique_antinodes))

# a = number of antennas
# f = number of antennas per frequency
# n = number of characters in the input file

# Time Complexity:
# Reading the file: O(n)
# Grouping antennas by frequency: O(a)
# Checking antenna pairs for antinodes: O(f^2) for each frequency group
# f^2 for each frequency, summed over a/f frequencies so
# Total: O(n + a * f)       so O(n)
# Worst case all antenas are same frequency, n + a^2 complexity

# Space Complexity:
# Storing the frequency map: O(a)
# Storing unique antinode positions: O(a)
# Total: O(a)     so worst case O(n)
