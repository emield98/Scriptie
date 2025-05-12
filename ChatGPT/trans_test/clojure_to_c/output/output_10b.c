#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_ROWS 100
#define MAX_COLS 100

int grid[MAX_ROWS][MAX_COLS];
int memo[MAX_ROWS][MAX_COLS][10];
int rows = 0, cols = 0;

int in_bounds(int r, int c) {
    return r >= 0 && r < rows && c >= 0 && c < cols;
}

int dfs(int r, int c, int h) {
    if (h == 9) return 1;
    if (memo[r][c][h] != -1) return memo[r][c][h];

    int total = 0;
    int dirs[4][2] = {{-1,0},{1,0},{0,-1},{0,1}};
    for (int d = 0; d < 4; d++) {
        int nr = r + dirs[d][0];
        int nc = c + dirs[d][1];
        if (in_bounds(nr, nc) && grid[nr][nc] == h + 1) {
            total += dfs(nr, nc, h + 1);
        }
    }

    memo[r][c][h] = total;
    return total;
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    char line[MAX_COLS + 2];
    while (fgets(line, sizeof(line), file)) {
        size_t len = strcspn(line, "\r\n");
        for (int i = 0; i < len; i++) {
            grid[rows][i] = line[i] - '0';
        }
        if (cols == 0) cols = len;
        rows++;
    }
    fclose(file);

    memset(memo, -1, sizeof(memo));
    int sum = 0;
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            if (grid[r][c] == 0) {
                sum += dfs(r, c, 0);
            }
        }
    }

    printf("%d\n", sum);
    return 0;
}
