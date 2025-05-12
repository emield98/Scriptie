import os
import datetime
import subprocess
import csv
from typing import Dict

# Define the base directory for all code files
BASE_DIR = "/home/scriptie/Scriptie/"

# Paths to the required directories and files
TRANS_TEST_DIR = os.path.join(BASE_DIR, "ChatGPT/trans_test/")
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
            "Source Language", 
            "Target Language", 
            "File", 
            "Status", 
            "Error Message", 
            "Execution Time (s)",
            "Date/Time"
        ])
    log_message(f"{Colors.BLUE}CSV report initialized at {CSV_REPORT_PATH}{Colors.RESET}")

def run_trans_test(source_lang: str, target_lang: str, folder: str) -> None:
    """Run and verify translations for a specific source-target language pair"""
    trans_dir = os.path.join(TRANS_TEST_DIR, folder)
    
    if not os.path.exists(trans_dir):
        log_message(f"{Colors.RED}✗ Directory not found: {trans_dir}{Colors.RESET}")
        return

    for file in os.listdir(trans_dir):
        if not file.endswith(".py"):
            continue

        file_path = os.path.join(trans_dir, file)
        start_time = datetime.datetime.now()

        log_message(f"{Colors.BOLD}Checking translation: {file} ({source_lang} → {target_lang}){Colors.RESET}")

        try:
            # Run the Python file
            run_process = subprocess.run(
                ["python3", file_path],
                capture_output=True,
                text=True,
                timeout=60
            )

            output = run_process.stdout.strip()
            error = run_process.stderr.strip()

            # Determine status
            status = "SUCCESS" if run_process.returncode == 0 else "FAILURE"

            # Calculate execution time
            execution_time = (datetime.datetime.now() - start_time).total_seconds()

            # Log result
            if status == "SUCCESS":
                log_message(f"  {Colors.GREEN}✓ Translation succeeded: {file}{Colors.RESET}")
            else:
                log_message(f"  {Colors.RED}✗ Translation failed: {file}{Colors.RESET}")
                log_message(f"    Error: {error}")

            # Write to CSV
            with open(CSV_REPORT_PATH, mode='a', newline='') as csv_file:
                writer = csv.writer(csv_file)
                writer.writerow([
                    source_lang,
                    target_lang,
                    file,
                    status,
                    error if error else "",
                    f"{execution_time:.3f}",
                    datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
                ])

        except subprocess.TimeoutExpired:
            log_message(f"  {Colors.RED}✗ Execution timed out (>60s): {file}{Colors.RESET}")
            with open(CSV_REPORT_PATH, mode='a', newline='') as csv_file:
                writer = csv.writer(csv_file)
                writer.writerow([
                    source_lang,
                    target_lang,
                    file,
                    "TIMEOUT",
                    "Execution timed out after 60 seconds",
                    "60.000",
                    datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
                ])
        except Exception as e:
            log_message(f"  {Colors.RED}✗ Execution error: {file} - {str(e)}{Colors.RESET}")
            with open(CSV_REPORT_PATH, mode='a', newline='') as csv_file:
                writer = csv.writer(csv_file)
                writer.writerow([
                    source_lang,
                    target_lang,
                    file,
                    "ERROR",
                    str(e),
                    "",
                    datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
                ])

def main() -> None:
    """Main function to run all translation tests"""
    print(f"\n{Colors.BOLD}{'='*80}{Colors.RESET}")
    print(f"{Colors.BOLD}TRANSLATION TEST CHECKER{Colors.RESET}")
    print(f"{Colors.BOLD}{'='*80}{Colors.RESET}\n")

    # Initialize log file
    with open(LOG_FILE_PATH, "w") as f:
        f.write("Translation Test Log\n")

    # Initialize CSV file header
    write_csv_header()

    # Run tests for each source-target language pair
    run_trans_test("C", "Clojure", "c_to_clojure")
    run_trans_test("C", "Python", "c_to_python")
    run_trans_test("Clojure", "C", "clojure_to_c")
    run_trans_test("Clojure", "Python", "clojure_to_python")
    run_trans_test("Python", "C", "python_to_c")
    run_trans_test("Python", "Clojure", "python_to_clojure")

if __name__ == "__main__":
    main()