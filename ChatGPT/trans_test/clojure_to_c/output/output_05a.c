#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#define MAX_LINES 1000
#define MAX_UPDATE_LEN 100

typedef struct {
    long a;
    long b;
} Rule;

int parse_line_is_rule(const char *line) {
    return strchr(line, '|') != NULL;
}

int cmp_long(const void *a, const void *b) {
    long x = *(long *)a;
    long y = *(long *)b;
    return (x > y) - (x < y);
}

int index_of(long *arr, int len, long value) {
    for (int i = 0; i < len; i++) {
        if (arr[i] == value) return i;
    }
    return -1;
}

int valid_update(Rule *rules, int rule_count, long *update, int update_len) {
    for (int i = 0; i < rule_count; i++) {
        int idx_a = index_of(update, update_len, rules[i].a);
        int idx_b = index_of(update, update_len, rules[i].b);
        if (idx_a != -1 && idx_b != -1 && idx_a >= idx_b) {
            return 0;
        }
    }
    return 1;
}

long middle_page(long *update, int len) {
    return update[len / 2];
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    Rule rules[MAX_LINES];
    long updates[MAX_LINES][MAX_UPDATE_LEN];
    int update_lengths[MAX_LINES];
    int rule_count = 0, update_count = 0;

    char line[1024];
    while (fgets(line, sizeof(line), file)) {
        size_t len = strcspn(line, "\r\n");
        line[len] = '\0';
        if (len == 0) continue;

        if (parse_line_is_rule(line)) {
            char *sep = strchr(line, '|');
            *sep = '\0';
            rules[rule_count].a = strtol(line, NULL, 10);
            rules[rule_count].b = strtol(sep + 1, NULL, 10);
            rule_count++;
        } else {
            int count = 0;
            char *token = strtok(line, ",");
            while (token) {
                updates[update_count][count++] = strtol(token, NULL, 10);
                token = strtok(NULL, ",");
            }
            update_lengths[update_count] = count;
            update_count++;
        }
    }
    fclose(file);

    long sum = 0;
    for (int i = 0; i < update_count; i++) {
        if (valid_update(rules, rule_count, updates[i], update_lengths[i])) {
            sum += middle_page(updates[i], update_lengths[i]);
        }
    }

    printf("%ld\n", sum);
    return 0;
}
