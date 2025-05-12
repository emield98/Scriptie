#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#define MAX_ROWS 1000
#define MAX_COLS 1000
#define MAX_SEEN 1000000

char grid[MAX_ROWS][MAX_COLS];
int rows = 0, cols = 0;

typedef struct {
    int r, c, dir;
} State;

int dr[] = {-1, 0, 1, 0};
int dc[] = {0, 1, 0, -1};

int turn_right(int dir) {
    return (dir + 1) % 4;
}

bool seen[MAX_ROWS][MAX_COLS][4];
bool visited[MAX_ROWS][MAX_COLS];

bool simulate(int sr, int sc, int dir_idx) {
    memset(seen, 0, sizeof(seen));
    memset(visited, 0, sizeof(visited));

    int r = sr, c = sc;
    seen[r][c][dir_idx] = true;
    visited[r][c] = true;

    while (1) {
        int nr = r + dr[dir_idx];
        int nc = c + dc[dir_idx];

        if (nr < 0 || nr >= rows || nc < 0 || nc >= cols)
            return false;

        if (grid[nr][nc] == '#') {
            dir_idx = turn_right(dir_idx);
        } else {
            r = nr;
            c = nc;
            if (seen[r][c][dir_idx])
                return true;
            seen[r][c][dir_idx] = true;
            visited[r][c] = true;
        }
    }
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    char line[MAX_COLS + 2];
    while (fgets(line, sizeof(line), file)) {
        size_t len = strcspn(line, "\r\n");
        if (len == 0) continue;
        strncpy(grid[rows], line, len);
        cols = len;
        rows++;
    }
    fclose(file);

    const char directions[] = "^>v<";
    int start_r = -1, start_c = -1, dir_idx = -1;

    for (int r = 0; r < rows && dir_idx == -1; r++) {
        for (int c = 0; c < cols; c++) {
            for (int d = 0; d < 4; d++) {
                if (grid[r][c] == directions[d]) {
                    start_r = r;
                    start_c = c;
                    dir_idx = d;
                    break;
                }
            }
            if (dir_idx != -1) break;
        }
    }

    simulate(start_r, start_c, dir_idx);

    int count = 0;
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            if ((r != start_r || c != start_c) && grid[r][c] == '.') {
                grid[r][c] = '#';
                if (simulate(start_r, start_c, dir_idx))
                    count++;
                grid[r][c] = '.';
            }
        }
    }

    printf("%d\n", count);
    return 0;
}
