"""
Module for executing scripts and processing results.
"""
import os
import datetime
from config import Colors, ANSWERS_FILE
from data_loader import load_answer, prepare_input_file
from runners import run_script
from utils import log_message, write_csv_row

def process_scripts(lang, lang_dir, log_file, csv_file, attempt, timeout=60):
    """
    Process and execute scripts for a given language directory.
    
    Args:
        lang: Language of the scripts (python, c, or clojure)
        lang_dir: Directory containing the scripts
        log_file: Path to the log file
        csv_file: Path to the CSV report file
        attempt: Attempt identifier
        timeout: Timeout in seconds for each script
    """
    if not os.path.exists(lang_dir):
        log_message(f"{Colors.YELLOW}Directory not found: {lang_dir}{Colors.RESET}", log_file)
        return
        
    log_message(f"{Colors.BLUE}Processing {lang} scripts in {lang_dir}{Colors.RESET}", log_file)
    
    # Get and sort all files in the directory
    try:
        files = sorted(os.listdir(lang_dir))
    except Exception as e:
        log_message(f"{Colors.RED}Error listing directory {lang_dir}: {str(e)}{Colors.RESET}", log_file)
        return
    
    for file in files:
        # Skip files that do not match the expected pattern
        if "_" not in file or len(file.split("_")) < 2:
            log_message(f"{Colors.YELLOW}Skipping invalid file: {file}{Colors.RESET}", log_file)
            continue

        file_path = os.path.join(lang_dir, file)
        day_part = file.split("_")[1].split(".")[0]
        
        # Extract day and part from filename
        try:
            day = int(day_part[:2])
            part = day_part[2:]
            if part == "a":
                part = "1"
            elif part == "b":
                part = "2"
        except (ValueError, IndexError) as e:
            log_message(f"{Colors.YELLOW}Skipping file with invalid naming: {file} - {str(e)}{Colors.RESET}", log_file)
            continue
            
        # Load answer for this day and part
        answer = load_answer(day, part, ANSWERS_FILE)
        if answer is None:
            log_message(f"{Colors.YELLOW}No answer found for day {day}, part {part}.{Colors.RESET}", log_file)
            write_csv_row(csv_file, day, part, lang, attempt, "N/A", "N/A", "NO_ANSWER", "", 0.0)
            continue

        # Prepare input file
        input_file_path = prepare_input_file(day, ANSWERS_FILE)
        if input_file_path is None:
            log_message(f"{Colors.RED}Failed to prepare input for day {day}.{Colors.RESET}", log_file)
            write_csv_row(csv_file, day, part, lang, attempt, "N/A", str(answer), "ERROR", "Failed to prepare input", 0.0)
            continue
            
        # Execute the script
        log_message(f"{Colors.BLUE}Running day {day}, part {part} ({lang}){Colors.RESET}", log_file)
        start_time = datetime.datetime.now()
        try:
            result = run_script(lang, file_path, input_file_path, timeout)
            if result is None:
                raise ValueError("Script execution returned None")

            result = result.strip()
            execution_time = (datetime.datetime.now() - start_time).total_seconds()
            
            # Normalize both result and expected answer for comparison
            normalized_result = str(result).strip().replace(" ", "").replace("[", "").replace("]", "")
            normalized_answer = str(answer).strip().replace(" ", "").replace("[", "").replace("]", "")
            
            # Check result against expected answer
            if normalized_result == normalized_answer:
                log_message(f"{Colors.GREEN}Day {day}, Part {part}: Correct{Colors.RESET}", log_file)
                write_csv_row(csv_file, day, part, lang, attempt, result, str(answer).strip(), "CORRECT", "", execution_time)
            elif normalized_result == "TIMEOUT":
                log_message(f"{Colors.YELLOW}Day {day}, Part {part}: Timeout{Colors.RESET}", log_file)
                write_csv_row(csv_file, day, part, lang, attempt, result, str(answer).strip(), "TIMEOUT", "", execution_time)
            else:
                log_message(f"{Colors.RED}Day {day}, Part {part}: Incorrect: Expected {answer}, got {result}.{Colors.RESET}", log_file)
                write_csv_row(csv_file, day, part, lang, attempt, result, str(answer).strip(), "INCORRECT", "", execution_time)
        except Exception as e:
            execution_time = (datetime.datetime.now() - start_time).total_seconds()
            log_message(f"{Colors.RED}Day {day}, Part {part}: Error - {str(e)}{Colors.RESET}", log_file)
            write_csv_row(csv_file, day, part, lang, attempt, "N/A", str(answer), "ERROR", str(e), execution_time)