#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#define MAX_PROGRAM 1000
#define MAX_OUTPUT 1000

int reg[3] = {0}; // A = 0, B = 1, C = 2
int program[MAX_PROGRAM];
int program_len = 0;
char output[MAX_OUTPUT * 2];
int output_pos = 0;

int combo_value(int op) {
    if (op <= 3) return op;
    else if (op == 4) return reg[0];
    else if (op == 5) return reg[1];
    else if (op == 6) return reg[2];
    else {
        fprintf(stderr, "Invalid combo operand: %d\n", op);
        exit(1);
    }
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    char line[256];
    while (fgets(line, sizeof(line), file)) {
        if (strncmp(line, "Register", 8) == 0) {
            char reg_name;
            int val;
            sscanf(line, "Register %c %d", &reg_name, &val);
            if (reg_name == 'A') reg[0] = val;
            else if (reg_name == 'B') reg[1] = val;
            else if (reg_name == 'C') reg[2] = val;
        } else if (strncmp(line, "Program:", 8) == 0) {
            char *ptr = strchr(line, ':') + 1;
            while (*ptr) {
                while (*ptr && !isdigit(*ptr) && *ptr != '-') ptr++;
                if (*ptr == '\0') break;
                int num;
                sscanf(ptr, "%d", &num);
                program[program_len++] = num;
                while (*ptr && (isdigit(*ptr) || *ptr == '-')) ptr++;
            }
        }
    }
    fclose(file);

    int ip = 0;
    while (ip < program_len) {
        int opcode = program[ip];
        if (ip + 1 >= program_len) break;
        int operand = program[ip + 1];

        if (opcode == 0) { // adv
            int denom = 1 << combo_value(operand);
            reg[0] = reg[0] / denom;
        } else if (opcode == 1) { // bxl
            reg[1] ^= operand;
        } else if (opcode == 2) { // bst
            reg[1] = combo_value(operand) % 8;
        } else if (opcode == 3) { // jnz
            if (reg[0] != 0) {
                ip = operand;
                continue;
            }
        } else if (opcode == 4) { // bxc
            reg[1] ^= reg[2];
        } else if (opcode == 5) { // out
            int val = combo_value(operand) % 8;
            output_pos += sprintf(output + output_pos, "%d,", val);
        } else if (opcode == 6) { // bdv
            int denom = 1 << combo_value(operand);
            reg[1] = reg[0] / denom;
        } else if (opcode == 7) { // cdv
            int denom = 1 << combo_value(operand);
            reg[2] = reg[0] / denom;
        } else {
            fprintf(stderr, "Unknown opcode: %d\n", opcode);
            exit(1);
        }

        ip += 2;
    }

    if (output_pos > 0 && output[output_pos - 1] == ',') {
        output[output_pos - 1] = '\0';
    }
    printf("%s\n", output);
    return 0;
}
