#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <inttypes.h>
#include <string.h>
#include <assert.h>

#define MEMZ 32
#define BUFZ 64

enum { ADV, BXL, BST, JNZ, BXC, OUT, BDV, CDV };

struct vm {
    int64_t mem[MEMZ]; int nm, pc;
    int64_t a, b, c;
} vm;

static int64_t step_and_get_output(void) {
    int64_t op, ar, ac;

    assert(vm.pc >= 0);
    assert(vm.pc + 2 <= MEMZ);
    if (vm.pc >= vm.nm) return -1;

    op = vm.mem[vm.pc++];
    ar = vm.mem[vm.pc++];
    ac = (ar == 4) ? vm.a : (ar == 5) ? vm.b : (ar == 6) ? vm.c : ar;

    switch (op) {
        case ADV: vm.a = vm.a >> ac; break;
        case BDV: vm.b = vm.a >> ac; break;
        case CDV: vm.c = vm.a >> ac; break;
        case BXL: vm.b = vm.b ^ ar; break;
        case BXC: vm.b = vm.b ^ vm.c; break;
        case BST: vm.b = ac % 8; break;
        case JNZ: if (vm.a) vm.pc = (int)ar; break;
        case OUT: return ac % 8;
        default: assert(!"invalid opcode");
    }

    return -1;
}

static int64_t recur_p2(int64_t a0, int pos) {
    int64_t a, a1, i, out;

    if (pos >= vm.nm)
        return a0 >> 3;

    for (i = 0; i < 8; i++) {
        vm.a = a = a0 + i;
        vm.b = vm.c = vm.pc = 0;

        while ((out = step_and_get_output()) == -1)
            ;

        if (out == vm.mem[vm.nm - pos - 1])
            if ((a1 = recur_p2(a << 3, pos + 1)))
                return a1;
    }

    return 0;
}

int main(int argc, char* argv[]) {
    char b[BUFZ], *tok, *rest;

    FILE* f = stdin;
    if (argc > 1) {
        f = fopen(argv[1], "r");
        assert(f);
    }

    fgets(b, BUFZ, f); sscanf(b, "Register A: %"PRId64, &vm.a);
    fgets(b, BUFZ, f); sscanf(b, "Register B: %"PRId64, &vm.b);
    fgets(b, BUFZ, f); sscanf(b, "Register C: %"PRId64, &vm.c);
    fgets(b, BUFZ, f); // blank line

    assert(vm.b == 0);
    assert(vm.c == 0);

    rest = fgets(b, sizeof(b), f);
    strtok(rest, ":");  // Skip "Program:"

    while ((tok = strtok(NULL, ","))) {
        assert(vm.nm < MEMZ);
        vm.mem[vm.nm++] = atoi(tok);
    }

    if (f != stdin) fclose(f);

    printf("%" PRId64 "\n", recur_p2(0, 0));
    return 0;
}
