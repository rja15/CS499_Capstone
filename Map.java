/**
 * @author raymondagosto_snhu
 * raymond.agosto1@snhu.edu
 * CS499 Capstone Milestone 1
 * 
 * Map class creates linked grid of Room objects, allows navigation between them in cardinal directions;
 * Keeps track of where player and boss are, as well as placement of Items.
 */

import java.util.ArrayList;
import java.util.Random;

public class Map {
	
	private Room mapGrid[][];
	private int dmnX = 3;
	private int rngY = 3;

	public Map()
	{
		
		Room mapGrid[][] = new Room[dmnX][rngY];
		this.mapGrid = mapGrid;
		
		//initialize map rooms 
		mapGrid[0][0] = new Room(null,true,false);
		mapGrid[0][0].setRoomName("Aluminum Alloys");
		mapGrid[0][0].setRoomXY(0,0);
//		mapGrid[0][0].setSouth(mapGrid[0][1]); mapGrid[0][0].setEast(mapGrid[1][0]); connections can't be set for rooms that don't exist yet
		
		mapGrid[1][0] = new Room("Left Leg",false,false);
		mapGrid[1][0].setRoomName("Broken Bottles");
		mapGrid[1][0].setRoomXY(1,0);

		mapGrid[2][0] = new Room("Right Leg",false,false);
		mapGrid[2][0].setRoomName("Copper Contraptions");
		mapGrid[2][0].setRoomXY(2,0);
		
		mapGrid[0][1] = new Room("Left Arm",false,false);
		mapGrid[0][1].setRoomName("Debris Dunes");
		mapGrid[0][1].setRoomXY(0,1);
		
		mapGrid[1][1] = new Room(null,false,false);
		mapGrid[1][1].setRoomName("Empty Engines");
		mapGrid[1][1].setRoomXY(1,1);
		
		mapGrid[2][1] = new Room("Screwdriver",false,false);
		mapGrid[2][1].setRoomName("Ferrous Fibers");
		mapGrid[2][1].setRoomXY(2,1);
		
		mapGrid[0][2] = new Room("Head",false,false);
		mapGrid[0][2].setRoomName("Gilded Gears");
		mapGrid[0][2].setRoomXY(0,2);
		
		mapGrid[1][2] = new Room("Right Arm",false,false);
		mapGrid[1][2].setRoomName("Heavy Hydraulics");
		mapGrid[1][2].setRoomXY(1,2);
		
		mapGrid[2][2] = new Room(null,false,true);
		mapGrid[2][2].setRoomName("Incendiary Implements");
		mapGrid[2][2].setRoomXY(2,2);
		
		//establish room connections
		// A
		mapGrid[0][0].setSouth(mapGrid[0][1]);
		mapGrid[0][0].setEast(mapGrid[1][0]);
		
		//B
		mapGrid[1][0].setSouth(mapGrid[1][1]);
		mapGrid[1][0].setEast(mapGrid[2][0]);
		mapGrid[1][0].setWest(mapGrid[0][0]);
		
		//C
		mapGrid[2][0].setSouth(mapGrid[2][1]);
		mapGrid[2][0].setWest(mapGrid[1][0]);
		
		//D
		mapGrid[0][1].setNorth(mapGrid[0][0]);
		mapGrid[0][1].setSouth(mapGrid[0][2]);
		mapGrid[0][1].setEast(mapGrid[1][1]);
		
		//E
		mapGrid[1][1].setNorth(mapGrid[1][0]);
		mapGrid[1][1].setSouth(mapGrid[1][2]);
		mapGrid[1][1].setEast(mapGrid[2][1]);
		mapGrid[1][1].setWest(mapGrid[0][1]);
		
		//F
		mapGrid[2][1].setNorth(mapGrid[2][0]);
		mapGrid[2][1].setSouth(mapGrid[2][2]);
		mapGrid[2][1].setWest(mapGrid[1][1]);
		
		//G
		mapGrid[0][2].setNorth(mapGrid[0][1]);
		mapGrid[0][2].setEast(mapGrid[1][2]);
		
		//H
		mapGrid[1][2].setNorth(mapGrid[1][1]);
		mapGrid[1][2].setEast(mapGrid[2][2]);
		mapGrid[1][2].setWest(mapGrid[0][2]);
		
		//I
		mapGrid[2][2].setNorth(mapGrid[2][1]);
		mapGrid[2][2].setWest(mapGrid[1][2]);			
		
	}
	
	public Room getMapGrid(int x, int y)
	{
		return mapGrid[x][y];
	}
	
	//Visual Map with labeled rooms [___], p=player, b=boss, i=item
	public String mapToString()
	{
		String mapString = "";
		for (int currY = 0; currY<rngY; currY++)
		{
			for (int currX = 0; currX<dmnX; currX++)
			{
//				mapString.concat(""); does not work?
				mapString+=(mapGrid[currX][currY].getRoomName().substring(0, 1));
				mapString+= "[";
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
	
	//corresponding to "scan" player action
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
		return "Farther Out";
	}
	
	public void moveBoss()
	{
		Room bossRoom = locateBoss();
		Room playerRoom = locatePlayer();
//		ArrayList<String> npcMoves = null; //setup for failure
		ArrayList<String> npcMoves = new ArrayList<String>();
		String npcVerticalMove = null;
		String npcHorizontalMove = null;
		
		//analyze possible moves
		if (bossRoom.getRoomX() > playerRoom.getRoomX())
		{
			npcHorizontalMove = "West";
		}
		else if (bossRoom.getRoomX() < playerRoom.getRoomX())
		{
			npcHorizontalMove = "East";
		}
		
		if (bossRoom.getRoomY() > playerRoom.getRoomY())
		{
			npcVerticalMove = "North";
		}
		else if (bossRoom.getRoomY() < playerRoom.getRoomY())
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
		
		if (playerRoom.getNorth()!=null)
		{
			validMoves.add("North");
		}
		if (playerRoom.getSouth()!=null)
		{
			validMoves.add("South");
		}
		if (playerRoom.getEast()!=null)
		{
			validMoves.add("East");
		}
		if (playerRoom.getWest()!=null)
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
	
	public String collectItem()
	{
		Room playerRoom = locatePlayer();
		String collectedItem = playerRoom.getRoomItem();
		playerRoom.setRoomItem(null);
		
		return collectedItem;
	}
	
}
