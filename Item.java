/**
 * @author raymondagosto_snhu
 * raymond.agosto1@snhu.edu
 * CS499 Capstone Milestone 1
 * 
 * Item class, for keeping track of named parts, which should be initialized before player takes them
 */

public class Item {

	private String itemName = null;
	private boolean itemSpawned = false;
	private boolean itemTaken = false;
	
//	private String itemAbility;
	
	public Item(String itemName, boolean itemSpawned, boolean itemTaken)
	{
		this.setItemName(itemName);
		this.setItemSpawned(itemSpawned);
		this.setItemTaken(itemTaken);
	}

	//Setters and Getters:
	
	public String getItemName()
	{
		return itemName;
	}

	public void setItemName(String itemName)
	{
		this.itemName = itemName;
	}

	public boolean isItemSpawned()
	{
		return itemSpawned;
	}

	public void setItemSpawned(boolean itemSpawned)
	{
		this.itemSpawned = itemSpawned;
	}

	public boolean isItemTaken()
	{
		return itemTaken;
	}

	public void setItemTaken(boolean itemTaken)
	{
		this.itemTaken = itemTaken;
	}

}
