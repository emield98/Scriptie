#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#define MAX_ROWS 1000
#define MAX_COLS 1000
#define MAX_ANTENNAS 10000
#define MAX_FREQ 256

typedef struct {
    int r, c;
} Position;

typedef struct Node {
    int r, c;
    struct Node *next;
} Node;

#define HASH_SIZE 10007

Node *hash_table[HASH_SIZE];

unsigned int hash(int r, int c) {
    return ((unsigned int)(r * 1009 + c)) % HASH_SIZE;
}

bool exists(int r, int c) {
    unsigned int h = hash(r, c);
    Node *node = hash_table[h];
    while (node) {
        if (node->r == r && node->c == c) return true;
        node = node->next;
    }
    return false;
}

void insert(int r, int c) {
    if (exists(r, c)) return;
    unsigned int h = hash(r, c);
    Node *new_node = malloc(sizeof(Node));
    new_node->r = r;
    new_node->c = c;
    new_node->next = hash_table[h];
    hash_table[h] = new_node;
}

char grid[MAX_ROWS][MAX_COLS];
Position antennas[MAX_FREQ][MAX_ANTENNAS];
int antenna_counts[MAX_FREQ];

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    int rows = 0, cols = 0;
    char line[MAX_COLS + 2];

    while (fgets(line, sizeof(line), file)) {
        int len = strcspn(line, "\r\n");
        if (len == 0) continue;
        strncpy(grid[rows], line, len);
        cols = len;
        rows++;
    }
    fclose(file);

    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            char ch = grid[r][c];
            if (ch != '.') {
                int idx = (unsigned char)ch;
                antennas[idx][antenna_counts[idx]++] = (Position){r, c};
            }
        }
    }

    for (int freq = 0; freq < MAX_FREQ; freq++) {
        int count = antenna_counts[freq];
        for (int i = 0; i < count; i++) {
            int r1 = antennas[freq][i].r;
            int c1 = antennas[freq][i].c;
            for (int j = 0; j < count; j++) {
                if (i == j) continue;
                int r2 = antennas[freq][j].r;
                int c2 = antennas[freq][j].c;
                int mid_r = 2 * r2 - r1;
                int mid_c = 2 * c2 - c1;
                if (mid_r >= 0 && mid_r < rows && mid_c >= 0 && mid_c < cols) {
                    insert(mid_r, mid_c);
                }
            }
        }
    }

    int antinode_count = 0;
    for (int i = 0; i < HASH_SIZE; i++) {
        Node *node = hash_table[i];
        while (node) {
            antinode_count++;
            node = node->next;
        }
    }

    printf("%d\n", antinode_count);
    return 0;
}
