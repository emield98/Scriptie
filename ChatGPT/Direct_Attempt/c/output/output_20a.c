// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_SIZE 100
#define INF 9999999

int n, m;
char grid[MAX_SIZE][MAX_SIZE];
int dist[MAX_SIZE][MAX_SIZE][2];

int directions[4][2] = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};  // Right, Down, Left, Up

// Check if the cell is within bounds
int in_bounds(int x, int y) {
    return x >= 0 && x < n && y >= 0 && y < m;
}

// BFS with the option to cheat
void bfs(int startX, int startY, int endX, int endY) {
    // Distances: dist[x][y][0] for no cheat, dist[x][y][1] for with cheat
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            dist[i][j][0] = dist[i][j][1] = INF;
        }
    }

    dist[startX][startY][0] = 0;

    int queue[MAX_SIZE * MAX_SIZE][3];
    int front = 0, back = 0;
    queue[back][0] = startX;
    queue[back][1] = startY;
    queue[back++][2] = 0;  // 0 means no cheat used yet

    while (front < back) {
        int x = queue[front][0];
        int y = queue[front][1];
        int cheat_used = queue[front++][2];

        for (int i = 0; i < 4; i++) {
            int nx = x + directions[i][0];
            int ny = y + directions[i][1];
            int new_cheat_used = cheat_used;

            if (in_bounds(nx, ny) && grid[nx][ny] != '#') {
                if (grid[nx][ny] == '.' || grid[nx][ny] == 'S' || grid[nx][ny] == 'E') {
                    if (dist[nx][ny][new_cheat_used] > dist[x][y][cheat_used] + 1) {
                        dist[nx][ny][new_cheat_used] = dist[x][y][cheat_used] + 1;
                        queue[back][0] = nx;
                        queue[back][1] = ny;
                        queue[back++][2] = new_cheat_used;
                    }
                }

                if (grid[nx][ny] == '#') {
                    // Use cheat if not used yet
                    if (cheat_used == 0 && dist[nx][ny][1] > dist[x][y][0] + 2) {
                        dist[nx][ny][1] = dist[x][y][0] + 2;
                        queue[back][0] = nx;
                        queue[back][1] = ny;
                        queue[back++][2] = 1;  // Mark as using cheat
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

    // Output the result
    if (dist[endX][endY][0] == INF) {
        printf("No path found\n");
    } else {
        printf("Shortest path: %d\n", dist[endX][endY][0]);
    }

    return 0;
}
