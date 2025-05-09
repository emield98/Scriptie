// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_SIZE 100
#define MAX_CHEAT_TIME 20
#define INF 9999999

int n, m;
char grid[MAX_SIZE][MAX_SIZE];
int dist[MAX_SIZE][MAX_SIZE][MAX_CHEAT_TIME + 1];

int directions[4][2] = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};  // Right, Down, Left, Up

// Check if the cell is within bounds
int in_bounds(int x, int y) {
    return x >= 0 && x < n && y >= 0 && y < m;
}

// BFS with the option to cheat up to 20 picoseconds
void bfs(int startX, int startY, int endX, int endY) {
    // Initialize the distances: dist[x][y][cheat_used] for each cheat time used
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            for (int k = 0; k <= MAX_CHEAT_TIME; k++) {
                dist[i][j][k] = INF;
            }
        }
    }

    dist[startX][startY][0] = 0;

    int queue[MAX_SIZE * MAX_SIZE * (MAX_CHEAT_TIME + 1)][4];
    int front = 0, back = 0;
    queue[back][0] = startX;
    queue[back][1] = startY;
    queue[back][2] = 0;  // Cheat time used
    queue[back++][3] = 0;  // Step count

    while (front < back) {
        int x = queue[front][0];
        int y = queue[front][1];
        int cheat_used = queue[front][2];
        int steps = queue[front++][3];

        // Try moving in all 4 directions
        for (int i = 0; i < 4; i++) {
            int nx = x + directions[i][0];
            int ny = y + directions[i][1];
            int new_cheat_used = cheat_used;

            if (in_bounds(nx, ny) && grid[nx][ny] != '#') {
                if (grid[nx][ny] == '.' || grid[nx][ny] == 'S' || grid[nx][ny] == 'E') {
                    // Moving to an empty space or start or end without cheating
                    if (dist[nx][ny][new_cheat_used] > steps + 1) {
                        dist[nx][ny][new_cheat_used] = steps + 1;
                        queue[back][0] = nx;
                        queue[back][1] = ny;
                        queue[back][2] = new_cheat_used;
                        queue[back++][3] = steps + 1;
                    }
                }

                if (grid[nx][ny] == '#') {
                    // Use cheat if not used yet
                    if (cheat_used < MAX_CHEAT_TIME && dist[nx][ny][cheat_used + 1] > steps + 1) {
                        dist[nx][ny][cheat_used + 1] = steps + 1;
                        queue[back][0] = nx;
                        queue[back][1] = ny;
                        queue[back][2] = cheat_used + 1;
                        queue[back++][3] = steps + 1;
                    }
                }
            }
        }
    }
}

int main() {
    FILE *file = fopen("input.txt", "r");

    // Read input
    char line[MAX_SIZE];
    n = 0;
    while (fgets(line, sizeof(line), file)) {
        m = strlen(line) - 1;
        strncpy(grid[n], line, m);
        grid[n][m] = '\0';
        n++;
    }
    fclose(file);

    int startX = -1, startY = -1, endX = -1, endY = -1;

    // Locate 'S' and 'E' positions
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            if (grid[i][j] == 'S') {
                startX = i;
                startY = j;
            }
            if (grid[i][j] == 'E') {
                endX = i;
                endY = j;
            }
        }
    }

    // Run BFS to calculate the shortest path
    bfs(startX, startY, endX, endY);

    int result = 0;
    // Count the cheats that save at least 100 picoseconds
    for (int i = 0; i <= MAX_CHEAT_TIME; i++) {
        if (dist[endX][endY][i] != INF) {
            result++;
        }
    }

    // Output the result
    printf("%d\n", result);

    return 0;
}
