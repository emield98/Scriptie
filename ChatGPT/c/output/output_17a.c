// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_PROGRAM_SIZE 1000

int A, B, C;
int program[MAX_PROGRAM_SIZE];
int program_size;

int get_operand_value(int operand) {
    if (operand >= 0 && operand <= 3) {
        return operand; // Literal operand
    } else if (operand == 4) {
        return A; // Register A
    } else if (operand == 5) {
        return B; // Register B
    } else if (operand == 6) {
        return C; // Register C
    } else {
        return -1; // Invalid operand (not used)
    }
}

void run_program() {
    int ip = 0; // Instruction pointer
    int output_values[MAX_PROGRAM_SIZE];
    int output_count = 0;
    
    while (ip < program_size) {
        int opcode = program[ip];
        int operand = program[ip + 1];
        ip += 2; // Move past opcode and operand

        switch (opcode) {
            case 0: // adv
                A = A / (1 << operand); // A divided by 2^operand
                break;
            case 1: // bxl
                B ^= operand; // Bitwise XOR of B and operand
                break;
            case 2: // bst
                B = operand % 8; // B = operand modulo 8
                break;
            case 3: // jnz
                if (A != 0) {
                    ip = operand; // Jump to operand if A is non-zero
                }
                break;
            case 4: // bxc
                B ^= C; // Bitwise XOR of B and C
                break;
            case 5: // out
                output_values[output_count++] = operand % 8; // Output operand modulo 8
                break;
            case 6: // bdv
                B = A / (1 << operand); // B = A divided by 2^operand
                break;
            case 7: // cdv
                C = A / (1 << operand); // C = A divided by 2^operand
                break;
        }
    }

    // Print output values separated by commas
    for (int i = 0; i < output_count; i++) {
        if (i > 0) {
            printf(",");
        }
        printf("%d", output_values[i]);
    }
    printf("\n");
}

int main() {
    FILE *f = fopen("input.txt", "r");

    // Read initial register values
    fscanf(f, "Register A: %d\n", &A);
    fscanf(f, "Register B: %d\n", &B);
    fscanf(f, "Register C: %d\n", &C);

    // Read program
    program_size = 0;
    while (fscanf(f, "%d,", &program[program_size]) != EOF) {
        program_size++;
    }
    fclose(f);

    run_program();

    return 0;
}
