# ChatGPT-generated solution will go here.
def main():
    with open("input.txt") as f:
        sections = f.read().strip().split("\n\n")

    towel_patterns = sections[0].split(", ")
    designs = sections[1].splitlines()
    towel_set = set(towel_patterns)

    from functools import lru_cache

    @lru_cache(maxsize=None)
    def count_ways(design):
        if design == "":
            return 1
        total = 0
        for pattern in towel_set:
            if design.startswith(pattern):
                total += count_ways(design[len(pattern):])
        return total

    total_ways = sum(count_ways(design) for design in designs)
    print(total_ways)

if __name__ == "__main__":
    main()
