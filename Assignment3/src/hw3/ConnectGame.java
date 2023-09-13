package hw3;

import java.util.ArrayList;
import java.util.Random;

import api.ScoreUpdateListener;
import api.ShowDialogListener;
import api.Tile;

/**
 * Class that models a game.
 */
public class ConnectGame 

{
	private ShowDialogListener dialogListener;
	private ScoreUpdateListener scoreListener;
	private long currScore;
	private Tile prevTile;
	private int minGrid;
	private boolean initialTileSel;
	private int tileHeight;
	private Tile currTile;
	private Grid gameBoard;
	private int maxGrid;
	private int widthOfTile;
	private ArrayList<Tile> tileList = new ArrayList<>();
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Constructs a new ConnectGame object with given grid dimensions and minimum
	 * and maximum tile levels.
	 * 
	 * @param width  grid width
	 * @param height grid height
	 * @param min    minimum tile level
	 * @param max    maximum tile level
	 * @param rand   random number generator
	 */
	public ConnectGame(int width, int height, int min, int max, Random rand) {		
		gameBoard = new Grid(width, height);	
		minGrid = min;		
		maxGrid = max;
		widthOfTile = width;		
		tileHeight = height;			
	}

	/**
	 * Gets a random tile with level between minimum tile level inclusive and
	 * maximum tile level exclusive. For example, if minimum is 1 and maximum is 4,
	 * the random tile can be either 1, 2, or 3.
	 * <p>
	 * DO NOT RETURN TILES WITH MAXIMUM LEVEL
	 * 
	 * @return a tile with random level between minimum inclusive and maximum
	 *         exclusive
	 */
	public Tile getRandomTile() {
		Random rand = new Random();
	    int numberOnTile = rand.nextInt( maxGrid - minGrid ) +  minGrid;
	    Tile randTile = new Tile(numberOnTile);
	    return randTile;
	}
	

	/**
	 * Regenerates the grid with all random tiles produced by getRandomTile().
	 */
	public void radomizeTiles() {
		for (int x = 0; x < tileHeight; x++) {
	        for (int y = 0; y < widthOfTile; y++) {
	            gameBoard.setTile(getRandomTile(), y, x);
	        }
	    }
	}

	/**
	 * Determines if two tiles are adjacent to each other. The may be next to each
	 * other horizontally, vertically, or diagonally.
	 * 
	 * @param t1 one of the two tiles
	 * @param t2 one of the two tiles
	 * @return true if they are next to each other horizontally, vertically, or
	 *         diagonally on the grid, false otherwise
	 */
	public boolean isAdjacent(Tile t1, Tile t2) {
		int Tile1x = t1.getX();
		int Tile2x = t2.getX();
		int Tile1y = t1.getY();
		int Tile2y = t2.getY();			
		
		if(Tile1x == Tile2x && (Tile1y == Tile2y - 1 || Tile1y == Tile2y + 1)){
			return true;
		}
		if(Tile1y == Tile2y && (Tile1x == Tile2x - 1 || Tile1x == Tile2x+1)) {
			return true;
		} 
		if((Tile1y == Tile2y + 1 || Tile1y == Tile2y - 1) && (Tile1x == Tile2x + 1 || Tile1x == Tile2x - 1)) {
			return true;
		}
		return false;	
	}

	/**
	 * Indicates the user is trying to select (clicked on) a tile to start a new
	 * selection of tiles.
	 * <p>
	 * If a selection of tiles is already in progress, the method should do nothing
	 * and return false.
	 * <p>
	 * If a selection is not already in progress (this is the first tile selected),
	 * then start a new selection of tiles and return true.
	 * 
	 * @param x the column of the tile selected
	 * @param y the row of the tile selected
	 * @return true if this is the first tile selected, otherwise false
	 */
	public boolean tryFirstSelect(int x, int y) {
		if(tileList.isEmpty())	
		{
			Tile t = gameBoard.getTile(x, y);
			t.setSelect(true);
			tileList.add(t);	
			return true;
		}
		return false;
	}

