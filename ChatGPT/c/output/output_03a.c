// ChatGPT-generated solution will go here.
#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>

int main() {
    FILE *f = fopen("input.txt", "r");
    char ch;
    int total = 0;
    
    while ((ch = fgetc(f)) != EOF) {
        if (ch == 'm') {
            if (fgetc(f) == 'u' && fgetc(f) == 'l' && fgetc(f) == '(') {
                int x = 0, y = 0;
                // Read first number
                while (isdigit(ch = fgetc(f))) {
                    x = x * 10 + (ch - '0');
                }
                // Skip comma
                fgetc(f);
                // Read second number
                while (isdigit(ch = fgetc(f))) {
                    y = y * 10 + (ch - '0');
                }
                // Skip closing parenthesis
                fgetc(f);
                total += x * y;
            }
        }
    }

    fclose(f);
    printf("%d\n", total);
    return 0;
}
