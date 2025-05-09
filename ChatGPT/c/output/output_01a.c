// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <stdlib.h>

int compare(const void *a, const void *b) {
    return (*(int*)a - *(int*)b);
}

int main() {
    int left[1000], right[1000];
    int n_left = 0, n_right = 0;
    FILE *f = fopen("input.txt", "r");

    while (fscanf(f, "%d %d", &left[n_left], &right[n_right]) != EOF) {
        n_left++;
        n_right++;
    }
    fclose(f);

    qsort(left, n_left, sizeof(int), compare);
    qsort(right, n_right, sizeof(int), compare);

    int total_distance = 0;
    for (int i = 0; i < n_left; i++) {
        total_distance += abs(left[i] - right[i]);
    }

    printf("%d\n", total_distance);
    return 0;
}
