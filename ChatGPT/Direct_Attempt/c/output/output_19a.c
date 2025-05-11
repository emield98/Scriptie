#include <stdio.h>
#include <string.h>

#define MAX_DESIGNS 100
#define MAX_TOWELS 100
#define MAX_LENGTH 100

char towel_patterns[MAX_TOWELS][MAX_LENGTH];
char designs[MAX_DESIGNS][MAX_LENGTH];

int can_create_design(char* design, int towel_count) {
    int dp[MAX_LENGTH + 1];
    memset(dp, 0, sizeof(dp));
    dp[0] = 1;

    for (int i = 1; i <= strlen(design); i++) {
        for (int j = 0; j < towel_count; j++) {
            int towel_len = strlen(towel_patterns[j]);
            if (i >= towel_len && strncmp(design + i - towel_len, towel_patterns[j], towel_len) == 0) {
                dp[i] = dp[i] || dp[i - towel_len];
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

    int possible_designs = 0;
    for (int i = 0; i < design_count; i++) {
        if (can_create_design(designs[i], towel_count)) {
            possible_designs++;
        }
    }

    printf("%d\n", possible_designs);
    return 0;
}
