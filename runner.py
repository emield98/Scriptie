import os
import sys
import subprocess

def pythonrunner(python_file, input):
    try:
        # Run the Python script with a timeout of 60 seconds
        log_message(f"  → Running Python script: {python_file}")
        run_process = subprocess.run(
            [sys.executable, full_path],
            capture_output=True,
            text=True,
            timeout=60,
            cwd=BASE_DIR
        )

def clojurerunner(clojure_file, input):
    try:
        # Run the Clojure script with a timeout of 60 seconds
        log_message(f"  → Running Clojure script: {clojure_file}")
        run_process = subprocess.run(  
            ["clojure", "-M", clojure_file],
            capture_output=True,
            text=True,
            timeout=60,
            cwd=BASE_DIR
        )
        