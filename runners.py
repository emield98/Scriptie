"""
Module containing language-specific runners.
"""
import os
import re
import subprocess
from utils import log_message
from config import DEFAULT_TIMEOUT

class RunnerError(Exception):
    """Exception raised for errors in the runner."""
    pass

def clojure_runner(clojure_file, input_file, timeout=DEFAULT_TIMEOUT):
    """
    Run a Clojure script (that declares an (ns ...) with a -main).

    Args:
        clojure_file: Path to the Clojure file
        input_file: Path to the input file
        timeout: Seconds to wait before killing the process
        
    Returns:
        Script output as string or None on error
    """
    input_arg = os.path.abspath(input_file)

    # Run in the script's directory so its deps.edn is used
    script_dir = os.path.dirname(os.path.abspath(clojure_file))
    
    # Extract namespace from the (ns ...) declaration
    ns = None
    ns_pattern = re.compile(r'^\s*\(ns\s+([\w\._-]+)')
    try:
        with open(clojure_file, 'r', encoding='utf-8') as f:
            for line in f:
                m = ns_pattern.match(line)
                if m:
                    ns = m.group(1)
                    break
        
        if not ns:
            raise RunnerError(f"Could not find a (ns …) in {clojure_file!r}")
    
        # Build and run the CLI command
        cmd = ["clojure", "-M", "-m", ns, input_arg]
        log_message(f"→ Running Clojure script: {clojure_file!r} in {script_dir!r}")
        log_message(f"→ Command: {' '.join(cmd)}")
    
        proc = subprocess.run(
            cmd,
            cwd=script_dir,
            capture_output=True,
            text=True,
            timeout=timeout,
        )
        proc.check_returncode()
        log_message(f"→ Success! Output: {proc.stdout}")
        return proc.stdout
    
    except subprocess.TimeoutExpired:
        log_message(f"ERROR: Script timed out after {timeout} seconds.")
        return "TIMEOUT"
    
    except subprocess.CalledProcessError as e:
        log_message(f"ERROR: Script exited with code {e.returncode}.")
        log_message("STDOUT:")
        print(e.stdout)
        log_message("STDERR:")
        print(e.stderr)
        return {
            "error": True,
            "returncode": e.returncode,
            "stdout": e.stdout,
            "stderr": e.stderr
        }

    
    except Exception as e:
        log_message(f"ERROR: {str(e)}")
        return None

def python_runner(python_file, input_file, timeout=DEFAULT_TIMEOUT):
    """
    Run a Python script that expects an 'input.txt' in its cwd, without modifying the script.

    Args:
        python_file: Path to the Python file
        input_file: Path to the input file
        timeout: Seconds to wait before killing the process
        
    Returns:
        Script output as string or None on error
    """
    python_file = os.path.abspath(python_file)
    input_arg = os.path.abspath(input_file)
    script_dir = os.path.dirname(python_file)
    script_name = os.path.basename(python_file)

    # Create or overwrite a symlink named 'input.txt' in script_dir
    link_path = os.path.join(script_dir, 'input.txt')
    try:
        if os.path.islink(link_path) or os.path.exists(link_path):
            os.remove(link_path)
        os.symlink(input_arg, link_path)
    except OSError as e:
        log_message(f"WARNING: Could not create symlink: {e}")

    cmd = ["python3", script_name]
    log_message(f"→ Running Python script: {python_file!r} in {script_dir!r}")
    log_message(f"→ Command: {' '.join(cmd)}")
    
    try:
        proc = subprocess.run(
            cmd, cwd=script_dir, capture_output=True, text=True, timeout=timeout
        )
        proc.check_returncode()
        log_message(f"→ Success! Output: {proc.stdout}")
        return proc.stdout
    
    except subprocess.TimeoutExpired:
        log_message(f"ERROR: Script timed out after {timeout} seconds.")
        return "TIMEOUT"
    
    except subprocess.CalledProcessError as e:
        log_message(f"ERROR: Script exited with code {e.returncode}.")
        log_message("STDOUT:")
        print(e.stdout)
        log_message("STDERR:")
        print(e.stderr)
        return {
            "error": True,
            "returncode": e.returncode,
            "stdout": e.stdout,
            "stderr": e.stderr
        }

    
    finally:
        # Clean up symlink
        try:
            if os.path.islink(link_path):
                os.remove(link_path)
        except OSError:
            pass

