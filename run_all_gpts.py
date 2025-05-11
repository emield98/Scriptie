import json
import subprocess
import os
import csv
import sys
import datetime
from typing import Dict, Any, Optional

# Define the base directory for all code files
BASE_DIR = "/home/scriptie/Scriptie/"

# Paths to the required directories and files
JSON_PATH = os.path.join(BASE_DIR, "input_answers/answers2024.json")
INPUT_FILENAME = os.path.join(BASE_DIR, "input.txt")
CSV_REPORT_PATH = os.path.join(BASE_DIR, "Reports/Direct_Attempt/Check_Report.csv")
LOG_FILE_PATH = os.path.join(BASE_DIR, "Reports/Direct_Attempt/execution_log.txt")

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
    
    # Print to console with colors
    print(f"[{timestamp}] {message}")
    
    # Create directory if it doesn't exist
    log_dir = os.path.dirname(log_file)
    if not os.path.exists(log_dir):
        os.makedirs(log_dir)
    
    # Remove ANSI color codes for log file
    clean_message = message
    for color_code in [Colors.GREEN, Colors.YELLOW, Colors.RED, Colors.BLUE, Colors.RESET, Colors.BOLD]:
        clean_message = clean_message.replace(color_code, "")
    
    # Write to log file without color codes
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
            "Day", 
            "Part", 
            "Language", 
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
    day: str, 
    part: str, 
    language: str, 
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
            day, 
            part, 
            language, 
            attempt, 
            output, 
            expected_output, 
            status, 
            error, 
            f"{execution_time:.3f}",
            timestamp
        ])
    
    # Color code the status for terminal output
    status_colored = status
    if status == "CORRECT":
        status_colored = f"{Colors.GREEN}CORRECT{Colors.RESET}"
    elif status == "INCORRECT":
        status_colored = f"{Colors.YELLOW}INCORRECT{Colors.RESET}"
    elif "ERROR" in status or status == "TIMEOUT" or status == "SCRIPT_NOT_FOUND":
        status_colored = f"{Colors.RED}{status}{Colors.RESET}"
    
    log_message(f"Day {day} Part {part} ({language}): {status_colored} - Time: {execution_time:.3f}s")

