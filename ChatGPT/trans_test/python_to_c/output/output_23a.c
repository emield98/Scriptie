#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#define MAX_NODES 1000
#define MAX_EDGES 2000
#define MAX_NAME_LEN 32

typedef struct {
    char name[MAX_NAME_LEN];
} Node;

char node_names[MAX_NODES][MAX_NAME_LEN];
int node_count = 0;

bool adj[MAX_NODES][MAX_NODES];

int get_node_index(const char *name) {
    for (int i = 0; i < node_count; i++) {
        if (strcmp(node_names[i], name) == 0)
            return i;
    }
    strcpy(node_names[node_count], name);
    return node_count++;
}

bool is_triangle(int a, int b, int c) {
    return adj[a][b] && adj[a][c] && adj[b][a] && adj[b][c] &&
           adj[c][a] && adj[c][b];
}

bool starts_with_t(const char *name) {
    return name[0] == 't';
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    memset(adj, 0, sizeof(adj));
    char line[128];
    while (fgets(line, sizeof(line), file)) {
        char a[MAX_NAME_LEN], b[MAX_NAME_LEN];
        if (sscanf(line, "%[^-]-%s", a, b) == 2) {
            int ia = get_node_index(a);
            int ib = get_node_index(b);
            adj[ia][ib] = true;
            adj[ib][ia] = true;
        }
    }
    fclose(file);

    int count = 0;
    for (int i = 0; i < node_count; i++) {
        for (int j = i + 1; j < node_count; j++) {
            for (int k = j + 1; k < node_count; k++) {
                if (is_triangle(i, j, k)) {
                    if (starts_with_t(node_names[i]) ||
                        starts_with_t(node_names[j]) ||
                        starts_with_t(node_names[k])) {
                        count++;
                    }
                }
            }
        }
    }

    printf("%d\n", count);
    return 0;
}
