/**
 * @author raymondagosto_snhu
 * raymond.agosto1@snhu.edu
 * CS499 Capstone Milestone 1
 * 
 * Player class keeps track of parts inventory using Item class objects.
 * Inventory can be searched, modified, and printed to a string
 */
import java.util.ArrayList;


public class Player {
	
	ArrayList<Item> inventory = new ArrayList<Item>();
	private int itemTotal = 6;

	//constructor initialized with Items
	public Player()
	{
		
		ArrayList<Item> inventory = new ArrayList<Item>();
		this.inventory = inventory;

		inventory.add(new Item("Head",false,false));	//(itemName,itemSpawned,itemTaken)
		inventory.add(new Item("Right Arm",false,false));
		inventory.add(new Item("Left Arm",false,false));
		inventory.add(new Item("Right Leg",false,false));
		inventory.add(new Item("Left Leg",false,false));
		inventory.add(new Item("Screwdriver",false,false));

	}
	
	//corresponds to "diagnostic" player action
	public String inventoryToString()
	{
		String inventoryString ="";
		inventoryString+="Checking parts... \n"
				+"\nParts collected:\n";
		
		for(int i=0;i<itemTotal;i++)
		{
			if(inventory.get(i).isItemTaken())
			{
				inventoryString+=(inventory.get(i).getItemName()+"\n");
			}
		}
		
		inventoryString+="\nParts still missing:\n";
		for(int i=0;i<itemTotal;i++)
		{
			if(!inventory.get(i).isItemTaken())
			{
				inventoryString+=(inventory.get(i).getItemName()+"\n");
			}
		}
		
		return inventoryString;
	}
	

	public ArrayList<Item> getInventory()
	{
		return inventory;
	}

	public void setInventory(ArrayList<Item> inventory)
	{
		this.inventory = inventory;
	}
	
	public int getItemTotal()
	{
		return this.itemTotal;
	}
	
	public Item getItemByName(String searchString)
	{
		for (int i = 0; i< inventory.size();i++)
		{
			if(inventory.get(i).getItemName().equals(searchString))
			{
				return inventory.get(i);
			}
		}
		return null;
	}
}