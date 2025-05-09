import os

def print_tree(startpath, prefix=''):
    files = sorted(os.listdir(startpath))
    for index, name in enumerate(files):
        if name in {".cpcache", ".venv"}:  # Exclude specified directories
            continue
        path = os.path.join(startpath, name)
        connector = '└── ' if index == len(files) - 1 else '├── '
        print(prefix + connector + name)
        if os.path.isdir(path):
            extension = '    ' if index == len(files) - 1 else '│   '
            print_tree(path, prefix + extension)

if __name__ == "__main__":
    print("📁 Project Structure:\n")
    print_tree(".")
