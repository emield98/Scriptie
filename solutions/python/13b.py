def solve_machine_optimized(Ax, Ay, Bx, By, Px, Py):
    Px += 10000000000000
    Py += 10000000000000

    denominator = Ax * By - Ay * Bx
    if denominator == 0:  # Division by 0 error later
        return None

    # Solve using the direct computation method
    i = (Px * By - Py * Bx) / denominator
    j = (Px - Ax * i) / Bx if Bx != 0 else 0  # Prevent division by zero again

    # Check if integers and above zero for it to be valid
    if i % 1 == 0 and j % 1 == 0 and \
    i >= 0 and j >= 0:
        cost = int(3 * i + 1 * j)  # Calculate total cost
        return cost

    return None


machines = []
# Read input from file
with open("input.txt", "r") as file:
    lines = file.readlines()

i = 0
while i < len(lines):
    if lines[i].startswith("Button A:"):
        # Parse A line
        part = lines[i].split(",")
        Ax_str = part[0].split("X+")[-1].strip()
        Ay_str = part[1].split("Y+")[-1].strip()
        Ax = int(Ax_str)
        Ay = int(Ay_str)

        # Next line: Button B
        i += 1
        part = lines[i].split(",")
        Bx_str = part[0].split("X+")[-1].strip()
        By_str = part[1].split("Y+")[-1].strip()
        Bx = int(Bx_str)
        By = int(By_str)

        # Next line: Prize
        i += 1
        part = lines[i].split(",")
        Px_str = part[0].split("X=")[-1].strip()
        Py_str = part[1].split("Y=")[-1].strip()
        Px = int(Px_str)
        Py = int(Py_str)

        machines.append((Ax, Ay, Bx, By, Px, Py))
    
    i += 1

counter = 0
total_cost = 0
for (Ax, Ay, Bx, By, Px, Py) in machines:
    cost = solve_machine_optimized(Ax, Ay, Bx, By, Px, Py)
    counter += 1
    if cost is not None:
        total_cost += cost

print(total_cost)


# n = total number of machines, less than total number of lines.

# Time Complexity:
# Reading Input:               O(n)
# Solve Machine Calculations:  O(n)
# Total:                       O(n)

# Space Complexity:
# Storing Machines:            O(n)
# Temporary Variables:         O(1)
# Total:                       O(n)
