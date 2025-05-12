#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#define MAX_PATTERNS 100
#define MAX_PATTERN_LEN 32
#define MAX_DESIGNS 1000
#define MAX_DESIGN_LEN 256
#define MAX_MEMO 1000003

typedef struct MemoEntry {
    char *key;
    bool value;
    struct MemoEntry *next;
} MemoEntry;

MemoEntry *memo_table[MAX_MEMO];

unsigned int hash_string(const char *s) {
    unsigned int hash = 5381;
    while (*s) {
        hash = ((hash << 5) + hash) + (unsigned char)(*s++);
    }
    return hash % MAX_MEMO;
}

bool memo_get(const char *key, bool *value) {
    unsigned int h = hash_string(key);
    MemoEntry *entry = memo_table[h];
    while (entry) {
        if (strcmp(entry->key, key) == 0) {
            *value = entry->value;
            return true;
        }
        entry = entry->next;
    }
    return false;
}

void memo_set(const char *key, bool value) {
    unsigned int h = hash_string(key);
    MemoEntry *entry = malloc(sizeof(MemoEntry));
    entry->key = strdup(key);
    entry->value = value;
    entry->next = memo_table[h];
    memo_table[h] = entry;
}

char *patterns[MAX_PATTERNS];
int pattern_count = 0;

bool can_form(const char *design) {
    bool val;
    if (memo_get(design, &val)) return val;
    if (design[0] == '\0') {
        memo_set(design, true);
        return true;
    }
    for (int i = 0; i < pattern_count; i++) {
        int len = strlen(patterns[i]);
        if (strncmp(design, patterns[i], len) == 0) {
            if (can_form(design + len)) {
                memo_set(design, true);
                return true;
            }
        }
    }
    memo_set(design, false);
    return false;
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    char *buffer = NULL;
    size_t bufsize = 0;
    fseek(file, 0, SEEK_END);
    long length = ftell(file);
    rewind(file);
    buffer = malloc(length + 1);
    fread(buffer, 1, length, file);
    buffer[length] = '\0';
    fclose(file);

    char *sections[2];
    sections[0] = strtok(buffer, "\n\n");
    sections[1] = strtok(NULL, "");

    char *token = strtok(sections[0], ", ");
    while (token) {
        patterns[pattern_count++] = strdup(token);
        token = strtok(NULL, ", ");
    }

    int count = 0;
    char *line = strtok(sections[1], "\n");
    while (line) {
        if (can_form(line)) {
            count++;
        }
        line = strtok(NULL, "\n");
    }

    printf("%d\n", count);

    // Cleanup
    for (int i = 0; i < pattern_count; i++) {
        free(patterns[i]);
    }
    for (int i = 0; i < MAX_MEMO; i++) {
        MemoEntry *entry = memo_table[i];
        while (entry) {
            MemoEntry *tmp = entry;
            entry = entry->next;
            free(tmp->key);
            free(tmp);
        }
    }
    free(buffer);
    return 0;
}
