// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_PAGES 100
#define MAX_RULES 100

typedef struct {
    int before[MAX_PAGES];
    int after[MAX_PAGES];
} Graph;

int is_valid_order(int pages[], int n, Graph* graph) {
    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
            if (graph->before[pages[j]] == pages[i] || graph->after[pages[i]] == pages[j]) {
                return 0;
            }
        }
    }
    return 1;
}

void sort_pages(int pages[], int n, Graph* graph) {
    int sorted = 0;
    while (!sorted) {
        sorted = 1;
        for (int i = 0; i < n - 1; i++) {
            if (graph->before[pages[i + 1]] == pages[i]) {
                int temp = pages[i];
                pages[i] = pages[i + 1];
                pages[i + 1] = temp;
                sorted = 0;
            }
        }
    }
}

int main() {
    FILE *f = fopen("input.txt", "r");
    char line[100];
    Graph graph = {0};

    // Read rules
    while (fgets(line, sizeof(line), f) && strchr(line, '|')) {
        int before, after;
        sscanf(line, "%d|%d", &before, &after);
        graph.before[after] = before;
        graph.after[before] = after;
    }

    int invalid_sum = 0;
    // Read updates and check their validity
    while (fgets(line, sizeof(line), f)) {
        int pages[MAX_PAGES], n = 0;
        char* token = strtok(line, ",");
        while (token) {
            pages[n++] = atoi(token);
            token = strtok(NULL, ",");
        }

        if (!is_valid_order(pages, n, &graph)) {
            sort_pages(pages, n, &graph);
            invalid_sum += pages[n / 2]; // Add the middle page number after sorting
        }
    }

    fclose(f);
    printf("%d\n", invalid_sum);
    return 0;
}
