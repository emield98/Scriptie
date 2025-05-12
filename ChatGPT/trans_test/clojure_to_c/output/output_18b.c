#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_GRID 71
#define MAX_COORDS 5000
#define INF 1000000

typedef struct {
    int x, y;
    int cost;
} Node;

int dx[] = {1, -1, 0, 0};
int dy[] = {0, 0, 1, -1};

int in_bounds(int x, int y) {
    return x >= 0 && x < MAX_GRID && y >= 0 && y < MAX_GRID;
}

int bfs(int sx, int sy, int gx, int gy, int blocked[MAX_GRID][MAX_GRID]) {
    int dist[MAX_GRID][MAX_GRID];
    int visited[MAX_GRID][MAX_GRID] = {0};

    for (int i = 0; i < MAX_GRID; i++)
        for (int j = 0; j < MAX_GRID; j++)
            dist[i][j] = INF;

    Node queue[MAX_GRID * MAX_GRID];
    int front = 0, back = 0;

    dist[sx][sy] = 0;
    queue[back++] = (Node){sx, sy, 0};

    while (front < back) {
        Node curr = queue[front++];
        if (visited[curr.x][curr.y]) continue;
        visited[curr.x][curr.y] = 1;

        if (curr.x == gx && curr.y == gy) {
            return 1;
        }

        for (int d = 0; d < 4; d++) {
            int nx = curr.x + dx[d];
            int ny = curr.y + dy[d];
            if (in_bounds(nx, ny) && !blocked[nx][ny] && dist[nx][ny] > curr.cost + 1) {
                dist[nx][ny] = curr.cost + 1;
                queue[back++] = (Node){nx, ny, curr.cost + 1};
            }
        }
    }

    return 0;
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    int coords[MAX_COORDS][2];
    int coord_count = 0;
    char line[32];

    while (fgets(line, sizeof(line), file)) {
        int x, y;
        if (sscanf(line, "%d,%d", &x, &y) == 2) {
            coords[coord_count][0] = x;
            coords[coord_count][1] = y;
            coord_count++;
        }
    }
    fclose(file);

    int blocked[MAX_GRID][MAX_GRID] = {0};

    for (int i = 0; i < coord_count; i++) {
        int x = coords[i][0];
        int y = coords[i][1];
        if (x >= 0 && x < MAX_GRID && y >= 0 && y < MAX_GRID)
            blocked[x][y] = 1;

        if (!bfs(0, 0, 70, 70, blocked)) {
            printf("%d,%d\n", x, y);
            return 0;
        }
    }

    return 0;
}
