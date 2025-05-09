// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_SIZE 100

int map[MAX_SIZE][MAX_SIZE];
int visited[MAX_SIZE][MAX_SIZE];
int rows, cols;

int directions[4][2] = {
    {-1, 0},  // up
    {1, 0},   // down
    {0, -1},  // left
    {0, 1}    // right
};

int is_valid(int x, int y) {
    return x >= 0 && x < rows && y >= 0 && y < cols && !visited[x][y];
}

int dfs(int x, int y) {
    visited[x][y] = 1;
    int count = 1;

    for (int i = 0; i < 4; i++) {
        int nx = x + directions[i][0];
        int ny = y + directions[i][1];

        if (is_valid(nx, ny) && map[nx][ny] == map[x][y] + 1) {
            count += dfs(nx, ny);
        }
    }
    return count;
}

int main() {
    FILE *f = fopen("input.txt", "r");
    char line[MAX_SIZE];
    rows = 0;

    while (fgets(line, sizeof(line), f)) {
        cols = strlen(line) - 1;
        for (int j = 0; j < cols; j++) {
            map[rows][j] = line[j] - '0';
        }
        rows++;
    }
    fclose(f);

    memset(visited, 0, sizeof(visited));
    int total_rating = 0;

    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            if (map[i][j] == 0 && !visited[i][j]) {
                total_rating += dfs(i, j);
            }
        }
    }

    printf("%d\n", total_rating);
    return 0;
}
