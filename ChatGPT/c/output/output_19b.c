// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <string.h>

#define MAX_DESIGNS 100
#define MAX_TOWELS 100
#define MAX_LENGTH 100

char towel_patterns[MAX_TOWELS][MAX_LENGTH];
char designs[MAX_DESIGNS][MAX_LENGTH];

int count_ways(char* design, int towel_count) {
    int dp[MAX_LENGTH + 1];
    memset(dp, 0, sizeof(dp));
    dp[0] = 1;

    for (int i = 1; i <= strlen(design); i++) {
        for (int j = 0; j < towel_count; j++) {
            int towel_len = strlen(towel_patterns[j]);
            if (i >= towel_len && strncmp(design + i - towel_len, towel_patterns[j], towel_len) == 0) {
                dp[i] += dp[i - towel_len];
            }
        }
    }

    return dp[strlen(design)];
}

int main() {
    FILE* file = fopen("input.txt", "r");
    char line[MAX_LENGTH];
    int towel_count = 0, design_count = 0;

    // Reading towel patterns
    fgets(line, sizeof(line), file);
    char* token = strtok(line, ", ");
    while (token != NULL) {
        strcpy(towel_patterns[towel_count++], token);
        token = strtok(NULL, ", ");
    }

    // Skip empty line
    fgets(line, sizeof(line), file);

    // Reading design patterns
    while (fgets(line, sizeof(line), file)) {
        line[strcspn(line, "\n")] = '\0';
        strcpy(designs[design_count++], line);
    }

    fclose(file);

    int total_ways = 0;
    for (int i = 0; i < design_count; i++) {
        total_ways += count_ways(designs[i], towel_count);
    }

    printf("%d\n", total_ways);
    return 0;
}
