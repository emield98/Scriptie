"""
Module for loading and managing data for the AOC Runner.
"""
import json
import os
from utils import log_message

def load_answer(day, part, answers_file):
    """
    Load the answer for a specific day and part from the JSON file.
    
    Args:
        day: Day number
        part: Part number or identifier
        answers_file: Path to the answers JSON file
        
    Returns:
        The answer for the specified day and part, or None if not found
    """
    try:
        with open(answers_file, "r") as f:
            answers = json.load(f)
        
        for entry in answers:
            if entry.get("day") == day:
                return entry.get(f"part{part}", None)
                
        return None
    except Exception as e:
        log_message(f"Error loading answer for day {day}, part {part}: {str(e)}")
        return None

def load_input(day, answers_file):
    """
    Load the input for a specific day from the JSON file.
    
    Args:
        day: Day number
        answers_file: Path to the answers JSON file
        
    Returns:
        The input for the specified day, or None if not found
    """
    try:
        with open(answers_file, "r") as f:
            answers = json.load(f)
        
        for entry in answers:
            if entry.get("day") == day:
                return entry.get("input", None)
                
        return None
    except Exception as e:
        log_message(f"Error loading input for day {day}: {str(e)}")
        return None

def prepare_input_file(day, answers_file):
    """
    Prepare the input file for a specific day.
    
    Args:
        day: Day number
        answers_file: Path to the answers JSON file
        
    Returns:
        Path to the created input file
    """
    input_content = load_input(day, answers_file)
    if input_content is None:
        return None
        
    input_file_path = os.path.join(os.getcwd(), "input.txt")
    with open(input_file_path, "w") as f:
        f.write(input_content)
        
    return input_file_path