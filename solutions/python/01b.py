left_list = [] #list
right_list = []

with open("input.txt", "r") as file: # O(n)
    for line in file:
        left, right = map(int, line.strip().split())
        left_list.append(left)
        right_list.append(right)

right_count = {} #dict
for num in right_list: # O(n)
    if num in right_count:
        right_count[num] += 1
    else:
        right_count[num] = 1
similarity_score = 0

for num in left_list: # O(n)
    if num in right_count:
        similarity_score += num * right_count[num]

print(similarity_score)

# Time complexity: O(n) + O(n) + O(n) = O(3n) = O(n)
# O(n)