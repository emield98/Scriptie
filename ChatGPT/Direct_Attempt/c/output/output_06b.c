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

int is_valid_position(int x, int y) {
    return x >= 0 && y >= 0 && grid[x][y] != '#';
}

int simulate_guard(int start_x, int start_y, int dir, int max_x, int max_y) {
    int x = start_x, y = start_y;
    int visited_count = 0;
    int path[MAX_SIZE][MAX_SIZE] = {0};

    while (x >= 0 && y >= 0 && x < max_x && y < max_y) {
        if (path[x][y]) {
            return 1; // Loop detected
        }
        path[x][y] = 1;
        visited_count++;

        int next_x = x + directions[dir][0];
        int next_y = y + directions[dir][1];

        if (!is_valid_position(next_x, next_y)) {
            dir = (dir + 1) % 4; // Turn right
        } else {
            x = next_x;
            y = next_y;
        }
    }
    return 0; // No loop detected
}

int main() {
    FILE *f = fopen("input.txt", "r");
    char line[MAX_SIZE];
    int start_x = 0, start_y = 0, dir = 0;

    // Read the grid
    for (int i = 0; fgets(line, sizeof(line), f); i++) {
        strcpy(grid[i], line);
        for (int j = 0; j < strlen(line); j++) {
            if (line[j] == '^') {
                start_x = i;
                start_y = j;
                dir = 0; // Start facing up
                grid[i][j] = '.';
            }
        }
    }

    fclose(f);

    int possible_positions = 0;
    for (int i = 0; i < MAX_SIZE; i++) {
        for (int j = 0; j < MAX_SIZE; j++) {
            if (grid[i][j] == '.' && (i != start_x || j != start_y)) {
                grid[i][j] = '#'; // Place obstruction
                if (simulate_guard(start_x, start_y, dir, MAX_SIZE, MAX_SIZE)) {
                    possible_positions++;
                }
                grid[i][j] = '.'; // Remove obstruction
            }
        }
    }

    printf("%d\n", possible_positions);
    return 0;
}
