import re

with open("input.txt", "r") as file:
    corrupted_memory = file.read()

pattern = r"mul\((\d{1,3}),(\d{1,3})\)" #Grouping
control_pattern = r"(do\(\)|don't\(\))"

all_matches = re.findall(f"{control_pattern}|{pattern}",corrupted_memory)
# print(all_matches)

result_sum = 0
mul_enabled = True

for match in all_matches:
    if match[0]:
        if match[0] == "do()":
            mul_enabled = True
        elif match[0] == "don't()":
            mul_enabled = False
    elif match[1] and match[2]:
        if mul_enabled:
            x, y = int(match[1]), int(match[2])
            result_sum += x * y

print(result_sum)
# Time O(2n + m) so O(n)
# Space O(n + m) so O(n)
# LINEAR!!!!