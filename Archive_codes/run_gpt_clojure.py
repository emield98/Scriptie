import json
import subprocess
import os

JSON_PATH = "input_answers/answers2024.json"
INPUT_FILENAME = "input.txt"

print("Running all puzzles and verifying output...\n")

def run_day(day_str, input_data, expected_output, is_part2=False):
    label = "Part 2" if is_part2 else "Part 1"
    day_str = str(day_str).zfill(2)
    filename = f"output_{day_str}{'b' if is_part2 else 'a'}.clj"
    path = f"ChatGPT/clojure/output/{filename}"
    print(path)
    # Check if source file exists
    if not os.path.exists(path):
        print(f"{day_str} {label}: No source file found: {filename}")
        return

    # Write input to file
    with open(INPUT_FILENAME, "w") as f:
        f.write(input_data)

    # Run the Clojure program
    try:
        ns_day = f"output_{day_str}{'b' if is_part2 else 'a'}"
        run = subprocess.run(
            ["clojure", "-M", "-m", ns_day, INPUT_FILENAME],
            capture_output=True, text=True)
        
        output = run.stdout.strip()
        error = run.stderr.strip()

        # Print the error if there is one
        if error:
            print(f"{day_str} {label}: Error output: {error}")
        else:
            print(f"  RAW Output:   {repr(output)}")
            print(f"  RAW Expected: {repr(str(expected_output))}")

            # Compare output to expected
            output_clean = output.replace(" ", "").replace("[", "").replace("]", "")
            expected_clean = str(expected_output).replace(" ", "").replace("[", "").replace("]", "")

            is_correct = output_clean == expected_clean
            status = "[GOOD]" if is_correct else "[WRONG]"
            print(f"{day_str} {label}: {status} Output = {output} | Expected = {expected_output}")

    except subprocess.CalledProcessError as e:
        print(f"{day_str} {label}: Runtime error")
        print(str(e))

# Load the puzzle answers
with open(JSON_PATH) as f:
    data = json.load(f)

# Run for all days
for entry in data:
    day_str = str(entry["day"]).zfill(2)
    input_data = entry["input"]
    part1 = entry.get("part1", "")
    part2 = entry.get("part2", "")

    run_day(day_str, input_data, part1, is_part2=False)
    run_day(day_str, input_data, part2, is_part2=True)

# Clean up
if os.path.exists(INPUT_FILENAME):
    os.remove(INPUT_FILENAME)
