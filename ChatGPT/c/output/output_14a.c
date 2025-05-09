// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define WIDTH 101
#define HEIGHT 103

typedef struct {
    int x, y;
    int vx, vy;
} Robot;

void move_robot(Robot *r) {
    r->x = (r->x + r->vx + WIDTH) % WIDTH;
    r->y = (r->y + r->vy + HEIGHT) % HEIGHT;
}

int main() {
    FILE *f = fopen("input.txt", "r");
    Robot robots[100];
    int robot_count = 0;

    // Read robot positions and velocities
    while (fscanf(f, "p=%d,%d v=%d,%d\n", &robots[robot_count].x, &robots[robot_count].y,
                   &robots[robot_count].vx, &robots[robot_count].vy) != EOF) {
        robot_count++;
    }
    fclose(f);

    // Simulate for 100 seconds
    for (int t = 0; t < 100; t++) {
        for (int i = 0; i < robot_count; i++) {
            move_robot(&robots[i]);
        }
    }

    // Create a grid to count robots in each position
    int grid[HEIGHT][WIDTH];
    memset(grid, 0, sizeof(grid));

    // Count the robots in the grid
    for (int i = 0; i < robot_count; i++) {
        grid[robots[i].y][robots[i].x]++;
    }

    // Count robots in each quadrant
    int quad[4] = {0}; // top-left, top-right, bottom-left, bottom-right
    int mid_x = WIDTH / 2, mid_y = HEIGHT / 2;

    for (int y = 0; y < HEIGHT; y++) {
        for (int x = 0; x < WIDTH; x++) {
            if (grid[y][x] > 0) {
                if (x < mid_x && y < mid_y) {
                    quad[0]++;  // top-left
                } else if (x >= mid_x && y < mid_y) {
                    quad[1]++;  // top-right
                } else if (x < mid_x && y >= mid_y) {
                    quad[2]++;  // bottom-left
                } else {
                    quad[3]++;  // bottom-right
                }
            }
        }
    }

    // Calculate the safety factor
    int safety_factor = quad[0] * quad[1] * quad[2] * quad[3];
    printf("%d\n", safety_factor);

    return 0;
}
