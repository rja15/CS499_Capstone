"""
This program(TextBasedGame.py) is a text game that has the player collecting items while avoiding the boss enemy.
Collecting all items before boss encounter wins the game, while encountering the boss before that loses the game.
Player can choose to exit early, and has to make decisions in a turn-based gameplay loop. Includes very crude AI.
Commented sections containing additional behind the scenes info are marked by "fixme"
Raymond Agosto
2/19/2023
"""
import random       #needed for AI decisionmaking

def instructions():
    spacer()        #tidy, new line function to avoid walls of text
    print("Welcome to the junk planet. Bolbi the robot awakens and runs a quick diagnostic. His parts are scattered all over!")
    print("Help him get all of his parts, and find him something he can use to defend himself.")
    print("The nearby bots look like they want to melt Bolbi down with the rest of the scrap. Here comes one now!")
    print("This massive robot has a furnace for a face, and he's headed straight towards Bolbi!")
    print("Roll Bolbi's torso around and collect his parts before Furnace-Face catches up to him.")
    spacer()
    print("Bolbi can 'scan' for parts, which also warns of nearby danger; Bolbi can run 'diagnostic' to check which parts he has;")
    print("Bolbi can 'move' to another section of the junkyard; Bolbi can 'wait' in place to skip a turn; the player can also 'exit' the game")
    print("Bolbi has 3 turns. After that, Furnace-Face will move towards him, and the cycle repeats with another 3 turns.")
    print("'move','collect', and 'wait' will consume 1 turn each; 'scan' and 'diagnostic' are unlimited and use no turns. Good luck!")

def diagnostic():   #fancy word for checking player inventory
    spacer()
    print("Checking inventory...")
    haves = []                              #initialized to empty
    for key, value in inventory.items():    #loops through tuples of dict, checks boolean
        if value == True:
            haves.append(key)
    if not haves:                           #if haves list is empty
        haves = ["None"]
    print("Items collected:")
    for i in range(len(haves)):             #display items collected
        print(haves[i])

    spacer()
    have_nots = []
    for key, value in inventory.items():
        if value == False:
            have_nots.append(key)
    if not have_nots:                           #if have_nots list is empty
        have_nots = ["None"]
    print("Items still missing:")
    for i in range(len(have_nots)):
        print(have_nots[i])                     #same process for missing items not collected

    return haves, have_nots

def scan():     #scans for nearby items, also checks if boss is in adjacent room
    # room orientation
    print("Bolbi is surrounded by ", end="")
    print(pos_player)
    # item check
    print("He scans around for items...")
    print(rooms[pos_player]['Item'], end="")
    print(" can be collected here!")

    # debugging position data
    # print(str(rooms[pos_player]['xy']))                   #dbg fixme
    # print(str(rooms[pos_boss]['xy']))                     #dbg fixme

    # BOSS SCAN
    # check x[0] and y[1] coordinates in dictionary and compare player's position to boss'. If adjacent, give warning!
    if rooms[pos_player]['xy'][0] - rooms[pos_boss]['xy'][0] == 1 and rooms[pos_player]['xy'][1] == rooms[pos_boss]['xy'][1]:
        print("Bolbi senses Furnace-Face is very close... West!")
    elif rooms[pos_player]['xy'][0] - rooms[pos_boss]['xy'][0] == -1 and rooms[pos_player]['xy'][1] == rooms[pos_boss]['xy'][1]:
        print("Bolbi senses Furnace-Face is very close... East!")
    elif rooms[pos_player]['xy'][1] - rooms[pos_boss]['xy'][1] == -1 and rooms[pos_player]['xy'][0] == rooms[pos_boss]['xy'][0]:
        print("Bolbi senses Furnace-Face is very close... South!")
    elif rooms[pos_player]['xy'][1] - rooms[pos_boss]['xy'][1] == 1 and rooms[pos_player]['xy'][0] == rooms[pos_boss]['xy'][0]:
        print("Bolbi senses Furnace-Face is very close... North!")
    else:
        print("No sign of Furnace-Face, must be far away.")

