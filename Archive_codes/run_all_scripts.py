import json
import os
import datetime
import csv
from runner import clojurerunner, pythonrunner, crunner

# Path to the answers JSON file
ANSWERS_FILE = "input_answers/answers2024.json"

# Timeout for script execution
TIMEOUT = 60

class Colors:
    GREEN = "\033[92m"
    YELLOW = "\033[93m"
    RED = "\033[91m"
    BLUE = "\033[94m"
    RESET = "\033[0m"
    BOLD = "\033[1m"

def log_message(message: str, log_file: str) -> None:
    """Write a message to both console and log file with timestamp."""
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

def write_csv_header(csv_file: str) -> None:
    """Write the CSV header."""
    # Ensure the directory for the CSV file exists
    csv_dir = os.path.dirname(csv_file)
    if not os.path.exists(csv_dir):
        os.makedirs(csv_dir)

    if not os.path.exists(csv_file):
        with open(csv_file, mode='w', newline='') as file:
            writer = csv.writer(file)
            writer.writerow([
                "Day", "Part", "Language", "Attempt", "Output", "Expected Output", "Status", "Error Message", "Execution Time (s)", "Date/Time"
            ])

def write_csv_row(csv_file: str, day: int, part: str, language: str, attempt: str, output: str, expected_output: str, status: str, error_message: str, execution_time: float) -> None:
    """Write a row to the CSV file."""
    timestamp = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    with open(csv_file, mode='a', newline='') as file:
        writer = csv.writer(file)
        writer.writerow([
            f"{day:02}",
            f"{part}",
            language,
            attempt,
            output,
            expected_output,
            status,
            error_message,
            f"{execution_time:.3f}",
            timestamp
        ])

def load_answer(day, part):
    """Load the answer for a specific day and part from the JSON file."""
    with open(ANSWERS_FILE, "r") as f:
        answers = json.load(f)
    for entry in answers:
        if entry.get("day") == day:
            return entry.get(f"part{part}", None)
    return None

def load_input(day):
    """Load the input for a specific day from the JSON file."""
    with open(ANSWERS_FILE, "r") as f:
        answers = json.load(f)
    for entry in answers:
        if entry.get("day") == day:
            return entry.get(f"input", None)
    return None

def run_script(lang, script_path, input_file):
    """Run a script using the appropriate runner."""
    if lang == "clojure":
        return clojurerunner(script_path, input_file, timeout=TIMEOUT)
    elif lang == "python":
        return pythonrunner(script_path, input_file, timeout=TIMEOUT)
    elif lang == "c":
        return crunner(script_path, input_file, timeout=TIMEOUT)
    else:
        raise ValueError(f"Unsupported language: {lang}")

def process_scripts(lang, lang_dir, log_file, csv_file, attempt):
    """Process and execute scripts for a given language."""
    files = sorted(os.listdir(lang_dir))
    for file in files:
        # Skip files that do not match the expected pattern
        if "_" not in file or len(file.split("_")) < 2:
            log_message(f"Skipping invalid file: {file}", log_file)
            continue

        file_path = os.path.join(lang_dir, file)
        day_part = file.split("_")[1].split(".")[0]
        day = int(day_part[:2])
        part = day_part[2:]
        if part == "a":
            part = "1"
        elif part == "b":
            part = "2"
        answer = load_answer(day, part)
        if answer is None:
            log_message(f"No answer found for day {day}, part {part}.", log_file)
            write_csv_row(csv_file, day, part, lang, attempt, "N/A", "N/A", "NO_ANSWER", "", 0.0)
            continue

        input_file_path = os.path.join(os.getcwd(), "input.txt")
        with open(input_file_path, "w") as f:
            f.write(load_input(day))
        start_time = datetime.datetime.now()
        try:
            result = run_script(lang, file_path, input_file_path)
            if result is None:
                raise ValueError("Script execution returned None")

            result = result.strip()
            execution_time = (datetime.datetime.now() - start_time).total_seconds()
            if str(result) == str(answer).strip():
                log_message(f"Day {day}, Part {part}: Correct", log_file)
                write_csv_row(csv_file, day, part, lang, attempt, result, str(answer).strip(), "CORRECT", "", execution_time)
            else:
                log_message(f"Day {day}, Part {part}: Incorrect! Expected {answer}, got {result}.", log_file)
                write_csv_row(csv_file, day, part, lang, attempt, result, str(answer).strip(), "INCORRECT", "", execution_time)
        except Exception as e:
            execution_time = (datetime.datetime.now() - start_time).total_seconds()
            log_message(f"Day {day}, Part {part}: Error - {str(e)}", log_file)
            write_csv_row(csv_file, day, part, lang, attempt, "N/A", str(answer), "ERROR", str(e), execution_time)

def main(log_file="test/execution_log.txt", csv_file="test/check_report.csv"):
    c_direct_attempt = "ChatGPT/Direct_Attempt/c/output"
    python_direct_attempt = "ChatGPT/Direct_Attempt/python/output"
    clojure_direct_attempt = "ChatGPT/Direct_Attempt/clojure/output"

    # Initialize CSV file
    write_csv_header(csv_file)

    # Process scripts for each language
    #process_scripts("python", python_direct_attempt, log_file, csv_file, "direct_attempt")
    #process_scripts("c", c_direct_attempt, log_file, csv_file, "Direct_Attempt")
    #process_scripts("clojure", clojure_direct_attempt, log_file, csv_file, "Direct_Attempt")

    # Translation directories
    trans_c_clojure = "ChatGPT/trans_test/c_to_clojure/output"
    trans_c_python = "ChatGPT/trans_test/c_to_python/output"
    trans_python_clojure = "ChatGPT/trans_test/python_to_clojure/output"
    trans_python_c = "ChatGPT/trans_test/python_to_c/output"
    trans_clojure_c = "ChatGPT/trans_test/clojure_to_c/output"
    trans_clojure_python = "ChatGPT/trans_test/clojure_to_python/output"

    process_scripts("clojure", trans_c_clojure, log_file, csv_file, "translation_attempt_c_to_clojure")
    process_scripts("python", trans_c_python, log_file, csv_file, "translation_attempt_c_to_python")
    process_scripts("clojure", trans_python_clojure, log_file, csv_file, "translation_attempt_python_to_clojure")
    process_scripts("c", trans_python_c, log_file, csv_file, "translation_attempt_python_to_c")
    process_scripts("c", trans_clojure_c, log_file, csv_file, "translation_attempt_clojure_to_c")
    process_scripts("python", trans_clojure_python, log_file, csv_file, "translation_attempt_clojure_to_python")

if __name__ == "__main__":
    main()
