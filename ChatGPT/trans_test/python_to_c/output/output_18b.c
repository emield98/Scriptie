#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>

#define MAX_COORD 71
#define QUEUE_SIZE (MAX_COORD * MAX_COORD)

typedef struct {
    int x, y;
} Point;

bool blocked[MAX_COORD][MAX_COORD];
bool visited[MAX_COORD][MAX_COORD];
int directions[4][2] = {{-1,0},{1,0},{0,-1},{0,1}};

bool is_reachable() {
    if (blocked[0][0] || blocked[MAX_COORD-1][MAX_COORD-1]) return false;

    memset(visited, 0, sizeof(visited));
    Point queue[QUEUE_SIZE];
    int front = 0, back = 0;

    queue[back++] = (Point){0, 0};
    visited[0][0] = true;

    while (front < back) {
        Point cur = queue[front++];
        if (cur.x == MAX_COORD - 1 && cur.y == MAX_COORD - 1) {
            return true;
        }
        for (int d = 0; d < 4; d++) {
            int nx = cur.x + directions[d][0];
            int ny = cur.y + directions[d][1];
            if (nx >= 0 && nx < MAX_COORD && ny >= 0 && ny < MAX_COORD &&
                !blocked[nx][ny] && !visited[nx][ny]) {
                visited[nx][ny] = true;
                queue[back++] = (Point){nx, ny};
            }
        }
    }
    return false;
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    memset(blocked, 0, sizeof(blocked));

    char line[64];
    while (fgets(line, sizeof(line), file)) {
        if (line[0] == '\n' || line[0] == '\r') continue;
        int x, y;
        if (sscanf(line, "%d,%d", &x, &y) == 2) {
            if (x >= 0 && x < MAX_COORD && y >= 0 && y < MAX_COORD) {
                blocked[x][y] = true;
                if (!is_reachable()) {
                    printf("%d,%d\n", x, y);
                    fclose(file);
                    return 0;
                }
            }
        }
    }

    fclose(file);
    return 0;
}
