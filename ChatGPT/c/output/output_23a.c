// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_COMPUTERS 100
#define MAX_CONNECTIONS 1000

// Structure to store network connections
typedef struct {
    char *computer1;
    char *computer2;
} Connection;

Connection connections[MAX_CONNECTIONS];
int connection_count = 0;

char *computers[MAX_COMPUTERS];
int computer_count = 0;

int adjacency[MAX_COMPUTERS][MAX_COMPUTERS];

// Function to find the index of a computer
int get_computer_index(char *computer) {
    for (int i = 0; i < computer_count; i++) {
        if (strcmp(computers[i], computer) == 0) {
            return i;
        }
    }
    computers[computer_count] = computer;
    return computer_count++;
}

// Function to find all sets of three inter-connected computers
int main() {
    FILE *file = fopen("input.txt", "r");
    char line[256];
    
    // Reading the input and building the adjacency matrix
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
    
    int count = 0;
    
    // Checking all possible sets of three inter-connected computers
    for (int i = 0; i < connection_count; i++) {
        for (int j = i + 1; j < connection_count; j++) {
            for (int k = j + 1; k < connection_count; k++) {
                int a = get_computer_index(connections[i].computer1);
                int b = get_computer_index(connections[i].computer2);
                int c = get_computer_index(connections[j].computer1);
                int d = get_computer_index(connections[j].computer2);
                int e = get_computer_index(connections[k].computer1);
                int f = get_computer_index(connections[k].computer2);
                
                if ((adjacency[a][b] && adjacency[a][c] && adjacency[b][c]) ||
                    (adjacency[a][d] && adjacency[a][e] && adjacency[b][f])) {
                    count++;
                }
            }
        }
    }
    
    printf("%d\n", count);
    return 0;
}
