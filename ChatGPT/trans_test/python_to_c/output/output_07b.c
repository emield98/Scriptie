#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>

#define MAX_NUMS 10
#define MAX_LINE 256

long long concat(long long a, long long b) {
    long long temp = b;
    int digits = 0;
    if (temp == 0) digits = 1;
    while (temp > 0) {
        digits++;
        temp /= 10;
    }
    for (int i = 0; i < digits; i++) {
        a *= 10;
    }
    return a + b;
}

long long eval_expr(long long *nums, char *ops, int n) {
    long long result = nums[0];
    for (int i = 0; i < n - 1; i++) {
        if (ops[i] == '+') {
            result += nums[i + 1];
        } else if (ops[i] == '*') {
            result *= nums[i + 1];
        } else { // '||'
            result = concat(result, nums[i + 1]);
        }
    }
    return result;
}

int found_match;

void generate_ops(int depth, int n, char *ops, long long *nums, long long target) {
    if (found_match) return;
    if (depth == n - 1) {
        if (eval_expr(nums, ops, n) == target) {
            found_match = 1;
        }
        return;
    }
    ops[depth] = '+';
    generate_ops(depth + 1, n, ops, nums, target);
    ops[depth] = '*';
    generate_ops(depth + 1, n, ops, nums, target);
    ops[depth] = '|';
    generate_ops(depth + 1, n, ops, nums, target);
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
        long long target = atoll(line);
        char *right = colon + 1;

        long long nums[MAX_NUMS];
        int n = 0;
        char *token = strtok(right, " \t\n\r");
        while (token && n < MAX_NUMS) {
            nums[n++] = atoll(token);
            token = strtok(NULL, " \t\n\r");
        }

        char ops[MAX_NUMS];
        found_match = 0;
        generate_ops(0, n, ops, nums, target);
        if (found_match) {
            total += target;
        }
    }

    fclose(file);
    printf("%lld\n", total);
    return 0;
}
