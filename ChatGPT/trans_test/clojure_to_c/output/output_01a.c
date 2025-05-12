#include <stdio.h>
#include <stdlib.h>

#define MAX_LINES 1000

int cmp(const void *a, const void *b) {
    long x = *(long *)a;
    long y = *(long *)b;
    return (x > y) - (x < y);
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

    qsort(left, count, sizeof(long), cmp);
    qsort(right, count, sizeof(long), cmp);

    long sum = 0;
    for (int i = 0; i < count; i++) {
        long diff = left[i] - right[i];
        sum += diff < 0 ? -diff : diff;
    }

    printf("%ld\n", sum);
    return 0;
}
