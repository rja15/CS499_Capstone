/**
 * @author raymondagosto_snhu
 * raymond.agosto1@snhu.edu
 * CS499 Capstone Milestone 3 Enhancement 2
 * 
 * This program is a text game that has the player collecting items while avoiding the boss enemy.
 * Collecting all items before boss encounter wins the game, while encountering the boss before that loses the game.
 * Player can choose to exit early, and has to make decisions in a turn-based gameplay loop.
 * 
 * Statistics including player actions and scores are now recorded in an SQLite database
 * Required library, driver information, and examples found via https://www.tutorialspoint.com/sqlite/sqlite_java.htm
 */

import java.util.ArrayList;
import java.util.Scanner;

import java.sql.*;

public class main {
	
	public static void instructions()
	{
		System.out.println("Welcome to the junk planet. Bolbi the robot awakens and runs a quick diagnostic.\n"
				+ "His parts are scattered all over! Help him get all of his parts, and find him something he can use to defend himself.\n"
				+ "The nearby bots look like they want to melt Bolbi down with the rest of the scrap. Here comes one now!\n"
				+ "This massive robot has a furnace for a face, and he's headed straight towards Bolbi!\n"
				+ "Roll Bolbi's torso around and collect his parts before Furnace-Face catches up to him.\n\n"
				+ "Bolbi can 'scan' for parts, which also warns of nearby danger; Bolbi can run 'diagnostic' to check which parts he has;\n"
				+ "Bolbi can 'collect' parts within the current area; Bolbi can 'move' to another section of the junkyard\n"
				+ "Player can also choose to 'exit' the game; Bolbi can 'wait' in place to skip a turn; Bolbi has 2 turns.\n"
				+ "Bolbi takes a turn by moving. After that, Furnace-Face will move towards him, and the cycle repeats with another 2 turns.\n"
				+ "Bolbi cannot move into an area that is blocked by an obstacle. Furnace-Face can move through an obstacle,"
				+ "but it takes him 3 turns instead of 2.\n"
				+ "Good luck!");
	}

