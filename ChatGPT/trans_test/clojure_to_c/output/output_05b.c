#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_RULES 1000
#define MAX_UPDATES 1000
#define MAX_UPDATE_LEN 100
#define MAX_NODES 1000

typedef struct {
    long a, b;
} Rule;

typedef struct {
    long data[MAX_UPDATE_LEN];
    int len;
} Update;

typedef struct {
    long nodes[MAX_NODES];
    int len;
} NodeList;

int contains(long *arr, int len, long val) {
    for (int i = 0; i < len; i++) {
        if (arr[i] == val) return 1;
    }
    return 0;
}

int index_of(long *arr, int len, long val) {
    for (int i = 0; i < len; i++) {
        if (arr[i] == val) return i;
    }
    return -1;
}

int valid_update(Rule *rules, int rule_count, long *update, int update_len) {
    for (int i = 0; i < rule_count; i++) {
        int idx_a = index_of(update, update_len, rules[i].a);
        int idx_b = index_of(update, update_len, rules[i].b);
        if (idx_a != -1 && idx_b != -1 && idx_a >= idx_b) {
            return 0;
        }
    }
    return 1;
}

int compare_long(const void *a, const void *b) {
    long x = *(long *)a;
    long y = *(long *)b;
    return (x > y) - (x < y);
}

int topological_sort(long *nodes, int node_count, Rule *edges, int edge_count, long *result) {
    int in_degree[MAX_NODES] = {0};
    long adj[MAX_NODES][MAX_NODES];
    int adj_len[MAX_NODES] = {0};
    int i, j;

    for (i = 0; i < edge_count; i++) {
        int a_idx = index_of(nodes, node_count, edges[i].a);
        int b_idx = index_of(nodes, node_count, edges[i].b);
        if (a_idx != -1 && b_idx != -1) {
            adj[a_idx][adj_len[a_idx]++] = b_idx;
            in_degree[b_idx]++;
        }
    }

    int queue[MAX_NODES], front = 0, back = 0;
    for (i = 0; i < node_count; i++) {
        if (in_degree[i] == 0) {
            queue[back++] = i;
        }
    }

    int res_len = 0;
    while (front < back) {
        int u = queue[front++];
        result[res_len++] = nodes[u];
        for (i = 0; i < adj_len[u]; i++) {
            int v = adj[u][i];
            if (--in_degree[v] == 0) {
                queue[back++] = v;
            }
        }
    }

    return res_len == node_count ? res_len : 0;
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    Rule rules[MAX_RULES];
    int rule_count = 0;
    Update updates[MAX_UPDATES];
    int update_count = 0;

    char line[1024];
    while (fgets(line, sizeof(line), file)) {
        size_t len = strcspn(line, "\r\n");
        line[len] = '\0';
        if (len == 0) continue;

        if (strchr(line, '|')) {
            char *sep = strchr(line, '|');
            *sep = '\0';
            rules[rule_count].a = strtol(line, NULL, 10);
            rules[rule_count].b = strtol(sep + 1, NULL, 10);
            rule_count++;
        } else {
            char *token = strtok(line, ",");
            int count = 0;
            while (token) {
                updates[update_count].data[count++] = strtol(token, NULL, 10);
                token = strtok(NULL, ",");
            }
            updates[update_count].len = count;
            update_count++;
        }
    }
    fclose(file);

    long sum = 0;
    for (int i = 0; i < update_count; i++) {
        long *upd = updates[i].data;
        int len = updates[i].len;
        if (valid_update(rules, rule_count, upd, len)) continue;

        Rule filtered[MAX_RULES];
        int filtered_count = 0;
        for (int j = 0; j < rule_count; j++) {
            if (contains(upd, len, rules[j].a) && contains(upd, len, rules[j].b)) {
                filtered[filtered_count++] = rules[j];
            }
        }

        long sorted[MAX_UPDATE_LEN];
        memcpy(sorted, upd, sizeof(long) * len);
        qsort(sorted, len, sizeof(long), compare_long);

        long sorted_update[MAX_UPDATE_LEN];
        int ts_len = topological_sort(sorted, len, filtered, filtered_count, sorted_update);
        if (ts_len > 0) {
            sum += sorted_update[ts_len / 2];
        }
    }

    printf("%ld\n", sum);
    return 0;
}
