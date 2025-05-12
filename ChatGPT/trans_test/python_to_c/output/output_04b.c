#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#define MAX_ROWS 1000
#define MAX_COLS 1000

const int patterns[6][6][2] = {
    {{0,0},{1,1},{2,2},{0,2},{1,1},{2,0}},
    {{0,2},{1,1},{2,0},{0,0},{1,1},{2,2}},
    {{2,0},{1,1},{0,2},{0,0},{1,1},{2,2}},
    {{2,2},{1,1},{0,0},{0,2},{1,1},{2,0}},
    {{0,0},{1,1},{2,2},{2,0},{1,1},{0,2}},
    {{2,0},{1,1},{0,2},{2,2},{1,1},{0,0}}
};

bool is_valid_triplet(char a, char b, char c) {
    return (a == 'M' && b == 'A' && c == 'S') || (a == 'S' && b == 'A' && c == 'M');
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    char grid[MAX_ROWS][MAX_COLS];
    int rows = 0, cols = 0;
    char line[MAX_COLS + 2];

    while (fgets(line, sizeof(line), file)) {
        size_t len = strcspn(line, "\r\n");
        if (len == 0) continue;
        strncpy(grid[rows], line, len);
        cols = len;
        rows++;
    }
    fclose(file);

    int count = 0;
    for (int r = 0; r <= rows - 3; r++) {
        for (int c = 0; c <= cols - 3; c++) {
            for (int p = 0; p < 6; p++) {
                char chars[6];
                for (int i = 0; i < 6; i++) {
                    int dr = patterns[p][i][0];
                    int dc = patterns[p][i][1];
                    chars[i] = grid[r + dr][c + dc];
                }
                if (is_valid_triplet(chars[0], chars[1], chars[2]) &&
                    is_valid_triplet(chars[3], chars[4], chars[5])) {
                    count++;
                    break;
                }
            }
        }
    }

    printf("%d\n", count);
    return 0;
}
