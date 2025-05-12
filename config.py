"""
Configuration settings for the AOC Runner project.
"""
import os
from pathlib import Path

# Base paths
PROJECT_ROOT = Path(os.getcwd())

# File paths
ANSWERS_FILE = os.path.join(PROJECT_ROOT, "input_answers", "answers2024.json")
DEFAULT_LOG_FILE = os.path.join(PROJECT_ROOT, "logs", "execution_log.txt")
DEFAULT_CSV_FILE = os.path.join(PROJECT_ROOT, "reports", "check_report.csv")

# Execution settings
DEFAULT_TIMEOUT = 60  # seconds

# Directory settings
DIRECT_ATTEMPT_DIRS = {
    "python": "ChatGPT/Direct_Attempt/python/output",
    "c": "ChatGPT/Direct_Attempt/c/output",
    "clojure": "ChatGPT/Direct_Attempt/clojure/output"
}

TRANSLATION_DIRS = {
    "c_to_clojure": {
        "path": "ChatGPT/trans_test/c_to_clojure/output",
        "lang": "clojure",
        "attempt": "translation_attempt_c_to_clojure"
    },
    "c_to_python": {
        "path": "ChatGPT/trans_test/c_to_python/output",
        "lang": "python",
        "attempt": "translation_attempt_c_to_python"
    },
    "python_to_clojure": {
        "path": "ChatGPT/trans_test/python_to_clojure/output",
        "lang": "clojure",
        "attempt": "translation_attempt_python_to_clojure"
    },
    "python_to_c": {
        "path": "ChatGPT/trans_test/python_to_c/output",
        "lang": "c",
        "attempt": "translation_attempt_python_to_c"
    },
    "clojure_to_c": {
        "path": "ChatGPT/trans_test/clojure_to_c/output",
        "lang": "c",
        "attempt": "translation_attempt_clojure_to_c"
    },
    "clojure_to_python": {
        "path": "ChatGPT/trans_test/clojure_to_python/output",
        "lang": "python",
        "attempt": "translation_attempt_clojure_to_python"
    }
}

# Color definitions for console output
class Colors:
    GREEN = "\033[92m"
    YELLOW = "\033[93m"
    RED = "\033[91m"
    BLUE = "\033[94m"
    RESET = "\033[0m"
    BOLD = "\033[1m"