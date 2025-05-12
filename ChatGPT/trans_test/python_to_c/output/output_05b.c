#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#define MAX_RULES 10000
#define MAX_UPDATES 1000
#define MAX_UPDATE_LEN 100
#define MAX_NODES 1000

typedef struct {
    int x, y;
} Rule;

Rule rules[MAX_RULES];
int rule_count = 0;

int updates[MAX_UPDATES][MAX_UPDATE_LEN];
int update_lengths[MAX_UPDATES];
int update_count = 0;

int compare_ints(const void *a, const void *b) {
    return (*(int *)a - *(int *)b);
}

int find_index(int *arr, int len, int target) {
    for (int i = 0; i < len; i++) {
        if (arr[i] == target) return i;
    }
    return -1;
}

bool is_valid(int *update, int len, Rule *subset_rules, int subset_count) {
    int pos[MAX_NODES];
    for (int i = 0; i < len; i++) {
        pos[update[i]] = i;
    }
    for (int i = 0; i < subset_count; i++) {
        int x = subset_rules[i].x;
        int y = subset_rules[i].y;
        if (pos[x] >= pos[y]) return false;
    }
    return true;
}

void topo_sort(int *update, int len, Rule *subset_rules, int subset_count, int *output) {
    int indegree[MAX_NODES] = {0};
    int graph[MAX_NODES][MAX_UPDATE_LEN];
    int graph_size[MAX_NODES] = {0};
    bool in_update[MAX_NODES] = {false};

    for (int i = 0; i < len; i++) {
        in_update[update[i]] = true;
    }

    for (int i = 0; i < subset_count; i++) {
        int x = subset_rules[i].x;
        int y = subset_rules[i].y;
        graph[x][graph_size[x]++] = y;
        indegree[y]++;
    }

    int q[MAX_UPDATE_LEN];
    int q_start = 0, q_end = 0;

    int nodes[MAX_UPDATE_LEN], node_count = 0;
    for (int i = 0; i < len; i++) {
        nodes[node_count++] = update[i];
    }

    qsort(nodes, node_count, sizeof(int), compare_ints);

    for (int i = 0; i < node_count; i++) {
        if (indegree[nodes[i]] == 0) {
            q[q_end++] = nodes[i];
        }
    }

    int out_idx = 0;
    while (q_start < q_end) {
        int u = q[q_start++];
        output[out_idx++] = u;
        qsort(graph[u], graph_size[u], sizeof(int), compare_ints);
        for (int i = 0; i < graph_size[u]; i++) {
            int v = graph[u][i];
            indegree[v]--;
            if (indegree[v] == 0) {
                q[q_end++] = v;
            }
        }
    }
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    char line[1024];
    while (fgets(line, sizeof(line), file)) {
        char *trim = line;
        while (*trim == ' ' || *trim == '\n' || *trim == '\r') trim++;
        if (*trim == '\0') continue;

        char *sep = strchr(trim, '|');
        if (sep) {
            int x, y;
            sscanf(trim, "%d|%d", &x, &y);
            rules[rule_count++] = (Rule){x, y};
        } else {
            int val, len = 0;
            char *token = strtok(trim, ",\n\r");
            while (token && len < MAX_UPDATE_LEN) {
                sscanf(token, "%d", &val);
                updates[update_count][len++] = val;
                token = strtok(NULL, ",\n\r");
            }
            update_lengths[update_count] = len;
            update_count++;
        }
    }
    fclose(file);

    long long total = 0;

    for (int i = 0; i < update_count; i++) {
        int *update = updates[i];
        int len = update_lengths[i];
        Rule subset[MAX_RULES];
        int subset_count = 0;

        bool present[MAX_NODES] = {false};
        for (int j = 0; j < len; j++) {
            present[update[j]] = true;
        }

        for (int j = 0; j < rule_count; j++) {
            if (present[rules[j]()]()
