#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_LINES 1000
#define MAX_NUMS 10

typedef long long ll;

typedef struct {
    ll target;
    ll nums[MAX_NUMS];
    int num_count;
} Equation;

Equation equations[MAX_LINES];
int equation_count = 0;

void parse_line(char *line) {
    char *colon = strchr(line, ':');
    *colon = '\0';
    ll target = atoll(line);
    Equation eq;
    eq.target = target;
    eq.num_count = 0;

    char *token = strtok(colon + 1, " \n");
    while (token) {
        eq.nums[eq.num_count++] = atoll(token);
        token = strtok(NULL, " \n");
    }

    equations[equation_count++] = eq;
}

ll eval_expression(ll *nums, int num_count, int *ops) {
    ll acc = nums[0];
    for (int i = 0; i < num_count - 1; i++) {
        if (ops[i] == 0) {
            acc += nums[i + 1];
        } else {
            acc *= nums[i + 1];
        }
    }
    return acc;
}

int generate_valid(ll target, ll *nums, int num_count) {
    int ops_count = num_count - 1;
    int total_combos = 1 << ops_count;
    int ops[MAX_NUMS];

    for (int mask = 0; mask < total_combos; mask++) {
        for (int i = 0; i < ops_count; i++) {
            ops[i] = (mask & (1 << i)) ? 1 : 0; // 0 = +, 1 = *
        }
        if (eval_expression(nums, num_count, ops) == target) {
            return 1;
        }
    }
    return 0;
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    char line[256];
    while (fgets(line, sizeof(line), file)) {
        parse_line(line);
    }
    fclose(file);

    ll sum = 0;
    for (int i = 0; i < equation_count; i++) {
        if (generate_valid(equations[i].target, equations[i].nums, equations[i].num_count)) {
            sum += equations[i].target;
        }
    }

    printf("%lld\n", sum);
    return 0;
}
