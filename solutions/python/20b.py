from collections import deque
with open("input.txt", "r") as file:  
    lines = file.readlines()

grid = []
for line in lines:
    grid.append(list(line.strip()))

rows = len(grid)
cols = len(grid[0])

for row in range(rows):
    for col in range(cols):
        if grid[row][col] == "S":
            start = (row, col)
            break
    else:
        continue
    break

# Initialize distances
dist = [[-1] * cols for _ in range(rows)]
dist[start[0]][start[1]] = 0
queue = deque([start])

# BFS
while queue:
    row, col = queue.popleft()
    if grid[row][col] == "E":
        break
    for new_row, new_col in [(row + 1, col), (row - 1, col), (row, col + 1), (row, col - 1)]: # down, up, right, left
        if 0 <= new_row < rows and 0 <= new_col < cols \
        and grid[new_row][new_col] != "#" \
        and dist[new_row][new_col] == -1: # not out of bounds, not wall and not visited
            dist[new_row][new_col] = dist[row][col] + 1
            queue.append((new_row, new_col))

# Count the possible "cheats" (only this changes for part 2)
def count_cheats_part2(grid, dist):
    count = 0
    for row in range(rows):
        for col in range(cols):
            if grid[row][col] == "#": # Skip walls
                continue
            for radius in range(2, 21): # Cheat duration from 2 to 20
                for dir_row in range(radius + 1): # Vertical offset
                    dir_col = radius - dir_row # Horizontal offset
                    for new_row, new_col in {
                        (row + dir_row, col + dir_col), # I
                        (row + dir_row, col - dir_col),
                        (row - dir_row, col + dir_col),
                        (row - dir_row, col - dir_col),
                    }:  # All possible cheat directions
                        if 0 <= new_row < rows and 0 <= new_col < cols \
                        and grid[new_row][new_col] != "#":  # Valid endpoint
                            if dist[row][col] - dist[new_row][new_col] >= 100 + radius:  # Cheat saves >= 100
                                count += 1
    return count

possible_cheats = count_cheats_part2(grid, dist)
print(possible_cheats)


# n = size of the input grid (r * c)

# Time Complexity:
# Reading Input: O(n)
# BFS Initialization: O(n)
# BFS Traversal: O(n)
# Cheat Detection:
#   Outer Grid Iteration: O(n)
#   Radius Range (2 to 20): O(19) ≈ O(1)
#   Directional Checks per Cell and Radius: O(radius) ≈ O(20) ≈ O(1) per radius
#   Total for Cheat Detection: O(n)
# Total: O(n)

# Space Complexity:
# Grid Storage: O(n)
# Distance Matrix: O(n)
# Queue for BFS: O(n)
# Total: O(n)
