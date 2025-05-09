// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <string.h>

#define SIZE 10
#define WORD "XMAS"
#define WORD_LEN 4

int check_word(char grid[SIZE][SIZE], int x, int y, int dx, int dy) {
    for (int i = 0; i < WORD_LEN; i++) {
        int nx = x + dx * i;
        int ny = y + dy * i;
        if (nx < 0 || nx >= SIZE || ny < 0 || ny >= SIZE || grid[nx][ny] != WORD[i]) {
            return 0;
        }
    }
    return 1;
}

int main() {
    char grid[SIZE][SIZE];
    FILE *f = fopen("input.txt", "r");
    int count = 0;

    for (int i = 0; i < SIZE; i++) {
        fscanf(f, "%s", grid[i]);
    }
    fclose(f);

    int directions[8][2] = {
        {0, 1}, {1, 0}, {1, 1}, {1, -1}, // horizontal, vertical, diagonal
        {0, -1}, {-1, 0}, {-1, -1}, {-1, 1} // reverse directions
    };

    for (int i = 0; i < SIZE; i++) {
        for (int j = 0; j < SIZE; j++) {
            for (int d = 0; d < 8; d++) {
                if (check_word(grid, i, j, directions[d][0], directions[d][1])) {
                    count++;
                }
            }
        }
    }

    printf("%d\n", count);
    return 0;
}
