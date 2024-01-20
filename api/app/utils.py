from time import gmtime, strftime
from subprocess import run
from hashlib import sha512
from json import dump as dump_json, load as load_json, JSONDecodeError
from os.path import exists as is_path_exists

from app.settings import DATA_FILE


def get_time() -> str:
    time = strftime('%Y-%m-%d %H:%M:%S', gmtime())
    return time

def run_bash_command(command: str = " ") -> str:
    result = run(args=command,
                 shell=True,
                 executable='/bin/bash',
                 capture_output=True,
                 text=True).stdout

    return str(result)

def str_to_hash(*string: str) -> str:
    return sha512(string="".join(string).encode(encoding="utf-8")).hexdigest()

class Data:
    @staticmethod
    def initialize(data_file: str = DATA_FILE):
        """
        Initialize method creates an initial data structure and writes it to a JSON file.
        """
        # Check if the data file already exists
        if not is_path_exists(data_file):
            # Initial data structure with three keys
            data = {
                "services": []
            }

            # Writing the initial data to a JSON file
            with open(data_file, 'w') as file:
                dump_json(data, file, indent=2)

    @staticmethod
    def write(key: str, value: str, data_file: str = DATA_FILE):
        """
        Static method to write data to the specified key within the data file.
        Args:
        - key (str): The key within the data file.
        - value (str): The value to be stored.
        - data_file (str): The path to the data file. Defaults to Const.DATA_FILE.
        Returns:
        - 0: Means the write process is failed.
        - 1: Means the write process is succeed.
        - 2: Means the value is already exists.
        """
        loaded_data = Data.load()
        try:
            if not loaded_data:
                loaded_data = {
                    "services": []
                }

            if value in loaded_data[key]:
                return 2

            loaded_data[key].append(value)

            with open(data_file, 'w') as file:
                dump_json(loaded_data, file, indent=2)

            return 1
        except Exception as e:
            print(f"Error writing to file: {e}")
            return 0

    @staticmethod
    def load(key: str = None, data_file: str = DATA_FILE):
        """
        Load method reads data from the specified key within the data file.
        Args:
        - key (str): The key within the data file. If not provided, returns the entire data.
        Returns:
        - dict or None: The data associated with the specified key or None if the key is invalid.
        """
        try:
            with open(data_file, "r") as file:
                data: dict = load_json(file)
                return data
        except FileNotFoundError:
            Data.initialize(data_file=data_file)
            return {key: []} if key else {}

        except JSONDecodeError as e:
            print(f"Error decoding JSON: {e}")
            return {key: []} if key else {}
