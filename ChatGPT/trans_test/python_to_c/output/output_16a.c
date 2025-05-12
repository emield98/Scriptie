#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_ROWS 1000
#define MAX_COLS 1000
#define HEAP_SIZE (MAX_ROWS * MAX_COLS * 4)
#define INF 1000000000

typedef struct {
    int cost, r, c, dir;
} State;

char grid[MAX_ROWS][MAX_COLS];
int rows = 0, cols = 0;

State heap[HEAP_SIZE];
int heap_size = 0;

int visited[MAX_ROWS][MAX_COLS][4];

int dr[4] = {0, 1, 0, -1};
int dc[4] = {1, 0, -1, 0};

void push(State s) {
    heap[heap_size++] = s;
    int i = heap_size - 1;
    while (i > 0) {
        int p = (i - 1) / 2;
        if (heap[p].cost <= heap[i].cost) break;
        State temp = heap[p]; heap[p] = heap[i]; heap[i] = temp;
        i = p;
    }
}

State pop() {
    State top = heap[0];
    heap[0] = heap[--heap_size];
    int i = 0;
    while (1) {
        int left = 2*i + 1, right = 2*i + 2, smallest = i;
        if (left < heap_size && heap[left].cost < heap[smallest].cost) smallest = left;
        if (right < heap_size && heap[right].cost < heap[smallest].cost) smallest = right;
        if (smallest == i) break;
        State temp = heap[i]; heap[i] = heap[smallest]; heap[smallest] = temp;
        i = smallest;
    }
    return top;
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    char line[MAX_COLS + 2];
    while (fgets(line, sizeof(line), file)) {
        int len = strcspn(line, "\r\n");
        if (len == 0) continue;
        strncpy(grid[rows], line, len);
        cols = len;
        rows++;
    }
    fclose(file);

    int start_r, start_c, end_r, end_c;
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            if (grid[r][c] == 'S') {
                start_r = r;
                start_c = c;
            } else if (grid[r][c] == 'E') {
                end_r = r;
                end_c = c;
            }
        }
    }

    memset(visited, 0, sizeof(visited));
    for (int d = 0; d < 4; d++) {
        push((State){1000, start_r, start_c, d});
    }

    while (heap_size > 0) {
        State s = pop();
        if (visited[s.r][s.c][s.dir]) continue;
        visited[s.r][s.c][s.dir] = 1;
        if (s.r == end_r && s.c == end_c) {
            printf("%d\n", s.cost);
            return 0;
        }
        // Move forward
        int nr = s.r + dr[s.dir];
        int nc = s.c + dc[s.dir];
        if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] != '#') {
            push((State){s.cost + 1, nr, nc, s.dir});
        }
        // Turn left and right
        for (int i = -1; i <= 1; i += 2) {
            int ndir = (s.dir + i + 4) % 4;
            if (!visited[s.r][s.c][ndir]) {
                push((State){s.cost + 1000, s.r, s.c, ndir});
            }
        }
    }

    return 0;
}