def run_c(day_str: str, input_data: str, expected_output: str, is_part2: bool = False) -> None:
    """Run and verify C code with improved error handling and reporting"""
    start_time = datetime.datetime.now()
    label = "Part 2" if is_part2 else "Part 1"
    day_str = str(day_str).zfill(2)
    filename = f"output_{day_str}{'b' if is_part2 else 'a'}.c"
    path = f"ChatGPT/Direct_Attempt/c/output/{filename}"
    full_path = os.path.join(BASE_DIR, path)

    log_message(f"{Colors.BOLD}Checking C solution for Day {day_str} {label}{Colors.RESET}")
    
    if not os.path.exists(full_path):
        log_message(f"  {Colors.RED}✗ No source file found: {filename}{Colors.RESET}")
        write_csv_row(day_str, label, "C", "N/A", expected_output, "SCRIPT_NOT_FOUND", "Source file not found", "initial_attempt")
        return

    # Write input to file
    with open(INPUT_FILENAME, "w") as f:
        f.write(input_data)

    # Temporary output binary
    output_program = os.path.join(BASE_DIR, f"output_program_{day_str}_{('part2' if is_part2 else 'part1')}")
    
    try:
        # Compilation
        log_message(f"  → Compiling C program: {filename}")
        compile_command = f"gcc {full_path} -o {output_program} -Wall"
        compile_process = subprocess.run(
            compile_command, 
            shell=True, 
            capture_output=True, 
            text=True, 
            cwd=BASE_DIR
        )
        
        # Handle compilation errors
        if compile_process.returncode != 0:
            error = compile_process.stderr.strip()
            error_msg = error[:500] + "..." if len(error) > 500 else error  # Truncate long error messages
            error_msg = error_msg.replace("\n", " ")
            log_message(f"  {Colors.RED}✗ Compilation failed: {error_msg}{Colors.RESET}")
            
            execution_time = (datetime.datetime.now() - start_time).total_seconds()
            write_csv_row(
                day_str, 
                label, 
                "C", 
                "N/A", 
                expected_output, 
                "COMPILATION_ERROR", 
                error_msg, 
                "initial_attempt",
                execution_time
            )
            return

        # Execution
        log_message(f"  → Running C program")
        run_process = subprocess.run(
            output_program, 
            capture_output=True, 
            text=True, 
            timeout=60, 
            cwd=BASE_DIR
        )
        
        output = run_process.stdout.strip()
        error = run_process.stderr.strip()
        
        # Check if output is empty and stderr has content
        if not output and error:
            log_message(f"  {Colors.RED}✗ Program produced an error: {error}{Colors.RESET}")
            execution_time = (datetime.datetime.now() - start_time).total_seconds()
            write_csv_row(
                day_str, 
                label, 
                "C", 
                "", 
                expected_output, 
                "RUNTIME_ERROR", 
                error, 
                "initial_attempt",
                execution_time
            )
            return

    except subprocess.TimeoutExpired:
        log_message(f"  {Colors.RED}✗ Execution timed out (>60s){Colors.RESET}")
        execution_time = 60.0  # Timeout duration
        write_csv_row(
            day_str, 
            label, 
            "C", 
            "N/A", 
            expected_output, 
            "TIMEOUT", 
            "Execution timed out after 60 seconds", 
            "initial_attempt",
            execution_time
        )
        return
    except Exception as e:
        log_message(f"  {Colors.RED}✗ Execution error: {str(e)}{Colors.RESET}")
        execution_time = (datetime.datetime.now() - start_time).total_seconds()
        write_csv_row(
            day_str, 
            label, 
            "C", 
            "N/A", 
            expected_output, 
            "RUNTIME_ERROR", 
            str(e), 
            "initial_attempt",
            execution_time
        )
        return
    finally:
        # Clean up
        if os.path.exists(INPUT_FILENAME):
            os.remove(INPUT_FILENAME)
        if os.path.exists(output_program):
            os.remove(output_program)

    # Check if output is empty even without errors
    if not output:
        log_message(f"  {Colors.RED}✗ Program produced no output{Colors.RESET}")
        execution_time = (datetime.datetime.now() - start_time).total_seconds()
        write_csv_row(
            day_str, 
            label, 
            "C", 
            "", 
            expected_output, 
            "EMPTY_OUTPUT", 
            "Program produced no output", 
            "initial_attempt",
            execution_time
        )
        return

    # Normalize output and expected output for comparison
    output_clean = output.replace(" ", "").replace("[", "").replace("]", "").strip()
    expected_clean = str(expected_output).replace(" ", "").replace("[", "").replace("]", "").strip()
    
    # Determine status
    status = "CORRECT" if output_clean == expected_clean else "INCORRECT"
    
    # Calculate execution time
    execution_time = (datetime.datetime.now() - start_time).total_seconds()
    
    # Print result with appropriate color
    if status == "CORRECT":
        log_message(f"  {Colors.GREEN}✓ Output matches expected: {output}{Colors.RESET}")
    else:
        log_message(f"  {Colors.YELLOW}✗ Output does not match expected:{Colors.RESET}")
        log_message(f"    - Got:      {output}")
        log_message(f"    - Expected: {expected_output}")
    
    # Write to CSV
    write_csv_row(
        day_str, 
        label, 
        "C", 
        output, 
        expected_output, 
        status, 
        error if error else "", 
        "initial_attempt",
        execution_time
    )

