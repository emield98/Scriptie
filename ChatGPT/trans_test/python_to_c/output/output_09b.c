#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#define MAX_BLOCKS 10000
#define MAX_FILES 1000

typedef struct {
    int start;
    int length;
} FileRange;

char blocks[MAX_BLOCKS];
FileRange file_ranges[MAX_FILES];
int total_len = 0;
int file_count = 0;

void get_free_spans(int *starts, int *lengths, int *count) {
    int i = 0, span_count = 0;
    while (i < total_len) {
        if (blocks[i] == '.') {
            int start = i;
            while (i < total_len && blocks[i] == '.') i++;
            starts[span_count] = start;
            lengths[span_count] = i - start;
            span_count++;
        } else {
            i++;
        }
    }
    *count = span_count;
}

int compare_desc(const void *a, const void *b) {
    return (*(int *)b - *(int *)a);
}

int main() {
    FILE *file = fopen("input.txt", "r");
    if (!file) return 1;

    char line[MAX_BLOCKS];
    if (!fgets(line, sizeof(line), file)) return 1;
    fclose(file);

    int i = 0, pos = 0;
    int file_id = 0;
    while (line[i] && line[i] != '\n' && line[i] != '\r') {
        if (!isdigit(line[i])) {
            i++;
            continue;
        }
        int length = line[i] - '0';
        int is_file = (i % 2 == 0);
        if (is_file) {
            for (int j = 0; j < length; j++) {
                blocks[pos + j] = '0' + file_id;
            }
            file_ranges[file_id].start = pos;
            file_ranges[file_id].length = length;
            file_id++;
        } else {
            for (int j = 0; j < length; j++) {
                blocks[pos + j] = '.';
            }
        }
        pos += length;
        i++;
    }
    total_len = pos;
    file_count = file_id;

    int file_ids[MAX_FILES];
    for (int i = 0; i < file_count; i++) {
        file_ids[i] = i;
    }

    qsort(file_ids, file_count, sizeof(int), compare_desc);

    int span_starts[MAX_BLOCKS], span_lengths[MAX_BLOCKS], span_count;

    for (int i = 0; i < file_count; i++) {
        int fid = file_ids[i];
        int start = file_ranges[fid].start;
        int length = file_ranges[fid].length;

        get_free_spans(span_starts, span_lengths, &span_count);

        for (int s = 0; s < span_count; s++) {
            int fstart = span_starts[s];
            int flen = span_lengths[s];
            if (flen >= length && fstart < start) {
                for (int j = 0; j < length; j++) {
                    blocks[fstart + j] = '0' + fid;
                }
                for (int j = 0; j < length; j++) {
                    blocks[start + j] = '.';
                }
                file_ranges[fid].start = fstart;
                break;
            }
        }
    }

    long long checksum = 0;
    for (int idx = 0; idx < total_len; idx++) {
        if (blocks[idx] != '.') {
            checksum += idx * (blocks[idx] - '0');
        }
    }

    printf("%lld\n", checksum);
    return 0;
}
