# ChatGPT-generated solution will go here.
def main():
    with open("input.txt") as f:
        sections = f.read().strip().split("\n\n")

    towel_patterns = sections[0].split(", ")
    designs = sections[1].splitlines()

    towel_set = set(towel_patterns)
    memo = {}

    def can_form(design):
        if design in memo:
            return memo[design]
        if design == "":
            return True
        for pattern in towel_set:
            if design.startswith(pattern):
                if can_form(design[len(pattern):]):
                    memo[design] = True
                    return True
        memo[design] = False
        return False

    count = sum(1 for design in designs if can_form(design))
    print(count)

if __name__ == "__main__":
    main()
