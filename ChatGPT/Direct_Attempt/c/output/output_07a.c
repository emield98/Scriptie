// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_NUMBERS 10

int evaluate(int numbers[], int n, char operators[]) {
    int result = numbers[0];
    for (int i = 0; i < n - 1; i++) {
        if (operators[i] == '+') {
            result += numbers[i + 1];
        } else if (operators[i] == '*') {
            result *= numbers[i + 1];
        }
    }
    return result;
}

int is_valid_equation(int target, int numbers[], int n) {
    int total_ways = 0;
    char operators[] = {'+', '*'};
    int operator_combinations = 1 << (n - 1);  // 2^(n-1) combinations of + and *
    
    for (int mask = 0; mask < operator_combinations; mask++) {
        char ops[MAX_NUMBERS];
        for (int i = 0; i < n - 1; i++) {
            ops[i] = (mask & (1 << i)) ? '*' : '+';
        }

        if (evaluate(numbers, n, ops) == target) {
            total_ways++;
        }
    }
    return total_ways > 0;
}

int main() {
    FILE *f = fopen("input.txt", "r");
    char line[100];
    int total_sum = 0;

    while (fgets(line, sizeof(line), f)) {
        int target, n = 0, numbers[MAX_NUMBERS];
        sscanf(line, "%d:", &target);

        char *token = strtok(line + strlen(line) - strlen(strtok(line, ":")), " ");
        while (token) {
            numbers[n++] = atoi(token);
            token = strtok(NULL, " ");
        }

        if (is_valid_equation(target, numbers, n)) {
            total_sum += target;
        }
    }

    fclose(f);
    printf("%d\n", total_sum);
    return 0;
}