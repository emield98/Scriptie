// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <limits.h>

#define MAX_ROWS 50
#define MAX_COLS 50

typedef struct {
    int x, y;
    int cost;
    int direction;  // 0: East, 1: North, 2: West, 3: South
} State;

int directions[4][2] = {
    {1, 0},  // East
    {0, -1}, // North
    {-1, 0}, // West
    {0, 1}   // South
};

int turn_costs[4][4] = {
    {0, 1000, 2000, 1000},
    {1000, 0, 1000, 2000},
    {2000, 1000, 0, 1000},
    {1000, 2000, 1000, 0}
};

int maze[MAX_ROWS][MAX_COLS];
State queue[MAX_ROWS * MAX_COLS * 4];
int visited[MAX_ROWS][MAX_COLS][4];
int rows, cols;

int is_valid(int x, int y) {
    return x >= 0 && x < cols && y >= 0 && y < rows && maze[y][x] != '#';
}

void bfs(int start_x, int start_y, int end_x, int end_y) {
    int head = 0, tail = 0;
    memset(visited, -1, sizeof(visited));
    queue[tail++] = (State){start_x, start_y, 0, 0};  // Starting position, facing East
    visited[start_y][start_x][0] = 0;

    while (head < tail) {
        State state = queue[head++];
        int x = state.x, y = state.y, cost = state.cost, dir = state.direction;

        // If we reached the end
        if (x == end_x && y == end_y) {
            printf("%d\n", cost);
            return;
        }

        // Move forward in the current direction
        int nx = x + directions[dir][0], ny = y + directions[dir][1];
        if (is_valid(nx, ny) && visited[ny][nx][dir] == -1) {
            visited[ny][nx][dir] = cost + 1;
            queue[tail++] = (State){nx, ny, cost + 1, dir};
        }

        // Try all possible rotations (turning 90 degrees)
        for (int i = 0; i < 4; i++) {
            if (i != dir) {  // Skip the current direction
                int turn_cost = cost + 1000;  // Cost to turn 90 degrees
                int tx = x + directions[i][0], ty = y + directions[i][1];
                if (is_valid(tx, ty) && visited[ty][tx][i] == -1) {
                    visited[ty][tx][i] = turn_cost;
                    queue[tail++] = (State){tx, ty, turn_cost, i};
                }
            }
        }
    }
}

int main() {
    FILE *f = fopen("input.txt", "r");
    char line[MAX_COLS + 2];
    rows = 0;
    while (fgets(line, sizeof(line), f)) {
        cols = strlen(line) - 1;
        for (int i = 0; i < cols; i++) {
            if (line[i] == '#') {
                maze[rows][i] = -1; // Wall
            } else {
                maze[rows][i] = 0; // Free space
            }
        }
        rows++;
    }
    fclose(f);

    // Find Start and End positions
    int start_x, start_y, end_x, end_y;
    for (int y = 0; y < rows; y++) {
        for (int x = 0; x < cols; x++) {
            if (maze[y][x] == 0) {
                if (line[x] == 'S') {
                    start_x = x;
                    start_y = y;
                } else if (line[x] == 'E') {
                    end_x = x;
                    end_y = y;
                }
            }
        }
    }

    bfs(start_x, start_y, end_x, end_y);

    return 0;
}
