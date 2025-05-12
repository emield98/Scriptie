#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>

#define MODULO 16777216

uint32_t mix(uint32_t secret, uint32_t val) {
    return secret ^ val;
}

uint32_t prune(uint32_t val) {
    return val % MODULO;
}

uint32_t next_secret(uint32_t secret) {
    secret = mix(secret, secret * 64);
    secret = prune(secret);
    secret = mix(secret, secret / 32);
    secret = prune(secret);
    secret = mix(secret, secret * 2048);
    secret = prune(secret);
    return secret;
}

uint32_t nth_secret(uint32_t start, int n) {
    uint32_t secret = start;
    for (int i = 0; i < n; i++) {
        secret = next_secret(secret);
    }
    return secret;
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    char line[64];
    uint64_t sum = 0;

    while (fgets(line, sizeof(line), file)) {
        uint32_t val = (uint32_t)strtoul(line, NULL, 10);
        sum += nth_secret(val, 2000);
    }

    fclose(file);
    printf("%llu\n", sum);
    return 0;
}
