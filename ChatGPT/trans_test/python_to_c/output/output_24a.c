#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#define MAX_GATES 1000
#define MAX_WIRES 1000
#define MAX_NAME_LEN 32
#define MAX_LINE_LEN 128

typedef struct {
    char in1[MAX_NAME_LEN];
    char op[4];
    char in2[MAX_NAME_LEN];
    char out[MAX_NAME_LEN];
} Gate;

typedef struct {
    char name[MAX_NAME_LEN];
    int value;
    int is_set;
} Wire;

Gate gates[MAX_GATES];
int gate_count = 0;

Wire wires[MAX_WIRES];
int wire_count = 0;

int get_wire_index(const char *name) {
    for (int i = 0; i < wire_count; i++) {
        if (strcmp(wires[i].name, name) == 0) {
            return i;
        }
    }
    strcpy(wires[wire_count].name, name);
    wires[wire_count].is_set = 0;
    return wire_count++;
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    char line[MAX_LINE_LEN];
    while (fgets(line, sizeof(line), file)) {
        if (strchr(line, ':')) {
            char name[MAX_NAME_LEN];
            int val;
            sscanf(line, "%[^:]: %d", name, &val);
            int idx = get_wire_index(name);
            wires[idx].value = val;
            wires[idx].is_set = 1;
        } else {
            char in1[MAX_NAME_LEN], op[4], in2[MAX_NAME_LEN], out[MAX_NAME_LEN];
            if (sscanf(line, "%s %s %s -> %s", in1, op, in2, out) == 4) {
                strcpy(gates[gate_count].in1, in1);
                strcpy(gates[gate_count].op, op);
                strcpy(gates[gate_count].in2, in2);
                strcpy(gates[gate_count].out, out);
                gate_count++;
                get_wire_index(in1);
                get_wire_index(in2);
                get_wire_index(out);
            }
        }
    }
    fclose(file);

    int progress;
    do {
        progress = 0;
        for (int i = 0; i < gate_count; i++) {
            int in1_idx = get_wire_index(gates[i].in1);
            int in2_idx = get_wire_index(gates[i].in2);
            int out_idx = get_wire_index(gates[i].out);

            if (wires[out_idx].is_set) continue;
            if (wires[in1_idx].is_set && wires[in2_idx].is_set) {
                int a = wires[in1_idx].value;
                int b = wires[in2_idx].value;
                if (strcmp(gates[i].op, "AND") == 0)
                    wires[out_idx].value = a & b;
                else if (strcmp(gates[i].op, "OR") == 0)
                    wires[out_idx].value = a | b;
                else if (strcmp(gates[i].op, "XOR") == 0)
                    wires[out_idx].value = a ^ b;
                else
                    continue;
                wires[out_idx].is_set = 1;
                progress = 1;
            }
        }
    } while (progress);

    // Collect wires starting with 'z'
    char *z_wires[MAX_WIRES];
    int z_count = 0;

    for (int i = 0; i < wire_count; i++) {
        if (wires[i].is_set && wires[i].name[0] == 'z') {
            z_wires[z_count++] = wires[i].name;
        }
    }

    // Simple bubble sort reverse lexicographic order
    for (int i = 0; i < z_count - 1; i++) {
        for (int j = i + 1; j < z_count; j++) {
            if (strcmp(z_wires[i], z_wires[j]) < 0) {
                char *tmp = z_wires[i];
                z_wires[i] = z_wires[j];
                z_wires[j] = tmp;
            }
        }
    }

    // Build binary string and convert
    unsigned int result = 0;
    for (int i = 0; i < z_count; i++) {
        int idx = get_wire_index(z_wires[i]);
        result = (result << 1) | (wires[idx].value & 1);
    }

    printf("%u\n", result);
    return 0;
}
