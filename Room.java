/**
 * @author raymondagosto_snhu
 * raymond.agosto1@snhu.edu
 * CS499 Capstone Milestone 1
 * 
 * Room class keeps track of coordinates for use in grid, neighboring Room objects,
 * name of Room, and pc/npc presence in Room. 
 */

public class Room {

	private int roomX;	//needed for pc-npc comparison
	private int roomY;
	private String roomName = null;
	private String roomItem = null;
	private boolean playerHere = false;	//change to false in one room, true to adjacent during move
	private boolean bossHere = false;
	
	private Room North = null;	//navigable direction
	private Room South = null;
	private Room East = null;
	private Room West = null;
	
	public Room(String roomItem, boolean playerHere, boolean bossHere)
	{
		this.setRoomItem(roomItem);
		this.setPlayerHere(playerHere);
		this.setBossHere(bossHere);
	}
	
	//getters and setters
	public String getRoomName()
	{
		return roomName;
	}

	public void setRoomName(String roomName)
	{
		this.roomName = roomName;
	}

	public String getRoomItem()
	{
		return roomItem;
	}

	public void setRoomItem(String roomItem)
	{
		this.roomItem = roomItem;
	}

	public boolean isPlayerHere()
	{
		return playerHere;
	}

	public void setPlayerHere(boolean playerHere)
	{
		this.playerHere = playerHere;
	}

	public boolean isBossHere()
	{
		return bossHere;
	}

	public void setBossHere(boolean bossHere)
	{
		this.bossHere = bossHere;
	}

	public Room getNorth()
	{
		return North;
	}

	public void setNorth(Room north)
	{
		North = north;
	}

	public Room getSouth()
	{
		return South;
	}

	public void setSouth(Room south)
	{
		South = south;
	}

	public Room getEast()
	{
		return East;
	}

	public void setEast(Room east)
	{
		East = east;
	}

	public Room getWest()
	{
		return West;
	}

	public void setWest(Room west)
	{
		West = west;
	}

	public void setRoomXY(int x, int y)
	{
		this.roomX = x;
		this.roomY = y;
	}
	
	public int getRoomX()
	{
		return roomX;
	}

	public int getRoomY()
	{
		return roomY;
	}

}
