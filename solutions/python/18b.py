from collections import deque
with open("input.txt", "r") as file:
    lines = file.readlines()

def is_path_possible(grid, start, end):
    directions = [(-1, 0), (1, 0), (0, -1), (0, 1)]
    queue = deque([start])
    visited = set()
    visited.add(start)
    # similar to day 16 code :)
    while queue:
        row, col = queue.popleft()
        if (row, col) == end:
            return True
        for dir_row, dir_col in directions:
            new_row, new_col = row + dir_row, col + dir_col
            if 0 <= new_row < 71 and 0 <= new_col < 71 and \
                grid[new_row][new_col] != '#' and (new_row, new_col) not in visited:
                visited.add((new_row, new_col))
                queue.append((new_row, new_col))
    return False



bytes_to_fall = []
for line in lines:
    coords = line.strip().split(',')
    x = int(coords[0])
    y = int(coords[1])
    bytes_to_fall.append((x, y))

final_grid = [['.' for _ in range(70 + 1)] for _ in range(70 + 1)]
start = (0, 0) # no need to search like day 16.
end = (70, 70)

# Binary search to find the first blocking byte
# log n complexity instead of n if linear search.
low = 0
high = len(bytes_to_fall) - 1
blocking_index = -1

while low <= high:
    mid = (low + high) // 2

    # Reset the grid
    final_grid = [['.' for _ in range(70 + 1)] for _ in range(70 + 1)]


    # Apply bytes up to mid
    for i in range(mid + 1):
        x, y = bytes_to_fall[i]
        final_grid[y][x] = '#'

    # Check if the path is still possible
    if is_path_possible(final_grid, start, end):
        low = mid + 1  # Path is still possible, search later bytes
    else:
        blocking_index = mid
        high = mid - 1  # Path is blocked, search earlier bytes

if blocking_index != -1:
    x, y = bytes_to_fall[blocking_index]
    print(f"{x},{y}")


# n = number of corrupted bytes_to_fall
# m = size of the grid (rows * columns)

# Time Complexity:
# Reading Input: O(n)
# Initializing the Grid: O(m) per reset, called O(log n) times
# Binary Search Iterations: O(log n)
#   - Resetting the Grid: O(m) per iteration
#   - Applying Bytes to Grid: O(n) per iteration
#   - BFS Path Check (is_path_possible): O(m)
# Total per iteration: O(m + n)
# Total: O((m + n) * log n)

# Space Complexity:
# Storing the Grid: O(m)
# Storing the Visited Set (BFS): O(m)
# BFS Queue: O(m)
# Temporary Variables: O(1)
# Total: O(m)
