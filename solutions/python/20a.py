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
        if 0 <= new_row < rows and 0 <= new_col < cols\
        and grid[new_row][new_col] != "#" \
        and dist[new_row][new_col] == -1: # not out of bounds, not wall and not visited
            dist[new_row][new_col] = dist[row][col] + 1
            queue.append((new_row, new_col))

# Check for possible "cheats"
def count_cheats(grid, dist):
    count = 0
    for row in range(rows):
        for col in range(cols):
            if grid[row][col] == "#": # Skip walls
                continue
            for new_row, new_col in [(row + 2, col), (row + 1, col + 1), (row, col + 2), (row - 1, col + 1)]:
                if 0 <= new_row < rows and 0 <= new_col < cols and grid[new_row][new_col] != "#":
                    if abs(dist[row][col] - dist[new_row][new_col]) >= 102: # abs saves us from having 8 directions to check
                        count += 1 # other side will check mirrored path
    return count

possible_cheats = count_cheats(grid, dist)
print(possible_cheats)


# n = size of the input grid (r * c)

# Time Complexity:
# Reading Input: O(n)
# BFS Initialization: O(n)
# BFS Traversal: O(n)
# Cheat Detection: O(n * 4) 4 directions per cell
# Total: O(n)

# Space Complexity:
# Grid Storage: O(n)
# Distance Matrix: O(n)
# Queue for BFS: O(n)
# Total: O(n)
