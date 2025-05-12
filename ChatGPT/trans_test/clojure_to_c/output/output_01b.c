#include <stdio.h>
#include <stdlib.h>

#define MAX_LINES 1000

typedef struct {
    long key;
    int count;
} Freq;

int find_or_insert(Freq *freq, int *size, long key) {
    for (int i = 0; i < *size; i++) {
        if (freq[i].key == key) {
            return i;
        }
    }
    freq[*size].key = key;
    freq[*size].count = 0;
    return (*size)++;
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    long left[MAX_LINES], right[MAX_LINES];
    int count = 0;

    while (fscanf(file, "%ld %ld", &left[count], &right[count]) == 2) {
        count++;
    }
    fclose(file);

    Freq freq_right[MAX_LINES];
    int freq_size = 0;

    for (int i = 0; i < count; i++) {
        int idx = find_or_insert(freq_right, &freq_size, right[i]);
        freq_right[idx].count++;
    }

    long sum = 0;
    for (int i = 0; i < count; i++) {
        long key = left[i];
        for (int j = 0; j < freq_size; j++) {
            if (freq_right[j].key == key) {
                sum += key * freq_right[j].count;
                break;
            }
        }
    }

    printf("%ld\n", sum);
    return 0;
}
