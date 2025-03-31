from os.path import exists
from os import getcwd
import sys

from random import randint as random

million = (10**6)+1
conf = {
    "start": 2,
    "end": million,
    "current": 2,
    "primes": []
}

old_primes = []

def load():

    # * Save format: start = , end = , primes = 

    prog: str = input(f"Enter file path to load status (relative to : {getcwd()})> ")
    
    if not exists(prog):
        raise FileNotFoundError("File not found!")
    
    contents: list = []

    with open(prog, 'r') as reader:
        contents: list = reader.readlines();

    conf['start'] = int(contents[0].split("=")[1].strip())
    conf['end'] = int(contents[1].split("=")[1].strip())
    conf['current'] = int(contents[2].split("=")[1].strip())
    primes_str = contents[3].split("=")[1].strip()
    global old_primes ; old_primes= list(map(int, primes_str.strip("[]").split(", ")))  # Convert to list of ints

    print("Config loaded...")

def save(cnum: int, quick=False):

    if quick:
        prog: str = "quick_crash.stSave"
    else:
        prog: str = input(f"Enter file path to store status (relative to : {getcwd()})> ")

    with open(prog, 'w') as writer:
        writer.write(f"start={conf['start']}\n")
        writer.write(f"end={conf['end']}\n")
        writer.write(f"current={cnum}\n")
        writer.write(f"primes=[{', '.join(map(str, conf['primes']))}]\n")  # Properly format list

    print("Config saved...")

load()
print("Starting the shuffling...")
print(len(old_primes))

prev_pos:list[int,int,int] = [-1,-1,-1]

def update_prevs(new: int):

    temp = prev_pos[1]
    prev_pos[1] = prev_pos[0]
    prev_pos[2] = temp
    prev_pos[0] = new

counter = 0
max_c = len(old_primes)
l_stuck = 0

for i in range(0, len(old_primes)):

    r = random(0,len(old_primes)-1);

    while (r in prev_pos) and (l_stuck < 5):
        l_stuck += 1
        r = random(0,len(old_primes)-1);

    l_stuck = 0
    update_prevs(r)

    conf["primes"].append(old_primes[r])
    old_primes.pop(r)
    counter += 1

    # Calculate progress as a percentage
    progress = (counter / max_c) * 100
    bar_length = 40  # Length of the progress bar
    block = int(round(bar_length * progress / 100))  # Calculate number of blocks

    # Create the progress bar string
    progress_bar = f"[{'#' * block}{'.' * (bar_length - block)}] {progress:.2f}%"

    # Print the progress bar and overwrite it on the same line
    sys.stdout.write('\r' + progress_bar)
    sys.stdout.flush()

print()
save(counter)