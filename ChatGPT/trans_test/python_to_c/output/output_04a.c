#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#define MAX_ROWS 1000
#define MAX_COLS 1000
#define WORD_LEN 4

const char *word = "XMAS";
const int directions[8][2] = {
    {-1, -1}, {-1, 0}, {-1, 1},
    {0, -1},          {0, 1},
    {1, -1},  {1, 0},  {1, 1}
};

bool in_bounds(int r, int c, int rows, int cols) {
    return r >= 0 && r < rows && c >= 0 && c < cols;
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
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            for (int d = 0; d < 8; d++) {
                int dr = directions[d][0];
                int dc = directions[d][1];
                bool match = true;
                for (int i = 0; i < WORD_LEN; i++) {
                    int nr = r + dr * i;
                    int nc = c + dc * i;
                    if (!in_bounds(nr, nc, rows, cols) || grid[nr][nc] != word[i]) {
                        match = false;
                        break;
                    }
                }
                if (match) count++;
            }
        }
    }

    printf("%d\n", count);
    return 0;
}
