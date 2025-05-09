// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_STONES 10000

void split_number(char *num_str, int *stones, int *stone_count) {
    int len = strlen(num_str);
    int mid = len / 2;

    num_str[mid] = '\0';  // Split the number in half
    stones[*stone_count] = atoi(num_str);
    (*stone_count)++;
    stones[*stone_count] = atoi(num_str + mid);
    (*stone_count)++;
}

void blink(int *stones, int *stone_count) {
    int new_stones[MAX_STONES];
    int new_stone_count = 0;

    for (int i = 0; i < *stone_count; i++) {
        int stone = stones[i];
        char num_str[20];
        sprintf(num_str, "%d", stone);

        if (stone == 0) {
            new_stones[new_stone_count++] = 1;
        } else if (strlen(num_str) % 2 == 0) {
            split_number(num_str, new_stones, &new_stone_count);
        } else {
            new_stones[new_stone_count++] = stone * 2024;
        }
    }

    // Update original stones and count
    memcpy(stones, new_stones, new_stone_count * sizeof(int));
    *stone_count = new_stone_count;
}

int main() {
    FILE *f = fopen("input.txt", "r");
    int stones[MAX_STONES];
    int stone_count = 0;

    // Read initial stones from file
    while (fscanf(f, "%d", &stones[stone_count]) != EOF) {
        stone_count++;
    }
    fclose(f);

    // Blink 25 times
    for (int i = 0; i < 25; i++) {
        blink(stones, &stone_count);
    }

    printf("%d\n", stone_count);
    return 0;
}