def c_runner(c_file, input_file, timeout=DEFAULT_TIMEOUT):
    """
    Compile a C program into the project root, run it with the input, then remove the executable.

    Args:
        c_file: Path to the C file
        input_file: Path to the input file
        timeout: Seconds to wait before killing the process
        
    Returns:
        Program output as string or None on error
    """
    # Track root directory (where runner.py was invoked)
    root_dir = os.getcwd()

    # Absolute paths
    c_file = os.path.abspath(c_file)
    input_arg = os.path.abspath(input_file)
    script_dir = os.path.dirname(c_file)

    # Name of the executable in the root folder
    base = os.path.splitext(os.path.basename(c_file))[0]
    exe_path = os.path.join(root_dir, base)

    try:
        # Compile into root
        compile_cmd = ["gcc", c_file, "-o", exe_path]
        log_message(f"→ Compiling C program: {c_file!r}")
        log_message(f"→ Command: {' '.join(compile_cmd)}")
        
        compile_proc = subprocess.run(
            compile_cmd, cwd=script_dir, capture_output=True, text=True, timeout=timeout
        )
        compile_proc.check_returncode()
        
        log_message("→ Compilation succeeded.")
    
        # Run executable with absolute input path
        run_cmd = [exe_path, input_arg]
        log_message(f"→ Running executable: {exe_path!r}")
        log_message(f"→ Command: {' '.join(run_cmd)}")
        
        run_proc = subprocess.run(
            run_cmd, cwd=root_dir, capture_output=True, text=True, timeout=timeout
        )
        run_proc.check_returncode()
        log_message(f"→ Success! Program output: {run_proc.stdout}")
        return run_proc.stdout
    
    except subprocess.TimeoutExpired:
        log_message(f"ERROR: Process timed out after {timeout} seconds.")
        return "TIMEOUT"
    
    except subprocess.CalledProcessError as e:
        if 'compile_proc' in locals():
            log_message(f"ERROR: Compilation failed with code {e.returncode}.")
            log_message("Compiler STDOUT:")
            print(e.stdout)
            log_message("Compiler STDERR:")
            print(e.stderr)
        else:
            log_message(f"ERROR: Program exited with code {e.returncode}.")
            log_message("Program STDOUT:")
            print(e.stdout)
            log_message("Program STDERR:")
            print(e.stderr)
        return {
            "error": True,
            "type": "compilation" if 'compile_proc' in locals() else "runtime",
            "returncode": e.returncode,
            "stdout": e.stdout,
            "stderr": e.stderr
        }

    
    except Exception as e:
        log_message(f"ERROR: {str(e)}")
        return None
    
    finally:
        # Clean up executable
        try:
            if os.path.exists(exe_path):
                os.remove(exe_path)
        except OSError:
            pass

def run_script(lang, script_path, input_file, timeout=DEFAULT_TIMEOUT):
    """
    Run a script using the appropriate runner based on language.
    
    Args:
        lang: Language of the script (python, c, or clojure)
        script_path: Path to the script file
        input_file: Path to the input file
        timeout: Seconds to wait before killing the process
        
    Returns:
        Script output as string or None on error
    """
    if lang == "clojure":
        return clojure_runner(script_path, input_file, timeout=timeout)
    elif lang == "python":
        return python_runner(script_path, input_file, timeout=timeout)
    elif lang == "c":
        return c_runner(script_path, input_file, timeout=timeout)
    else:
        raise ValueError(f"Unsupported language: {lang}")