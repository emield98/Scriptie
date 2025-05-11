// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <string.h>

#define MAX_SIZE 1000

int directions[4][2] = {
    {-1, 0}, // up
    {0, 1},  // right
    {1, 0},  // down
    {0, -1}  // left
};

char grid[MAX_SIZE][MAX_SIZE];
int visited[MAX_SIZE][MAX_SIZE] = {0};

int main() {
    FILE *f = fopen("input.txt", "r");
    char line[MAX_SIZE];
    int x = 0, y = 0, dir = 0;

    // Read the grid
    for (int i = 0; fgets(line, sizeof(line), f); i++) {
        strcpy(grid[i], line);
        for (int j = 0; j < strlen(line); j++) {
            if (line[j] == '^') {
                x = i;
                y = j;
                dir = 0; // Up
                grid[i][j] = '.';
            }
        }
    }

    fclose(f);

    int visited_count = 0;
    while (x >= 0 && x < MAX_SIZE && y >= 0 && y < MAX_SIZE && grid[x][y] != '\0') {
        if (!visited[x][y]) {
            visited[x][y] = 1;
            visited_count++;
        }

        int next_x = x + directions[dir][0];
        int next_y = y + directions[dir][1];

        if (grid[next_x][next_y] == '#') {
            dir = (dir + 1) % 4; // Turn right (clockwise)
        } else {
            x = next_x;
            y = next_y;
        }
    }

    printf("%d\n", visited_count);
    return 0;
}
