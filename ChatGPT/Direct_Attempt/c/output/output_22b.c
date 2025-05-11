// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>

#define MAX_BUYERS 100
#define NUM_CHANGES 2000

uint32_t mix_and_prune(uint32_t secret, uint32_t mix_value) {
    secret ^= mix_value;
    return secret % 16777216;
}

uint32_t get_price(uint32_t secret) {
    return secret % 10;
}

uint32_t generate_secret(uint32_t secret, int steps) {
    for (int i = 0; i < steps; i++) {
        secret = mix_and_prune(secret, secret * 64);
        secret = mix_and_prune(secret, secret / 32);
        secret = mix_and_prune(secret, secret * 2048);
    }
    return secret;
}

int main() {
    FILE *file = fopen("input.txt", "r");
    uint32_t initial_secret;
    uint32_t prices[MAX_BUYERS][NUM_CHANGES];
    int buyer_count = 0;

    while (fscanf(file, "%u", &initial_secret) != EOF) {
        for (int i = 0; i < NUM_CHANGES; i++) {
            prices[buyer_count][i] = get_price(generate_secret(initial_secret, i+1));
        }
        buyer_count++;
    }
    fclose(file);

    // Now we need to calculate price changes
    int max_bananas = 0;
    for (int sequence_start = 0; sequence_start < NUM_CHANGES - 3; sequence_start++) {
        int sequence[4];
        for (int i = 0; i < 4; i++) {
            sequence[i] = prices[0][sequence_start + i] - prices[0][sequence_start + i - 1];
        }
        int bananas = 0;
        for (int i = 0; i < buyer_count; i++) {
            int found = 0;
            for (int j = 0; j < NUM_CHANGES - 3; j++) {
                int match = 1;
                for (int k = 0; k < 4; k++) {
                    if (prices[i][j + k] - prices[i][j + k - 1] != sequence[k]) {
                        match = 0;
                        break;
                    }
                }
                if (match) {
                    bananas += prices[i][j + 3];
                    found = 1;
                    break;
                }
            }
        }
        if (bananas > max_bananas) {
            max_bananas = bananas;
        }
    }

    printf("%d\n", max_bananas);
    return 0;
}
