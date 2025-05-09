// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_SIZE 100

char grid[MAX_SIZE][MAX_SIZE];
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

int count_sides(int x, int y, char plant) {
    int sides = 0;

    for (int i = 0; i < 4; i++) {
        int nx = x + directions[i][0];
        int ny = y + directions[i][1];

        if (!is_valid(nx, ny) || grid[nx][ny] != plant) {
            sides++;
        }
    }
    return sides;
}

void dfs(int x, int y, char plant, int *area, int *sides) {
    visited[x][y] = 1;
    (*area)++;
    *sides += count_sides(x, y, plant);

    for (int i = 0; i < 4; i++) {
        int nx = x + directions[i][0];
        int ny = y + directions[i][1];

        if (is_valid(nx, ny) && grid[nx][ny] == plant) {
            dfs(nx, ny, plant, area, sides);
        }
    }
}

int main() {
    FILE *f = fopen("input.txt", "r");
    char line[MAX_SIZE];
    rows = 0;

    while (fgets(line, sizeof(line), f)) {
        cols = strlen(line) - 1;
        for (int j = 0; j < cols; j++) {
            grid[rows][j] = line[j];
        }
        rows++;
    }
    fclose(f);

    memset(visited, 0, sizeof(visited));
    int total_price = 0;

    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            if (!visited[i][j]) {
                int area = 0;
                int sides = 0;
                dfs(i, j, grid[i][j], &area, &sides);
                total_price += area * sides;
            }
        }
    }

    printf("%d\n", total_price);
    return 0;
}
