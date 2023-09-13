package hw3;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import api.Tile;



/**
 * Utility class with static methods for saving and loading game files.
 * @author Declan Creadon
 */
public class GameFileUtil {
	/**
	 * Saves the current game state to a file at the given file path.
	 * <p>
	 * The format of the file is one line of game data followed by multiple lines of
	 * game grid. The first line contains the: width, height, minimum tile level,
	 * maximum tile level, and score. The grid is represented by tile levels. The
	 * conversion to tile values is 2^level, for example, 1 is 2, 2 is 4, 3 is 8, 4
	 * is 16, etc. The following is an example:
	 * 
	 * <pre>
	 * 5 8 1 4 100
	 * 1 1 2 3 1
	 * 2 3 3 1 3
	 * 3 3 1 2 2
	 * 3 1 1 3 1
	 * 2 1 3 1 2
	 * 2 1 1 3 1
	 * 4 1 3 1 1
	 * 1 3 3 3 3
	 * </pre>
	 * 
	 * @param filePath the path of the file to save
	 * @param game     the game to save
	 */
	public static void save(String filePath, ConnectGame game) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
						
			Grid formattedOutput = game.getGrid();	
			long lastSavedScore = game.getScore();
			int lowestLevel = game.getMinTileLevel();
			int tileHeight = formattedOutput.getHeight();
			int tileWidth = formattedOutput.getWidth();
			int highestLevel = game.getMaxTileLevel();
			
			writer.write(tileWidth + " " + tileHeight + " " + lowestLevel + " " + highestLevel + " " + lastSavedScore);
			
			for (int y = 0; y < tileHeight; y++){
				writer.write('\n');
				
				for (int x = 0; x < tileWidth; x++){
					Tile t = formattedOutput.getTile(x, y);
					
					if (x == tileWidth - 1 && t != null ){
						writer.write(t.getLevel() + "");
					}
					else if (t != null) {
						writer.write(t.getLevel() + " ");
					}
					else{
						writer.write("0 ");
					}
				}
			}
			writer.close();
			
		} 
		
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads the file at the given file path into the given game object. When the
	 * method returns the game object has been modified to represent the loaded
	 * game.
	 * <p>
	 * See the save() method for the specification of the file format.
	 * 
	 * @param filePath the path of the file to load
	 * @param game     the game to modify
	 */
	public static void load(String filePath, ConnectGame game) {
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath)))
		{
			String[] data = reader.readLine().split(" ");
			int x = Integer.parseInt(data[0]);
			int y = Integer.parseInt(data[1]);
			int min = Integer.parseInt(data[2]);
			int max = Integer.parseInt(data[3]);
			int endingScore = Integer.parseInt(data[4]);
			Grid grid = new Grid(x, y);
			game.setMinTileLevel(min);
			game.setMaxTileLevel(max);
			game.setScore(endingScore);
			//Read game grid from file
			grid = new Grid(x, y);
			
			for (int horizontal = 0; horizontal < y; horizontal++)
			{
				String[] singleData = reader.readLine().split(" ");
				
				for (int vertical = 0; vertical < x; vertical++)
				{
					int currentLevel = Integer.parseInt(singleData[vertical]);
					Tile gridArea = new Tile(currentLevel);
					grid.setTile(gridArea, vertical, horizontal);
				}
			}
			game.setGrid(grid);
		}
		
		catch (IOException e)
		
		{
			e.printStackTrace();
		}
	}
	}
