#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_ROWS 100
#define MAX_COLS 100

int is_valid_mas(char a, char b, char c) {
    return (a == 'M' && b == 'A' && c == 'S') ||
           (a == 'S' && b == 'A' && c == 'M');
}

int valid_xmas(char grid[MAX_ROWS][MAX_COLS], int r, int c) {
    char nw1 = grid[r - 1][c - 1];
    char nw2 = grid[r][c];
    char nw3 = grid[r + 1][c + 1];
    char ne1 = grid[r - 1][c + 1];
    char ne2 = grid[r][c];
    char ne3 = grid[r + 1][c - 1];
    return is_valid_mas(nw1, nw2, nw3) && is_valid_mas(ne1, ne2, ne3);
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
    for (int r = 1; r < rows - 1; r++) {
        for (int c = 1; c < cols - 1; c++) {
            if (valid_xmas(grid, r, c)) {
                count++;
            }
        }
    }

    printf("%d\n", count);
    return 0;
}
