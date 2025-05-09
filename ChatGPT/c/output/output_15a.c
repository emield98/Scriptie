// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_ROWS 50
#define MAX_COLS 50

typedef struct {
    int x, y;
    int vx, vy;
} Robot;

void move_robot(Robot *r) {
    r->x = (r->x + r->vx + MAX_COLS) % MAX_COLS;
    r->y = (r->y + r->vy + MAX_ROWS) % MAX_ROWS;
}

int calculate_gps(int x, int y) {
    return 100 * y + x;
}

int main() {
    FILE *f = fopen("input.txt", "r");
    char grid[MAX_ROWS][MAX_COLS];
    Robot robots[100];
    int robot_count = 0;

    // Read initial grid and robots
    for (int i = 0; i < MAX_ROWS; i++) {
        fgets(grid[i], MAX_COLS + 2, f);
        for (int j = 0; j < MAX_COLS; j++) {
            if (grid[i][j] == '@') {
                robots[robot_count].x = j;
                robots[robot_count].y = i;
                robots[robot_count].vx = 0;
                robots[robot_count].vy = 0;
                robot_count++;
            } else if (grid[i][j] == 'O') {
                // Add box initial positions here if necessary
            }
        }
    }

    fclose(f);

    // Read the moves from input
    char moves[10000];
    fgets(moves, sizeof(moves), stdin);  // Reading moves from stdin

    for (int i = 0; moves[i] != '\0'; i++) {
        for (int j = 0; j < robot_count; j++) {
            switch (moves[i]) {
                case '^': robots[j].vy = -1; break;
                case 'v': robots[j].vy = 1; break;
                case '<': robots[j].vx = -1; break;
                case '>': robots[j].vx = 1; break;
            }
            move_robot(&robots[j]);
        }
    }

    // Calculate the GPS coordinates of the boxes
    int sum_gps = 0;
    for (int i = 0; i < robot_count; i++) {
        sum_gps += calculate_gps(robots[i].x, robots[i].y);
    }

    printf("%d\n", sum_gps);
    return 0;
}
