#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define WIDTH 101
#define HEIGHT 103
#define TIME 100
#define MAX_ROBOTS 10000
#define HASH_SIZE 20011

typedef struct {
    int x, y;
    int count;
} Position;

typedef struct Node {
    int x, y;
    int count;
    struct Node *next;
} Node;

Node *hash_table[HASH_SIZE];

unsigned int hash(int x, int y) {
    return ((unsigned int)(x * 1009 + y)) % HASH_SIZE;
}

void insert_position(int x, int y) {
    unsigned int h = hash(x, y);
    Node *node = hash_table[h];
    while (node) {
        if (node->x == x && node->y == y) {
            node->count++;
            return;
        }
        node = node->next;
    }
    Node *new_node = malloc(sizeof(Node));
    new_node->x = x;
    new_node->y = y;
    new_node->count = 1;
    new_node->next = hash_table[h];
    hash_table[h] = new_node;
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    int px, py, vx, vy;
    char line[256];

    while (fgets(line, sizeof(line), file)) {
        if (strlen(line) <= 1) continue;
        sscanf(line, "p=%d,%d v=%d,%d", &px, &py, &vx, &vy);
        int x = (px + vx * TIME) % WIDTH;
        if (x < 0) x += WIDTH;
        int y = (py + vy * TIME) % HEIGHT;
        if (y < 0) y += HEIGHT;
        insert_position(x, y);
    }

    fclose(file);

    int mid_x = WIDTH / 2;
    int mid_y = HEIGHT / 2;
    long long quadrants[4] = {0, 0, 0, 0}; // Q1, Q2, Q3, Q4

    for (int i = 0; i < HASH_SIZE; i++) {
        Node *node = hash_table[i];
        while (node) {
            int x = node->x;
            int y = node->y;
            int count = node->count;
            if (x == mid_x || y == mid_y) {
                node = node->next;
                continue;
            }
            if (x < mid_x && y < mid_y) {
                quadrants[0] += count;
            } else if (x >= mid_x && y < mid_y) {
                quadrants[1] += count;
            } else if (x < mid_x && y >= mid_y) {
                quadrants[2] += count;
            } else {
                quadrants[3] += count;
            }
            node = node->next;
        }
    }

    long long safety_factor = 1;
    for (int i = 0; i < 4; i++) {
        safety_factor *= quadrants[i];
    }

    printf("%lld\n", safety_factor);

    // Cleanup
    for (int i = 0; i < HASH_SIZE; i++) {
        Node *node = hash_table[i];
        while (node) {
            Node *tmp = node;
            node = node->next;
            free(tmp);
        }
    }

    return 0;
}
