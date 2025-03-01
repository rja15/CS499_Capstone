/**
 * @author raymondagosto_snhu
 * raymond.agosto1@snhu.edu
 * CS499 Capstone Milestone 3 Enhancement 2
 * 
 * This program is a text game that has the player collecting items while avoiding the boss enemy.
 * Collecting all items before boss encounter wins the game, while encountering the boss before that loses the game.
 * Player can choose to exit early, and has to make decisions in a turn-based gameplay loop.
 */

import java.util.ArrayList;
import java.util.Scanner;
  
public class main {
	
	public void instructions()
	{
		System.out.println("Welcome to the junk planet. Bolbi the robot awakens and runs a quick diagnostic."
				+ "His parts are scattered all over! Help him get all of his parts, and find him something he can use to defend himself."
				+ "The nearby bots look like they want to melt Bolbi down with the rest of the scrap. Here comes one now!"
				+ ""
				+ "");
	}

	public static void main(String[] args)
	{

		System.out.println("Welcome to the junk planet. Bolbi the robot awakens and runs a quick diagnostic.\n"
				+ "His parts are scattered all over! Help him get all of his parts, and find him something he can use to defend himself.\n"
				+ "The nearby bots look like they want to melt Bolbi down with the rest of the scrap. Here comes one now!\n"
				+ "This massive robot has a furnace for a face, and he's headed straight towards Bolbi!\n"
				+ "Roll Bolbi's torso around and collect his parts before Furnace-Face catches up to him.\n\n"
				+ "Bolbi can 'scan' for parts, which also warns of nearby danger; Bolbi can run 'diagnostic' to check which parts he has;\n"
				+ "Bolbi can 'collect' parts within the current area; Bolbi can 'move' to another section of the junkyard\n"
				+ "Player can also choose to 'exit' the game; Bolbi can 'wait' in place to skip a turn;\n"
				+ "Bolbi has 2 turns. Bolbi takes a turn by moving. After that, Furnace-Face will move towards him, and the cycle repeats with another 2 turns.\n"
				+ "Bolbi cannot move into an area that is blocked by an obstacle. Furnace-Face can move through an obstacle,"
				+ "but it takes him 3 turns instead of 2. Good luck!\\n"
				+ "");
		
		Scanner input = new Scanner(System.in);
		
		int MAX_PLAYER_TURNS = 2;		//this has been changed
		int turns = MAX_PLAYER_TURNS;
		
		Map map = new Map();
		
		ArrayList<String> validCommands = new ArrayList<String>();
		validCommands.add("scan");
		validCommands.add("diagnostic");
		validCommands.add("move");
		validCommands.add("collect");
		validCommands.add("wait");
		validCommands.add("exit");
		
		boolean quitGame = false;
		
		// Gameplay Loop
		while (!quitGame)
		{
			//Visual map for demonstration and testing purposes, not normally available to player:
//			System.out.println("\nUpdated Game Map: \n"+map.mapToString()); //FIXME uncomment for visual map
			
			//Boss Move
			if (turns<=0)
			{
				System.out.println("Bolbi detects movement... Furnace-Face is closing in!");
				map.moveBoss();
				turns = MAX_PLAYER_TURNS;
				if(map.locateBoss().isObstacle())
				{
					System.out.println("Furnace-Face has encountered an obstacle. It will only take a moment for him to get through!");
					turns++;
				}
			}
			
			//Confrontation and end-game state
			if (map.locatePlayer().isBossHere() == true)
			{
				System.out.println("Bolbi and Furnace-Face are about to face off!");
				int itemsAcquired = 0;
				
				for (String i : map.getInventory().keySet())
				{
					if(map.getInventory().get(i).isItemTaken())
					{
						itemsAcquired++;
					}
				}

				if (itemsAcquired == map.getItemTotal())
				{
					System.out.println("Bolbi was ready to fight off Furnace-Face! He unscrewed the bolts "
							+ "from the mechanical menace and watched the heavy machine parts collapse into a scrap heap.");
					System.out.println("You have won the game, Congratulations!");
				}
				else
				{
					System.out.println("Bolbi was not prepared to fight. Furnace-Face was easily able to crush him and melt him down for scrap...");				
					System.out.println("Player collected " + itemsAcquired
							+ " out of " + map.getItemTotal()+ ", and it was not enough. Try again!");
				}
			quitGame = true;
			break;
			}
			
			// Player Choice Loop
			String playerCommand = "";
			while (!playerCommand.equals("exit")
					&& !playerCommand.equals("scan")
					&& !playerCommand.equals("diagnostic")
					&& !playerCommand.equals("move")
					&& !playerCommand.equals("collect")
					&& !playerCommand.equals("wait")
					&& !playerCommand.equals("exit")
				  )
			{
				System.out.println("OPTIONS: scan, diagnostic, move, collect, wait, exit");
				playerCommand = input.nextLine();
				
				if (!validCommands.contains(playerCommand))
				{
					System.out.println("invalid command");
				}
			}
				
				if (playerCommand.equals("exit"))
				{
					quitGame = true;
					continue;
				}
				
				else if (playerCommand.equals("scan"))
				{
					Room playerRoom = map.locatePlayer();
					System.out.println("Bolbi is surrounded by " + playerRoom.getRoomName());
					System.out.println("He scans around for items...");
					if(playerRoom.getRoomItem() == null)
					{System.out.print("None");}
					else
					{System.out.print(playerRoom.getRoomItem());} 
					System.out.print(" can be found here!\n");
					
					System.out.print("Bolbi senses Furnace-Face is");
					if (!map.whereBossAdjacent().equals("farther out"))
					{System.out.print(" very close... "+map.whereBossAdjacent()+"!\n");}
					else {System.out.print(" somewhere "+map.whereBossAdjacent() +"...\n");}
				}
				
				else if (playerCommand.equals("move"))
				{
					String direction = "";
					ArrayList<String> directions = new ArrayList<String>();
					directions.add("North");
					directions.add("South");
					directions.add("East");
					directions.add("West");
		
					while(!directions.contains(direction))
					{
						System.out.println("You can move ");
						System.out.print(map.getValidMoves().get(0)+" ");
						for(int i = 1; i < map.getValidMoves().size(); i++)
						{
							System.out.print("or " + map.getValidMoves().get(i) + " ");
						}	
						System.out.println("");
						
						direction = input.nextLine();
						
						if(!directions.contains(direction))
						{
							System.out.println("invalid direction");
						}
						
						if(!map.getValidMoves().contains(direction))
						{
							System.out.println("can't go that way");
							direction = "";
						}
						
					}
					System.out.println("Bolbi gets moving... \n");
					map.movePlayer(direction);
					System.out.println("Bolbi finds himself among " + map.locatePlayer().getRoomName());
					turns-=1;
				}
				else if (playerCommand.equals("diagnostic"))
				{
					System.out.println(map.inventoryToString());
				}
				else if (playerCommand.equals("collect"))
				{
					Room playerRoom = map.locatePlayer();
					String itemName = playerRoom.getRoomItem();
					if (playerRoom.getRoomItem()!=null)
					{
						System.out.println("Bolbi takes the part.\nHe now has a " + itemName + "!");
						map.collectItem();
					}
					else
					{
						System.out.println("Nothing to take here, no action taken.");
					}
					
				}
				else if (playerCommand.equals("wait"))
				{
					System.out.println("Bolbi sits in place for a moment...");
					turns-=1;
				}
				
		}	//end Gameplay Loop
		
		System.out.println("Logging off now...");
		input.close();
	}
}
