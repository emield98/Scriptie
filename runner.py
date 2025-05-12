#!/usr/bin/env python3

import subprocess
import os
import re
import argparse


def log_message(msg):
    print(msg)


def clojurerunner(clojure_file, input_file, timeout=60):
    """
    Run a Clojure script (that declares an (ns ...) with a -main).

    clojure_file: path/to/output_01a.clj
    input_file:   path/to/input.txt
    timeout:      seconds to wait before killing the process
    """

    input_arg = os.path.abspath(input_file)


    # 1) Run in the script's directory so its deps.edn is used
    script_dir = os.path.dirname(os.path.abspath(clojure_file))
    print(script_dir)
    # 2) Extract namespace from the (ns ...) declaration
    ns = None
    ns_pattern = re.compile(r'^\s*\(ns\s+([\w\._-]+)')
    with open(clojure_file, 'r', encoding='utf-8') as f:
        for line in f:
            m = ns_pattern.match(line)
            if m:
                ns = m.group(1)
                break
    if not ns:
        raise RuntimeError(f"Could not find a (ns …) in {clojure_file!r}")

    # 3) Build and run the CLI command
    cmd = ["clojure", "-M", "-m", ns, input_arg]
    log_message(f"→ Running Clojure script: {clojure_file!r} in {script_dir!r}")
    log_message(f"→ Command: {' '.join(cmd)}")

    try:
        proc = subprocess.run(
            cmd,
            cwd=script_dir,
            capture_output=True,
            text=True,
            timeout=timeout,
        )
        proc.check_returncode()
        log_message("→ Success! Output:")
        print(proc.stdout)
        return proc.stdout

    except subprocess.TimeoutExpired:
        log_message(f"ERROR: Script timed out after {timeout} seconds.")
        return None

    except subprocess.CalledProcessError as e:
        log_message(f"ERROR: Script exited with code {e.returncode}.")
        log_message("STDOUT:")
        print(e.stdout)
        log_message("STDERR:")
        print(e.stderr)
        return None


def pythonrunner(python_file, input_file, timeout=60):
    """
    Run a Python script that expects an 'input.txt' in its cwd, without modifying the script.

    python_file: path/to/script.py
    input_file:  path/to/input.txt to symlink
    timeout:     seconds to wait before killing the process
    """
    python_file = os.path.abspath(python_file)
    input_arg   = os.path.abspath(input_file)
    script_dir  = os.path.dirname(python_file)
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
        log_message("→ Success! Output:")
        print(proc.stdout)
        return proc.stdout
    except subprocess.TimeoutExpired:
        log_message(f"ERROR: Script timed out after {timeout} seconds.")
        return None
    except subprocess.CalledProcessError as e:
        log_message(f"ERROR: Script exited with code {e.returncode}.")
        log_message("STDOUT:")
        print(e.stdout)
        log_message("STDERR:")
        print(e.stderr)
        return None
    finally:
        # Clean up symlink
        try:
            if os.path.islink(link_path):
                os.remove(link_path)
        except OSError:
            pass


def crunner(c_file, input_file, timeout=60):
    """
    Compile a C program into the project root, run it with the input, then remove the executable.

    c_file:      path/to/program.c
    input_file:  path/to/input.txt
    timeout:     seconds to wait before killing the process
    """
    # Track root directory (where runner.py was invoked)
    root_dir = os.getcwd()

    # Absolute paths
    c_file    = os.path.abspath(c_file)
    input_arg = os.path.abspath(input_file)
    script_dir= os.path.dirname(c_file)

    # Name of the executable in the root folder
    base     = os.path.splitext(os.path.basename(c_file))[0]
    exe_path = os.path.join(root_dir, base)

    # Compile into root
    compile_cmd = ["gcc", c_file, "-o", exe_path]
    log_message(f"→ Compiling C program: {c_file!r}")
    log_message(f"→ Command: {' '.join(compile_cmd)}")
    try:
        compile_proc = subprocess.run(
            compile_cmd, cwd=script_dir, capture_output=True, text=True, timeout=timeout
        )
        compile_proc.check_returncode()
    except subprocess.TimeoutExpired:
        log_message(f"ERROR: Compilation timed out after {timeout} seconds.")
        return None
    except subprocess.CalledProcessError as e:
        log_message(f"ERROR: Compilation failed with code {e.returncode}.")
        log_message("Compiler STDOUT:")
        print(e.stdout)
        log_message("Compiler STDERR:")
        print(e.stderr)
        return None

    log_message("→ Compilation succeeded.")

    # Run executable with absolute input path
    run_cmd = [exe_path, input_arg]
    log_message(f"→ Running executable: {exe_path!r}")
    log_message(f"→ Command: {' '.join(run_cmd)}")
    try:
        run_proc = subprocess.run(
            run_cmd, cwd=root_dir, capture_output=True, text=True, timeout=timeout
        )
        run_proc.check_returncode()
        log_message("→ Success! Program output:")
        print(run_proc.stdout)
        return run_proc.stdout

    except subprocess.TimeoutExpired:
        log_message(f"ERROR: Program timed out after {timeout} seconds.")
        return None
    except subprocess.CalledProcessError as e:
        log_message(f"ERROR: Program exited with code {e.returncode}.")
        log_message("Program STDOUT:")
        print(e.stdout)
        log_message("Program STDERR:")
        print(e.stderr)
        return None
    finally:
        # Clean up executable
        try:
            if os.path.exists(exe_path):
                os.remove(exe_path)
        except OSError:
            pass


def main():
    parser = argparse.ArgumentParser(
        description="Run scripts in Clojure, Python, or C with a specified input file."
    )
    parser.add_argument(
        "lang", choices=["clojure", "python", "c"],
        help="Language of the script to run"
    )
    parser.add_argument("script", help="Path to the source file (.clj/.py/.c)")
    parser.add_argument("input", help="Path to the input file to pass as an argument")
    parser.add_argument(
        "--timeout", type=int, default=60,
        help="Timeout in seconds for compile/run phases"
    )
    args = parser.parse_args()

    if args.lang == "clojure":
        clojurerunner(args.script, args.input, timeout=args.timeout)
    elif args.lang == "python":
        pythonrunner(args.script, args.input, timeout=args.timeout)
    elif args.lang == "c":
        crunner(args.script, args.input, timeout=args.timeout)


if __name__ == "__main__":
    main()