	/**
	 * Indicates the user is trying to select (mouse over) a tile to add to the
	 * selected sequence of tiles. The rules of a sequence of tiles are:
	 * 
	 * <pre>
	 * 1. The first two tiles must have the same level.
	 * 2. After the first two, each tile must have the same level or one greater than the level of the previous tile.
	 * </pre>
	 * 
	 * For example, given the sequence: 1, 1, 2, 2, 2, 3. The next selected tile
	 * could be a 3 or a 4. If the use tries to select an invalid tile, the method
	 * should do nothing. If the user selects a valid tile, the tile should be added
	 * to the list of selected tiles.
	 * 
	 * @param x the column of the tile selected
	 * @param y the row of the tile selected
	 */
	public void tryContinueSelect(int x, int y) 
	{	
		 Tile t = gameBoard.getTile(x, y);
		    if (tileList.size() > 1 && t == tileList.get(tileList.size() - 2)) { 
		    	Tile lastSelTile = tileList.get(tileList.size() - 1);    
		        lastSelTile.setSelect(false);
		        tileList.remove(tileList.size() - 1);  
		        return;
		    }
		    
		    if (!t.isSelected() && 
		    		isAdjacent(t, tileList.get(tileList.size() - 1))) {		    	
		        int startingLevel = tileList.get(0).getLevel();		        
		        int highestLevel = tileList.get(tileList.size() - 1).getLevel();		        
		        int currTileLevel = t.getLevel();
		        if (tileList.size() == 1){		        	
		            if (currTileLevel == startingLevel){
		                t.setSelect(true);		                
		                tileList.add(t);
		            }		            
		        } 
		        else if (currTileLevel == highestLevel || currTileLevel == highestLevel + 1){
		            t.setSelect(true);		            
		            tileList.add(t);
		        }
		    }
		}



	/**
	 * Indicates the user is trying to finish selecting (click on) a sequence of
	 * tiles. If the method is not called for the last selected tile, it should do
	 * nothing and return false. Otherwise it should do the following:
	 * 
	 * <pre>
	 * 1. When the selection contains only 1 tile reset the selection and make sure all tiles selected is set to false.
	 * 2. When the selection contains more than one block:
	 *     a. Upgrade the last selected tiles with upgradeLastSelectedTile().
	 *     b. Drop all other selected tiles with dropSelected().
	 *     c. Reset the selection and make sure all tiles selected is set to false.
	 * </pre>
	 * 
	 * @param x the column of the tile selected
	 * @param y the row of the tile selected
	 * @return return false if the tile was not selected, otherwise return true
	 */
	public boolean tryFinishSelection(int x, int y) 
	{	
		 Tile t = gameBoard.getTile(x, y);
		    if (tileList.size() == 1){
	            t.setSelect(false);
	            tileList.clear();
	            return true;
	        }        
	        if (t != tileList.get(tileList.size() - 1)){
	            return false;
	        }
	        for(Tile chosenTile : tileList){
	        	chosenTile.setSelect(false);
	        	currScore += chosenTile.getValue();
	        }	        
	        tileList.clear();
	        upgradeLastSelectedTile();	        
	        dropSelected();	        
	        scoreListener.updateScore(currScore);	        
	        return true;
	    }

	/**
	 * Increases the level of the last selected tile by 1 and removes that tile from
	 * the list of selected tiles. The tile itself should be set to unselected.
	 * <p>
	 * If the upgrade results in a tile that is greater than the current maximum
	 * tile level, both the minimum and maximum tile level are increased by 1. A
	 * message dialog should also be displayed with the message "New block 32,
	 * removing blocks 2". Not that the message shows tile values and not levels.
	 * Display a message is performed with dialogListener.showDialog("Hello,
	 * World!");
	 */
	public void upgradeLastSelectedTile() 
	{
		if(tileList.isEmpty()){
            return;
        }

        Tile lastTile = tileList.get(tileList.size() - 1);
        int updateLev = lastTile.getLevel() + 1;        
        lastTile.setSelect(false);
        

        if (maxGrid < updateLev) {
            minGrid++;           
            maxGrid++;           
            dialogListener.showDialog("New block " + lastTile.getLevel() + ", removing blocks " + lastTile.getLevel());
        } 
        else {
            lastTile.setLevel(updateLev);
        }
        tileList.remove(lastTile);
	}

	/**
	 * Gets the selected tiles in the form of an array. This does not mean selected
	 * tiles must be stored in this class as a array.
	 * 
	 * @return the selected tiles in the form of an array
	 */
	public Tile[] getSelectedAsArray(){		
		return tileList.toArray(new Tile[tileList.size()]);
	}

