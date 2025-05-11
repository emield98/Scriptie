// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#define MAX_PRESSED 100

typedef struct {
    int A_x, A_y, A_cost;
    int B_x, B_y, B_cost;
    long long prize_x, prize_y;
} ClawMachine;

int min_tokens_to_win(ClawMachine *machine) {
    for (int a = 0; a <= MAX_PRESSED; a++) {
        for (int b = 0; b <= MAX_PRESSED; b++) {
            long long x_move = a * machine->A_x + b * machine->B_x;
            long long y_move = a * machine->A_y + b * machine->B_y;

            if (x_move == machine->prize_x && y_move == machine->prize_y) {
                return a * machine->A_cost + b * machine->B_cost;
            }
        }
    }
    return -1;  // impossible
}

int main() {
    FILE *f = fopen("input.txt", "r");
    ClawMachine machines[100];
    int machine_count = 0;

    while (fscanf(f, "Button A: X+%d, Y+%d\nButton B: X+%d, Y+%d\nPrize: X=%lld, Y=%lld\n",
                  &machines[machine_count].A_x, &machines[machine_count].A_y,
                  &machines[machine_count].B_x, &machines[machine_count].B_y,
                  &machines[machine_count].prize_x, &machines[machine_count].prize_y) != EOF) {
        machines[machine_count].A_cost = 3;
        machines[machine_count].B_cost = 1;
        machines[machine_count].prize_x += 10000000000000;
        machines[machine_count].prize_y += 10000000000000;
        machine_count++;
    }
    fclose(f);

    int total_tokens = 0;
    for (int i = 0; i < machine_count; i++) {
        int tokens = min_tokens_to_win(&machines[i]);
        if (tokens != -1) {
            total_tokens += tokens;
        }
    }

    printf("%d\n", total_tokens);
    return 0;
}