def run_python(day_str: str, input_data: str, expected_output: str, is_part2: bool = False) -> None:
    """Run and verify Python code with improved error handling and reporting"""
    start_time = datetime.datetime.now()
    label = "Part 2" if is_part2 else "Part 1"
    day_str = str(day_str).zfill(2)
    filename = f"output_{day_str}{'b' if is_part2 else 'a'}.py"
    path = f"ChatGPT/Direct_Attempt/python/output/{filename}"
    full_path = os.path.join(BASE_DIR, path)

    log_message(f"{Colors.BOLD}Checking Python solution for Day {day_str} {label}{Colors.RESET}")
    
    if not os.path.exists(full_path):
        log_message(f"  {Colors.RED}✗ No source file found: {filename}{Colors.RESET}")
        write_csv_row(day_str, label, "Python", "N/A", expected_output, "SCRIPT_NOT_FOUND", "Source file not found", "initial_attempt")
        return

    # Write input to file
    input_path = os.path.join(BASE_DIR, "input.txt")
    with open(input_path, "w") as f:
        f.write(input_data)

    try:
        # Execute Python script
        log_message(f"  → Running Python script: {filename}")
        run_process = subprocess.run(
            [sys.executable, full_path],
            capture_output=True,
            text=True,
            timeout=60,
            cwd=BASE_DIR
        )

        output = run_process.stdout.strip()
        error = run_process.stderr.strip()

        # Handle runtime errors
        if error:
            error_message = error[:500] + "..." if len(error) > 500 else error  # Truncate long error messages
            error_message = error_message.replace("\n", " ")
            log_message(f"  {Colors.RED}✗ Runtime error: {error_message}{Colors.RESET}")
            
            execution_time = (datetime.datetime.now() - start_time).total_seconds()
            write_csv_row(
                day_str, 
                label, 
                "Python", 
                output if output else "N/A", 
                expected_output, 
                "ERROR", 
                error_message, 
                "initial_attempt",
                execution_time
            )
            return

    except subprocess.TimeoutExpired:
        log_message(f"  {Colors.RED}✗ Execution timed out (>60s){Colors.RESET}")
        execution_time = 60.0  # Timeout duration
        write_csv_row(
            day_str, 
            label, 
            "Python", 
            "N/A", 
            expected_output, 
            "TIMEOUT", 
            "Execution timed out after 60 seconds", 
            "initial_attempt",
            execution_time
        )
        return
    except Exception as e:
        log_message(f"  {Colors.RED}✗ Execution error: {str(e)}{Colors.RESET}")
        execution_time = (datetime.datetime.now() - start_time).total_seconds()
        write_csv_row(
            day_str, 
            label, 
            "Python", 
            "N/A", 
            expected_output, 
            "RUNTIME_ERROR", 
            str(e), 
            "initial_attempt",
            execution_time
        )
        return
    finally:
        # Clean up
        if os.path.exists(input_path):
            os.remove(input_path)

    # Check if output is empty
    if not output:
        log_message(f"  {Colors.RED}✗ Program produced no output{Colors.RESET}")
        execution_time = (datetime.datetime.now() - start_time).total_seconds()
        write_csv_row(
            day_str, 
            label, 
            "Python", 
            "", 
            expected_output, 
            "EMPTY_OUTPUT", 
            "Program produced no output", 
            "initial_attempt",
            execution_time
        )
        return

    # Normalize output and expected output for comparison
    output_clean = output.replace(" ", "").replace("[", "").replace("]", "").strip()
    expected_clean = str(expected_output).replace(" ", "").replace("[", "").replace("]", "").strip()
    
    # Determine status
    status = "CORRECT" if output_clean == expected_clean else "INCORRECT"
    
    # Calculate execution time
    execution_time = (datetime.datetime.now() - start_time).total_seconds()
    
    # Print result with appropriate color
    if status == "CORRECT":
        log_message(f"  {Colors.GREEN}✓ Output matches expected: {output}{Colors.RESET}")
    else:
        log_message(f"  {Colors.YELLOW}✗ Output does not match expected:{Colors.RESET}")
        log_message(f"    - Got:      {output}")
        log_message(f"    - Expected: {expected_output}")
    
    # Write to CSV
    write_csv_row(
        day_str, 
        label, 
        "Python", 
        output, 
        expected_output, 
        status, 
        "", 
        "initial_attempt",
        execution_time
    )

