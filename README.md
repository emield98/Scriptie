# Project Structure

I've reorganized your script into a more modular, maintainable structure with the following files:

1. **config.py** - Contains all configuration parameters and constants
2. **utils.py** - General utility functions for logging and CSV operations  
3. **data_loader.py** - Functions for loading inputs and answers from the JSON file
4. **runners.py** - Contains the runner implementations for each language
5. **executor.py** - Logic for processing script directories and executing scripts
6. **aoc_runner.py** - Main entry point with command-line argument parsing

## Key Improvements

1. **Separation of Concerns**
   - Each file has a clear, focused responsibility
   - Configuration is centralized in one place

2. **Better Error Handling**
   - Consistent try/except patterns
   - Better error reporting

3. **Command Line Interface**
   - Added command-line arguments for more flexibility
   - Can select specific languages or translation types
   - Can choose to run only direct attempts or only translations

4. **Enhanced Logging**
   - Colored console output for better readability
   - Log files and CSV report directories are created automatically if they don't exist

5. **More Robust File Processing**
   - Better handling of file naming and parsing
   - Directory existence checks

## Usage

```bash
# Run all scripts
python aoc_runner.py

# Run only direct attempt scripts
python aoc_runner.py --direct-only

# Run only translation scripts
python aoc_runner.py --translations-only

# Run only Python and C scripts
python aoc_runner.py --languages python c

# Run only specific translations
python aoc_runner.py --translations-only --translations python_to_c c_to_python

# Specify custom log and report files
python aoc_runner.py --log-file ./logs/custom_log.txt --csv-file ./reports/custom_report.csv

# Change the timeout
python aoc_runner.py --timeout 120
```

This modular structure makes the code much easier to maintain, extend, and test.