#!/usr/bin/env python3
"""
Main entry point for the AOC Runner.
"""
import os
import argparse
from config import (
    DEFAULT_LOG_FILE,
    DEFAULT_CSV_FILE,
    DEFAULT_TIMEOUT,
    DIRECT_ATTEMPT_DIRS,
    TRANSLATION_DIRS,
    Colors
)
from utils import log_message, write_csv_header, ensure_dir_exists
from executor import process_scripts

def parse_arguments():
    """
    Parse command line arguments.
    
    Returns:
        Parsed arguments object
    """
    parser = argparse.ArgumentParser(description="Run and test Advent of Code solutions.")
    parser.add_argument("--log-file", default=DEFAULT_LOG_FILE, help="Path to the log file")
    parser.add_argument("--csv-file", default=DEFAULT_CSV_FILE, help="Path to the CSV report file")
    parser.add_argument("--timeout", type=int, default=DEFAULT_TIMEOUT, help="Timeout in seconds for each script")
    parser.add_argument("--direct-only", action="store_true", help="Run only direct attempt scripts")
    parser.add_argument("--translations-only", action="store_true", help="Run only translation scripts")
    parser.add_argument("--languages", nargs="+", choices=["python", "c", "clojure"], 
                        help="Languages to run (default: all)")
    parser.add_argument("--translations", nargs="+", choices=list(TRANSLATION_DIRS.keys()),
                        help="Translations to run (default: all)")
    
    return parser.parse_args()

def main():
    """
    Main function to run the script.
    """
    args = parse_arguments()
    
    # Ensure log and csv directories exist
    log_dir = os.path.dirname(args.log_file)
    csv_dir = os.path.dirname(args.csv_file)
    ensure_dir_exists(log_dir)
    ensure_dir_exists(csv_dir)
    
    # Initialize CSV file
    write_csv_header(args.csv_file)
    
    log_message(f"{Colors.BOLD}{Colors.BLUE}Starting AOC Runner{Colors.RESET}", args.log_file)
    log_message(f"Log file: {args.log_file}", args.log_file)
    log_message(f"CSV report: {args.csv_file}", args.log_file)
    log_message(f"Timeout: {args.timeout} seconds", args.log_file)
    
    # Filter languages if specified
    direct_attempt_dirs = DIRECT_ATTEMPT_DIRS
    if args.languages:
        direct_attempt_dirs = {k: v for k, v in DIRECT_ATTEMPT_DIRS.items() if k in args.languages}
    
    # Filter translations if specified
    translation_dirs = TRANSLATION_DIRS
    if args.translations:
        translation_dirs = {k: v for k, v in TRANSLATION_DIRS.items() if k in args.translations}
    
    # Process direct attempt scripts
    if not args.translations_only:
        log_message(f"{Colors.BOLD}Processing direct attempt scripts...{Colors.RESET}", args.log_file)
        for lang, lang_dir in direct_attempt_dirs.items():
            process_scripts(lang, lang_dir, args.log_file, args.csv_file, f"Direct_Attempt", args.timeout)
    
    # Process translation scripts
    if not args.direct_only:
        log_message(f"{Colors.BOLD}Processing translation scripts...{Colors.RESET}", args.log_file)
        for name, config in translation_dirs.items():
            process_scripts(
                config["lang"], 
                config["path"], 
                args.log_file, 
                args.csv_file, 
                config["attempt"],
                args.timeout
            )
    
    log_message(f"{Colors.BOLD}{Colors.GREEN}Script execution completed.{Colors.RESET}", args.log_file)
    log_message(f"Results saved to {args.csv_file}", args.log_file)

if __name__ == "__main__":
    main()