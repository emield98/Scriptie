// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#define MAX_WIRES 256

typedef struct {
    char wire[16];
    unsigned short value;
    int is_set;
} Wire;

Wire wires[MAX_WIRES];
int wire_count = 0;

int get_wire_index(const char *wire) {
    for (int i = 0; i < wire_count; i++) {
        if (strcmp(wires[i].wire, wire) == 0) {
            return i;
        }
    }
    strcpy(wires[wire_count].wire, wire);
    wires[wire_count].is_set = 0;
    wire_count++;
    return wire_count - 1;
}

unsigned short get_wire_value(const char *wire) {
    int index = get_wire_index(wire);
    if (wires[index].is_set) {
        return wires[index].value;
    }
    return 0;  // Default value if wire hasn't been set
}

void set_wire_value(const char *wire, unsigned short value) {
    int index = get_wire_index(wire);
    wires[index].value = value;
    wires[index].is_set = 1;
}

unsigned short execute_operation(const char *op, unsigned short operand1, unsigned short operand2) {
    if (strcmp(op, "AND") == 0) {
        return operand1 & operand2;
    } else if (strcmp(op, "OR") == 0) {
        return operand1 | operand2;
    } else if (strcmp(op, "XOR") == 0) {
        return operand1 ^ operand2;
    }
    return 0;  // Default return value for unsupported operations
}

unsigned short execute_operation_single_operand(const char *op, unsigned short operand) {
    if (strcmp(op, "NOT") == 0) {
        return ~operand;
    }
    return 0;
}

void process_line(const char *line) {
    char wire1[16], wire2[16], wire_out[16], op[5];
    unsigned short value1, value2;
    char *token = strtok(line, " ");
    
    if (isdigit(token[0])) {  // Single value assignment, e.g., "123 -> wire"
        value1 = atoi(token);
        token = strtok(NULL, " ");
        strcpy(wire_out, token);
        set_wire_value(wire_out, value1);
    } else if (strcmp(token, "NOT") == 0) {  // NOT operation, e.g., "NOT x -> y"
        token = strtok(NULL, " ");
        strcpy(wire1, token);
        token = strtok(NULL, " ");
        strcpy(wire_out, token);
        set_wire_value(wire_out, execute_operation_single_operand("NOT", get_wire_value(wire1)));
    } else if (strchr(token, '-') != NULL) {  // AND, OR, XOR, e.g., "x AND y -> z"
        strcpy(wire1, token);
        token = strtok(NULL, " ");
        strcpy(op, token);
        token = strtok(NULL, " ");
        strcpy(wire2, token);
        token = strtok(NULL, " ");
        strcpy(wire_out, token);
        
        value1 = get_wire_value(wire1);
        value2 = get_wire_value(wire2);
        
        set_wire_value(wire_out, execute_operation(op, value1, value2));
    }
}

int main() {
    FILE *file = fopen("input.txt", "r");
    char line[256];
    
    while (fgets(line, sizeof(line), file)) {
        line[strcspn(line, "\n")] = 0;  // Remove the newline at the end of each line
        process_line(line);
    }
    
    fclose(file);

    // Now, print the value of the wires starting with "z"
    printf("%d\n", get_wire_value("z"));
    return 0;
}
