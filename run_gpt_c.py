import json
import subprocess
import os

BASE_DIR = "/home/scriptie/Scriptie/ChatGPT/c/output"
JSON_PATH = "input_answers/answers2024.json"
REPORT_PATH = "Reports/C/Report_C_Direct_Attempt.txt"

import os
print("Current working directory:", os.getcwd())


def run_day(day_str, input_data, expected_output, is_part2=False, report_lines=None):
    label = "Part 2" if is_part2 else "Part 1"
    part_suffix = "b" if is_part2 else "a"
    script_file = f"output_{day_str}{part_suffix}.c"
    script_path = os.path.join(BASE_DIR, script_file)


    result = f"\n=== Checking {script_file} ({label}) ==="

    if not os.path.exists(script_path):
        result += f"\nScript not found: {script_path}"
        report_lines.append(result)
        print(result)
        return

    # Write input.txt in script's directory
    input_path = os.path.join(BASE_DIR, "input.txt")
    with open(input_path, "w") as f:
        f.write(input_data)

    # Compile and run the C script in BASE_DIR with input.txt
    try:
        # Compile the C program
        compile_command = f"gcc {script_path} -o {os.path.join(BASE_DIR, 'output_program')}"
        compile_process = subprocess.run(compile_command, shell=True, capture_output=True, text=True, cwd=BASE_DIR)
        
        if compile_process.returncode != 0:
            result += f"\nCompilation error: {compile_process.stderr}"
            report_lines.append(result)
            print(result)
            return

        # Run the compiled C program
        run_command = os.path.join(BASE_DIR, "output_program")
        run_process = subprocess.run(run_command, capture_output=True, text=True, timeout=60, cwd=BASE_DIR)
        output = run_process.stdout.strip()
    except Exception as e:
        result += f"\nRuntime error: {e}"
        report_lines.append(result)
        print(result)
        return
    finally:
        if os.path.exists(input_path):
            os.remove(input_path)

    result += f"\nOutput:   {repr(output)}"
    result += f"\nExpected: {repr(expected_output)}"

    # Normalize whitespace/brackets
    output_clean = output.replace(" ", "").replace("[", "").replace("]", "").strip()
    expected_clean = str(expected_output).replace(" ", "").replace("[", "").replace("]", "").strip()

    if output_clean == expected_clean:
        result += f"\n---CORRECT---"
    else:
        result += f"\n---INCORRECT---"

    report_lines.append(result)
    print(result)

# Load all puzzle inputs and answers
if not os.path.exists(JSON_PATH):
    print(f"ERROR: JSON file not found at {JSON_PATH}")
else:
    with open(JSON_PATH, "r", encoding="utf-8") as f:
        data = json.load(f)

    report_lines = ["== ChatGPT C Output Check Report =="]

    # Run all days
    for entry in data:
        day_str = str(entry["day"]).zfill(2)
        input_data = entry["input"]
        part1 = entry.get("part1", "")
        part2 = entry.get("part2", "")

        run_day(day_str, input_data, part1, is_part2=False, report_lines=report_lines)
        run_day(day_str, input_data, part2, is_part2=True, report_lines=report_lines)

    # Write full report
    with open(REPORT_PATH, "w", encoding="utf-8") as f:
        f.write("\n".join(report_lines))

    print("\nReport written.")