	public static void main(String[] args)
	{
		//DATABASE VARIABLES
		int totalGames;
		int totalWins;
		int totalLosses;
		int totalPartsCollected;
		int totalPlayerActions;
		int recentPartsCollected = 0;
		int recentPlayerActions = 0;
		int Score = 0;
		String nameScore = null;
		int MAX_TO_KEEP = 5;

		
		instructions();
		Scanner input = new Scanner(System.in);
		
		int MAX_PLAYER_TURNS = 2;
		int turns = MAX_PLAYER_TURNS;
		
		Map map = new Map();
		
		ArrayList<String> validCommands = new ArrayList<String>();
		validCommands.add("scan");
		validCommands.add("diagnostic");
		validCommands.add("move");
		validCommands.add("collect");
		validCommands.add("wait");
		validCommands.add("exit");
		
		ArrayList<String> scoreboardCommands = new ArrayList<String>();
		scoreboardCommands.add("stats");
		scoreboardCommands.add("recent");
		scoreboardCommands.add("scores");
		scoreboardCommands.add("exit");
		
		boolean quitGame = false;
		boolean endPostGame = false;
		
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
				
				int scoreMultiplier;
				
				//Trigger Win/Loss Condition
				if (itemsAcquired == map.getItemTotal())
				{
					System.out.println("Bolbi was ready to fight off Furnace-Face! He unscrewed the bolts "
							+ "from the mechanical menace and watched the heavy machine parts collapse into a scrap heap.");
					System.out.println("You have won the game, Congratulations!");
					scoreMultiplier = 1000;
				}
				else
				{
					System.out.println("Bolbi was not prepared to fight. Furnace-Face was easily able to crush him and melt him down for scrap...");				
					System.out.println("Player collected " + itemsAcquired
							+ " out of " + map.getItemTotal()+ " parts, and it was not enough. Try again!");
					scoreMultiplier = 100;
				}
				
				//SCORE CALCULATION: victory (6 items/actions)*1000 or... defeat (<6 items/actions)*100 
				if(recentPlayerActions < 1){recentPlayerActions = 100;}	//prevent division by zero, 100 is arbitrary unfavorable number
				float fraction = ((float)itemsAcquired/(float)recentPlayerActions) * scoreMultiplier;
				Score = (int) fraction;
				System.out.println("Your score: " + Score);
				
				// DATABASE START	///////////////////////////////////////////////////////////////////////////
				Connection c = null;
				Statement stmt = null;
				
				/** OPEN THE DATABASE */
				try
				{
					Class.forName("org.sqlite.JDBC");
					c = DriverManager.getConnection("jdbc:sqlite:BBvFF.db");
					c.setAutoCommit(false);
//					System.out.println("Opened database successfully");	//FIXME: Uncomment for Database Info
				}
				catch ( Exception e )
				{
			          System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			          System.exit(0);
				}
				
				//CREATE TABLES
				try
				{
					stmt = c.createStatement();	// only needs to be done once
					
					//GLOBAL_STATS TABLE, single entry
					String 	sql = "CREATE TABLE GLOBAL_STATS " +
								"( GAMES_PLAYED				INT,"
								+ "GAMES_WON				INT,"
								+ "GAMES_LOST				INT,"
								+ "TOTAL_PARTS_COLLECTED	INT,"
								+ "TOTAL_PLAYER_ACTIONS		INT)";
					stmt.executeUpdate(sql);
					c.commit();
					
					//RECENT_GAMES TABLE, up to MAX_TO_KEEP
							sql = "CREATE TABLE RECENT_GAMES " +
								"( GAMES_AGO 				INT,"
								+ "PARTS_COLLECTED			INT,"
								+ "PLAYER_ACTIONS			INT,"
								+ "ROUND_SCORE				INT)";
					stmt.executeUpdate(sql);
					c.commit();
					
					//HIGH_SCORES TABLE, up to MAX_TO_KEEP, ranked from greatest to least
							sql = "CREATE TABLE HIGH_SCORES " +
								"( RANKING					INT,"
								+ "SCORE					INT,"
								+ "NAME						VARCHAR(3))";
					stmt.executeUpdate(sql);
					c.commit();
				
//					System.out.println("Tables created successfully");	//FIXME: Uncomment for Database Info
					
				}
				catch (Exception e)
				{
			        if(e.getMessage().contains(" already exists"))
			        {
//						System.out.println("Tables already created");	//FIXME: Uncomment for Database Info
			        }
			        else
			        {
						System.err.println( e.getClass().getName() + ": " + e.getMessage() );
					}
				}
				
				//INITIALIZE
				try
				{
					//GLOBAL_STATS
					ResultSet rs = stmt.executeQuery("SELECT * FROM GLOBAL_STATS;");
					if (!rs.next())	//check for existing before trying to initialize
					{
						String sql = "INSERT INTO GLOBAL_STATS (GAMES_PLAYED,GAMES_WON,GAMES_LOST,TOTAL_PARTS_COLLECTED,"
								+ "TOTAL_PLAYER_ACTIONS) VALUES (0,0,0,0,0);";
						stmt.executeUpdate(sql);
//						System.out.println("GLOBAL_STATS Initialized Successfully");	//FIXME: Uncomment for Database Info
					}
					else
					{
//						System.out.println("GLOBAL_STATS already initialized");		//FIXME: Uncomment for Database Info
					}
					
					//RECENT_GAMES
							rs = stmt.executeQuery("SELECT * FROM RECENT_GAMES;");
					if (!rs.next())
					{
						for(int i=1; i<=MAX_TO_KEEP; i++)
						{
						String 	sql = "INSERT INTO RECENT_GAMES (GAMES_AGO, PARTS_COLLECTED, PLAYER_ACTIONS, ROUND_SCORE)"
								+ " VALUES ("+i+",0,0,0);";
						stmt.executeUpdate(sql);
						}
						c.commit();
//						System.out.println("RECENT_GAMES Initialized Successfully");	//FIXME: Uncomment for Database Info
					}
					else
					{
//						System.out.println("RECENT_GAMES already initialized");		//FIXME: Uncomment for Database Info
					}
					
					//HIGH_SCORES
							rs = stmt.executeQuery("SELECT * FROM HIGH_SCORES;");
					if (!rs.next())
					{
						String sql = "INSERT INTO HIGH_SCORES (RANKING,SCORE,NAME)"
								+ "VALUES (1,0,'AAA');";
						stmt.executeUpdate(sql);
						c.commit();
//						System.out.println("HIGH_SCORES Initialized Successfully");	//FIXME: Uncomment for Database Info
					}
					else
					{
//						System.out.println("HIGH_SCORES already initialized");		//FIXME: Uncomment for Database Info
					}
				}
				catch(Exception e)
				{
					System.err.println( e.getClass().getName() + ": " + e.getMessage() );
				}		    
				
				//Access/Update GLOBAL_STATS
				try
				{
					ResultSet rs = stmt.executeQuery("SELECT * FROM GLOBAL_STATS;");
					totalGames = rs.getInt("GAMES_PLAYED");
					totalWins = rs.getInt("GAMES_WON");
					totalLosses = rs.getInt("GAMES_LOST");
					totalPartsCollected = rs.getInt("TOTAL_PARTS_COLLECTED");
					totalPlayerActions = rs.getInt("TOTAL_PLAYER_ACTIONS");
					
					//Increment GAMES_PLAYED
					totalGames++;
					String 	sql ="UPDATE GLOBAL_STATS set GAMES_PLAYED = " + totalGames + " ;";
					stmt.executeUpdate(sql);
					
					/** Try-catch precludes tying db variables to the previous win/loss condition because "local, not initialized"
						rather game win/loss not depend on whether db is in a good mood or not */
					if (itemsAcquired == map.getItemTotal())
					{
						//Increment GAMES_WON
						totalWins++;
							sql ="UPDATE GLOBAL_STATS set GAMES_WON = " + totalWins + " ;";
							stmt.executeUpdate(sql);
					}
					else
					{
						//Increment GAMES_LOST
						totalLosses++;
							sql ="UPDATE GLOBAL_STATS set GAMES_LOST = " + totalLosses + " ;";
							stmt.executeUpdate(sql);
					}
					
					//Update TOTAL_PARTS_COLLECTED
					totalPartsCollected += itemsAcquired;
							sql ="UPDATE GLOBAL_STATS set TOTAL_PARTS_COLLECTED = " + totalPartsCollected + " ;";
					stmt.executeUpdate(sql);
					
					//Update TOTAL_PLAYER_ACTIONS
					totalPlayerActions += recentPlayerActions;
							sql ="UPDATE GLOBAL_STATS set TOTAL_PLAYER_ACTIONS = " + totalPlayerActions + " ;";
					stmt.executeUpdate(sql);
					
					c.commit();
					rs.close();

				}
				catch(Exception e)
				{
					System.err.println( e.getClass().getName() + ": " + e.getMessage() );
				}
				
				//UPDATE RECENT_GAMES
				try
				{
					//INCREMENT OLDER GAMES
					for (int i=MAX_TO_KEEP; i>0; i--)
					{
					String 	sql = ("UPDATE RECENT_GAMES set GAMES_AGO = "+(i+1)
							+ "	WHERE GAMES_AGO = "+ (i));
					stmt.execute(sql);
					}
					
					//CUT OFF AFTER MAX
					String 	sql = ("DELETE FROM RECENT_GAMES WHERE GAMES_AGO = " + (MAX_TO_KEEP + 1));
					stmt.execute(sql);
					
					//INSERT MOST RECENT (1)
							sql = ("INSERT INTO RECENT_GAMES (GAMES_AGO, PARTS_COLLECTED, PLAYER_ACTIONS, ROUND_SCORE)"
									+ "VALUES (1," +itemsAcquired+ ", " +recentPlayerActions+ ", " +Score+ ")");
					stmt.execute(sql);												//FIXME
					c.commit();

				}
				catch(Exception e)
				{
					System.err.println( e.getClass().getName() + ": " + e.getMessage() );
				}

				//UPDATE HIGH_SCORES
				int tempScore = Score;
				String tempName = nameScore;
				int dbScore;
				String dbName;
				int insertAt = 1;	//db starts at 1 instead of 0
				try
				{
					ResultSet rs = stmt.executeQuery("SELECT * FROM HIGH_SCORES ORDER BY SCORE DESC");
					rs.next();	//go from start-1 to start
					for(int j=1; j<=MAX_TO_KEEP; j++)	// lone < was causing dangling duplicates if new was lower than ending entry
					{
						if(rs.getInt("SCORE") < tempScore)
						{
							insertAt = j;
							for(int k=MAX_TO_KEEP; k>=insertAt; k--)
							{
								String 	sql = ("UPDATE HIGH_SCORES set RANKING = "+(k+1)
										+ "	WHERE RANKING = "+ (k));
								stmt.execute(sql);
							}
							break;
						}
						else
						{
							insertAt++;
						}
						rs.next();
					}
					
					//INSERT NEW SCORE WHERE ...before delete because otherwise might create another caboose
					String 	sql = ("INSERT INTO HIGH_SCORES (RANKING,SCORE,NAME)"
							+ "VALUES (" +insertAt+ ", " +tempScore+ ", " +tempName+ ")");
					stmt.execute(sql);
					//CUT OFF AFTER MAX
							sql = ("DELETE FROM HIGH_SCORES WHERE RANKING >= " + (MAX_TO_KEEP + 1));
					stmt.execute(sql);	
					c.commit();
				}
				catch(Exception e)
				{
					System.err.println( e.getClass().getName() + ": " + e.getMessage() );
				}

				System.out.println("You can now view Overall Statistics, Recent Games, or High Scores.");
				
				while(!endPostGame)
				{
					String scoreboardCommand = "";
					while (!scoreboardCommand.equals("stats")
							&& !scoreboardCommand.equals("recent")
							&& !scoreboardCommand.equals("scores")
							&& !scoreboardCommand.equals("exit")
						  )
					{
						System.out.println("OPTIONS: stats, recent, scores, exit");
						scoreboardCommand = input.nextLine();
					
						if (!scoreboardCommands.contains(scoreboardCommand))
						{
							System.out.println("invalid command");
						}
					}
				
					if (scoreboardCommand.equals("exit"))
					{
						endPostGame = true;
						quitGame = true;
						break;
					}
				
					else if(scoreboardCommand.equals("stats"))
					{
						try
						{
							ResultSet rs = stmt.executeQuery("SELECT * FROM GLOBAL_STATS;");
							
								totalGames = rs.getInt("GAMES_PLAYED");
								totalWins = rs.getInt("GAMES_WON");
								totalLosses = rs.getInt("GAMES_LOST");
								totalPartsCollected = rs.getInt("TOTAL_PARTS_COLLECTED");
								
								System.out.println("Games Played\tGames Won\tGames Lost\tTotal Parts Collected");
								System.out.println("------------\t---------\t----------\t---------------------");
								System.out.println("     "+totalGames+
													"\t\t    "+totalWins+
													"\t\t     "+totalLosses+
													"\t\t           "+totalPartsCollected);
							rs.close();
						}
						catch(Exception e)
						{
							System.err.println( e.getClass().getName() + ": " + e.getMessage() );
						}
					}
				
					else if(scoreboardCommand.equals("recent"))
					{
						try
						{
							ResultSet rs = stmt.executeQuery("SELECT * FROM RECENT_GAMES ORDER BY GAMES_AGO;");
							
							int gamesAgo;
							
							System.out.println("Games Ago    Parts Collected\tPLayer Actions\tScore That Round");
							System.out.println("---------    ---------------\t--------------\t----------------");
							while(rs.next())
							{
								gamesAgo = rs.getInt("GAMES_AGO");
								recentPartsCollected = rs.getInt("PARTS_COLLECTED");
								recentPlayerActions = rs.getInt("PLAYER_ACTIONS");
								Score = rs.getInt("ROUND_SCORE");
								
								System.out.println("   "+gamesAgo+
													"\t\t    "+recentPartsCollected+
													"\t\t       "+recentPlayerActions+
													"\t       "+Score);
							}
							rs.close();
						}
						catch(Exception e)
						{
							System.err.println( e.getClass().getName() + ": " + e.getMessage() );
						}
					}
				
					else if(scoreboardCommand.equals("scores"))
					{
						try
						{
							ResultSet rs = stmt.executeQuery("SELECT * FROM HIGH_SCORES ORDER BY RANKING;");
							
							int ranking;
							
							System.out.println("Ranking\tHigh Score  Name");
							System.out.println("-------\t----------  ----");
							while(rs.next())
							{
								ranking = rs.getInt("RANKING");
								Score = rs.getInt("SCORE");
								nameScore = rs.getString("NAME");								
								System.out.println("   "+ranking+
													"\t   "+Score+
													"\t    "+nameScore);
							}
							rs.close();
						}
						catch(Exception e)
						{
							System.err.println( e.getClass().getName() + ": " + e.getMessage() );
						}
					}
				}
				//DATABASE END\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
				
			try
			{
				stmt.close();
				c.close();
			}
			catch(Exception e){System.err.println( e.getClass().getName() + ": " + e.getMessage() );}
			
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
					recentPlayerActions++;	//DB
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
					recentPlayerActions++;	//DB
				}
				else if (playerCommand.equals("diagnostic"))
				{
					System.out.println(map.inventoryToString());
					recentPlayerActions++;	//DB
				}
				else if (playerCommand.equals("collect"))
				{
					Room playerRoom = map.locatePlayer();
					String itemName = playerRoom.getRoomItem();
					if (playerRoom.getRoomItem()!=null)
					{
						System.out.println("Bolbi takes the part.\nHe now has a " + itemName + "!");
						map.collectItem();
						recentPlayerActions++;	//DB
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
					recentPlayerActions++;	//DB
				}
				
		}	//end Gameplay Loop
		
		System.out.println("Logging off now...");
		input.close();
	}
}

