// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <stdlib.h>

int main() {
    int left[1000], right[1000];
    int n_left = 0, n_right = 0;
    FILE *f = fopen("input.txt", "r");

    while (fscanf(f, "%d %d", &left[n_left], &right[n_right]) != EOF) {
        n_left++;
        n_right++;
    }
    fclose(f);

    int similarity_score = 0;
    for (int i = 0; i < n_left; i++) {
        int count = 0;
        for (int j = 0; j < n_right; j++) {
            if (left[i] == right[j]) {
                count++;
            }
        }
        similarity_score += left[i] * count;
    }

    printf("%d\n", similarity_score);
    return 0;
}
