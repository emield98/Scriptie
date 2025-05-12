#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#define MAX_FILE_SIZE 100000

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    char buffer[MAX_FILE_SIZE];
    size_t len = fread(buffer, 1, MAX_FILE_SIZE - 1, file);
    buffer[len] = '\0';
    fclose(file);

    long sum = 0;
    char *ptr = buffer;
    while ((ptr = strstr(ptr, "mul(")) != NULL) {
        int a, b;
        if (sscanf(ptr, "mul(%d,%d)", &a, &b) == 2) {
            sum += a * b;
        }
        ptr += 4;
    }

    printf("%ld\n", sum);
    return 0;
}