def move_pc(pos_player):
    spacer()    #tidy
    # initialize direction to something invalid
    direction = ""

    # keep asking for input until matches one of the directions on the valid list
    # Direction loop {
    while direction not in directions:
        print("Where would you like to move?")

        # prints only valid movement options available by listing keys from inner dict
        print("You can move ", end="")

        # returns list of room "keys" starting after the coord and item
        move_opt = list(rooms[pos_player].keys())[2:]

        # prints the first option
        print(move_opt[0], end=" ")

        # loops through printing the remaining options if present
        for i in range(1, len(move_opt)):
            print("or " + move_opt[i], end="")
            print(" ", end="")

        # input and validation for directions
        direction = str(input())

        if direction not in directions:
            print("invalid direction")

        elif direction not in move_opt:
            print("can't go that way")
            direction = ""  # forces loop cond to fail
    #       } end Direction Loop

    # update player location by assigning the dictionary's inner dictionary value to player location string
    print("Bolbi gets moving...")
    spacer()
    print("Bolbi is now in " + rooms[pos_player][direction])
    spacer()
    pos_player = rooms[pos_player][direction]
    return pos_player

def collect():
    # check if room is empty of items
    if rooms[pos_player]['Item'] != 'None':
        # update inventory
        inventory[rooms[pos_player]['Item']] = True

        # print(inventory)                                      #dbg fixme

        print(rooms[pos_player]['Item'] + " acquired!")
        # update room and "empty" it

        # print(rooms[pos_player])                              #dbg fixme
        rooms[pos_player].update({'Item':'None'})
        # print(rooms[pos_player])                              #dbg fixme
    else:
        print("No item to collect")

def wait():         # do nothing but subtract 1 turn
    print("Bolbi sits in place for a moment...")
    return -1

def exit():         #for setting current room to end gameplay loop
    return 'exit'

def spacer():       #new line to prevent walls of text
    print("")

def move_npc(pos_player,pos_boss):     #the boss turn every 3 player turns
    print("Bolbi detects movement... Furnace-Face is closing in!")
    # make a list of logical moves; if more than one, randomly decide between them
    npc_moves = []
    # compare boss and player xy, plan accordingly
    if rooms[pos_boss]['xy'][0] > rooms[pos_player]['xy'][0]:
        npc_horizontal_move = 'West'
    elif rooms[pos_boss]['xy'][0] < rooms[pos_player]['xy'][0]:
        npc_horizontal_move = 'East'
    else:
        npc_horizontal_move = None
    if rooms[pos_boss]['xy'][1] > rooms[pos_player]['xy'][1]:
        npc_vertical_move = 'North'
    elif rooms[pos_boss]['xy'][1] < rooms[pos_player]['xy'][1]:
        npc_vertical_move = 'South'
    else:
        npc_vertical_move = None
    # add only the logical move choices to the list

    if npc_horizontal_move: npc_moves.append(npc_horizontal_move)
    if npc_vertical_move: npc_moves.append(npc_vertical_move)

    # uncomment to see npc decision-making
    # print(npc_moves)  # dbg fixme
    # print(npc_vertical_move)  # dbg fixme
    # print(npc_horizontal_move)  # dbg fixme

    # randomly decide between them
    npc_move = random.choice(npc_moves)
    # update boss location
    pos_boss = rooms[pos_boss][npc_move]

    # dbg, uncomment to see where boss is after
    # print("boss moved " + npc_move)  # dbg fixme
    # print("Boss now in ")  # dbg fixme
    # print(pos_boss)  # dbg fixme

    return pos_boss

