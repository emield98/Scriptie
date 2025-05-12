#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#define MAX_LINES 1000
#define MAX_UPDATE_LEN 100

typedef struct {
    int a, b;
} Rule;

Rule rules[MAX_LINES];
int rule_count = 0;

int updates[MAX_LINES][MAX_UPDATE_LEN];
int update_lengths[MAX_LINES];
int update_count = 0;

int find_index(int *arr, int len, int target) {
    for (int i = 0; i < len; i++) {
        if (arr[i] == target) return i;
    }
    return -1;
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    char line[1024];
    while (fgets(line, sizeof(line), file)) {
        char *trim = line;
        while (*trim == ' ' || *trim == '\n' || *trim == '\r') trim++;
        if (*trim == '\0') continue;

        char *sep = strchr(trim, '|');
        if (sep) {
            int a, b;
            sscanf(trim, "%d|%d", &a, &b);
            rules[rule_count].a = a;
            rules[rule_count].b = b;
            rule_count++;
        } else {
            int val;
            int len = 0;
            char *token = strtok(trim, ",\n\r");
            while (token && len < MAX_UPDATE_LEN) {
                sscanf(token, "%d", &val);
                updates[update_count][len++] = val;
                token = strtok(NULL, ",\n\r");
            }
            update_lengths[update_count] = len;
            update_count++;
        }
    }
    fclose(file);

    long long total = 0;
    for (int i = 0; i < update_count; i++) {
        int *update = updates[i];
        int len = update_lengths[i];
        bool valid = true;

        for (int j = 0; j < rule_count; j++) {
            int a_idx = find_index(update, len, rules[j].a);
            int b_idx = find_index(update, len, rules[j].b);
            if (a_idx != -1 && b_idx != -1 && a_idx >= b_idx) {
                valid = false;
                break;
            }
        }

        if (valid) {
            total += update[len / 2];
        }
    }

    printf("%lld\n", total);
    return 0;
}
