#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#define MAX_ROWS 1000
#define MAX_COLS 1000
#define QUEUE_SIZE (MAX_ROWS * MAX_COLS)

typedef struct {
    int x, y, h;
} Node;

int grid[MAX_ROWS][MAX_COLS];
bool visited[MAX_ROWS][MAX_COLS];
int rows = 0, cols = 0;

int bfs(int r, int c) {
    memset(visited, 0, sizeof(visited));
    Node queue[QUEUE_SIZE];
    int front = 0, back = 0;
    bool seen_nines[MAX_ROWS][MAX_COLS];
    memset(seen_nines, 0, sizeof(seen_nines));

    queue[back++] = (Node){r, c, 0};
    visited[r][c] = true;
    int count_nines = 0;

    while (front < back) {
        Node current = queue[front++];
        int x = current.x;
        int y = current.y;
        int h = current.h;

        if (grid[x][y] == 9) {
            if (!seen_nines[x][y]) {
                seen_nines[x][y] = true;
                count_nines++;
            }
            continue;
        }

        int directions[4][2] = {{-1,0},{1,0},{0,-1},{0,1}};
        for (int d = 0; d < 4; d++) {
            int nx = x + directions[d][0];
            int ny = y + directions[d][1];
            if (nx >= 0 && nx < rows && ny >= 0 && ny < cols &&
                !visited[nx][ny] && grid[nx][ny] == grid[x][y] + 1) {
                visited[nx][ny] = true;
                queue[back++] = (Node){nx, ny, grid[nx][ny]};
            }
        }
    }

    return count_nines;
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

    int total_score = 0;
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            if (grid[r][c] == 0) {
                total_score += bfs(r, c);
            }
        }
    }

    printf("%d\n", total_score);
    return 0;
}
