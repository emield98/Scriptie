left_list = []
right_list = []

with open("input.txt", "r") as file: # O(n)
    for line in file:
        left, right = map(int, line.strip().split())
        left_list.append(left)
        right_list.append(right)

left_list.sort() # Tim sort (merge sort & insertion sort based on size)
right_list.sort() # O(nlogn)
total_distance = 0 

for i in range(len(left_list)): #O(n)
    total_distance += abs(left_list[i] - right_list[i]) 

print(total_distance)

# Time complexity: O(n) + O(nlogn) + O(n) = O(2n + nLogn) = O(nLogn)
# O(nlogn)