#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_PATTERNS 1000
#define MAX_DESIGNS 1000
#define MAX_LEN 1024
#define MAX_PATTERN_LEN 10

char *patterns[MAX_PATTERNS];
int pattern_count = 0;

char *designs[MAX_DESIGNS];
int design_count = 0;

int pattern_exists(const char *substr) {
    for (int i = 0; i < pattern_count; i++) {
        if (strcmp(substr, patterns[i]) == 0) {
            return 1;
        }
    }
    return 0;
}

long long count_ways(const char *design) {
    int n = strlen(design);
    long long dp[MAX_LEN] = {0};
    dp[0] = 1;

    for (int i = 1; i <= n; i++) {
        for (int j = i - 1; j >= 0 && j >= i - MAX_PATTERN_LEN; j--) {
            char substr[MAX_PATTERN_LEN + 1];
            int len = i - j;
            strncpy(substr, design + j, len);
            substr[len] = '\0';
            if (pattern_exists(substr)) {
                dp[i] += dp[j];
            }
        }
    }

    return dp[n];
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    char line[MAX_LEN];
    if (!fgets(line, sizeof(line), file)) return 1;
    line[strcspn(line, "\r\n")] = 0;

    char *token = strtok(line, ", ");
    while (token) {
        patterns[pattern_count++] = strdup(token);
        token = strtok(NULL, ", ");
    }

    while (fgets(line, sizeof(line), file)) {
        line[strcspn(line, "\r\n")] = 0;
        if (strlen(line) > 0) {
            designs[design_count++] = strdup(line);
        }
    }
    fclose(file);

    long long total = 0;
    for (int i = 0; i < design_count; i++) {
        total += count_ways(designs[i]);
    }

    printf("%lld\n", total);
    return 0;
}
