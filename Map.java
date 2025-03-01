/**
 * @author raymondagosto_snhu
 * raymond.agosto1@snhu.edu
 * CS499 Capstone Milestone 3 Enhancement 2
 * 
 * Map class creates linked grid of Room objects, allows navigation between them in cardinal directions;
 * On construction, Map object randomly places items around map, spawns in player and non-player character
 * Inventory, a HashMap of Item class objects is used to keep track of and access items to be picked up by player
 * Obstacles are randomly placed around the map, which obstruct player movement and slow NPC movement.
 * 
 * Node connecting loop inspired by: https://stackoverflow.com/questions/2679503/java-how-to-code-node-neighbours-in-a-grid 
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Map {
	
	private HashMap<String,Item> inventory;
	
	private static String[] mapRoomNames = new String[]
		{
			"Aluminum Alloys","Broken Bottles", "Copper Contraptions", "Debris Dunes", "Empty Engines",
			"Ferrous Fibers", "Gilded Gears", "Heavy Hydraulics", "Incendiary Implements", "Jittering Junk",
			"Kerosene Kitsch", "Lumpy Litter", "Morose Machinery", "Nasty Nails", "Outcast Offal",
			"Panned Piffle", "Quaggy Quarry", "Rusted Rubbish", "Sodden Scrap", "Torn Tapestry",
			"Used Undercarriages", "Viscous Varnish", "Wasted Welding", "Xenon Xenoliths", "Zapped Zippers"			
		};	//	around here we like alliteration
	
	private String[] itemsToPlace =
			{
				"Head","Right Arm","Left Arm","Right Leg","Left Leg","Screwdriver"
			};
	
	private int itemTotal = 6;

	private Room mapGrid[][];
	private int dmnX = 5;
	private int rngY = 5;
	
	private static int maxObstacles = 3;
	
	public Map()
	{
		//INITIALIZING MAP
		Room mapGrid[][] = new Room[dmnX][rngY];
		this.mapGrid = mapGrid;
		
		//INITIALIZING INVENTORY
		inventory = new HashMap<String,Item>();
		
		//NAMING MAP
		int nameNum = 0;
		for (int currY = 0; currY < rngY; currY++)
		{
			for (int currX = 0; currX < dmnX; currX++)
			{
				this.mapGrid[currX][currY] = new Room(mapRoomNames[nameNum],new Coordinate(currX,currY));
				nameNum++;
			}
		}
		
		//CONNECTING MAP
		//Node connect loop inspired by https://stackoverflow.com/questions/2679503/java-how-to-code-node-neighbours-in-a-grid
		for (int currY = 0; currY < rngY; currY++)
		{
			for (int currX = 0; currX < dmnX; currX++)
			{
				if(currY > 0)	// North not out of bounds
				{
					mapGrid[currX][currY].setNorth(mapGrid[currX][currY-1]);
				}
				if(currY < rngY-1)	// South not out of bounds
				{
					mapGrid[currX][currY].setSouth(mapGrid[currX][currY+1]);
				}
				if(currX > 0)	// West not out of bounds
				{
					mapGrid[currX][currY].setWest(mapGrid[currX-1][currY]);
				}
				if(currX < dmnX-1)	// East not out of bounds
				{
					mapGrid[currX][currY].setEast(mapGrid[currX+1][currY]);
				}
			}
		}
		
		//BLOCKING MAP
		Random random = new Random();
		// nextInt() returns a pseudorandom, uniformly distributed int value between 0 (inclusive) and the specified value (exclusive),
		int randNumObstacles = random.nextInt(Map.maxObstacles); //something between 0 and max
		int currentObstacles = this.countObstacles();
		
		while((currentObstacles < Map.maxObstacles)&&(currentObstacles < randNumObstacles))
		{
			Coordinate randomXY = randomXYbetween(1,4);
			this.mapGrid[randomXY.getCoordX()][randomXY.getCoordY()].setObstacle(true);
			currentObstacles++;
		}

		//SPAWNING PC/NPC
		
		//spawn Player somewhere that is not blocked or occupied by boss
		boolean playerSpawned = false;
		while(playerSpawned == false)
		{
			Coordinate randomXY = randomXYbetween(0,dmnX);	//5x5 map, so "X" here applies to "Y" as well
			if ((!mapGrid[randomXY.getCoordX()][randomXY.getCoordY()].isBossHere())&&
					(!mapGrid[randomXY.getCoordX()][randomXY.getCoordY()].isObstacle()))
			{
				this.mapGrid[randomXY.getCoordX()][randomXY.getCoordY()].setPlayerHere(true);
				playerSpawned = true;
			}
		}
		
		//spawn Boss somewhere that is not blocked or occupied by player
		boolean bossSpawned = false;
		while(bossSpawned == false)
		{
			Coordinate randomXY = randomXYbetween(0,dmnX);
			if ((!mapGrid[randomXY.getCoordX()][randomXY.getCoordY()].isPlayerHere())&&
					(!mapGrid[randomXY.getCoordX()][randomXY.getCoordY()].isObstacle()))
			{
				this.mapGrid[randomXY.getCoordX()][randomXY.getCoordY()].setBossHere(true);
				bossSpawned = true;
			}
		}

		//NEED INVENTORY TO EXIST BEFORE PLACEMENT
		for(int i=0; i<getItemTotal(); i++)
		{
			this.inventory.put(itemsToPlace[i],new Item(itemsToPlace[i]));
		}
		
		//PLACING INVENTORY
		//remember to make unique placements
		int itemPlace = 0;
		do
		{
			{
				Coordinate randomXY = randomXYbetween(0,dmnX);
				if ((!mapGrid[randomXY.getCoordX()][randomXY.getCoordY()].isPlayerHere())&&
						(!mapGrid[randomXY.getCoordX()][randomXY.getCoordY()].isObstacle())&&
						(mapGrid[randomXY.getCoordX()][randomXY.getCoordY()].getRoomItem()==null))		// only 1 item per room
				{
					this.mapGrid[randomXY.getCoordX()][randomXY.getCoordY()].setRoomItem(itemsToPlace[itemPlace]);	//init RoomItem String
					inventory.get(itemsToPlace[itemPlace]).setItemSpawned(true);									//change Item spawned attributed
					inventory.get(itemsToPlace[itemPlace]).setItemLocation(randomXY);;								//set item Coord to same as mapGrid where just placed
					itemPlace++;
				}
			}
		}while(itemPlace < getItemTotal());
	}
	

	/**@param
	 * @return Room object at (x,y) coordinates*/
	public Room getMapGrid(int x, int y)
	{
		return mapGrid[x][y];
	}
	
	/** Visual Map with labeled rooms like: R[_p_i], p=player, b=boss, i=item, X=obstacle */
	public String mapToString()
	{
		String mapString = "";
		for (int currY = 0; currY<rngY; currY++)
		{
			for (int currX = 0; currX<dmnX; currX++)
			{
				mapString+=(mapGrid[currX][currY].getRoomName().substring(0, 1));
				mapString+= "[";
				if(mapGrid[currX][currY].isObstacle())
				{
					mapString+="X";
				}
				else{mapString+="_";}
				if(mapGrid[currX][currY].isPlayerHere())
				{
					mapString+=("p");
				}
				else{mapString+="_";}
				if(mapGrid[currX][currY].isBossHere())
				{
					mapString+="b";
				}
				else{mapString+="_";}
				if(mapGrid[currX][currY].getRoomItem()!=null)
				{
					mapString+="i";
				}
				else{mapString+="_";}
				mapString+= "] ";
			}
			mapString+="\n";
		}
		return mapString;
	}
	/** @return Room in which boss is marked as present*/
	public Room locateBoss()
	{
		for (int currY = 0; currY<rngY; currY++)
		{
			for (int currX = 0; currX<dmnX; currX++)
			{
				if(mapGrid[currX][currY].isBossHere())
				{
					return mapGrid[currX][currY];
				}
			}
		}
		return null;
	}
	
	/** @return Room in which player is marked as present*/
	public Room locatePlayer()
	{
		for (int currY = 0; currY<rngY; currY++)
		{
			for (int currX = 0; currX<dmnX; currX++)
			{
				if(mapGrid[currX][currY].isPlayerHere())
				{
					return mapGrid[currX][currY];
				}
			}
		}
		return null;
	}
	
	/**corresponding to "scan" player action */
	public String whereBossAdjacent()
	{
		Room playerRoom = locatePlayer();
		
		if ((playerRoom.getNorth()!=null)&&playerRoom.getNorth().isBossHere())
		{
			return "North";
		}
		if ((playerRoom.getSouth()!=null)&&playerRoom.getSouth().isBossHere())
		{
			return "South";
		}
		if ((playerRoom.getEast()!=null)&&playerRoom.getEast().isBossHere())
		{
			return "East";
		}
		if ((playerRoom.getWest()!=null)&&playerRoom.getWest().isBossHere())
		{
			return "West";
		}
		return "farther out";
	}
	
	public void moveBoss()
	{
		Room bossRoom = locateBoss();
		Room playerRoom = locatePlayer();
		ArrayList<String> npcMoves = new ArrayList<String>();
		String npcVerticalMove = null;
		String npcHorizontalMove = null;
		
		//establish potential moves
		if (bossRoom.getRoomCoord().getCoordX() > playerRoom.getRoomCoord().getCoordX())
		{
			npcHorizontalMove = "West";
		}
		else if (bossRoom.getRoomCoord().getCoordX() < playerRoom.getRoomCoord().getCoordX())
		{
			npcHorizontalMove = "East";
		}
		
		if (bossRoom.getRoomCoord().getCoordY() > playerRoom.getRoomCoord().getCoordY())
		{
			npcVerticalMove = "North";
		}
		else if (bossRoom.getRoomCoord().getCoordY() < playerRoom.getRoomCoord().getCoordY())
		{
			npcVerticalMove = "South";
		}
		
		if (npcHorizontalMove!=null)
		{
			npcMoves.add(npcHorizontalMove);
		}
		if (npcVerticalMove!=null)
		{
			npcMoves.add(npcVerticalMove);
		}
		
		//randomly choose from potential moves
		Random random = new Random();
		int randi = random.nextInt(npcMoves.size());
		String npcMove = npcMoves.get(randi);
		
		//actually move the boss
		bossRoom.setBossHere(false);
		if(npcMove.equals("North"))
		{
			bossRoom.getNorth().setBossHere(true);
		}
		if(npcMove.equals("South"))
		{
			bossRoom.getSouth().setBossHere(true);
		}
		if(npcMove.equals("East"))
		{
			bossRoom.getEast().setBossHere(true);
		}
		if(npcMove.equals("West"))
		{
			bossRoom.getWest().setBossHere(true);
		}
	}
	
	public ArrayList<String> getValidMoves()
	{
		ArrayList<String> validMoves = new ArrayList<String>();
		Room playerRoom = locatePlayer();
		
		if ((playerRoom.getNorth()!=null)&&(!playerRoom.getNorth().isObstacle()))
		{
			validMoves.add("North");
		}
		if ((playerRoom.getSouth()!=null)&&(!playerRoom.getSouth().isObstacle()))
		{
			validMoves.add("South");
		}
		if ((playerRoom.getEast()!=null)&&(!playerRoom.getEast().isObstacle()))
		{
			validMoves.add("East");
		}
		if ((playerRoom.getWest()!=null)&&(!playerRoom.getWest().isObstacle()))
		{
			validMoves.add("West");
		}
		return validMoves;
	}
	
	public void movePlayer(String direction)
	{
		Room playerRoom = locatePlayer();
		playerRoom.setPlayerHere(false);
		
		String playerMove = direction;
		if(playerMove.equals("North"))
		{
			playerRoom.getNorth().setPlayerHere(true);
		}
		if(playerMove.equals("South"))
		{
			playerRoom.getSouth().setPlayerHere(true);
		}
		if(playerMove.equals("East"))
		{
			playerRoom.getEast().setPlayerHere(true);
		}
		if(playerMove.equals("West"))
		{
			playerRoom.getWest().setPlayerHere(true);
		}
		
	}
	
	/** Where player is, see what item is there, mark it as taken, and delete from map
	 * @return name of the item collected*/
	public String collectItem()
	{
		Room playerRoom = locatePlayer();						//find player
		String collectedItem = playerRoom.getRoomItem();		//distinguish item
		playerRoom.setRoomItem(null);							//take if off the map
		this.inventory.get(collectedItem).setItemTaken(true);	//mark it as taken
		return collectedItem;
	}
	
	public int countObstacles()
	{
		int obstacles = 0;
		for (int currY = 0; currY < rngY; currY++)
		{
			for (int currX = 0; currX < dmnX; currX++)
			{
				if(this.mapGrid[currX][currY].isObstacle())
				{
					obstacles++;
				}
			}
		}
		return obstacles;
	}
	
	public HashMap<String, Item> getInventory() {
		return inventory;
	}

	public void setInventory(HashMap<String,Item> inventory) {
		this.inventory = inventory;
	}
	
	/**corresponds to "diagnostic" player action*/
	public String inventoryToString()
	{
		String inventoryString ="";
		inventoryString+="Checking parts... \n"
				+"\nParts collected:\n";
		
		for (String i : inventory.keySet())
		{
			if(inventory.get(i).isItemTaken())
			{
				inventoryString+=(inventory.get(i).getItemName()+"\n");
			}
		}
		
		inventoryString+="\nParts still missing:\n";
		for (String i : inventory.keySet())
		{
			if(!inventory.get(i).isItemTaken())
			{
				inventoryString+=(inventory.get(i).getItemName()+"\n");
			}
		}
		
		return inventoryString;
	}
	
	/**normally nextInt(max) yields (0<=result<=max), but tweaked here for (min<=result<=max)
	 * @param*/
	public Coordinate randomXYbetween(int min, int max)
	{
		Random random = new Random();
		int x = random.nextInt(max-1);
		if (x < min){x = min;}
		int y = random.nextInt(max-1);
		if (y < min){y = min;}
		Coordinate blindPick = new Coordinate(x,y);
		return blindPick;
	}

	public int getItemTotal() {
		return itemTotal;
	}

	public void setItemTotal(int itemTotal) {
		this.itemTotal = itemTotal;
	}
	
}
