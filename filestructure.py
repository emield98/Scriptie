import os

def generate_file_structure(root_dir, excluded_dirs=None):
    if excluded_dirs is None:
        excluded_dirs = []

    for dirpath, dirnames, filenames in os.walk(root_dir):
        # Exclude specified directories
        dirnames[:] = [d for d in dirnames if d not in excluded_dirs]

        # Print the current directory
        print(f"Directory: {dirpath}")

        # Print the files in the current directory
        for file in filenames:
            print(f"  File: {file}")

if __name__ == "__main__":
    # Define the root directory and directories to exclude
    root_directory = os.path.dirname(os.path.abspath(__file__))
    excluded_directories = [".cpcache", ".venv", "solutions", ".gitignore", ".git"]  # Replace with actual directory names to exclude

    # Generate the file structure
    generate_file_structure(root_directory, excluded_directories)