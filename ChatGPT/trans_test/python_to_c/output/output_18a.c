#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#define MAX_COORD 71
#define MAX_BLOCKED 1024
#define QUEUE_SIZE (MAX_COORD * MAX_COORD)

typedef struct {
    int x, y, steps;
} Node;

bool blocked[MAX_COORD][MAX_COORD];
bool visited[MAX_COORD][MAX_COORD];

int directions[4][2] = {{-1,0},{1,0},{0,-1},{0,1}};

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    memset(blocked, 0, sizeof(blocked));
    memset(visited, 0, sizeof(visited));

    char line[64];
    int count = 0;
    while (fgets(line, sizeof(line), file) && count < 1024) {
        int x, y;
        if (sscanf(line, "%d,%d", &x, &y) == 2) {
            if (x >= 0 && x < MAX_COORD && y >= 0 && y < MAX_COORD) {
                blocked[x][y] = true;
            }
        }
        count++;
    }
    fclose(file);

    Node queue[QUEUE_SIZE * 2];
    int front = 0, back = 0;

    queue[back++] = (Node){0, 0, 0};
    visited[0][0] = true;

    while (front < back) {
        Node curr = queue[front++];
        if (curr.x == MAX_COORD - 1 && curr.y == MAX_COORD - 1) {
            printf("%d\n", curr.steps);
            return 0;
        }

        for (int d = 0; d < 4; d++) {
            int nx = curr.x + directions[d][0];
            int ny = curr.y + directions[d][1];

            if (nx >= 0 && nx < MAX_COORD && ny >= 0 && ny < MAX_COORD &&
                !blocked[nx][ny] && !visited[nx][ny]) {
                visited[nx][ny] = true;
                queue[back++] = (Node){nx, ny, curr.steps + 1};
            }
        }
    }

    printf("No path found\n");
    return 0;
}
