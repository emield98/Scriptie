import time
with open("input.txt", "r") as file:
    lines = file.readlines()

def secretStep(secret):
    # Multiply by 64
    temp = secret * 64
    # Mix result into secret num
    secret = temp ^ secret
    # Prune secret number (modulus 16777216)
    secret = secret % 16777216 # 4096 * 4096
    # Calculate result of secret / 32, round down to nearest int, 
    temp = secret // 32 # floor division
    # mix with secret (mix is bitwise xor)
    secret = temp ^ secret
    # prune secret (modulus 16777216)
    secret = secret % 16777216
    # multiply by 2048, mix and prune
    temp = secret * 2048
    secret = temp ^ secret
    secret = secret % 16777216
    return secret

def shiftStep(secret): # Same speed sadly though
    # Multiply by 64 using bitwise left shift
    secret = (secret << 6) ^ secret
    # Prune secret (modulus 16777216 using bitwise AND)
    secret &= 0xFFFFFF # hex represntation of 16777216 since its 2^24
    # Divide by 32 and round down using bitwise right shift
    secret = (secret >> 5) ^ secret
    # Prune secret again
    secret &= 0xFFFFFF
    # Multiply by 2048 using left shift, mix and prune
    secret = (secret << 11) ^ secret
    secret &= 0xFFFFFF
    return secret

# Timers for secretStep
start_secret = time.time()
totalSecret = 0
for line in lines:
    secret = int(line)
    for _ in range(2000):
        secret = secretStep(secret)
    totalSecret += secret
end_secret = time.time()

# Timers for shiftStep
start_shift = time.time()
totalShift = 0
for line in lines:
    shift = int(line)
    for _ in range(2000):
        shift = shiftStep(shift)
    totalShift += shift
end_shift = time.time()

# Results
print(totalSecret)



# n = number of lines in the input

# Time Complexity:
# Reading Input: O(n)
# Outer Loop: O(n)
# Inner Loop: O(2000 * n) = O(n) (2000 is constant)
# Total: O(n)

# Space Complexity:
# Input Storage: O(n)
# Variables: O(1)
# Total: O(n)
