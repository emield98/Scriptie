"""
Module providing utilities for logging and file handling.
"""
import os
import datetime
import csv
from config import Colors

def ensure_dir_exists(directory):
    """
    Ensure that the specified directory exists.
    """
    if not os.path.exists(directory):
        os.makedirs(directory)

def log_message(message: str, log_file: str = None) -> None:
    """
    Write a message to both console and optionally to a log file with timestamp.
    
    Args:
        message: The message to log
        log_file: Path to the log file (optional)
    """
    timestamp = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")

    # Print to console with colors
    print(f"[{timestamp}] {message}")

    # If log file is specified, write to it
    if log_file:
        # Create directory if it doesn't exist
        log_dir = os.path.dirname(log_file)
        ensure_dir_exists(log_dir)

        # Remove ANSI color codes for log file
        clean_message = message
        for color_code in [Colors.GREEN, Colors.YELLOW, Colors.RED, Colors.BLUE, Colors.RESET, Colors.BOLD]:
            clean_message = clean_message.replace(color_code, "")

        # Write to log file without color codes
        with open(log_file, "a") as f:
            f.write(f"[{timestamp}] {clean_message}\n")

def write_csv_header(csv_file: str) -> None:
    """
    Write the CSV header to the report file.
    
    Args:
        csv_file: Path to the CSV file
    """
    # Ensure the directory for the CSV file exists
    csv_dir = os.path.dirname(csv_file)
    ensure_dir_exists(csv_dir)

    if not os.path.exists(csv_file):
        with open(csv_file, mode='w', newline='') as file:
            writer = csv.writer(file)
            writer.writerow([
                "Day", "Part", "Language", "Attempt", "Output", "Expected Output", 
                "Status", "Error Message", "Execution Time (s)", "Date/Time"
            ])

def write_csv_row(csv_file: str, day: int, part: str, language: str, attempt: str, 
                  output: str, expected_output: str, status: str, error_message: str, 
                  execution_time: float) -> None:
    """
    Write a row to the CSV file.
    
    Args:
        csv_file: Path to the CSV file
        day: Day number
        part: Part number or identifier
        language: Programming language
        attempt: Attempt identifier
        output: Script output
        expected_output: Expected output
        status: Status of the execution
        error_message: Error message if any
        execution_time: Execution time in seconds
    """
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