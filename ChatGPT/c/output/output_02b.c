// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <stdlib.h>

int is_safe(int levels[], int n) {
    int increasing = 0, decreasing = 0;

    for (int i = 1; i < n; i++) {
        int diff = levels[i] - levels[i - 1];
        if (diff >= 1 && diff <= 3) {
            if (diff > 0) increasing = 1;
            else if (diff < 0) decreasing = 1;
        } else {
            return 0; // Unsafe if difference is greater than 3 or less than 1
        }
    }

    return (increasing == 0 || decreasing == 0); // Safe if only increasing or decreasing
}

int can_be_safe_by_removal(int levels[], int n) {
    for (int i = 0; i < n; i++) {
        int new_levels[1000];
        int new_n = 0;
        
        // Copy levels except the i-th one
        for (int j = 0; j < n; j++) {
            if (j != i) {
                new_levels[new_n++] = levels[j];
            }
        }

        if (is_safe(new_levels, new_n)) {
            return 1; // Safe if removing this level makes it safe
        }
    }
    return 0; // Unsafe if no removal makes it safe
}

int main() {
    int levels[1000];
    int count_safe = 0;
    FILE *f = fopen("input.txt", "r");

    while (!feof(f)) {
        int n = 0;
        while (fscanf(f, "%d", &levels[n]) == 1) {
            n++;
            if (getc(f) == '\n') break;
        }

        if (is_safe(levels, n) || can_be_safe_by_removal(levels, n)) {
            count_safe++;
        }
    }

    fclose(f);
    printf("%d\n", count_safe);
    return 0;
}
