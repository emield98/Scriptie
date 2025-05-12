#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_ROWS 100
#define MAX_COLS 100

typedef struct {
    int r, c;
} Pos;

int dr[] = {-1, 0, 1, 0}; // ^, >, v, <
int dc[] = {0, 1, 0, -1};
char dirs[] = {'^', '>', 'v', '<'};

int dir_index(char ch) {
    for (int i = 0; i < 4; i++) {
        if (dirs[i] == ch) return i;
    }
    return -1;
}

char grid[MAX_ROWS][MAX_COLS];
int visited[MAX_ROWS][MAX_COLS];
int rows = 0, cols = 0;

int in_bounds(int r, int c) {
    return r >= 0 && r < rows && c >= 0 && c < cols;
}

int blocked(int r, int c) {
    return !in_bounds(r, c) || grid[r][c] == '#';
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    char line[MAX_COLS + 2];
    while (fgets(line, sizeof(line), file)) {
        size_t len = strcspn(line, "\r\n");
        strncpy(grid[rows], line, len);
        grid[rows][len] = '\0';
        if (cols == 0) cols = len;
        rows++;
    }
    fclose(file);

    int start_r = -1, start_c = -1, dir = -1;
    for (int r = 0; r < rows && dir == -1; r++) {
        for (int c = 0; c < cols; c++) {
            dir = dir_index(grid[r][c]);
            if (dir != -1) {
                start_r = r;
                start_c = c;
                break;
            }
        }
    }

    int r = start_r, c = start_c;
    visited[r][c] = 1;
    int count = 1;

    while (1) {
        int nr = r + dr[dir];
        int nc = c + dc[dir];

        if (!in_bounds(nr, nc)) break;

        if (blocked(nr, nc)) {
            dir = (dir + 1) % 4;
            continue;
        }

        r = nr;
        c = nc;
        if (!visited[r][c]) {
            visited[r][c] = 1;
            count++;
        }
    }

    printf("%d\n", count);
    return 0;
}
