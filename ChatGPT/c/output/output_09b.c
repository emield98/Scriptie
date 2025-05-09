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
    int file_lengths[MAX_SIZE], file_ids[MAX_SIZE];
    int num_files = 0, checksum = 0;

    // Read file lengths and assign file IDs
    for (int i = 0; i < length;) {
        if (line[i] != '.') {
            int file_length = line[i] - '0'; // Get the file length
            file_lengths[num_files] = file_length;
            file_ids[num_files] = num_files; // Assign file ID based on order
            num_files++;
            i += file_length; // Skip over the file's length
        } else {
            i++;
        }
    }

    // Try to compact files in reverse order of their IDs
    for (int i = num_files - 1; i >= 0; i--) {
        int file_length = file_lengths[i];
        int start_pos = -1;

        // Find a span of free space to fit the file
        for (int j = 0; j <= length - file_length; j++) {
            int can_fit = 1;
            for (int k = j; k < j + file_length; k++) {
                if (line[k] != '.') {
                    can_fit = 0;
                    break;
                }
            }
            if (can_fit) {
                start_pos = j;
                break;
            }
        }

        // If we found a valid space, move the file
        if (start_pos != -1) {
            for (int j = 0; j < file_length; j++) {
                line[start_pos + j] = '0' + file_ids[i]; // Move the file
                line[start_pos + j + file_length] = '.'; // Leave free space
            }
        }
    }

    // Calculate the checksum
    for (int i = 0; i < length; i++) {
        if (line[i] != '.') {
            checksum += i * (line[i] - '0');
        }
    }

    printf("%d\n", checksum);
    return 0;
}
