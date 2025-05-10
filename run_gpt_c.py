import json
import subprocess
import os

BASE_DIR = "/home/scriptie/Scriptie/"
JSON_PATH = "input_answers/answers2024.json"
REPORT_JSON_PATH = "Reports/C/Report_C_Direct_Attempt.json"

# Ensure the report directory exists
if not os.path.exists(os.path.dirname(REPORT_JSON_PATH)):
    os.makedirs(os.path.dirname(REPORT_JSON_PATH))

# Print current working directory
print("Current working directory:", os.getcwd())

def run_day(day_str, input_data, expected_output, is_part2=False, report_data=None, compile_timeout=60, run_timeout=60):
    label = "Part 2" if is_part2 else "Part 1"
    part_suffix = "b" if is_part2 else "a"
    script_file = f"output_{day_str}{part_suffix}.c"
    script_path = os.path.join(BASE_DIR, "ChatGPT/c/output/", script_file)

    result = {
        "day": day_str,
        "part": label,
        "script_file": script_file,
        "status": "",
        "output": "",
        "expected": expected_output
    }

    if not os.path.exists(script_path):
        result["status"] = "SCRIPT_NOT_RUN: Script file not found"
        if day_str not in report_data:
            report_data[day_str] = {}
        report_data[day_str][f"c_1st_attempt" if not is_part2 else f"c_2nd_attempt"] = result
        print(f"Script not found for day {day_str} {label}")
        return

    # Write input.txt in script's directory
    input_path = os.path.join(BASE_DIR, "input.txt")
    with open(input_path, "w") as f:
        f.write(input_data)

    try:
        # Compile the C program
        compile_command = f"gcc {script_path} -o {os.path.join(BASE_DIR, 'output_program')}"
        compile_process = subprocess.run(compile_command, shell=True, capture_output=True, text=True, cwd=BASE_DIR, timeout=compile_timeout)

        if compile_process.returncode != 0:
            result["status"] = f"SCRIPT_NOT_RUN: Compilation error - {compile_process.stderr.strip()}"
            if day_str not in report_data:
                report_data[day_str] = {}
            report_data[day_str][f"c_1st_attempt" if not is_part2 else f"c_2nd_attempt"] = result
            print(f"Compilation error for day {day_str} {label}")
            return

        # Run the compiled C program
        run_command = os.path.join(BASE_DIR, "output_program")
        run_process = subprocess.run(run_command, capture_output=True, text=True, timeout=run_timeout, cwd=BASE_DIR)
        output = run_process.stdout.strip()
    except subprocess.TimeoutExpired as e:
        result["status"] = f"TIMEOUT: {e}"
        if day_str not in report_data:
            report_data[day_str] = {}
        report_data[day_str][f"c_1st_attempt" if not is_part2 else f"c_2nd_attempt"] = result
        print(f"Timeout error for day {day_str} {label}")
        return
    except Exception as e:
        result["status"] = f"RUNTIME_ERROR: {e}"
        if day_str not in report_data:
            report_data[day_str] = {}
        report_data[day_str][f"c_1st_attempt" if not is_part2 else f"c_2nd_attempt"] = result
        print(f"Runtime error for day {day_str} {label}")
        return
    finally:
        if os.path.exists(input_path):
            os.remove(input_path)

    result["output"] = output

    # Normalize whitespace/brackets
    output_clean = output.replace(" ", "").replace("[", "").replace("]", "").strip()
    expected_clean = str(expected_output).replace(" ", "").replace("[", "").replace("]", "").strip()

    if output_clean == expected_clean:
        result["status"] = "CORRECT"
    else:
        result["status"] = "WRONG_OUTPUT"

    if day_str not in report_data:
        report_data[day_str] = {}

    report_data[day_str][f"c_1st_attempt" if not is_part2 else f"c_2nd_attempt"] = result
    print(f"Result for day {day_str} {label}: {result['status']}")

# Load all puzzle inputs and answers
if not os.path.exists(JSON_PATH):
    print(f"ERROR: JSON file not found at {JSON_PATH}")
else:
    with open(JSON_PATH, "r", encoding="utf-8") as f:
        data = json.load(f)

    if not data:
        print("ERROR: JSON file is empty or not properly loaded.")
    else:
        report_data = {}

        # Run all days
        for entry in data:
            day_str = str(entry["day"]).zfill(2)
            input_data = entry["input"]
            part1 = entry.get("part1", "")
            part2 = entry.get("part2", "")

            # Ensure each day has an entry in the report
            if day_str not in report_data:
                report_data[day_str] = {}

            run_day(day_str, input_data, part1, is_part2=False, report_data=report_data)
            run_day(day_str, input_data, part2, is_part2=True, report_data=report_data)

        # Write full report as JSON
        with open(REPORT_JSON_PATH, "w", encoding="utf-8") as f:
            json.dump(report_data, f, indent=4)

        print("\nReport written to JSON.")
