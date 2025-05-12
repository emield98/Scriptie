#include <stdio.h>
#include <stdlib.h>

#define MODULO 16777216
#define ITERATIONS 2000

unsigned int next_secret(unsigned int secret) {
    secret ^= secret * 64;
    secret %= MODULO;
    secret ^= secret / 32;
    secret %= MODULO;
    secret ^= secret * 2048;
    secret %= MODULO;
    return secret;
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    unsigned int total = 0;
    unsigned int secret;
    while (fscanf(file, "%u", &secret) == 1) {
        for (int i = 0; i < ITERATIONS; i++) {
            secret = next_secret(secret);
        }
        total += secret;
    }

    fclose(file);
    printf("%u\n", total);
    return 0;
}
