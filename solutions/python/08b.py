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

            # Step along the line in both directions and add antinodes
            for direction in [-1, 1]:
                k = 1  # Start stepping
                while True:
                    new_x = x1 + direction * k * step_x
                    new_y = y1 + direction * k * step_y

                    if 0 <= new_x < len(lines[0]) and 0 <= new_y < len(lines):
                        unique_antinodes.add((new_x, new_y))
                        k += 1
                    else:
                        break  # Out of bounds

# Add all antenna positions to unique antinodes if they belong to a frequency with more than one antenna
for freq, positions in frequency_map.items():
    if len(positions) > 1:
        for x, y in positions:
            unique_antinodes.add((x, y))

print(len(unique_antinodes))


# a = total number of antennas
# f = average number of antennas per frequency
# n = total number of characters in the input file
# d = average distance between antennas (steps taken along the line)

# Time Complexity:
# Reading the file: O(n)
# Grouping antennas by frequency: O(a)
# Checking antenna pairs and stepping along the line: O(d * f^2) for each frequency group
# Total: O(n + a * f * d)     so O(n)
# Worst case all antenas are same frequency, n + d * a^2 complexity


# Space Complexity:
# Storing the frequency map: O(a)
# Storing unique antinode positions: O(a + positions), where positions are all extra antinodes
# Total: O(a + positions)