# contains all board information and item placements, navigable directions, also includes (x,y) coordinates for comparing player and boss locations
rooms =\
{
'Aluminum Alloys'       :{'xy':(0,0),'Item':'None',         'South':'Debris Dunes','East':'Broken Bottles'},
'Broken Bottles'        :{'xy':(1,0),'Item':'Left Leg',     'South':'Empty Engines','East':'Copper Contraptions','West':'Aluminum Alloys'},
'Copper Contraptions'   :{'xy':(2,0),'Item':'Right Leg',    'South':'Ferrous Fibers','West':'Broken Bottles'},
'Debris Dunes'          :{'xy':(0,1),'Item':'Left Arm',     'North':'Aluminum Alloys','South':'Gilded Gears','East':'Empty Engines'},
'Empty Engines'         :{'xy':(1,1),'Item':'None',         'North':'Broken Bottles','South':'Heavy Hydraulics','East':'Ferrous Fibers','West':'Debris Dunes',},
'Ferrous Fibers'        :{'xy':(2,1),'Item':'Screwdriver',  'North':'Copper Contraptions','South':'Incendiary Implements','West':'Empty Engines'},
'Gilded Gears'          :{'xy':(0,2),'Item':'Head',         'North':'Debris Dunes','East':'Heavy Hydraulics'},
'Heavy Hydraulics'      :{'xy':(1,2),'Item':'Right Arm',    'North':'Empty Engines','East':'Incendiary Implements','West':'Gilded Gears'},
'Incendiary Implements' :{'xy':(2,2),'Item':'None',         'North':'Ferrous Fibers','West':'Heavy Hydraulics'},
}
directions = ('North','East','South','West')    #for input validation

# false for missing parts, true for collected parts
inventory =\
{
    'Head':         False,
    'Right Arm':    False,
    'Left Arm':     False,
    'Right Leg':    False,
    'Left Leg':     False,
    'Screwdriver':  False
}

turns = 3                       #3 for player loop, 1 for boss

# initial boss and player locations, can be altered for replayability ##########################################
pos_player = 'Aluminum Alloys'
pos_boss = 'Incendiary Implements'

print(instructions())

if __name__ == '__main__':
    # command loop
    while pos_player != 'exit':

        # Boss move
        if turns<=0:
            pos_boss = move_npc(pos_player,pos_boss)
            # reset turns
            turns = 3

        #boss encounter, check for all items collected: if yes, win game; if no, lose game and display "score"
        if pos_player == pos_boss:
            print("Bolbi and Furnace-Face are about to face off!")
            if all(inventory.values()):
                print("Bolbi was ready to fight off Furnace-Face! He unscrewed the bolts from the mechanical menace and watched the heavy machine parts collapse into a scrap heap.")
                print("You have won the game, Congratulations!")
            else:
                print("Bolbi was not prepared to fight. Furnace-Face was easily able to crush him and melt him down for scrap...")
                haves = []                          #extract collected item tuple stats from dictionary and add up score
                for key, value in inventory.items():
                    if value == True:
                        haves.append(key)
                print("Player collected " + str(len(haves)) + " out of 6 parts, but it was not enough. Try Again!")

            pos_player=exit()
            break

        spacer()
        pcom = ""   #player command, the primary user input
        #display options while looping and validating input
        while pcom != 'scan' and pcom != 'diagnostic' and pcom != 'move' and pcom!= 'collect' and pcom!= 'wait' and pcom!= 'exit':
            pcom = input("OPTIONS: scan, diagnostic, move, collect, wait, exit")
            print(pcom + " was chosen")                                             #dbg optional fixme

            if pcom != 'scan' and pcom != 'diagnostic' and pcom != 'move' and pcom!= 'collect' and pcom!= 'wait' and pcom!= 'exit':  #seems redundant but fits rubric
                print("invalid command")

        if pcom == 'exit':              #ends the game
            pos_player = exit()
            continue

        elif pcom == 'move':
            pos_player = move_pc(pos_player)
            turns-=1

        elif pcom == 'diagnostic':
            diagnostic()

        elif pcom == 'collect':
            collect()
            turns-=1

        elif pcom == 'wait':
            turns +=wait()

        elif pcom == 'scan':    #check items and if boss enemy is close (adjacent room)
            spacer()
            scan()

# end Gameplay Loop

print("logging off now...")