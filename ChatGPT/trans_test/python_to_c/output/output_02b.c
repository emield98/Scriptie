#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#define MAX_LINE 1024
#define MAX_NUMS 100

bool is_safe(int *report, int length) {
    if (length < 2) return false;
    bool increasing = true, decreasing = true;

    for (int i = 0; i < length - 1; i++) {
        int diff = report[i + 1] - report[i];
        if (!(diff > 0 && diff <= 3)) increasing = false;
        if (!(diff < 0 && diff >= -3)) decreasing = false;
    }
    return increasing || decreasing;
}

bool is_safe_with_dampener(int *report, int length) {
    int reduced[MAX_NUMS];
    for (int i = 0; i < length; i++) {
        int idx = 0;
        for (int j = 0; j < length; j++) {
            if (j != i) {
                reduced[idx++] = report[j];
            }
        }
        if (is_safe(reduced, length - 1)) {
            return true;
        }
    }
    return false;
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    char line[MAX_LINE];
    int safe_count = 0;

    while (fgets(line, sizeof(line), file)) {
        if (line[0] == '\n') continue;

        int nums[MAX_NUMS];
        int count = 0;
        char *token = strtok(line, " \n");
        while (token && count < MAX_NUMS) {
            nums[count++] = atoi(token);
            token = strtok(NULL, " \n");
        }

        if (is_safe(nums, count) || is_safe_with_dampener(nums, count)) {
            safe_count++;
        }
    }

    fclose(file);
    printf("%d\n", safe_count);
    return 0;
}
