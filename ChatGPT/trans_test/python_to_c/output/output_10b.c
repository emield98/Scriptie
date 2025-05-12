#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_ROWS 1000
#define MAX_COLS 1000

int grid[MAX_ROWS][MAX_COLS];
int memo[MAX_ROWS][MAX_COLS];
int rows = 0, cols = 0;

int dfs(int r, int c) {
    if (memo[r][c] != -1) return memo[r][c];
    if (grid[r][c] == 9) return memo[r][c] = 1;

    int count = 0;
    int directions[4][2] = {{-1,0},{1,0},{0,-1},{0,1}};
    for (int d = 0; d < 4; d++) {
        int nr = r + directions[d][0];
        int nc = c + directions[d][1];
        if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
            if (grid[nr][nc] == grid[r][c] + 1) {
                count += dfs(nr, nc);
            }
        }
    }

    return memo[r][c] = count;
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    char line[MAX_COLS + 2];
    while (fgets(line, sizeof(line), file)) {
        int len = strcspn(line, "\r\n");
        if (len == 0) continue;
        for (int i = 0; i < len; i++) {
            grid[rows][i] = line[i] - '0';
        }
        cols = len;
        rows++;
    }
    fclose(file);

    memset(memo, -1, sizeof(memo));

    int total_rating = 0;
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            if (grid[r][c] == 0) {
                total_rating += dfs(r, c);
            }
        }
    }

    printf("%d\n", total_rating);
    return 0;
}
