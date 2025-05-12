#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define HASH_SIZE 262144
#define MULTIPLIER 2024
#define ITERATIONS 75
#define MAX_LINE 1000000

typedef struct Entry {
    long long key;
    long long value;
    struct Entry *next;
} Entry;

Entry *hash_table[HASH_SIZE];

unsigned int hash_func(long long key) {
    return ((unsigned long long)key) % HASH_SIZE;
}

void insert(Entry **table, long long key, long long value) {
    unsigned int h = hash_func(key);
    Entry *cur = table[h];
    while (cur) {
        if (cur->key == key) {
            cur->value += value;
            return;
        }
        cur = cur->next;
    }
    Entry *new_entry = malloc(sizeof(Entry));
    new_entry->key = key;
    new_entry->value = value;
    new_entry->next = table[h];
    table[h] = new_entry;
}

void clear_table(Entry **table) {
    for (int i = 0; i < HASH_SIZE; i++) {
        Entry *cur = table[i];
        while (cur) {
            Entry *tmp = cur;
            cur = cur->next;
            free(tmp);
        }
        table[i] = NULL;
    }
}

int num_digits(long long num) {
    if (num == 0) return 1;
    int count = 0;
    while (num) {
        num /= 10;
        count++;
    }
    return count;
}

void split_number(long long num, long long *left, long long *right) {
    char buf[32];
    sprintf(buf, "%lld", num);
    int len = strlen(buf);
    int mid = len / 2;

    char left_part[32], right_part[32];
    strncpy(left_part, buf, mid);
    left_part[mid] = '\0';
    strcpy(right_part, buf + mid);

    *left = atoll(left_part);
    *right = atoll(right_part);
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    char line[MAX_LINE];
    fgets(line, sizeof(line), file);
    fclose(file);

    Entry *current_table[HASH_SIZE] = {0};
    Entry *next_table[HASH_SIZE] = {0};

    char *token = strtok(line, " \t\n\r");
    while (token) {
        long long num = atoll(token);
        insert(current_table, num, 1);
        token = strtok(NULL, " \t\n\r");
    }

    for (int iter = 0; iter < ITERATIONS; iter++) {
        clear_table(next_table);
        for (int i = 0; i < HASH_SIZE; i++) {
            Entry *cur = current_table[i];
            while (cur) {
                long long key = cur->key, cnt = cur->value;
                if (key == 0) {
                    insert(next_table, 1, cnt);
                } else if (num_digits(key) % 2 == 0) {
                    long long l, r;
                    split_number(key, &l, &r);
                    insert(next_table, l, cnt);
                    insert(next_table, r, cnt);
                } else {
                    insert(next_table, key * MULTIPLIER, cnt);
                }
                cur = cur->next;
            }
        }
        memcpy(current_table, next_table, sizeof(current_table));
        memset(next_table, 0, sizeof(next_table));
    }

    long long total = 0;
    for (int i = 0; i < HASH_SIZE; i++) {
        Entry *cur = current_table[i];
        while (cur) {
            total += cur->value;
            cur = cur->next;
        }
    }

    printf("%lld\n", total);
    clear_table(current_table);
    return 0;
}