	/**
	 * Removes all tiles of a particular level from the grid. When a tile is
	 * removed, the tiles above it drop down one spot and a new random tile is
	 * placed at the top of the grid.
	 * 
	 * @param level the level of tile to remove
	 */
	public void dropLevel(int level) {		
		for (int x = 0; x < gameBoard.getWidth(); x++){
			ArrayList<Tile> col = new ArrayList<>();
			for (int y = 0; y < gameBoard.getHeight(); y++){
				Tile mainTile = gameBoard.getTile(x, y);
				if (mainTile.getLevel() != level){
					col.add(mainTile);
				}
			}			
			while (col.size() < gameBoard.getHeight()){
				col.add(0, getRandomTile());
			}		
			for (int y = 0; y < gameBoard.getHeight(); y++){
				gameBoard.setTile(col.get(y), x, y);
			}
		}
	}

	/**
	 * Removes all selected tiles from the grid. When a tile is removed, the tiles
	 * above it drop down one spot and a new random tile is placed at the top of the
	 * grid.
	 */
	public void dropSelected(){
		if (tileList.isEmpty()){
	        return;
	    }
		
	    ArrayList<Integer> colUpdate = new ArrayList<>();
	    
	    for (Tile temp : tileList) {
	        int vertical = temp.getX();
	        if (!colUpdate.contains(vertical)){
	            colUpdate.add(vertical);
	        }
	    }
	    for (int vertical : colUpdate){
	        ArrayList<Tile> col = new ArrayList<>();	        
	        for (int horizontal = 0; horizontal < gameBoard.getHeight(); horizontal++){
	            Tile mainTile = gameBoard.getTile(vertical, horizontal);
	            if (!tileList.contains(mainTile)){
	                col.add(mainTile);
	            }
	        } 
	        int tilesDropped = gameBoard.getHeight() - col.size();	        
	        for (int x = 0; x < tilesDropped; ++x){
	            col.add(0, getRandomTile());
	        }
	        for (int horizontal = 0; horizontal < gameBoard.getHeight(); horizontal++){
	            gameBoard.setTile(col.get(horizontal),vertical, horizontal);
	        }
	    }	    
	    tileList.clear();
	}

	/**
	 * Remove the tile from the selected tiles.
	 * 
	 * @param x column of the tile
	 * @param y row of the tile
	 */
	public void unselect(int x, int y){
		Tile currTile = gameBoard.getTile(x, y);		
		currTile.setSelect(false);		
		tileList.remove(currTile);
	}

	/**
	 * Gets the player's score.
	 * 
	 * @return the score
	 */
	public long getScore(){
		return currScore;
	}

	/**
	 * Gets the game grid.
	 * 
	 * @return the grid
	 */
	public Grid getGrid(){
		return gameBoard;
	}

	/**
	 * Gets the minimum tile level.
	 * 
	 * @return the minimum tile level
	 */
	public int getMinTileLevel(){		
		return minGrid;
	}

	/**
	 * Sets the game's grid.
	 * 
	 * @param grid game's grid
	 */
	public void setGrid(Grid grid) {	
		this.gameBoard = grid;
	}
	
	/**
	 * Gets the maximum tile level.
	 * 
	 * @return the maximum tile level
	 */
	public int getMaxTileLevel(){	
		return maxGrid;
	}

	/**
	 * Sets the player's score.
	 * 
	 * @param score number of points
	 */
	public void setScore(long score){		
		currScore = score;
	}

	/**
	 * Sets the minimum tile level.
	 * 
	 * @param minTileLevel the lowest level tile
	 */
	public void setMinTileLevel(int minTileLevel){
		minGrid = minTileLevel;
	}

	/**
	 * Sets the maximum tile level.
	 * 
	 * @param maxTileLevel the highest level tile
	 */
	public void setMaxTileLevel(int maxTileLevel){	
		maxGrid = maxTileLevel;
	}

	/**
	 * Sets callback listeners for game events.
	 * 
	 * @param dialogListener listener for creating a user dialog
	 * @param scoreListener  listener for updating the player's score
	 */
	public void setListeners(ShowDialogListener dialogListener, ScoreUpdateListener scoreListener){
		this.dialogListener = dialogListener;
		this.scoreListener = scoreListener;
	}

	/**
	 * Save the game to the given file path.
	 * 
	 * @param filePath location of file to save
	 */
	public void save(String filePath) {
		GameFileUtil.save(filePath, this);
	}

	/**
	 * Load the game from the given file path
	 * 
	 * @param filePath location of file to load
	 */
	public void load(String filePath) {
		GameFileUtil.load(filePath, this);
	}
}