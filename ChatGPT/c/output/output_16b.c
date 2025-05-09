// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_ROWS 50
#define MAX_COLS 50

typedef struct {
    int x, y;
    int direction;  // 0: East, 1: North, 2: West, 3: South
} Position;

int directions[4][2] = {
    {1, 0},  // East
    {0, -1}, // North
    {-1, 0}, // West
    {0, 1}   // South
};

char maze[MAX_ROWS][MAX_COLS];
int visited[MAX_ROWS][MAX_COLS];

int is_valid(int x, int y) {
    return x >= 0 && x < MAX_COLS && y >= 0 && y < MAX_ROWS && maze[y][x] != '#';
}

void bfs(int start_x, int start_y) {
    Position queue[MAX_ROWS * MAX_COLS];
    int front = 0, rear = 0;
    queue[rear++] = (Position){start_x, start_y, -1};
    visited[start_y][start_x] = 1;

    while (front < rear) {
        Position p = queue[front++];
        int x = p.x, y = p.y;

        for (int i = 0; i < 4; i++) {
            int nx = x + directions[i][0], ny = y + directions[i][1];
            if (is_valid(nx, ny) && !visited[ny][nx]) {
                visited[ny][nx] = 1;
                queue[rear++] = (Position){nx, ny, i};
            }
        }
    }
}

int main() {
    FILE *f = fopen("input.txt", "r");
    char line[MAX_COLS + 2];
    int start_x, start_y, end_x, end_y;

    int rows = 0;
    while (fgets(line, sizeof(line), f)) {
        for (int i = 0; i < strlen(line); i++) {
            maze[rows][i] = line[i];
            if (line[i] == 'S') {
                start_x = i;
                start_y = rows;
            }
            if (line[i] == 'E') {
                end_x = i;
                end_y = rows;
            }
        }
        rows++;
    }
    fclose(f);

    memset(visited, 0, sizeof(visited));
    bfs(start_x, start_y);
    bfs(end_x, end_y);

    int count = 0;
    for (int y = 0; y < rows; y++) {
        for (int x = 0; x < MAX_COLS; x++) {
            if (visited[y][x] && maze[y][x] != '#') {
                count++;
            }
        }
    }

    printf("%d\n", count);

    return 0;
}
