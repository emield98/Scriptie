import json
import subprocess
import os
import csv
import sys
import datetime
from typing import Dict, Any

# Define the base directory for all code files
BASE_DIR = "/home/scriptie/Scriptie/"

# Paths to the required directories and files
JSON_PATH = os.path.join(BASE_DIR, "input_answers/answers2024.json")
INPUT_FILENAME = os.path.join(BASE_DIR, "input.txt")
CSV_REPORT_PATH = os.path.join(BASE_DIR, "Reports/trans_test/Check_Report.csv")
LOG_FILE_PATH = os.path.join(BASE_DIR, "Reports/trans_test/execution_log.txt")

# ANSI color codes for terminal output
class Colors:
    GREEN = "\033[92m"
    YELLOW = "\033[93m"
    RED = "\033[91m"
    BLUE = "\033[94m"
    RESET = "\033[0m"
    BOLD = "\033[1m"

def log_message(message: str, log_file: str = LOG_FILE_PATH) -> None:
    """Write a message to both console and log file with timestamp"""
    timestamp = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    print(f"[{timestamp}] {message}")
    log_dir = os.path.dirname(log_file)
    if not os.path.exists(log_dir):
        os.makedirs(log_dir)
    clean_message = message
    for color_code in [Colors.GREEN, Colors.YELLOW, Colors.RED, Colors.BLUE, Colors.RESET, Colors.BOLD]:
        clean_message = clean_message.replace(color_code, "")
    with open(log_file, "a") as f:
        f.write(f"[{timestamp}] {clean_message}\n")

def ensure_dir_exists(path: str) -> None:
    """Ensure the directory exists for the given path"""
    directory = os.path.dirname(path)
    if not os.path.exists(directory):
        os.makedirs(directory)
        log_message(f"Created directory: {directory}")

def write_csv_header() -> None:
    """Write the CSV header with an enhanced column structure"""
    ensure_dir_exists(CSV_REPORT_PATH)
    with open(CSV_REPORT_PATH, mode='w', newline='') as file:
        writer = csv.writer(file)
        writer.writerow([
            "Source Language", 
            "Target Language", 
            "Day", 
            "Part", 
            "Attempt", 
            "Output", 
            "Expected Output", 
            "Status", 
            "Error Message", 
            "Execution Time (s)",
            "Date/Time"
        ])
    log_message(f"{Colors.BLUE}CSV report initialized at {CSV_REPORT_PATH}{Colors.RESET}")

def write_csv_row(
    source_lang: str,
    target_lang: str,
    day: str, 
    part: str, 
    output: str, 
    expected_output: str, 
    status: str, 
    error: str, 
    attempt: str = "initial_attempt",
    execution_time: float = 0.0
) -> None:
    """Write a row to the CSV report with execution details"""
    timestamp = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    with open(CSV_REPORT_PATH, mode='a', newline='') as file:
        writer = csv.writer(file)
        writer.writerow([
            source_lang,
            target_lang,
            day, 
            part, 
            attempt, 
            output, 
            expected_output, 
            status, 
            error, 
            f"{execution_time:.3f}",
            timestamp
        ])
    status_colored = status
    if status == "CORRECT":
        status_colored = f"{Colors.GREEN}CORRECT{Colors.RESET}"
    elif status == "INCORRECT":
        status_colored = f"{Colors.YELLOW}INCORRECT{Colors.RESET}"
    elif "ERROR" in status or status == "TIMEOUT" or status == "SCRIPT_NOT_FOUND":
        status_colored = f"{Colors.RED}{status}{Colors.RESET}"
    log_message(f"{source_lang} to {target_lang} - Day {day} Part {part}: {status_colored} - Time: {execution_time:.3f}s")