def run_clojure(day_str: str, input_data: str, expected_output: str, is_part2: bool = False) -> None:
    """Run and verify Clojure code with improved error handling and reporting"""
    start_time = datetime.datetime.now()
    label = "Part 2" if is_part2 else "Part 1"
    day_str = str(day_str).zfill(2)
    filename = f"output_{day_str}{'b' if is_part2 else 'a'}.clj"
    path = f"ChatGPT/Direct_Attempt/clojure/output/{filename}"
    full_path = os.path.join(BASE_DIR, path)

    log_message(f"{Colors.BOLD}Checking Clojure solution for Day {day_str} {label}{Colors.RESET}")
    
    if not os.path.exists(full_path):
        log_message(f"  {Colors.RED}✗ No source file found: {filename}{Colors.RESET}")
        write_csv_row(day_str, label, "Clojure", "N/A", expected_output, "SCRIPT_NOT_FOUND", "Source file not found", "initial_attempt")
        return

    # Write input to file
    with open(INPUT_FILENAME, "w") as f:
        f.write(input_data)

    try:
        # Execute Clojure script
        log_message(f"  → Running Clojure script: {filename}")
        ns_day = f"output_{day_str}{'b' if is_part2 else 'a'}"
        run_process = subprocess.run(
            ["clojure", "-M", "-m", ns_day, INPUT_FILENAME],
            capture_output=True,
            text=True,
            timeout=60,
            cwd=BASE_DIR
        )
        
        output = run_process.stdout.strip()
        error = run_process.stderr.strip()
        
        # Handle runtime errors
        if error:
            error_message = error[:500] + "..." if len(error) > 500 else error  # Truncate long error messages
            error_message = error_message.replace("\n", " ")
            log_message(f"  {Colors.RED}✗ Runtime error: {error_message}{Colors.RESET}")
            
            execution_time = (datetime.datetime.now() - start_time).total_seconds()
            write_csv_row(
                day_str, 
                label, 
                "Clojure", 
                output if output else "N/A", 
                expected_output, 
                "ERROR", 
                error_message, 
                "initial_attempt",
                execution_time
            )
            return
            
        # Check if return code is non-zero
        if run_process.returncode != 0:
            error_msg = f"Process exited with return code {run_process.returncode}"
            log_message(f"  {Colors.RED}✗ {error_msg}{Colors.RESET}")
            
            execution_time = (datetime.datetime.now() - start_time).total_seconds()
            write_csv_row(
                day_str, 
                label, 
                "Clojure", 
                output if output else "N/A", 
                expected_output, 
                "ERROR", 
                error_msg, 
                "initial_attempt",
                execution_time
            )
            return

    except subprocess.TimeoutExpired:
        log_message(f"  {Colors.RED}✗ Execution timed out (>60s){Colors.RESET}")
        execution_time = 60.0  # Timeout duration
        write_csv_row(
            day_str, 
            label, 
            "Clojure", 
            "N/A", 
            expected_output, 
            "TIMEOUT", 
            "Execution timed out after 60 seconds", 
            "initial_attempt",
            execution_time
        )
        return
    except Exception as e:
        log_message(f"  {Colors.RED}✗ Execution error: {str(e)}{Colors.RESET}")
        execution_time = (datetime.datetime.now() - start_time).total_seconds()
        write_csv_row(
            day_str, 
            label, 
            "Clojure", 
            "N/A", 
            expected_output, 
            "RUNTIME_ERROR", 
            str(e), 
            "initial_attempt",
            execution_time
        )
        return
    finally:
        # Clean up
        if os.path.exists(INPUT_FILENAME):
            os.remove(INPUT_FILENAME)

    # Check if output is empty
    if not output:
        log_message(f"  {Colors.RED}✗ Program produced no output{Colors.RESET}")
        execution_time = (datetime.datetime.now() - start_time).total_seconds()
        write_csv_row(
            day_str, 
            label, 
            "Clojure", 
            "", 
            expected_output, 
            "EMPTY_OUTPUT", 
            "Program produced no output", 
            "initial_attempt",
            execution_time
        )
        return

    # Normalize output and expected output for comparison
    output_clean = output.replace(" ", "").replace("[", "").replace("]", "").strip()
    expected_clean = str(expected_output).replace(" ", "").replace("[", "").replace("]", "").strip()
    
    # Determine status
    status = "CORRECT" if output_clean == expected_clean else "INCORRECT"
    
    # Calculate execution time
    execution_time = (datetime.datetime.now() - start_time).total_seconds()
    
    # Print result with appropriate color
    if status == "CORRECT":
        log_message(f"  {Colors.GREEN}✓ Output matches expected: {output}{Colors.RESET}")
    else:
        log_message(f"  {Colors.YELLOW}✗ Output does not match expected:{Colors.RESET}")
        log_message(f"    - Got:      {output}")
        log_message(f"    - Expected: {expected_output}")
    
    # Write to CSV
    write_csv_row(
        day_str, 
        label, 
        "Clojure", 
        output, 
        expected_output, 
        status, 
        "", 
        "initial_attempt",
        execution_time
    )

