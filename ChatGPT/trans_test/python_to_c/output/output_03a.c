#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#define BUFFER_SIZE 1000000

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    char *buffer = (char *)malloc(BUFFER_SIZE);
    if (!buffer) return 1;

    size_t len = fread(buffer, 1, BUFFER_SIZE - 1, file);
    buffer[len] = '\0';
    fclose(file);

    long long total = 0;
    char *ptr = buffer;

    while ((ptr = strstr(ptr, "mul(")) != NULL) {
        int a, b;
        if (sscanf(ptr, "mul(%d,%d)", &a, &b) == 2) {
            total += (long long)a * b;
        }
        ptr += 4;
    }

    printf("%lld\n", total);
    free(buffer);
    return 0;
}
