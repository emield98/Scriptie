#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <limits.h>

#define MAX_MACHINES 1000
#define MAX_LINE 128
#define MAX_PRESSES 100

typedef struct {
    int ax, ay;
    int bx, by;
    int px, py;
} Machine;

int extract_value(const char *part) {
    const char *plus = strchr(part, '+');
    if (plus) {
        return atoi(plus + 1);
    } else {
        const char *eq = strchr(part, '=');
        return atoi(eq + 1);
    }
}

void extract_coords(const char *line, int *x, int *y) {
    char line_copy[MAX_LINE];
    strncpy(line_copy, line, MAX_LINE);
    line_copy[MAX_LINE - 1] = '\0';

    char *token = strtok(line_copy, ",");
    *x = extract_value(token);
    token = strtok(NULL, ",");
    *y = extract_value(token);
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    Machine machines[MAX_MACHINES];
    int machine_count = 0;
    char line1[MAX_LINE], line2[MAX_LINE], line3[MAX_LINE];

    while (fgets(line1, sizeof(line1), file) &&
           fgets(line2, sizeof(line2), file) &&
           fgets(line3, sizeof(line3), file)) {

        // Skip empty lines
        if (strlen(line1) <= 1 || strlen(line2) <= 1 || strlen(line3) <= 1) continue;

        int ax, ay, bx, by, px, py;
        extract_coords(line1, &ax, &ay);
        extract_coords(line2, &bx, &by);
        extract_coords(line3, &px, &py);

        machines[machine_count++] = (Machine){ax, ay, bx, by, px, py};
    }

    fclose(file);

    int total_cost = 0;

    for (int i = 0; i < machine_count; i++) {
        Machine m = machines[i];
        int min_cost = INT_MAX;

        for (int a = 0; a <= MAX_PRESSES; a++) {
            for (int b = 0; b <= MAX_PRESSES; b++) {
                int x = a * m.ax + b * m.bx;
                int y = a * m.ay + b * m.by;
                if (x == m.px && y == m.py) {
                    int cost = a * 3 + b;
                    if (cost < min_cost) {
                        min_cost = cost;
                    }
                }
            }
        }

        if (min_cost != INT_MAX) {
            total_cost += min_cost;
        }
    }

    printf("%d\n", total_cost);
    return 0;
}
