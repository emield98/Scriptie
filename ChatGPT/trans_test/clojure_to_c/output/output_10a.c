#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_ROWS 100
#define MAX_COLS 100
#define MAX_QUEUE (MAX_ROWS * MAX_COLS)

typedef struct {
    int r, c;
    int val;
} Cell;

int grid[MAX_ROWS][MAX_COLS];
int visited[MAX_ROWS][MAX_COLS];
int rows = 0, cols = 0;

int in_bounds(int r, int c) {
    return r >= 0 && r < rows && c >= 0 && c < cols;
}

int bfs(int sr, int sc) {
    memset(visited, 0, sizeof(visited));
    int count = 0;

    int qr[MAX_QUEUE], qc[MAX_QUEUE], qh = 0, qt = 0;
    qr[qt] = sr;
    qc[qt++] = sc;
    visited[sr][sc] = 1;

    while (qh < qt) {
        int r = qr[qh];
        int c = qc[qh++];
        int val = grid[r][c];
        if (val == 9) count++;

        int dirs[4][2] = {{-1,0},{1,0},{0,-1},{0,1}};
        for (int d = 0; d < 4; d++) {
            int nr = r + dirs[d][0];
            int nc = c + dirs[d][1];
            if (in_bounds(nr, nc) && !visited[nr][nc] && grid[nr][nc] == val + 1) {
                visited[nr][nc] = 1;
                qr[qt] = nr;
                qc[qt++] = nc;
            }
        }
    }

    return count;
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

    int sum = 0;
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            if (grid[r][c] == 0) {
                sum += bfs(r, c);
            }
        }
    }

    printf("%d\n", sum);
    return 0;
}
