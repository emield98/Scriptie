// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <string.h>

#define SIZE 10
#define WORD "MAS"
#define WORD_LEN 3

int check_xmas(char grid[SIZE][SIZE], int x, int y, int dx, int dy) {
    int directions[4][2] = {
        {1, 0}, {0, 1}, {-1, 0}, {0, -1} // directions for MAS in four orientations
    };
    for (int i = 0; i < 4; i++) {
        int dx1 = directions[i][0], dy1 = directions[i][1];
        int dx2 = -dx1, dy2 = -dy1;
        // Check first MAS in one direction
        for (int j = 0; j < WORD_LEN; j++) {
            int nx = x + dx1 * j, ny = y + dy1 * j;
            if (nx < 0 || nx >= SIZE || ny < 0 || ny >= SIZE || grid[nx][ny] != WORD[j]) {
                return 0;
            }
        }
        // Check second MAS in the opposite direction
        for (int j = 0; j < WORD_LEN; j++) {
            int nx = x + dx2 * j, ny = y + dy2 * j;
            if (nx < 0 || nx >= SIZE || ny < 0 || ny >= SIZE || grid[nx][ny] != WORD[j]) {
                return 0;
            }
        }
        return 1;
    }
    return 0;
}

int main() {
    char grid[SIZE][SIZE];
    FILE *f = fopen("input.txt", "r");
    int count = 0;

    for (int i = 0; i < SIZE; i++) {
        fscanf(f, "%s", grid[i]);
    }
    fclose(f);

    for (int i = 0; i < SIZE; i++) {
        for (int j = 0; j < SIZE; j++) {
            if (check_xmas(grid, i, j, 0, 0)) {
                count++;
            }
        }
    }

    printf("%d\n", count);
    return 0;
}
