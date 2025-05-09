// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_SIZE 71

int memory[MAX_SIZE][MAX_SIZE];
int visited[MAX_SIZE][MAX_SIZE];

typedef struct {
    int x, y;
} Point;

int directions[4][2] = {
    {1, 0},  // Right
    {-1, 0}, // Left
    {0, 1},  // Down
    {0, -1}  // Up
};

int is_valid(int x, int y) {
    return x >= 0 && x < MAX_SIZE && y >= 0 && y < MAX_SIZE && memory[y][x] != 1;
}

int bfs(Point start, Point end) {
    Point queue[MAX_SIZE * MAX_SIZE];
    int front = 0, rear = 0;
    queue[rear++] = start;
    visited[start.y][start.x] = 1;

    while (front < rear) {
        Point p = queue[front++];
        int x = p.x, y = p.y;

        if (x == end.x && y == end.y) {
            return 1;
        }

        for (int i = 0; i < 4; i++) {
            int nx = x + directions[i][0], ny = y + directions[i][1];
            if (is_valid(nx, ny) && !visited[ny][nx]) {
                visited[ny][nx] = 1;
                queue[rear++] = (Point){nx, ny};
            }
        }
    }

    return 0;
}

int main() {
    FILE *f = fopen("input.txt", "r");
    char line[MAX_SIZE];
    Point start = {0, 0}, end = {MAX_SIZE - 1, MAX_SIZE - 1};
    int bytes[1024][2];
    int byte_count = 0;

    // Initialize memory grid
    for (int i = 0; i < MAX_SIZE; i++) {
        for (int j = 0; j < MAX_SIZE; j++) {
            memory[i][j] = 0;
        }
    }

    // Read the input
    while (fgets(line, sizeof(line), f)) {
        int x, y;
        sscanf(line, "%d,%d", &x, &y);
        bytes[byte_count][0] = x;
        bytes[byte_count][1] = y;
        byte_count++;
    }
    fclose(f);

    // Simulate the falling bytes and check when the path is blocked
    for (int i = 0; i < byte_count; i++) {
        memory[bytes[i][1]][bytes[i][0]] = 1;

        memset(visited, 0, sizeof(visited));
        if (!bfs(start, end)) {
            printf("%d,%d\n", bytes[i][0], bytes[i][1]);
            break;
        }
    }

    return 0;
}