def print_summary(csv_path: str) -> None:
    """Print a summary of the results at the end"""
    if not os.path.exists(csv_path):
        log_message("No results found to summarize.")
        return
        
    results: Dict[str, Dict[str, int]] = {
        "C": {"CORRECT": 0, "INCORRECT": 0, "ERROR": 0, "OTHER": 0},
        "Python": {"CORRECT": 0, "INCORRECT": 0, "ERROR": 0, "OTHER": 0},
        "Clojure": {"CORRECT": 0, "INCORRECT": 0, "ERROR": 0, "OTHER": 0}
    }
    
    # Track the highest execution times
    slowest_runs: Dict[str, Dict[str, float]] = {
        "C": {"time": 0.0, "day": "", "part": ""},
        "Python": {"time": 0.0, "day": "", "part": ""},
        "Clojure": {"time": 0.0, "day": "", "part": ""}
    }
    
    with open(csv_path, 'r') as f:
        reader = csv.DictReader(f)
        for row in reader:
            language = row['Language']
            status = row['Status']
            
            if language in results:
                if status == "CORRECT":
                    results[language]["CORRECT"] += 1
                elif status == "INCORRECT":
                    results[language]["INCORRECT"] += 1
                elif "ERROR" in status or status == "TIMEOUT":
                    results[language]["ERROR"] += 1
                else:
                    results[language]["OTHER"] += 1
                    
                # Track execution time for successfully completed runs
                if status == "CORRECT" or status == "INCORRECT":
                    try:
                        exec_time = float(row['Execution Time (s)'])
                        if exec_time > slowest_runs[language]["time"]:
                            slowest_runs[language]["time"] = exec_time
                            slowest_runs[language]["day"] = row["Day"]
                            slowest_runs[language]["part"] = row["Part"]
                    except (ValueError, KeyError):
                        pass
    
    # Create a horizontal line for formatting
    h_line = "="*60
    
    log_message("\n" + h_line)
    log_message(f"{Colors.BOLD}EXECUTION SUMMARY{Colors.RESET}")
    log_message(h_line)
    
    # Calculate and display total statistics
    total_correct = sum(stats["CORRECT"] for stats in results.values())
    total_incorrect = sum(stats["INCORRECT"] for stats in results.values())
    total_error = sum(stats["ERROR"] for stats in results.values())
    total_other = sum(stats["OTHER"] for stats in results.values())
    total_all = total_correct + total_incorrect + total_error + total_other
    
    if total_all > 0:
        log_message(f"{Colors.BOLD}OVERALL STATISTICS:{Colors.RESET}")
        log_message(f"  {Colors.GREEN}✓ Correct:    {total_correct}/{total_all} ({(total_correct/total_all*100):.1f}%){Colors.RESET}")
        log_message(f"  {Colors.YELLOW}✗ Incorrect:  {total_incorrect}/{total_all} ({(total_incorrect/total_all*100):.1f}%){Colors.RESET}")
        log_message(f"  {Colors.RED}✗ Errors:     {total_error}/{total_all} ({(total_error/total_all*100):.1f}%){Colors.RESET}")
        log_message(f"  ✗ Other:      {total_other}/{total_all} ({(total_other/total_all*100):.1f}%)")
        log_message("")
    
    # Display per-language statistics
    for language, stats in results.items():
        total = sum(stats.values())
        if total > 0:
            log_message(f"{Colors.BOLD}{language.upper()} RESULTS:{Colors.RESET}")
            log_message(f"  {Colors.GREEN}✓ Correct:    {stats['CORRECT']}/{total} ({(stats['CORRECT']/total*100):.1f}%){Colors.RESET}")
            log_message(f"  {Colors.YELLOW}✗ Incorrect:  {stats['INCORRECT']}/{total} ({(stats['INCORRECT']/total*100):.1f}%){Colors.RESET}")
            log_message(f"  {Colors.RED}✗ Errors:     {stats['ERROR']}/{total} ({(stats['ERROR']/total*100):.1f}%){Colors.RESET}")
            log_message(f"  ✗ Other:      {stats['OTHER']}/{total} ({(stats['OTHER']/total*100):.1f}%)")
            
            # Display slowest run if available
            if slowest_runs[language]["time"] > 0:
                log_message(f"  Slowest Run: Day {slowest_runs[language]['day']} {slowest_runs[language]['part']} - {slowest_runs[language]['time']:.3f}s")
            
            log_message("")
    
    log_message(h_line)
    log_message(f"Detailed report available at: {csv_path}")
    log_message(f"Log file available at: {LOG_FILE_PATH}")
    log_message(h_line)

