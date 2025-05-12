#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#define MAX_NUMS 10
#define MAX_LINE 256

long long eval_expr(int *nums, char *ops, int n) {
    long long result = nums[0];
    for (int i = 0; i < n - 1; i++) {
        if (ops[i] == '+') {
            result += nums[i + 1];
        } else {
            result *= nums[i + 1];
        }
    }
    return result;
}

void generate_ops(int depth, int n, char *ops, int *nums, int target, int *matched) {
    if (*matched) return;
    if (depth == n - 1) {
        if (eval_expr(nums, ops, n) == target) {
            *matched = 1;
        }
        return;
    }
    ops[depth] = '+';
    generate_ops(depth + 1, n, ops, nums, target, matched);
    ops[depth] = '*';
    generate_ops(depth + 1, n, ops, nums, target, matched);
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    char line[MAX_LINE];
    long long total = 0;

    while (fgets(line, sizeof(line), file)) {
        if (line[0] == '\n' || line[0] == '\r') continue;

        char *colon = strchr(line, ':');
        if (!colon) continue;

        *colon = '\0';
        int target = atoi(line);
        char *right = colon + 1;

        int nums[MAX_NUMS], n = 0;
        char *token = strtok(right, " \t\n\r");
        while (token && n < MAX_NUMS) {
            nums[n++] = atoi(token);
            token = strtok(NULL, " \t\n\r");
        }

        char ops[MAX_NUMS];
        int matched = 0;
        generate_ops(0, n, ops, nums, target, &matched);
        if (matched) {
            total += target;
        }
    }

    fclose(file);
    printf("%lld\n", total);
    return 0;
}
