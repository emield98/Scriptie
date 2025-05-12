#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#define MAX_NODES 100
#define MAX_NAME_LEN 32

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

bool is_connected(int a, int b) {
    return adj[a][b];
}

bool is_clique(int *clique, int size) {
    for (int i = 0; i < size; i++) {
        for (int j = i + 1; j < size; j++) {
            if (!adj[clique[i]][clique[j]])
                return false;
        }
    }
    return true;
}

int max_clique[MAX_NODES];
int max_clique_size = 0;

void dfs(int *clique, int clique_size, int *candidates, int cand_size) {
    if (clique_size > max_clique_size) {
        memcpy(max_clique, clique, sizeof(int) * clique_size);
        max_clique_size = clique_size;
    }

    for (int i = 0; i < cand_size; i++) {
        int node = candidates[i];
        int valid = 1;
        for (int j = 0; j < clique_size; j++) {
            if (!adj[node][clique[j]]) {
                valid = 0;
                break;
            }
        }
        if (valid) {
            int new_clique[MAX_NODES];
            int new_candidates[MAX_NODES];
            int new_cand_size = 0;
            memcpy(new_clique, clique, sizeof(int) * clique_size);
            new_clique[clique_size] = node;

            for (int k = i + 1; k < cand_size; k++) {
                int next_node = candidates[k];
                int is_valid = 1;
                for (int j = 0; j <= clique_size; j++) {
                    if (!adj[next_node][new_clique[j]]) {
                        is_valid = 0;
                        break;
                    }
                }
                if (is_valid) {
                    new_candidates[new_cand_size++] = next_node;
                }
            }

            dfs(new_clique, clique_size + 1, new_candidates, new_cand_size);
        }
    }
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    memset(adj, 0, sizeof(adj));
    char line[128];
    while (fgets(line, sizeof(line), file)) {
        if (strlen(line) <= 1) continue;
        char a[MAX_NAME_LEN], b[MAX_NAME_LEN];
        if (sscanf(line, "%[^-]-%s", a, b) == 2) {
            int ia = get_node_index(a);
            int ib = get_node_index(b);
            adj[ia][ib] = true;
            adj[ib][ia] = true;
        }
    }
    fclose(file);

    int candidates[MAX_NODES];
    for (int i = 0; i < node_count; i++) candidates[i] = i;
    dfs(NULL, 0, candidates, node_count);

    char *sorted_names[MAX_NODES];
    for (int i = 0; i < max_clique_size; i++) {
        sorted_names[i] = node_names[max_clique[i]];
    }

    for (int i = 0; i < max_clique_size - 1; i++) {
        for (int j = i + 1; j < max_clique_size; j++) {
            if (strcmp(sorted_names[i], sorted_names[j]) > 0) {
                char *tmp = sorted_names[i];
                sorted_names[i] = sorted_names[j];
                sorted_names[j] = tmp;
            }
        }
    }

    for (int i = 0; i < max_clique_size; i++) {
        printf("%s", sorted_names[i]);
        if (i < max_clique_size - 1) printf(",");
    }
    printf("\n");

    return 0;
}
