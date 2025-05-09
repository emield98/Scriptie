def is_safe(report):
    levels = report.split()
    int_levels = []
    for level in levels:
        int_levels.append(int(level))
    levels = int_levels

    #Increasing or decreasing
    is_increasing = True
    is_decreasing = True

    for i in range(len(levels) - 1): # Time O(m)
        diff = abs(levels[i+1] - levels[i])

        if diff < 1 or diff > 3:
            return False
        
        if levels[i] >= levels[i + 1]:
            is_increasing = False
        if levels[i] <= levels[i + 1]:
            is_decreasing = False

    return is_decreasing or is_increasing

with open("input.txt", "r") as file:
    reports = file.readlines()

safe_count = 0
for report in reports: # Time O(n)
    if is_safe(report.strip()):
        safe_count += 1

print(safe_count)

#Time (n * m)
#Space(n + m)