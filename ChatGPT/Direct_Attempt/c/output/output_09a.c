// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_SIZE 1000

int main() {
    FILE *f = fopen("input.txt", "r");
    char line[MAX_SIZE];
    fgets(line, sizeof(line), f);
    fclose(f);

    int length = strlen(line);
    int file_id = 0;
    int checksum = 0;

    // Loop through the disk map
    for (int i = 0; i < length; i++) {
        if (line[i] != '.') {
            int file_length = line[i] - '0'; // Get the length of the file
            for (int j = 0; j < file_length; j++) {
                checksum += (i + j) * file_id; // Add to checksum: position * file ID
            }
            file_id++; // Increment file ID
            i += file_length - 1; // Skip over the length of the file
        }
    }

    printf("%d\n", checksum);
    return 0;
}
