// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <stdint.h>

#define MODULO 16777216

uint32_t mix_and_prune(uint32_t secret, uint32_t mix_value) {
    secret ^= mix_value;
    return secret % MODULO;
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
    uint32_t total_sum = 0;

    while (fscanf(file, "%u", &initial_secret) != EOF) {
        total_sum += generate_secret(initial_secret, 2000);
    }

    printf("%u\n", total_sum);
    fclose(file);
    return 0;
}
