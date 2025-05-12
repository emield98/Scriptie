#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#define MAX_ROWS 1000
#define MAX_COLS 1000

char grid[MAX_ROWS][MAX_COLS];
bool visited[MAX_ROWS][MAX_COLS];

int dir_map[256][2];
char turn_right[256];

int rows = 0, cols = 0;

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

    dir_map['^'][0] = -1; dir_map['^'][1] = 0;
    dir_map['>'][0] = 0;  dir_map['>'][1] = 1;
    dir_map['v'][0] = 1;  dir_map['v'][1] = 0;
    dir_map['<'][0] = 0;  dir_map['<'][1] = -1;

    turn_right['^'] = '>';
    turn_right['>'] = 'v';
    turn_right['v'] = '<';
    turn_right['<'] = '^';

    int start_r = -1, start_c = -1;
    char dir = 0;

    for (int r = 0; r < rows && dir == 0; r++) {
        for (int c = 0; c < cols; c++) {
            if (grid[r][c] == '^' || grid[r][c] == '>' || grid[r][c] == 'v' || grid[r][c] == '<') {
                start_r = r;
                start_c = c;
                dir = grid[r][c];
                break;
            }
        }
    }

    int r = start_r, c = start_c;
    visited[r][c] = true;
    int visited_count = 1;

    while (1) {
        int dr = dir_map[dir][0];
        int dc = dir_map[dir][1];
        int nr = r + dr;
        int nc = c + dc;

        if (0 <= nr && nr < rows && 0 <= nc && nc < cols && grid[nr][nc] != '#') {
            r = nr;
            c = nc;
            if (!visited[r][c]) {
                visited[r][c] = true;
                visited_count++;
            }
        } else {
            dir = turn_right[dir];
        }

        dr = dir_map[dir][0];
        dc = dir_map[dir][1];
        int next_r = r + dr;
        int next_c = c + dc;
        if (!(0 <= next_r && next_r < rows && 0 <= next_c && next_c < cols)) {
            break;
        }
    }

    printf("%d\n", visited_count);
    return 0;
}
