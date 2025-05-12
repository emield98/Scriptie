#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#define BUFFER_SIZE 1000000

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    char *buffer = (char *)malloc(BUFFER_SIZE);
    if (!buffer) return 1;

    size_t len = fread(buffer, 1, BUFFER_SIZE - 1, file);
    buffer[len] = '\0';
    fclose(file);

    bool enabled = true;
    long long total = 0;
    char *ptr = buffer;

    while (*ptr) {
        if (strncmp(ptr, "do()", 5) == 0) {
            enabled = true;
            ptr += 4;
        } else if (strncmp(ptr, "don't()", 8) == 0) {
            enabled = false;
            ptr += 7;
        } else if (strncmp(ptr, "mul(", 4) == 0) {
            int a, b;
            if (sscanf(ptr, "mul(%d,%d)", &a, &b) == 2) {
                if (enabled) {
                    total += (long long)a * b;
                }
            }
            while (*ptr && *ptr != ')') ptr++;
            if (*ptr == ')') ptr++;
        } else {
            ptr++;
        }
    }

    printf("%lld\n", total);
    free(buffer);
    return 0;
}
