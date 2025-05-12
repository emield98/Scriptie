#include <stdio.h>
#include <stdlib.h>
#include <math.h>

int compare(const void *a, const void *b) {
    return (*(int *)a - *(int *)b);
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    int left[100000], right[100000];
    int l_val, r_val;
    int count = 0;

    while (fscanf(file, "%d %d", &l_val, &r_val) == 2) {
        left[count] = l_val;
        right[count] = r_val;
        count++;
    }
    fclose(file);

    qsort(left, count, sizeof(int), compare);
    qsort(right, count, sizeof(int), compare);

    long long total_distance = 0;
    for (int i = 0; i < count; i++) {
        total_distance += llabs((long long)left[i] - right[i]);
    }

    printf("%lld\n", total_distance);
    return 0;
}
