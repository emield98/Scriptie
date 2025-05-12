#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#define MAX_PATTERNS 1000
#define MAX_DESIGNS 1000
#define MAX_LEN 1024
#define MAX_PATTERN_LEN 10

char *patterns[MAX_PATTERNS];
int pattern_count = 0;

char *designs[MAX_DESIGNS];
int design_count = 0;

bool pattern_exists(const char *substr) {
    for (int i = 0; i < pattern_count; i++) {
        if (strcmp(substr, patterns[i]) == 0) {
            return true;
        }
    }
    return false;
}

bool can_form(const char *design) {
    int n = strlen(design);
    bool dp[MAX_LEN] = {0};
    dp[0] = true;

    for (int i = 1; i <= n; i++) {
        for (int j = i - 1; j >= 0 && j >= i - MAX_PATTERN_LEN; j--) {
            if (dp[j]) {
                char substr[MAX_PATTERN_LEN + 1];
                int len = i - j;
                strncpy(substr, design + j, len);
                substr[len] = '\0';
                if (pattern_exists(substr)) {
                    dp[i] = true;
                    break;
                }
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

    int count = 0;
    for (int i = 0; i < design_count; i++) {
        if (can_form(designs[i])) {
            count++;
        }
    }

    printf("%d\n", count);
    return 0;
}
