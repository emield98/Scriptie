from collections import deque
with open("input.txt", "r") as file:
    lines = file.readlines()

final_grid = [['.' for _ in range(70 + 1)] for _ in range(70 + 1)]

# Mark corrupted memory locations
for i in range(1024):
    coords = lines[i].strip().split(',')
    x = int(coords[0])
    y = int(coords[1])
    final_grid[y][x] = '#'


start = (0, 0) # no need to search like day 16.
end = (70, 70)

# Breadth-first search (BFS) for shortest path
directions = [(-1, 0), (1, 0), (0, -1), (0, 1)] # up down left right
dist = [[-1] * (70 + 1) for _ in range(70 + 1)]
dist[start[0]][start[1]] = 0
queue = deque([start])
# similar to day 16 code
while queue:
    row, col = queue.popleft()
    if (row, col) == end:
        print(dist[row][col])
        break
    for dir_row, dir_col in directions:
        new_row, new_col = row + dir_row, col + dir_col
        if 0 <= new_row < 71 and 0 <= new_col < 71 and \
            final_grid[new_row][new_col] != '#' and dist[new_row][new_col] == -1: #not visited too
            dist[new_row][new_col] = dist[row][col] + 1
            queue.append((new_row, new_col))


# n = number of nodes processed (cells explored in the queue)
# m = size of the grid (rows * columns)

# Time Complexity:
# Reading Input: O(1) (1024 bytes are fixed, independent of m)
# Initializing the Grid: O(m)
# BFS Algorithm:
#   - Queue Operations: O(n)
#   - Direction Checks: O(n)
# Total: O(m + n)

# Space Complexity:
# Storing the Grid: O(m)
# Storing the Distance Array: O(m)
# Storing the BFS Queue: O(n)
# Total: O(m)

