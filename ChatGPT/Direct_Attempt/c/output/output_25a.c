// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_WIDTH 50
#define MAX_HEIGHT 50

// Function to read the schematic and convert it to heights
void read_schematic(char schematic[MAX_HEIGHT][MAX_WIDTH], int height[MAX_WIDTH], int width, int height_limit) {
    for (int i = 0; i < width; i++) {
        height[i] = 0;
        for (int j = 0; j < height_limit; j++) {
            if (schematic[j][i] == '#') {
                height[i]++;
            }
        }
    }
}

// Function to check if two keys and locks fit without overlapping
int check_fit(int lock_height[MAX_WIDTH], int key_height[MAX_WIDTH], int width) {
    for (int i = 0; i < width; i++) {
        if (lock_height[i] + key_height[i] > 5) {
            return 0;  // Overlap detected
        }
    }
    return 1;  // Fit found
}

int main() {
    FILE *file = fopen("input.txt", "r");
    char line[MAX_WIDTH + 1];
    
    char lock_schematic[MAX_HEIGHT][MAX_WIDTH];
    char key_schematic[MAX_HEIGHT][MAX_WIDTH];
    
    int lock_height[MAX_WIDTH];
    int key_height[MAX_WIDTH];
    
    int lock_count = 0;
    int key_count = 0;
    
    // Read the schematics from the input
    while (fgets(line, sizeof(line), file)) {
        if (line[0] == '\n') {
            break;  // Blank line, indicates switch from locks to keys
        }
        strcpy(lock_schematic[lock_count++], line);
    }
    
    while (fgets(line, sizeof(line), file)) {
        if (line[0] == '\n') {
            break;  // Ignore any trailing empty lines
        }
        strcpy(key_schematic[key_count++], line);
    }

    fclose(file);
    
    // Assume the lock and key schematics are the same size
    int width = strlen(lock_schematic[0]);
    int lock_height_final[MAX_WIDTH];
    int key_height_final[MAX_WIDTH];

    // Convert the schematics into height representations
    read_schematic(lock_schematic, lock_height_final, width, lock_count);
    read_schematic(key_schematic, key_height_final, width, key_count);
    
    // Calculate the number of unique lock/key pairs that fit
    int total_fits = 0;
    for (int i = 0; i < lock_count; i++) {
        for (int j = 0; j < key_count; j++) {
            if (check_fit(lock_height_final, key_height_final, width)) {
                total_fits++;
            }
        }
    }
    
    printf("%d\n", total_fits);

    return 0;
}
