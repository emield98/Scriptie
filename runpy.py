import json
import subprocess
import os
import shutil


BASE_DIR = "ChatGPT/python"
SOLUTIONS_DIR = "solutions/python"
JSON_PATH = os.path.join("in_out_answers/answers2024.json")
print("Running all Python puzzles and verifying output...\n")

def run_day(day_str, input_data, expected_output, is_part2=False):
    label = "Part 2" if is_part2 else "Part 1"
    part_suffix = "b" if is_part2 else "a"
    day_num = int(day_str)
    if day_num < 10:
        day_num = f"0{day_num}"
    script_file = f"{day_num}{part_suffix}.py"
    script_path = os.path.join(BASE_DIR, script_file)
    print(script_path)
    if not os.path.exists(script_path):
        print(f"{day_str} {label}: No script found: {script_path}")
        return

    # Write input.txt in script's own folder
    script_dir = os.path.dirname(script_path)
    input_file_path = os.path.join(script_dir, "input.txt")
    with open(input_file_path, "w") as f:
        f.write(input_data)

    # Run the script from its directory
    try:
        run = subprocess.run(
            [os.sys.executable, script_file],
            capture_output=True,
            text=True,
            timeout=30,
            cwd=script_dir  # ensure relative input paths work
        )
        output = run.stdout.strip()
    except Exception as e:
        print(f"{day_str} {label}: Runtime error: {e}")
        return
    finally:
        if os.path.exists(input_file_path):
            os.remove(input_file_path)

    print(f"  RAW Output:   {repr(output)}")
    print(f"  RAW Expected: {repr(str(expected_output))}")

    # Clean for comparison
    output_clean = output.replace(" ", "").replace("[", "").replace("]", "")
    expected_clean = str(expected_output).replace(" ", "").replace("[", "").replace("]", "")

    is_correct = output_clean == expected_clean
    status = "[GOOD]" if is_correct else "[FAILED]"
    print(f"{day_str} {label}: {status} Output = {output} | Expected = {expected_output}")

# Load JSON
with open(JSON_PATH) as f:
    data = json.load(f)

# Loop through all days
for entry in data:
    day_str = str(entry["day"]).zfill(2)
    input_data = entry["input"]
    part1 = entry.get("part1", "")
    part2 = entry.get("part2", "")

    run_day(day_str, input_data, part1, is_part2=False)
    run_day(day_str, input_data, part2, is_part2=True)
 # type: ignore