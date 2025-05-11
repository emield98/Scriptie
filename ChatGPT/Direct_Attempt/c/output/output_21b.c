// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_SIZE 5
#define INF 1000000

// Directions for movement: up, down, left, right
int directions[4][2] = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

// Define the keypad layout
char keypad[5][5] = {
    {' ', ' ', '7', '8', '9'},
    {' ', '4', '5', '6', ' '},
    {'1', '2', '3', ' ', ' '},
    {' ', ' ', ' ', '4', 'A'},
    {' ', ' ', ' ', ' ', ' '}
};

typedef struct {
    int x, y, steps;
} Position;

Position queue[1000];
int visited[5][5];

int is_valid(int x, int y) {
    return x >= 0 && x < 5 && y >= 0 && y < 5 && keypad[x][y] != ' ';
}

int bfs(Position start, Position end) {
    memset(visited, 0, sizeof(visited));
    int front = 0, back = 0;
    queue[back++] = start;
    visited[start.x][start.y] = 1;

    while (front < back) {
        Position current = queue[front++];
        if (current.x == end.x && current.y == end.y) {
            return current.steps;
        }

        for (int i = 0; i < 4; i++) {
            int nx = current.x + directions[i][0];
            int ny = current.y + directions[i][1];

            if (is_valid(nx, ny) && !visited[nx][ny]) {
                visited[nx][ny] = 1;
                queue[back++] = (Position){nx, ny, current.steps + 1};
            }
        }
    }

    return INF;
}

int main() {
    FILE *file = fopen("input.txt", "r");
    char code[100];
    int total_complexity = 0;

    while (fgets(code, sizeof(code), file)) {
        int length = strlen(code);
        if (code[length - 1] == '\n') {
            code[length - 1] = '\0';
        }

        int start_x = 0, start_y = 0;
        int end_x = 0, end_y = 0;

        // Finding start 'S' and end 'E'
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (keypad[i][j] == 'S') {
                    start_x = i;
                    start_y = j;
                }
                if (keypad[i][j] == 'E') {
                    end_x = i;
                    end_y = j;
                }
            }
        }

        // Perform BFS to get the shortest path length
        Position start = {start_x, start_y, 0};
        Position end = {end_x, end_y, 0};
        int min_steps = bfs(start, end);

        int num = atoi(code);  // Convert the code to number
        total_complexity += min_steps * num;
    }

    printf("%d\n", total_complexity);
    fclose(file);
    return 0;
}
