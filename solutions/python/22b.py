with open("input.txt", "r") as file:
    lines = file.readlines()

def secretStep(secret):
    # Cleaned up
    secret = (secret * 64 ^ secret) % 16777216
    secret = (secret // 32 ^ secret) % 16777216
    secret = (secret * 2048 ^ secret) % 16777216
    return secret

# Custom implementation of max function
def findMax(sequence_totals):
    max_key = None
    max_value = float('-inf')  # Start with the smallest possible value

    for key, value in sequence_totals.items():
        if value > max_value:
            max_value = value
            max_key = key

    return max_key

def findMaxBananas(lines):
    sequence_totals = {}

    for line in lines:
        secret_number = int(line)
        price_list = [secret_number % 10]  # Store last digit of secret numbers

        # Generate 2000 price steps
        for _ in range(2000):
            secret_number = secretStep(secret_number)
            price_list.append(secret_number % 10)
        
        tracked_sequences = set()

        # Examine all sequences of 4 consecutive price changes
        for index in range(len(price_list) - 4):
            p1, p2, p3, p4, p5 = price_list[index:index + 5]  # Extract 5 consecutive prices
            price_change = (p2 - p1, p3 - p2, p4 - p3, p5 - p4)  # Calculate changes
            
            if price_change in tracked_sequences:  # Skip since we wont choose same again
                continue
            tracked_sequences.add(price_change)

            if price_change not in sequence_totals:
                sequence_totals[price_change] = 0
            sequence_totals[price_change] += p5  # Add the price to the total

    # Find highest total
    # best_sequence = max(sequence_totals, key=sequence_totals.get)
    best_sequence = findMax(sequence_totals)
    max_bananas = sequence_totals[best_sequence]

    return best_sequence, max_bananas

best_sequence, max_bananas = findMaxBananas(lines)

print(max_bananas)


# n = number of lines in the input

# Time Complexity:
# Reading Input: O(n)
# Outer Loop over Lines: O(n)
# Price Generation (2000 iterations): O(2000 * n) = O(n) (2000 is constant)
# Inner Loop for Sequences: O(1996 * n) = O(n) (1996 = 2000 - 4 is constant)
# Sequence Operations (Set and Dict): O(1) per operation
# Total: O(n)

# Space Complexity:
# Input Storage: O(n)
# Price List: O(2000) = O(1) (constant space per buyer)
# Tracked Sequences: O(1) (bounded by unique 4-change sequences)
# Sequence Totals Dictionary: O(1) (bounded by unique sequences)
# Total: O(n)
