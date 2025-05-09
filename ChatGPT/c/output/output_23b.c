// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_COMPUTERS 100
#define MAX_CONNECTIONS 1000

typedef struct {
    char *computer1;
    char *computer2;
} Connection;

Connection connections[MAX_CONNECTIONS];
int connection_count = 0;
char *computers[MAX_COMPUTERS];
int computer_count = 0;
int adjacency[MAX_COMPUTERS][MAX_COMPUTERS];

int get_computer_index(char *computer) {
    for (int i = 0; i < computer_count; i++) {
        if (strcmp(computers[i], computer) == 0) {
            return i;
        }
    }
    computers[computer_count] = computer;
    return computer_count++;
}

void dfs(int node, int *visited, int *group) {
    visited[node] = 1;
    group[*group] = node;
    (*group)++;
    
    for (int i = 0; i < computer_count; i++) {
        if (adjacency[node][i] && !visited[i]) {
            dfs(i, visited, group);
        }
    }
}

int main() {
    FILE *file = fopen("input.txt", "r");
    char line[256];
    
    while (fgets(line, sizeof(line), file)) {
        char *computer1 = strtok(line, "-");
        char *computer2 = strtok(NULL, "\n");
        
        int index1 = get_computer_index(computer1);
        int index2 = get_computer_index(computer2);
        
        adjacency[index1][index2] = 1;
        adjacency[index2][index1] = 1;
        
        connections[connection_count].computer1 = computer1;
        connections[connection_count].computer2 = computer2;
        connection_count++;
    }
    fclose(file);
    
    int visited[MAX_COMPUTERS] = {0};
    int largest_group_size = 0;
    int largest_group[MAX_COMPUTERS];
    
    for (int i = 0; i < computer_count; i++) {
        if (!visited[i]) {
            int group[MAX_COMPUTERS];
            int group_size = 0;
            dfs(i, visited, &group_size);
            
            if (group_size > largest_group_size) {
                largest_group_size = group_size;
                memcpy(largest_group, group, sizeof(group));
            }
        }
    }
    
    char *lan_party[MAX_COMPUTERS];
    for (int i = 0; i < largest_group_size; i++) {
        lan_party[i] = computers[largest_group[i]];
    }
    
    qsort(lan_party, largest_group_size, sizeof(char *), (int (*)(const void *, const void *))strcmp);
    
    for (int i = 0; i < largest_group_size; i++) {
        if (i > 0) printf(",");
        printf("%s", lan_party[i]);
    }
    printf("\n");
    
    return 0;
}
