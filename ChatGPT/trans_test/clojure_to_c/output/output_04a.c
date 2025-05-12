#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_ROWS 100
#define MAX_COLS 100
#define WORD_LEN 4

const int directions[8][2] = {
    {0, 1}, {1, 0}, {1, 1}, {-1, 1},
    {0, -1}, {-1, 0}, {-1, -1}, {1, -1}
};

const char target[] = {'X', 'M', 'A', 'S'};

int in_bounds(int r, int c, int rows, int cols) {
    return r >= 0 && r < rows && c >= 0 && c < cols;
}

int match_word(char grid[MAX_ROWS][MAX_COLS], int rows, int cols, int r, int c, int dr, int dc) {
    for (int i = 0; i < WORD_LEN; i++) {
        int nr = r + i * dr;
        int nc = c + i * dc;
        if (!in_bounds(nr, nc, rows, cols) || grid[nr][nc] != target[i]) {
            return 0;
        }
    }
    return 1;
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    char grid[MAX_ROWS][MAX_COLS];
    int rows = 0, cols = 0;

    char line[MAX_COLS + 2];
    while (fgets(line, sizeof(line), file)) {
        size_t len = strcspn(line, "\r\n");
        if (cols == 0) cols = len;
        for (int i = 0; i < len; i++) {
            grid[rows][i] = line[i];
        }
        rows++;
    }
    fclose(file);

    int count = 0;
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            for (int d = 0; d < 8; d++) {
                if (match_word(grid, rows, cols, r, c, directions[d][0], directions[d][1])) {
                    count++;
                }
            }
        }
    }

    printf("%d\n", count);
    return 0;
}
