#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_STONES 10000000
#define MAX_LINE 1000000

int split_and_add(int num, int *next_stones, int idx) {
    if (num == 0) {
        next_stones[idx++] = 1;
    } else {
        char buffer[32];
        sprintf(buffer, "%d", num);
        int len = strlen(buffer);
        if (len % 2 == 0) {
            buffer[len] = '\0';
            int mid = len / 2;
            char left[16], right[16];
            strncpy(left, buffer, mid);
            left[mid] = '\0';
            strcpy(right, buffer + mid);
            next_stones[idx++] = atoi(left);
            next_stones[idx++] = atoi(right);
        } else {
            next_stones[idx++] = num * 2024;
        }
    }
    return idx;
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    char line[MAX_LINE];
    fgets(line, sizeof(line), file);
    fclose(file);

    int *stones = malloc(sizeof(int) * MAX_STONES);
    int *next_stones = malloc(sizeof(int) * MAX_STONES);
    int count = 0;

    char *token = strtok(line, " \t\n\r");
    while (token) {
        stones[count++] = atoi(token);
        token = strtok(NULL, " \t\n\r");
    }

    for (int iter = 0; iter < 25; iter++) {
        int next_count = 0;
        for (int i = 0; i < count; i++) {
            next_count = split_and_add(stones[i], next_stones, next_count);
        }
        int *temp = stones;
        stones = next_stones;
        next_stones = temp;
        count = next_count;
    }

    printf("%d\n", count);

    free(stones);
    free(next_stones);
    return 0;
}
