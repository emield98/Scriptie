// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_SIZE 1000

char grid[MAX_SIZE][MAX_SIZE];
int visited[MAX_SIZE][MAX_SIZE] = {0};

typedef struct {
    int x, y;
    char freq;
} Antenna;

int n_antennas = 0;
Antenna antennas[MAX_SIZE * MAX_SIZE];

int is_valid(int x, int y, int max_x, int max_y) {
    return x >= 0 && x < max_x && y >= 0 && y < max_y;
}

void mark_antinodes(int max_x, int max_y) {
    for (int i = 0; i < n_antennas; i++) {
        for (int j = i + 1; j < n_antennas; j++) {
            if (antennas[i].freq == antennas[j].freq) {
                int dx = antennas[j].x - antennas[i].x;
                int dy = antennas[j].y - antennas[i].y;

                if (dx == 0 || dy == 0) {  // Antennae in line horizontally or vertically
                    for (int k = 0; k < n_antennas; k++) {
                        if (antennas[k].freq == antennas[i].freq) {
                            int ax = antennas[k].x;
                            int ay = antennas[k].y;
                            if (ax == antennas[i].x && ay == antennas[i].y) {
                                visited[ax][ay] = 1;
                            }
                        }
                    }
                }
            }
        }
    }
}

int main() {
    FILE *f = fopen("input.txt", "r");
    char line[MAX_SIZE];
    int max_x = 0, max_y = 0;
    
    while (fgets(line, sizeof(line), f)) {
        for (int j = 0; j < strlen(line) - 1; j++) {
            grid[max_x][j] = line[j];
            if (line[j] != '.') {
                antennas[n_antennas].x = max_x;
                antennas[n_antennas].y = j;
                antennas[n_antennas].freq = line[j];
                n_antennas++;
            }
        }
        max_x++;
    }
    fclose(f);
    
    max_y = strlen(line) - 1;
    
    mark_antinodes(max_x, max_y);

    int total_antinodes = 0;
    for (int i = 0; i < max_x; i++) {
        for (int j = 0; j < max_y; j++) {
            total_antinodes += visited[i][j];
        }
    }
    
    printf("%d\n", total_antinodes);
    return 0;
}
