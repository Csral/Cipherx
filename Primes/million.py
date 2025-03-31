from os.path import exists
from os import getcwd
from time import sleep

log = open("status.log", 'a')

million = (10**6)+1

conf = {
    "start": 2,
    "end": million,
    "current": 2,
    "primes": []
}

def is_prime(num: int) -> bool:

    for i in range(2,num):

        if (num % i) == 0:
            return False
        
    return True

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
    conf['primes'] = list(map(int, primes_str.strip("[]").split(", ")))  # Convert to list of ints

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

inp: str = input("Are you loading progress [y/N]> ").lower()

if inp == "yes" or inp == 'y':
    load()
else:

    try:

        conf['current'] = conf["start"] = int(input("Enter starting point> "))
        conf["end"] = int(input("Enter ending range> "))+1
    
        if (conf["start"] >= conf["end"]):
            print("Nothing to do. enter valid input!")
            exit(1)
        elif (conf["start"] <= 1 or conf["end"] <= 1):
            print("Invalid range!")
            exit(1)

    except ValueError:
        print("Invalid input! Expected ints")

G_cnum: int = 0

try:

    for num in range(conf['current'],conf['end']):

        if is_prime(num):
            conf["primes"].append(num)
            print(f"{(num/conf['end'])*100}% Work completed")
        G_cnum = num

        #sleep(0.1)

    save(conf["end"])

except KeyboardInterrupt:
    print("Saving...")
    save(G_cnum)

except:

    print("Unknown exception. Quick saving!")
    save(G_cnum,quick=True)