def main() -> None:
    """Main function to run all tests"""
    print(f"\n{Colors.BOLD}{'='*80}{Colors.RESET}")
    print(f"{Colors.BOLD}PROGRAMMING PUZZLE CHECKER{Colors.RESET}")
    print(f"{Colors.BOLD}{'='*80}{Colors.RESET}\n")
    
    # Initialize log file
    with open(LOG_FILE_PATH, "w") as f:
        f.write(f"Execution started at {datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n")
    
    # Initialize CSV file header
    write_csv_header()
    
    # Check if JSON file exists
    if not os.path.exists(JSON_PATH):
        log_message(f"{Colors.RED}ERROR: Input answers file not found at {JSON_PATH}{Colors.RESET}")
        return
    
    try:
        # Load the puzzle answers
        with open(JSON_PATH) as f:
            data = json.load(f)
        
        log_message(f"Loaded {len(data)} puzzle entries from {JSON_PATH}")
        
        # Process all puzzles
        for i, entry in enumerate(data):
            day_str = str(entry["day"]).zfill(2)
            input_data = entry["input"]
            part1 = entry.get("part1", "")
            part2 = entry.get("part2", "")
            
            # Show progress
            progress = (i + 1) / len(data) * 100
            log_message(f"\n{Colors.BOLD}Processing Day {day_str} [{progress:.1f}%]{Colors.RESET}")
            
            # Run solutions for all languages
            run_c(day_str, input_data, part1, is_part2=False)
            run_c(day_str, input_data, part2, is_part2=True)
            run_python(day_str, input_data, part1, is_part2=False)
            run_python(day_str, input_data, part2, is_part2=True)
            run_clojure(day_str, input_data, part1, is_part2=False)
            run_clojure(day_str, input_data, part2, is_part2=True)
        
        # Clean up any temporary files
        if os.path.exists(INPUT_FILENAME):
            os.remove(INPUT_FILENAME)
        
        # Print summary
        print_summary(CSV_REPORT_PATH)
        
        log_message(f"\nExecution completed at {datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        
    except json.JSONDecodeError:
        log_message(f"{Colors.RED}ERROR: Invalid JSON format in {JSON_PATH}{Colors.RESET}")
    except Exception as e:
        log_message(f"{Colors.RED}ERROR: An unexpected error occurred: {str(e)}{Colors.RESET}")

if __name__ == "__main__":
    main()