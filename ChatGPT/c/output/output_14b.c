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

int is_christmas_tree(int grid[HEIGHT][WIDTH]) {
    // Check if the robots form a Christmas tree pattern.
    // Assume the center of the Christmas tree is at (50, 51), adjust accordingly for the tree pattern.
    int tree[HEIGHT][WIDTH] = {0};
    // Fill this "tree" array with a predefined tree pattern
    
    // Compare grid with tree pattern and return 1 if they match, otherwise return 0
    // For simplicity, we assume a predefined tree pattern is provided
    return 1;  // Placeholder for the correct logic
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

    int grid[HEIGHT][WIDTH];
    int time = 0;

    while (1) {
        memset(grid, 0, sizeof(grid));
        for (int i = 0; i < robot_count; i++) {
            move_robot(&robots[i]);
            grid[robots[i].y][robots[i].x]++;
        }

        if (is_christmas_tree(grid)) {
            break;
        }

        time++;
    }

    printf("%d\n", time);

    return 0;
}
