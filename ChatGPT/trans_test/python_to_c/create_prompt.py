import os

def create_prompts_from_template(base_dir):
    template_path = os.path.join(base_dir, "prompt_template.txt")
    puzzles_dir = "/home/scriptie/Scriptie/aoc_puzzles"
    output_dir = os.path.join(base_dir, "prompts")

    print(f"Base directory: {base_dir}")
    print(f"Template path: {template_path}")
    print(f"Puzzles directory: {puzzles_dir}")

    if not os.path.exists(template_path):
        return "Error: 'prompt_template.txt' not found in base_dir."
    if not os.path.exists(puzzles_dir):
        return "Error: 'aoc_puzzles' directory not found."

    with open(template_path, "r", encoding="utf-8") as f:
        base_prompt = f.read()

    for day in range(1, 26):
        day_str = str(day).zfill(2)
        file_a = os.path.join(puzzles_dir, f"{day_str}a.txt")
        file_b = os.path.join(puzzles_dir, f"{day_str}b.txt")

        os.makedirs(output_dir, exist_ok=True)


        # For part a
        if os.path.exists(file_a):
            with open(file_a, "r", encoding="utf-8") as f:
                desc_a = f.read().strip()
            with open(os.path.join(output_dir, f"prompt_{day_str}a.txt"), "w", encoding="utf-8") as f:
                f.write(base_prompt.replace("{puzzle_text}", desc_a))
        else:
            print(f"Warning: Missing {day_str}a.txt — skipping a.txt")

        # For part b
        if os.path.exists(file_b):
            with open(file_b, "r", encoding="utf-8") as f:
                desc_b = f.read().strip()
            with open(os.path.join(output_dir, f"prompt_{day_str}b.txt"), "w", encoding="utf-8") as f:
                f.write(base_prompt.replace("{puzzle_text}", desc_b))
        else:
            print(f"Note: {day_str}b.txt not found — skipping b.txt")


    return "Prompt files created successfully in the 'prompts' directory."

base_dir = "/home/scriptie/Scriptie/ChatGPT/python"
print("Creating prompts from aoc_puzzles...")
print(create_prompts_from_template(base_dir))
print("Done.")
