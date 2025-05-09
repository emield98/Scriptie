import os

def create_output_placeholders(prompt_dir, output_dir):
    os.makedirs(output_dir, exist_ok=True)

    for filename in os.listdir(prompt_dir):
        if filename.startswith("prompt_") and filename.endswith(".txt"):
            base_name = filename.replace("prompt_", "").replace(".txt", "")
            output_filename = f"output_{base_name}.c"
            output_path = os.path.join(output_dir, output_filename)

            if not os.path.exists(output_path):
                with open(output_path, "w", encoding="utf-8") as f:
                    f.write("// ChatGPT-generated solution will go here.\n")
                print(f"Created {output_filename}")
            else:
                print(f"Skipped existing {output_filename}")

    return "Done creating output Python files."

# Example usage:
prompt_dir = "/home/scriptie/Scriptie/ChatGPT/c/prompts"
output_dir = "/home/scriptie/Scriptie/ChatGPT/c/output"

print(create_output_placeholders(prompt_dir, output_dir))