def run_translation(source_lang: str, target_lang: str, day_str: str, input_data: str, expected_output: str, is_part2: bool = False) -> None:
    """Run and verify translation tests"""
    start_time = datetime.datetime.now()
    label = "Part 2" if is_part2 else "Part 1"
    day_str = str(day_str).zfill(2)
    filename = f"{source_lang}_to_{target_lang}/output_{day_str}{'b' if is_part2 else 'a'}.{target_lang}"
    path = f"ChatGPT/trans_test/{filename}"
    full_path = os.path.join(BASE_DIR, path)

    log_message(f"{Colors.BOLD}Checking {source_lang} to {target_lang} translation for Day {day_str} {label}{Colors.RESET}")
    if not os.path.exists(full_path):
        log_message(f"  {Colors.RED}✗ No source file found: {filename}{Colors.RESET}")
        write_csv_row(source_lang, target_lang, day_str, label, "N/A", expected_output, "SCRIPT_NOT_FOUND", "Source file not found", "initial_attempt")
        return

    with open(INPUT_FILENAME, "w") as f:
        f.write(input_data)

    try:
        log_message(f"  → Running {target_lang} program: {filename}")
        run_process = subprocess.run(
            [sys.executable, full_path] if target_lang == "python" else ["gcc", full_path, "-o", "output_binary"] if target_lang == "c" else ["clojure", "-M", full_path],
            capture_output=True,
            text=True,
            timeout=60,
            cwd=BASE_DIR
        )
        output = run_process.stdout.strip()
        error = run_process.stderr.strip()
        if error:
            log_message(f"  {Colors.RED}✗ Runtime error: {error}{Colors.RESET}")
            execution_time = (datetime.datetime.now() - start_time).total_seconds()
            write_csv_row(source_lang, target_lang, day_str, label, output if output else "N/A", expected_output, "ERROR", error, "initial_attempt", execution_time)
            return
    except subprocess.TimeoutExpired:
        log_message(f"  {Colors.RED}✗ Execution timed out (>60s){Colors.RESET}")
        execution_time = 60.0
        write_csv_row(source_lang, target_lang, day_str, label, "N/A", expected_output, "TIMEOUT", "Execution timed out after 60 seconds", "initial_attempt", execution_time)
        return
    except Exception as e:
        log_message(f"  {Colors.RED}✗ Execution error: {str(e)}{Colors.RESET}")
        execution_time = (datetime.datetime.now() - start_time).total_seconds()
        write_csv_row(source_lang, target_lang, day_str, label, "N/A", expected_output, "RUNTIME_ERROR", str(e), "initial_attempt", execution_time)
        return
    finally:
        if os.path.exists(INPUT_FILENAME):
            os.remove(INPUT_FILENAME)

    if not output:
        log_message(f"  {Colors.RED}✗ Program produced no output{Colors.RESET}")
        execution_time = (datetime.datetime.now() - start_time).total_seconds()
        write_csv_row(source_lang, target_lang, day_str, label, "", expected_output, "EMPTY_OUTPUT", "Program produced no output", "initial_attempt", execution_time)
        return

    output_clean = output.replace(" ", "").replace("[", "").replace("]", "").strip()
    expected_clean = str(expected_output).replace(" ", "").replace("[", "").replace("]", "").strip()
    status = "CORRECT" if output_clean == expected_clean else "INCORRECT"
    execution_time = (datetime.datetime.now() - start_time).total_seconds()
    if status == "CORRECT":
        log_message(f"  {Colors.GREEN}✓ Output matches expected: {output}{Colors.RESET}")
    else:
        log_message(f"  {Colors.YELLOW}✗ Output does not match expected:{Colors.RESET}")
        log_message(f"    - Got:      {output}")
        log_message(f"    - Expected: {expected_output}")
    write_csv_row(source_lang, target_lang, day_str, label, output, expected_output, status, "", "initial_attempt", execution_time)

def main() -> None:
    """Main function to run all translation tests"""
    print(f"\n{Colors.BOLD}{'='*80}{Colors.RESET}")
    print(f"{Colors.BOLD}TRANSLATION TEST CHECKER{Colors.RESET}")
    print(f"{Colors.BOLD}{'='*80}{Colors.RESET}\n")
    with open(LOG_FILE_PATH, "w") as f:
        f.write(f"Execution started at {datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n")
    write_csv_header()
    if not os.path.exists(JSON_PATH):
        log_message(f"{Colors.RED}ERROR: Input answers file not found at {JSON_PATH}{Colors.RESET}")
        return
    try:
        with open(JSON_PATH) as f:
            data = json.load(f)
        log_message(f"Loaded {len(data)} puzzle entries from {JSON_PATH}")
        for i, entry in enumerate(data):
            day_str = str(entry["day"]).zfill(2)
            input_data = entry["input"]
            part1 = entry.get("part1", "")
            part2 = entry.get("part2", "")
            progress = (i + 1) / len(data) * 100
            log_message(f"\n{Colors.BOLD}Processing Day {day_str} [{progress:.1f}%]{Colors.RESET}")
            for source_lang in ["c", "python", "clojure"]:
                for target_lang in ["c", "python", "clojure"]:
                    if source_lang != target_lang:
                        run_translation(source_lang, target_lang, day_str, input_data, part1, is_part2=False)
                        run_translation(source_lang, target_lang, day_str, input_data, part2, is_part2=True)
        if os.path.exists(INPUT_FILENAME):
            os.remove(INPUT_FILENAME)
        log_message(f"\nExecution completed at {datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    except json.JSONDecodeError:
        log_message(f"{Colors.RED}ERROR: Invalid JSON format in {JSON_PATH}{Colors.RESET}")
    except Exception as e:
        log_message(f"{Colors.RED}ERROR: An unexpected error occurred: {str(e)}{Colors.RESET}")

if __name__ == "__main__":
    main()
