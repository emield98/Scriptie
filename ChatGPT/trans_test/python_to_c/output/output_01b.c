#include <stdio.h>
#include <stdlib.h>

#define MAX_ENTRIES 100000
#define HASH_SIZE 100003

typedef struct Node {
    int key;
    int count;
    struct Node* next;
} Node;

Node* hash_table[HASH_SIZE];

unsigned int hash(int key) {
    return ((unsigned int)key) % HASH_SIZE;
}

void insert(int key) {
    unsigned int idx = hash(key);
    Node* curr = hash_table[idx];
    while (curr) {
        if (curr->key == key) {
            curr->count++;
            return;
        }
        curr = curr->next;
    }
    Node* new_node = (Node*)malloc(sizeof(Node));
    new_node->key = key;
    new_node->count = 1;
    new_node->next = hash_table[idx];
    hash_table[idx] = new_node;
}

int get_count(int key) {
    unsigned int idx = hash(key);
    Node* curr = hash_table[idx];
    while (curr) {
        if (curr->key == key) {
            return curr->count;
        }
        curr = curr->next;
    }
    return 0;
}

int main() {
    FILE* file = fopen("input.txt", "r");
    if (!file) return 1;

    int left[MAX_ENTRIES];
    int l_val, r_val;
    int count = 0;

    while (fscanf(file, "%d %d", &l_val, &r_val) == 2) {
        left[count++] = l_val;
        insert(r_val);
    }
    fclose(file);

    long long similarity_score = 0;
    for (int i = 0; i < count; i++) {
        similarity_score += (long long)left[i] * get_count(left[i]);
    }

    printf("%lld\n", similarity_score);

    return 0;
}
