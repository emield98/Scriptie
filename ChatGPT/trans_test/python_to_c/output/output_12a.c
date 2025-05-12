#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#define MAX_ROWS 1000
#define MAX_COLS 1000
#define QUEUE_SIZE (MAX_ROWS * MAX_COLS)

typedef struct {
    int r, c;
} Cell;

char grid[MAX_ROWS][MAX_COLS];
bool visited[MAX_ROWS][MAX_COLS];
bool region_mask[MAX_ROWS][MAX_COLS];
int rows = 0, cols = 0;

Cell queue[QUEUE_SIZE];

int bfs(int sr, int sc, Cell *region) {
    int front = 0, back = 0;
    char plant = grid[sr][sc];
    queue[back++] = (Cell){sr, sc};
    visited[sr][sc] = true;
    region[0] = (Cell){sr, sc};
    int size = 1;

    while (front < back) {
        Cell cur = queue[front++];
        int r = cur.r, c = cur.c;
        int directions[4][2] = {{-1,0},{1,0},{0,-1},{0,1}};
        for (int d = 0; d < 4; d++) {
            int nr = r + directions[d][0];
            int nc = c + directions[d][1];
            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols &&
                !visited[nr][nc] && grid[nr][nc] == plant) {
                visited[nr][nc] = true;
                queue[back++] = (Cell){nr, nc};
                region[size++] = (Cell){nr, nc};
            }
        }
    }
    return size;
}

int calculate_perimeter(Cell *region, int region_size) {
    memset(region_mask, 0, sizeof(region_mask));
    for (int i = 0; i < region_size; i++) {
        region_mask[region[i].r][region[i].c] = true;
    }

    int perimeter = 0;
    for (int i = 0; i < region_size; i++) {
        int r = region[i].r, c = region[i].c;
        int directions[4][2] = {{-1,0},{1,0},{0,-1},{0,1}};
        for (int d = 0; d < 4; d++) {
            int nr = r + directions[d][0];
            int nc = c + directions[d][1];
            if (nr < 0 || nr >= rows || nc < 0 || nc >= cols ||
                !region_mask[nr][nc]) {
                perimeter++;
            }
        }
    }
    return perimeter;
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    char line[MAX_COLS + 2];
    while (fgets(line, sizeof(line), file)) {
        int len = strcspn(line, "\r\n");
        if (len == 0) continue;
        strncpy(grid[rows], line, len);
        cols = len;
        rows++;
    }
    fclose(file);

    memset(visited, 0, sizeof(visited));

    long long total_price = 0;
    Cell region[QUEUE_SIZE];

    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            if (!visited[r][c]) {
                int region_size = bfs(r, c, region);
                int perimeter = calculate_perimeter(region, region_size);
                total_price += (long long)region_size * perimeter;
            }
        }
    }

    printf("%lld\n", total_price);
    return 0;
}
