#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>

#define MAX_LINES 1000
#define MAX_NUMS 100

int is_safe_report(long *nums, int len) {
    if (len < 2) return 0;
    int direction = 0;
    for (int i = 1; i < len; i++) {
        long delta = nums[i] - nums[i - 1];
        if (abs(delta) < 1 || abs(delta) > 3) return 0;
        int sign = (delta > 0) ? 1 : (delta < 0) ? -1 : 0;
        if (sign == 0) return 0;
        if (direction == 0) direction = sign;
        else if (direction != sign) return 0;
    }
    return 1;
}

int is_dampened_safe(long *nums, int len) {
    if (len < 3) return 0;
    long temp[MAX_NUMS];
    for (int i = 0; i < len; i++) {
        int idx = 0;
        for (int j = 0; j < len; j++) {
            if (j != i) {
                temp[idx++] = nums[j];
            }
        }
        if (is_safe_report(temp, len - 1)) {
            return 1;
        }
    }
    return 0;
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    char line[1024];
    int valid_count = 0;

    while (fgets(line, sizeof(line), file)) {
        long nums[MAX_NUMS];
        int len = 0;
        char *token = strtok(line, " \t\n");
        while (token && len < MAX_NUMS) {
            nums[len++] = strtol(token, NULL, 10);
            token = strtok(NULL, " \t\n");
        }
        if (is_safe_report(nums, len) || is_dampened_safe(nums, len)) {
            valid_count++;
        }
    }

    fclose(file);
    printf("%d\n", valid_count);
    return 0;
}
