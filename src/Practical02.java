import java.util.*;
import java.io.*;
import java.net.*;

public class Practical02 {
	
	public static InputStream inputStream;
	public static OutputStream outputStream;
	public static ServerSocket hostServerSocket;
	
	public static String serverIP;
	public static int serverPort;
	
	public static int defaultPort = 4242;
	public static int timeout = 20; // 20 milliseconds
	public static boolean userConnectedToServer = false;
	public static Socket connectionWithClient;
	public static Socket connectionWithServer;
	public static PrintWriter socketOut;
	public static BufferedReader socketIn;
	public static String clientIPAddress;
	public static boolean currentlyInGame = false;
	public static boolean displayOnlineGameBoard = true;
	
	public static final int SOLIDS = 1;
    public static final int CIRCLES = 2;
    public static final int EMPTY = 0;
    public static final int BOARDROWS = 5;
    public static final int NUMPIECES = 15;
    public static int turn;
    
    public static final int REAL_PLAYER = 1;
    public static final int AI = 2;
    public static final int MULTIPLAYER = 3;
	
    public static ArrayList<Moves> moveStore = new ArrayList<Moves>();
    public static ArrayList<Dice> DieStore = new ArrayList<Dice>();
    public static ArrayList<Players> playerStore = new ArrayList<Players>();
    
    public static ArrayList<Integer> diceValuesRolledByPlayer = new ArrayList<Integer>();
    public static ArrayList<Integer> movesMadeByPlayer = new ArrayList<Integer>();
    
    public static boolean illegalMove = false;
    public static int locationInMovesMadeByPlayer = 0;

    public static Board offlineBoard = new Board();
    public static Board onlineBoard = new Board();
    public static String DOTTED_LINE = "--------------------------------------------------------------------";
    
    public static void printMenu() {
    	
    	//Writes out an ascii title which was generated using "http://www.network-science.de/ascii/" using the font setting "big".
        System.out.println();        
        System.out.println(" ____             _                                               ");
        System.out.println("|  _ \\           | |                                              ");
        System.out.println("| |_) | __ _  ___| | ____ _  __ _ _ __ ___  _ __ ___   ___  _ __  ");
        System.out.println("|  _ < / _` |/ __| |/ / _` |/ _` | \'_ ` _ \\| \'_ ` _ \\ / _ \\| \'_ \\ ");
        System.out.println("| |_) | (_| | (__|   < (_| | (_| | | | | | | | | | | | (_) | | | |");
        System.out.println("|____/ \\__,_|\\___|_|\\_\\__, |\\__,_|_| |_| |_|_| |_| |_|\\___/|_| |_|");
        System.out.println("                       __/ |                                      ");
        System.out.println("                      |___/                                       ");
        
        
        System.out.println(DOTTED_LINE);
        System.out.println("\t\t\t1. Offline Play");
        System.out.println("\t\t\t2. Online Play");
        System.out.println("\t\t\t3. Exit Game");
        System.out.println(DOTTED_LINE);
        
        Scanner consoleReader = new Scanner(System.in);
        
        String userChoice="";
        boolean validChoice = false;
        
        while (validChoice == false) {
            System.out.print("Please input your choice: ");
            try {
            	userChoice = consoleReader.nextLine();  
            	validChoice = checkIfValidUserIntInput(userChoice, 1, 3);
    		} catch (Exception e) {
    		}
		} 
        
        switch (Integer.valueOf(userChoice)) {
            case 1:
            	playOfflineGame();
                break;
            case 2:
                System.out.println(DOTTED_LINE);
                displayOnlineMenu();
                break;
            case 3:
                System.out.println(DOTTED_LINE);
                System.exit(0);
                break;
        }
    }

    public static void playOfflineGame() {
    	playerStore.clear();//Clears the player store just in case it already has players in it.
    	setupOfflinePlayers();
    	Scanner consoleReader = new Scanner(System.in);
    	    	
    	System.out.println("Welcome to two player backgammon");
    	System.out.println();
    	
    	setWhichPlayerGoesFirst();
    	
    	
    	System.out.println("Press Enter to start the game...");
    	consoleReader.nextLine();
    	
    	boolean gameOver = false;
    	while(gameOver == false) {    
    		if (playerStore.get(turn).getAI() == true) {
    			turnProcess(offlineBoard, AI);
        		gameOver = isGameOver(offlineBoard, true);
        		turnChanger();				
			}
    		else{
    			turnProcess(offlineBoard, REAL_PLAYER);
        		gameOver = isGameOver(offlineBoard, true);
        		turnChanger();    			
    		}
    		
    	}
    	offlineBoard = new Board();
    	System.out.println("The game is over! Press enter to return to the menu...");
    	try {
			System.in.read();
		} catch (IOException e) {
		}
    	printMenu();
    }
    
    private static void setupThePlayers() {	
    	Players Player1 = new Players();
    	Player1.setPlayerIdentity(false);
    	playerStore.add(Player1);
    	Players Player2 = new Players();
    	Player2.setPlayerIdentity(true);
    	playerStore.add(Player2);		
	}
    
    private static void setupOfflinePlayers() {
    	System.out.println(DOTTED_LINE);
    	setupThePlayers();

    	for (int c = 0; c <= 1; c++) {
    		setupEachOfflinePlayer(c);
    	}
    	System.out.println(DOTTED_LINE);
	}
    
    public static void setupEachOfflinePlayer(int c) {
    	String userChoice;
        Scanner consoleReader = new Scanner(System.in);

    	boolean correctInput = false;
    	while (correctInput == false) {
    		System.out.print("Will Player " +(c+1) +" be controlled by (1) A Human / (2) The Computer: ");
        	userChoice = consoleReader.next();
        	if (userChoice.equals("1")) {
            	playerStore.get(c).setAsAI(false);
        		correctInput = true;
        	}
        	if (userChoice.equals("2")) {
            	playerStore.get(c).setAsAI(true);
            	boolean correctInput1 = false;
        		while (correctInput1 == false) {
        			System.out.println();
            		System.out.print("Set AI difficulty (1) Normal / (2) Difficult: ");
                	userChoice = consoleReader.next();
                	if (userChoice.equals("1")) {
                    	playerStore.get(c).setAsEasyAI(true);
                		correctInput = true;
                		correctInput1 = true;

                	}
                	if (userChoice.equals("2")) {
                    	playerStore.get(c).setAsEasyAI(false);
                		correctInput = true;
                		correctInput1 = true;
                	}
        		}	
        		System.out.println();
        	}
    	}
    }

	private static void setWhichPlayerGoesFirst() {
    	boolean repeatStart = true;
    	Scanner consoleReader = new Scanner(System.in);
    	
    	while (repeatStart == true) {
    		System.out.print("Press Enter to roll a pair of dice to see who plays first...");
        	consoleReader.nextLine();
        	System.out.println();
        	
        	Dice Dice1 = new Dice();
        	Dice Dice2 = new Dice();
        	
        	int a = Dice1.getRollValue();
        	int b = Dice2.getRollValue();

        	System.out.println("Player 1 rolled a " +a+"\tPlayer 2 rolled a "+ b);
        	
        	if (a == b) {
        		System.out.println("Both Players rolled the same number, please roll again!");
            	System.out.println();
        	}
        	else if (a > b) {
        		System.out.println("\nPlayer 1 moves first!");
        		repeatStart = false;
        		turn = 0;
        	}
        	else {
        		System.out.println("\nPlayer 2 moves first!");
        		turn = 1;
        		repeatStart = false;
        	}
    	}
		
	}

	/*
	 * Checks to see if all pieces of one type are cleared from the board,
	 * or beared off. If none of player one's pieces are left on the board,
	 * player 1 wins and the game stops looping and is over
	 */
	public static boolean isGameOver(Board gameBoard, boolean displayWinMessage) {
		boolean gameOver = false;
    	int pieceCounter = 0;
    	for (int i = 0; i < gameBoard.BOARDCOLS; i ++) {
    		if (turn == 0) {
    			if (gameBoard.pieceStore[i].getPieceOrientation() == SOLIDS) {
    				pieceCounter++;
    			}
    		}
    		else {
    			if (gameBoard.pieceStore[i].getPieceOrientation() == CIRCLES) {
    				pieceCounter++;
    			}
    		}
    	}
    	if (pieceCounter == 0) {
    		gameOver = true;
    		if (turn == 0 && displayWinMessage == true) {
    			gameBoard.displayBoard();
    			System.out.println("\nGame over, player 1 wins!");
    		}
    		else if (turn == 1 && displayWinMessage == true){
    			gameBoard.displayBoard();
    			System.out.println("\nGame over, player 2 wins!");
    		}
    	}
    	return gameOver;
    }
    
	/*
	 * Player 1 is 0, player 2 is 1. Turns are stored as integers
	 * and not booleans because they represent the index in the player arraylist.
	 * This makes it easier to bring up information from each player later in
	 * the code.
	 */
    public static void turnChanger() { //Player 1 is false, player 2 is true
    	if (turn == 0) { //if player one's turn, now player two's turn
    		turn = 1;
    	}
    	else { //if player two's turn, now player one's turn
    		turn = 0;
    	}
    }
    
    /*
     * The entire turn by turn process, optimized for both online and offline play.
     * If player is local, clears all their previous moves and dice rolls,
     * then displays the updated board. It then prints the current player, 
     * lets player roll die, finds all possible moves from dice values, ends turn
     * if no moves are available, lets player choose moves, ends game if player wins, 
     * otherwise ends turn and switches player.
     */
    public static void turnProcess(Board gameBoard, int typeOfPlayer) { //includes tests and piece movement

    	
    	if (typeOfPlayer == REAL_PLAYER || typeOfPlayer == AI) {
        	diceValuesRolledByPlayer.clear();
        	movesMadeByPlayer.clear();        	
        	gameBoard.displayBoard();
        	
		}
    	
    	System.out.print("Current Player: ");
		if (turn == 0) {
			System.out.println(" Player 1");
		} else {
			System.out.println(" Player 2");

		}
		
    	rollPlayerDie(typeOfPlayer); 
    	
    	findPlayerMoves(gameBoard,false);

    	boolean keepLooping = true;
    	
    	if (moveStore.isEmpty() || DieStore.isEmpty()) {
    		System.out.println("There are no possible moves for the player!");
    		movesMadeByPlayer.add(66); //66 is default number for empty move
			movesMadeByPlayer.add(66);
		} 
    	else {
			while (keepLooping == true) {
				
				try {
					DieStore.get(0);
					moveStore.get(0);					
					getPlayerMove(gameBoard, typeOfPlayer);						
				} catch (Exception e) {
					if (typeOfPlayer == REAL_PLAYER) {
	    				System.out.println("\nYou have done all your possible moves.");
						System.out.print("Press Enter to end your turn...");
						try {
							System.in.read();
						} catch (IOException ex) {


						}
					}
	    			else if (typeOfPlayer == AI) {
						System.out.println("AI has done all of their possible moves...");
						System.out.println(DOTTED_LINE);
						System.out.println();
					}
					keepLooping = false;
				}
				boolean gameOver = isGameOver(offlineBoard, false);
				
				if (gameOver == true) {
					keepLooping = false;
				}

	    		
	    		if (keepLooping == true) {
					findPlayerMoves(gameBoard, false); 
	    			if (typeOfPlayer == REAL_PLAYER||typeOfPlayer == AI) {
	    				gameBoard.displayBoard();	
					}
				}
	    			    		
	    	}
		}
    	moveStore.clear();
    }  

    /*
	 * Uses DieStore to assign possible move values. For example, if a player rolls a 2 and 3, 
	 * the possible move values are 2, 3, and 5. If the player uses the 3, it no longer has 5 
	 * and 3 and can only use move value 2. If player rolls doubles, for example 3, they can
	 * move 3, 6, 9, or 12. If player uses the 9, they can only move 3. If player uses 6, they 
	 * can only move 3 or 6. 
	 */
	private static void rollPlayerDie(int typeOfPlayer) {
		setupDieStore();
		
		if (typeOfPlayer == REAL_PLAYER||typeOfPlayer == AI) {
			diceValuesRolledByPlayer.clear();
			
			if (typeOfPlayer == REAL_PLAYER) {
				System.out.print("Press Enter to roll your dice...");
		    	
				try {
					System.in.read();
				} catch (IOException e) {
					
				}
		    	System.out.println();
			}
			
	    	DieStore.get(0).rollDice(); //Adds first and second dice values to store.
	    	DieStore.get(1).rollDice();
	    	
	    	//Stores the values which the player rolled in a seperate array list.
	     	diceValuesRolledByPlayer.add(DieStore.get(0).getRollValue());
	    	diceValuesRolledByPlayer.add(DieStore.get(1).getRollValue());
		}
		else if (typeOfPlayer == MULTIPLAYER) {
			DieStore.get(0).setRollValue(diceValuesRolledByPlayer.get(0));
			DieStore.get(1).setRollValue(diceValuesRolledByPlayer.get(1));
		}
		
		System.out.print("Player rolled a " +DieStore.get(0).getRollValue() +" and a " +DieStore.get(1).getRollValue());
    	System.out.println();
    	if (DieStore.get(0).getRollValue() == DieStore.get(1).getRollValue()) {
    		int dieValue = DieStore.get(0).getRollValue();
    		Dice Dice3 = new Dice();
    		Dice3.setRollValue(dieValue);
    		Dice Dice4 = new Dice();
    		Dice4.setRollValue(dieValue);
    		DieStore.add(Dice3);
    		DieStore.add(Dice4);
    		if (typeOfPlayer == REAL_PLAYER) {
    			System.out.println();
        		System.out.println("You rolled Doubles!!");
			}
    		
    	}
		
	}

	/*
	 * Uses DieStore to assign possible move values. For example, if a player rolls a 2 and 3, 
	 * the possible move values are 2, 3, and 5. If the player uses the 3, it no longer has 5 
	 * and 3 and can only use move value 2. If player rolls doubles, for example 3, they can
	 * move 3, 6, 9, or 12. If player uses the 9, they can only move 3. If player uses 6, they 
	 * can only move 3 or 6. 
	 */
	private static ArrayList<Integer> getAllPossibleDiceValues(boolean displayPossibleDiceValues) {
    	ArrayList<Integer> possibleDiceValues = new ArrayList<Integer>();
    	
    	if (DieStore.size() == 1) {
			possibleDiceValues.add(DieStore.get(0).getRollValue());
			
		} 
    	else if (DieStore.size() == 2) {
			if (DieStore.get(0).getRollValue() == DieStore.get(1).getRollValue()) {
				possibleDiceValues.add(DieStore.get(0).getRollValue());
				possibleDiceValues.add(DieStore.get(0).getRollValue()*2);
			}
			else {
				possibleDiceValues.add(DieStore.get(0).getRollValue());
				possibleDiceValues.add(DieStore.get(1).getRollValue());
				possibleDiceValues.add(DieStore.get(0).getRollValue()+DieStore.get(1).getRollValue());
			}			
		} 
    	else if (DieStore.size() == 3) {
			possibleDiceValues.add(DieStore.get(0).getRollValue());
			possibleDiceValues.add(DieStore.get(0).getRollValue()*2);
			possibleDiceValues.add(DieStore.get(0).getRollValue()*3);
			
		} 
    	else if (DieStore.size() == 4) {
			possibleDiceValues.add(DieStore.get(0).getRollValue());
			possibleDiceValues.add(DieStore.get(0).getRollValue()*2);
			possibleDiceValues.add(DieStore.get(0).getRollValue()*3);
			possibleDiceValues.add(DieStore.get(0).getRollValue()*4);				
		}
    	
    	if (displayPossibleDiceValues == true) {
    		System.out.println();
    		System.out.print("Current dice: ");
        	for (int i = 0; i < DieStore.size(); i++) {
    			System.out.print(DieStore.get(i).getRollValue()+" ");
    		}
        	System.out.println();
        	
        	
        	System.out.print("Possible dice values: ");
        	for (int i = 0; i < possibleDiceValues.size(); i++) {
    			System.out.print(possibleDiceValues.get(i)+" ");
    		}
        	System.out.println();
		}
    	
    	
    	
		return possibleDiceValues;
	}
        
	/*
	 * Searches the whole board other than the own players quadrant for player's own
	 * pieces. If there are no pieces outside of the player's quadrant, the player is 
	 * allowed to start bearing off its pieces. 
	 */
    public static boolean startBearOff(Board gameBoard) {
    	int piece = CIRCLES; 
    	int pieceCount = 0;
    	boolean startBearOff = false;
    	
    	if (playerStore.get(turn).getPlayerIdentity() == false) {
    		piece = SOLIDS;
    		for (int w = 7; w < gameBoard.BOARDCOLS; w++) {
    			if (gameBoard.pieceStore[w].getPieceOrientation() == piece) {
    				pieceCount = pieceCount +gameBoard.pieceStore[w].getNumberOfPieces();
    			}
    		}
    	}
    	else {
    		for (int w = 0; w <= 18; w++) {
    			if (gameBoard.pieceStore[w].getPieceOrientation() == piece) {
    				pieceCount = pieceCount +gameBoard.pieceStore[w].getNumberOfPieces();
    			}
    		}
    	}
    	if (pieceCount == 0) {
    		startBearOff = true;
    	}
    	return startBearOff;
    }
	
    /*
     * Get's the possible dice values and combinations, clears any moves still left in
     * moveStore, runs test to confirm whether or not player is still able/is able to 
     * bear off, forces player to move piece from start position/bar, and changes turn if 
     * no moves are available. Uses player possible dice values to search the board looking 
     * for player's pieces and the moves available to them.  
     */
	public static void findPlayerMoves(Board gameBoard, boolean displayPossibleDiceValues) { //narrows players piece moving options to own columns
    	
		ArrayList<Integer> possibleDiceValues = getAllPossibleDiceValues(displayPossibleDiceValues);	
    	moveStore.clear();
    	
    	if (startBearOff(gameBoard) == true) {
    		playerStore.get(turn).setPlayerToBearOff(true);
    	}
    	else {
    		playerStore.get(turn).setPlayerToBearOff(false);
    	}
    	
    	int startPosition = 0;
    	if (playerStore.get(turn).getPlayerIdentity() == false) {
    		startPosition = 25;
    	}
    	if (gameBoard.pieceStore[startPosition].getNumberOfPieces() > 0) { //If the player has a captured piece
    		for (int x = 0; x < possibleDiceValues.size(); x++) {
    			findPlayerMovesSub(startPosition, possibleDiceValues.get(x),gameBoard);
    		}
    		if (moveStore.isEmpty()) {
    			System.out.println("Player cannot move pieces from bar/starting point");
    		}
    	}
    	else {
    		for (int i = 1; i < gameBoard.BOARDCOLS-1; i++) {
        		for (int x = 0; x < possibleDiceValues.size(); x++) {
        			findPlayerMovesSub(i, possibleDiceValues.get(x),gameBoard);
        		}
        	}
    	}
    	if (playerStore.get(turn).getAI() == true) {
    		playerStore.get(turn).moveScanner(moveStore, gameBoard, DieStore);
    	}
    }
   
	/*
	 * Sets player piece, enemy piece, and potential move, then finds moves based on
	 * available current columns and destination columns.
	 */
    public static void findPlayerMovesSub(int i, int diceValue, Board gameBoard) {
    	int piece, enemyPiece, potentialMove;
		boolean moveAlreadyStored = false;
		
    	if (playerStore.get(turn).getPlayerIdentity() == true) {    	
    		piece = CIRCLES;
    		enemyPiece = SOLIDS;
    		potentialMove = i + diceValue;
    	}
    	else {
    		piece = SOLIDS;
    		enemyPiece = CIRCLES;
    		potentialMove = i - diceValue;
		}
    	
    	if (gameBoard.pieceStore[i].getPieceOrientation() == piece) {
    		if (playerStore.get(turn).getBearOff() == true) {
    			if (turn == 0) {
    				if (potentialMove < 6) {
    					if (potentialMove <= 0) {
    						potentialMove = 0;
    						findBearOffMoves(moveAlreadyStored, i, potentialMove);    						
    					}
    					else {
    						saveMove(i, potentialMove);
    					}
    				}
    			}
    			else {
    				if (potentialMove > 19) {
    					if (potentialMove >= 25) {
    						potentialMove = 25;
    						findBearOffMoves(moveAlreadyStored, i, potentialMove);
    					}
    					else {
    						saveMove(i, potentialMove);
    					}
    				}
    			}
    		}
    		else if (potentialMove > 0 && potentialMove < 25) {
    			if (gameBoard.pieceStore[potentialMove].getPieceOrientation() != enemyPiece) {
    				saveMove(i, potentialMove);
    			}
    			if (gameBoard.pieceStore[potentialMove].getPieceOrientation() == enemyPiece) {
    				if (gameBoard.pieceStore[potentialMove].getNumberOfPieces() < 2) {
    					saveMove(i, potentialMove);
    				}
    			}
    		}
    	}    	
    }
    
    /*
     * sub method in findPlayerMoves to avoid repeating code
     */
    public static void findBearOffMoves(boolean moveAlreadyStored, int i, int potentialMove) {
    	for (int j = 0; j < moveStore.size(); j++) {
			if (moveStore.get(j).getDestCol() == 0) {
				moveAlreadyStored = true;
			}
		}
		if (moveAlreadyStored == false) {
			saveMove(i, potentialMove);
		}
    }
    
    /*
     * creates new move and adds it to store,
     * sub method to avoid repeating code
     */
    public static void saveMove(int i, int potentialMove) {
    	Moves newMove = new Moves(i, potentialMove);
		moveStore.add(newMove);
    }
      
    public static void getPlayerMove(Board gameBoard, int typeOfPlayer) {
    	Scanner consoleReader = new Scanner(System.in);
    	boolean validColumn = false;
    	boolean validMove = false;
    	int moveStoreIndex = 0;
    	Moves AIMove = null;    	
    	
    	
    	if (typeOfPlayer == AI) {
    		findPlayerMoves(gameBoard,true);
    		AIMove = getMoveWithHighestMoveValue();   		
		}
    	
    	
    	int colChoice = 0, destColumn = 0;
    	String colChoiceAsString = "", destColumnAsString = "";
    	
    	if (typeOfPlayer == REAL_PLAYER) {
    		findPlayerMoves(gameBoard, true);
    		System.out.print("\nPossible columns to move from:  ");
    		
    		ArrayList<Integer> alreadySeenColumns = new ArrayList<Integer>();
    		
    		
        	for (int i = 0; i < moveStore.size(); i++) {
        		boolean alreadyPrinted = false;
        		
        		if (i == 0) {
					System.out.print(moveStore.get(i).getCurrentCol()+"  ");
					alreadySeenColumns.add(moveStore.get(i).getCurrentCol());
				}
        		else {
        			for (int j = 0; j < alreadySeenColumns.size(); j++) {
    					if (moveStore.get(i).getCurrentCol() == alreadySeenColumns.get(j)) {
							alreadyPrinted = true;
						}
    				}
        			
        			if (alreadyPrinted == false) {
        				alreadySeenColumns.add(moveStore.get(i).getCurrentCol());
						System.out.print(moveStore.get(i).getCurrentCol()+"  ");
					}
				}
    		}
        	
        	boolean validColChoice = false;
        	while (validColChoice == false) {
        		System.out.print("\nEnter the column of the piece you want to move: ");
				colChoiceAsString = consoleReader.nextLine();
				
				try {
					//Checks that colChoiceAsString is a number
					colChoice = Integer.valueOf(colChoiceAsString);
					validColChoice = true;
				} catch (Exception e) {
					validColChoice = false;
				}
			}    		
		}
    	else if (typeOfPlayer == AI) {
    		colChoice = AIMove.getCurrentCol();
			System.out.println("\nAI starting column choice: "+colChoice);
		}
    	else if(typeOfPlayer == MULTIPLAYER) {
			findPlayerMoves(gameBoard, false);
			colChoice = movesMadeByPlayer.get(locationInMovesMadeByPlayer);	
			System.out.println("\nPlayer starting column choice: "+colChoice);
			locationInMovesMadeByPlayer++;
		}	
    	
    	for (int i = 0; i < moveStore.size(); i++) {
			if (colChoice == moveStore.get(i).getCurrentCol()) {
				validColumn = true;
			}
		} 
    	
    	if (validColumn == true) {
    		if (typeOfPlayer == REAL_PLAYER) {
    			System.out.print("Possible moves from the chosen column:  ");
        		for (int i = 0; i < moveStore.size(); i++) {
        			if (colChoice == moveStore.get(i).getCurrentCol()) {
        				System.out.print(moveStore.get(i).getDestCol() +"  ");
        			}
    			}
        		System.out.println();
        		
        		boolean validDestChoice = false;
        		
            	while (validDestChoice == false) {
            		System.out.print("Enter the column you want to move the piece to: ");
    				destColumnAsString = consoleReader.nextLine();    				
    				try {
    					//Checks that destColumnAsString is a number
    					destColumn = Integer.valueOf(destColumnAsString);
    					validDestChoice = true;
    				} catch (Exception e) {
    					validDestChoice = false;
    				}
    			}
			}
    		else if (typeOfPlayer == AI) {
        		destColumn = AIMove.getDestCol();
    			System.out.println("\nAI destination column choice: "+destColumn);				
			}
    		else if (typeOfPlayer == MULTIPLAYER){    			
    			destColumn = movesMadeByPlayer.get(locationInMovesMadeByPlayer);
    			System.out.println("Player destination column choice: "+destColumn);
    			locationInMovesMadeByPlayer++;
    		}   		
			
			for (int i = 0; i < moveStore.size(); i++) {
				if (colChoice == moveStore.get(i).getCurrentCol()) {
					if (destColumn == moveStore.get(i).getDestCol()) {
						validMove = true;
					}
				}				
			}
			
			
			if (validMove == true) {
				if (typeOfPlayer == REAL_PLAYER || typeOfPlayer == AI) {
					movesMadeByPlayer.add(colChoice);
					movesMadeByPlayer.add(destColumn);
				}
				movePiece(colChoice,destColumn,gameBoard);
			}
			else {
				if (typeOfPlayer == REAL_PLAYER) {
					System.out.println("\nThat is an invalid move!");
					System.out.print("Press Enter to re-input your move...");
					try {
						System.in.read();
					} catch (IOException e) {
					
					}
				} else {
					illegalMove = true;
				}
				
			}			
		}
    	else {
    		if (typeOfPlayer == REAL_PLAYER) {
    			System.out.println("\nThere are no possible moves from that column!");
        		System.out.print("Press Enter to re-input your move...");
        		try {
    				System.in.read();
    			} catch (IOException e) {
    			
    			}
				
			} else {
				illegalMove = true;
			}
    	}
    }
    
    private static Moves getMoveWithHighestMoveValue() {
		int highestMoveValue = -1;
		Moves moveWithHighestMoveValue;
		
		try {
			moveWithHighestMoveValue = moveStore.get(0);
		} catch (Exception e) {
			moveWithHighestMoveValue = new Moves(1, 1);
		}
		for (int i = 0; i < moveStore.size(); i++) {
			if (moveStore.get(i).moveValue() > highestMoveValue) {
				highestMoveValue = moveStore.get(i).moveValue();
				moveWithHighestMoveValue = moveStore.get(i);
			}
		}
		
		return moveWithHighestMoveValue;
	}
    
	public static void movePiece(int colChoice, int destColumn, Board gameBoard) {
    	int piece,enemyPiece;
    	int numberOfPiecesAtColChoice = gameBoard.pieceStore[colChoice].getNumberOfPieces();    	
    	
    	gameBoard.pieceStore[colChoice].setNumberOfPieces(numberOfPiecesAtColChoice - 1);
    	
    	if (gameBoard.pieceStore[colChoice].getNumberOfPieces() == 0) {
			gameBoard.pieceStore[colChoice].setEmpty();
		}
    	
    	if (destColumn != 25 && destColumn != 0) {
    		if (playerStore.get(turn).getPlayerIdentity() == true) {
        		piece = CIRCLES;
        		enemyPiece = SOLIDS;
        	}
        	else {
    			piece = SOLIDS;
    			enemyPiece = CIRCLES;
    		}
        	if (gameBoard.pieceStore[destColumn].getPieceOrientation() == piece) {
        		int numPieces = gameBoard.pieceStore[destColumn].getNumberOfPieces() + 1;
        		gameBoard.pieceStore[destColumn].setNumberOfPieces(numPieces);
        	}
        	if (gameBoard.pieceStore[destColumn].getPieceOrientation() == EMPTY) {
        		gameBoard.pieceStore[destColumn].setNumberOfPieces(1);
    			if (playerStore.get(turn).getPlayerIdentity() == true) {
    				gameBoard.pieceStore[destColumn].setCircles();
    			} 
    			else {
    				gameBoard.pieceStore[destColumn].setSolids();
    			}
    		}
        	if (gameBoard.pieceStore[destColumn].getPieceOrientation() == enemyPiece) {
        		gameBoard.pieceStore[destColumn].setPieceOrientation(piece);
        		int ownBearOff = 0;
        		if (playerStore.get(turn).getPlayerIdentity() == true) {
        			ownBearOff = 25;
        		}
    			gameBoard.pieceStore[ownBearOff].setPieceOrientation(enemyPiece);
    			gameBoard.pieceStore[ownBearOff].setNumberOfPieces(1);
        	}
    	}
    	removeUsedDieValues(colChoice, destColumn);	
	}
    
    public static void removeUsedDieValues(int colChoice, int destChoice) {
    	int diceValueUsed;
    	boolean bearingOff = false;
    	boolean diceRemoved = false;
		if (playerStore.get(turn).getPlayerIdentity() == true) {
			diceValueUsed = destChoice - colChoice;
			if (destChoice == 25) {
				bearingOff = true;
			}
		}
		else{
			diceValueUsed = colChoice - destChoice;	
			if (destChoice == 0) {
				bearingOff = true;
			}
		}
		if (DieStore.size() == 1) {
			if (diceValueUsed == DieStore.get(0).getRollValue()) {
				DieStore.clear();
				diceRemoved = true;
			}
			if (bearingOff == true && diceRemoved == false) {
				if (diceValueUsed < DieStore.get(0).getRollValue()) {
					DieStore.clear();
					diceRemoved = true;
				}
			}
		}
		else if (DieStore.size() == 2) {
			if (diceValueUsed == DieStore.get(0).getRollValue()) {
				DieStore.remove(0);
				diceRemoved = true;
			}
			else if (diceValueUsed == DieStore.get(1).getRollValue()) {
				DieStore.remove(1);
				diceRemoved = true;
			}
			else if (diceValueUsed == (DieStore.get(0).getRollValue()+DieStore.get(1).getRollValue())) {
				DieStore.clear();
				diceRemoved = true;
			}
			if (bearingOff == true && diceRemoved == false) {
				if (diceValueUsed < DieStore.get(0).getRollValue()) {
					DieStore.remove(0);
					diceRemoved = true;
				}
				else if (diceValueUsed <  DieStore.get(1).getRollValue()) {
					DieStore.remove(1);
					diceRemoved = true;
				}
				else if (diceValueUsed < (DieStore.get(0).getRollValue() + DieStore.get(1).getRollValue())) {
					DieStore.clear();
					diceRemoved = true;
				}
			}
		}
		else if (DieStore.size() == 3 || DieStore.size() == 4) {
			if (diceValueUsed == DieStore.get(0).getRollValue()) {
				DieStore.remove(0);
				diceRemoved = true;
			}
			else if (diceValueUsed == DieStore.get(0).getRollValue()*2) {
				DieStore.remove(1);
				DieStore.remove(0);
				diceRemoved = true;
			}
			else if (diceValueUsed == DieStore.get(0).getRollValue()*3) {
				DieStore.remove(2);
				DieStore.remove(1);
				DieStore.remove(0);
				diceRemoved = true;
			}
			else if (diceValueUsed == DieStore.get(0).getRollValue()*4){
				DieStore.clear();
				diceRemoved = true;
			}	
			if (bearingOff == true && diceRemoved == false) {
				if (diceValueUsed < DieStore.get(0).getRollValue()) {
					DieStore.remove(0);
					diceRemoved = true;
				}
				else if (diceValueUsed < (DieStore.get(0).getRollValue()*2)) {
					DieStore.remove(1);
					DieStore.remove(0);
					diceRemoved = true;
				}
				else if (diceValueUsed < (DieStore.get(0).getRollValue()*3)) {
					DieStore.remove(2);
					DieStore.remove(1);
					DieStore.remove(0);
					diceRemoved = true;
				}
				else if (diceValueUsed < (DieStore.get(0).getRollValue()*4)) {
					DieStore.clear();
					diceRemoved = true;
				}
			}		
		}
	}
    
    private static void setupDieStore() {
		DieStore.clear();
		Dice dice1,dice2;
		dice1 = new Dice();
    	dice2 = new Dice();
    	DieStore.add(dice1);
    	DieStore.add(dice2);
	}
    
    private static boolean checkIfValidUserIntInput(String userChoice, int minChoice, int maxChoice){
    	
    	int userChoiceAsInt;
    	try {
    		userChoiceAsInt = Integer.valueOf(userChoice);
		} catch (NumberFormatException e) {
			return false;
		}
		
		
    	if (userChoiceAsInt >= minChoice && userChoiceAsInt <= maxChoice) {
			return true;
		}
    	else {
			return false;
		}
		
	}
    
    private static void displayOnlineMenu() {
		System.out.println("\t\t\t1. Join a game of Backgammon");
		System.out.println("\t\t\t2. Host a game of Backgammon");
		System.out.println("\t\t\t3. Return to the main Menu");
		System.out.println(DOTTED_LINE);
		
		Scanner consoleReader = new Scanner(System.in);
        
		String userChoice="";
        boolean validChoice = false;
        
        while (validChoice == false) {
            System.out.print("Please input your choice: ");
            try {
            	userChoice = consoleReader.nextLine();  
            	validChoice = checkIfValidUserIntInput(userChoice, 1, 3);
    		} catch (Exception e) {
    			
    		}

		} 
        
        if (Integer.valueOf(userChoice) == 1) {
        	System.out.println(DOTTED_LINE);
			joinGame();
			System.out.println(DOTTED_LINE);
			displayOnlineMenu();  
			
		}
        else if (Integer.valueOf(userChoice) == 2) {
        	System.out.println(DOTTED_LINE);
         	try {
        		hostGame();
    			hostServerSocket.close();
			} catch (Exception e) {
			}
			
			userConnectedToServer = false;
			System.out.println(DOTTED_LINE);
			displayOnlineMenu();

		}
        else if (Integer.valueOf(userChoice) == 3) {
			printMenu();
		}
	}   
		
    private static void joinGame() {
		getServerIPAndPort();
		
		//Uses a buffered reader so that the program can use the method ready().
		BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
		
		//Tries to connect to the server socket using the IP address and port number given by the user.
		try {
			Socket client = new Socket(serverIP, serverPort);
			client.setSoTimeout(timeout);
			
			//If the connection doesn't time out and the client successfully connects to the server socket...
			System.out.println("Connected to server!");
			System.out.println("\nType 'quit' at any time to disconnect from the server.");
			
			//Sets up the socket reader and writer.
			socketOut = new PrintWriter(client.getOutputStream(), true);
			socketIn =  new BufferedReader(new InputStreamReader(client.getInputStream()));
			
			
			//Sends the "hello" message to the server.
			socketOut.println("hello");
			
			//waits for the server to respond to the message.
			String serverMessage = getMessageFromServer();
			
			/*
			 * If the server's message is "hello" then the user has successfully connected to a valid 
			 * backgammon server socket.
			 */
			if (serverMessage.equals("hello")) {
				userConnectedToServer = true;
				connectionWithServer = client;
			/*
			 * If the server message is "busy" then another user is already connected to the server
			 * and the program disconnects from the server socket.	
			 */
			} else if (serverMessage.equals("busy")) {
				System.out.println("\nThe server is currently busy!");
				System.out.println("Disconnecting from the server...");
				client.close();
				return;
			/*
			 * If the server message is anything else, then it is not a valid backgammon server.
			 */
			} else {
				System.out.println("\nThis server is not a valid backgammon server!");
				System.out.println("Disconnecting from the server...");
				client.close();
				return;
			}
			
			//If the user has successfully to a backgammon server socket.
			if (userConnectedToServer == true) {
				System.out.println("\nType 'ready' when you're ready to start a new game of backgammon...");
				
				boolean quit = false;
				boolean readyToPlayGame = false;
				boolean recievingMessageFromServer = true;
				boolean setupGame = false;
				
				//When gameOver = true, one of the players has won the game.
				boolean gameOver = false;
				
				
				while (quit == false) {
					
					//If the user has inputed something into the console
					if (consoleReader.ready()) {
						String userInput = consoleReader.readLine();
						
						//If the user inputs "quit", they quit the game and disconnect from the server socket.
						if (userInput.equalsIgnoreCase("quit")) {
							readyToPlayGame = false;
							setupGame = false;
							userConnectedToServer = false;
							currentlyInGame = false;
							displayOnlineGameBoard = true;
							quit = true;
						}
						else if (userInput.equalsIgnoreCase("ready")) {
							if (readyToPlayGame == false && currentlyInGame == false) {
								readyToPlayGame = true;
								recievingMessageFromServer = true;
								
								//Tells the server socket that the program is ready to start a new game.
								socketOut.println("newgame");
								System.out.println("Now waiting for the server to confirm that they are ready.");
							}
							else if (readyToPlayGame == true && currentlyInGame == false) {
								System.out.println("You have already specified that you are ready to start a new game of backgammon!");							
							}
							else if (currentlyInGame == true) {
								System.out.println("You are already in a game!");
							}							
						}
					}
					//If the program is waiting for a message from the server.
					if (userConnectedToServer == true && recievingMessageFromServer == true) {
						serverMessage = getMessageFromServer();
						
						//If the message is null, it means that the server hasn't sent a message yet.
						if (serverMessage == null) {
						}
						//If the message is "ready", it means that the server is ready to start the game.
						else if(serverMessage.equals("ready")){
							if (readyToPlayGame == true && setupGame == false && currentlyInGame == false) {
								recievingMessageFromServer = false;
								setupGame = true;
								System.out.println("Setting up the game...");	
								//Tells the user which player they are in the game.
								System.out.println("\nYou will be Player 2 in this game.");
								System.out.println();
								//Allows the user to roll a pair of dice to see which player will go first.
								setWhichPlayerGoesFirst();
							}
						}
						//If the message is "bye", it means the program has been disconnected from the server socket.	
						else if (serverMessage.equals("bye")) {
							System.out.println("You have been disconnected from the server...");
							/*
							 * Resets all of these variables, so that the program is ready for when the user
							 * connects to another server socket or hosts their own.
							 */
							connectionWithServer.close();
							userConnectedToServer = false;
							currentlyInGame = false;
							displayOnlineGameBoard = true;
							readyToPlayGame = false;
							setupGame = false;
						}
						//If the message is "reject", the user has been kicked from the server socket.
						else if (serverMessage.equals("reject")) {
							System.out.println("You have been kicked from the server...");
							/*
							 * Resets all of these variables, so that the program is ready for when the user
							 * connects to another server socket or hosts their own.
							 */
							connectionWithServer.close();
							userConnectedToServer = false;
							currentlyInGame = false;
							displayOnlineGameBoard = true;
							readyToPlayGame = false;
							setupGame = false;
							
						}
						/*
						 * If the message from the server is a valid turn command, it processes the server's move
						 * and then performs it. 	
						 */
						else if (readMove(serverMessage) == true && currentlyInGame == true && turn == 0){								
								System.out.println("\nProcessing Player 1's move.");
								locationInMovesMadeByPlayer = 0;
								turnProcess(onlineBoard, MULTIPLAYER);
								
								//Checks to see if the server has won the game.
								gameOver = isGameOver(onlineBoard, true);
								
								/*
								 * If the server has won, the program tells the server that 
								 * they won and then disconnects from the server.
								 */
								if (gameOver == true) {
									socketOut.println("you-win; bye");
									
									System.out.println("\nPress Enter to leave the server...");
									
									try {
										System.in.read();
									} catch (Exception e) {
									}
									quit = true;
									
								}
								//If the server hasn't won, the game continues as normal.
								else {
									turnChanger();
									displayOnlineGameBoard = true;	
									recievingMessageFromServer = false;		
								}														
						}
					}
					//If the program needs to send a message to the server socket
					else if (userConnectedToServer == true && recievingMessageFromServer == false) {
						//Setting up the game for the first time.
						if (setupGame == true && currentlyInGame == false) {
							setupGame = false;
							readyToPlayGame = false;
							currentlyInGame = true;
							
							System.out.println(DOTTED_LINE);
							
							Scanner consoleReader2 = new Scanner(System.in);
							
							boolean validChoice = false;
					        String userChoice = "";
					        
					        setupThePlayers();
					        
							//Allows the user to choose whether they will be controlling the player, or whether the program's AI will. 
					        while (validChoice == false) {
					            System.out.print("Will this player be controlled by (1) A Human / (2) The Computer: ");
					            try {
					            	userChoice = consoleReader2.nextLine();  
					            	validChoice = checkIfValidUserIntInput(userChoice, 1, 2);
					    		} catch (Exception e) {
					    		}
							} 			        

					    	if (Integer.valueOf(userChoice) == 1) {
					        	playerStore.get(1).setAsAI(false);
					        					    
					    	}
					    	else {
					        	playerStore.get(1).setAsAI(true);
					        	validChoice = false;
					        	userChoice = "";
					        	
					        	while (validChoice == false) {
						            System.out.print("Set AI difficulty (1) Normal / (2) Difficult: ");
						            try {
						            	userChoice = consoleReader2.nextLine();  
						            	validChoice = checkIfValidUserIntInput(userChoice, 1, 2);
						    		} catch (Exception e) {
						    		}
								}
					        	
					        	if (Integer.valueOf(userChoice) == 1) {
									playerStore.get(1).setAsEasyAI(true);
								}
					        	else{
					        		playerStore.get(1).setAsEasyAI(false);
					        	}		     
					    	}
					    	//Sets up the board for the game
							onlineBoard = new Board();
							
							System.out.print("\nPress Enter to start the game...");
							consoleReader.readLine();
							
							if (turn == 0) {
								//If the first turn is the server's, then the program sends "pass"
								socketOut.println("pass");
								recievingMessageFromServer = true;//User will wait for the server to send their move.
							}
							else{
								recievingMessageFromServer = false;//User gets to send their move.
							}							
						}						
					}
					
					if (currentlyInGame == true) {
						//If it is the server's turn.
						if (turn == 0) {
							if (displayOnlineGameBoard == true) {
								displayOnlineGameBoard = false;
								onlineBoard.displayBoard();
								System.out.println("Waiting for Player 1 to make their move...");
								//Waits for the server to send a valid turn command.
								recievingMessageFromServer = true;
								
							}
							
						}
						//If it is the user's turn.
						else if (turn == 1) {
							if (displayOnlineGameBoard == true) {
								displayOnlineGameBoard = false;
								System.out.println(DOTTED_LINE);
								if (playerStore.get(turn).getAI() == true) {
									turnProcess(onlineBoard,AI);
								} else {
									turnProcess(onlineBoard,REAL_PLAYER);
								}
								
								//Checks to see if the user has won the game.
								gameOver = isGameOver(onlineBoard, true);
								
								/*
								 * If the user has won the game, the program sends the server the winning move
								 * made by the user and then disconnects from the server socket.
								 */
								if (gameOver == true) {
									sendMoves();
									System.out.println("\nPress Enter to leave the server...");
									
									try {
										System.in.read();
									} catch (Exception e) {
									}
									quit = true;
								}
								//If the user hasn't won, the game continues as normal.	
								else {
									turnChanger();
									sendMoves();
									displayOnlineGameBoard = true;	
								}
							}							
						}
					}										
				}
				try {
					//Disconnects from the server socket.
					socketOut.println("bye");
					userConnectedToServer = false;
					currentlyInGame = false;
					displayOnlineGameBoard = true;
					readyToPlayGame = false;
					setupGame = false;
					client.close();
				} catch (Exception e) {
					
				}
				
			}
		}
		catch (Exception e) {
			/*
			 * Tells the user that the program couldn't connect to the server socket that they specified
			 * and allows them to return to the menu, so that they can re-input the server socket IP address
			 * and port number if they made a mistake.
			 */
			System.out.println("Could not connect to the specified server ("+serverIP+":"+serverPort+")!");
			System.out.print("\nPress Enter to return to the menu...");
			try {
				System.in.read();
			} catch (IOException e1) {

			}
		}
	}
    
    /*
     * Puts the dice values rolled by the user and the moves made by the user 
     * during their turn into the correct turn command format and then sends
     * this turn command to the client/server socket.
     */
	private static void sendMoves() {
		int dice1 = diceValuesRolledByPlayer.get(0);
		int dice2 = diceValuesRolledByPlayer.get(1);
		String move = "";
		StringBuilder sb = new StringBuilder();
		
		sb.append(dice1+"-"+dice2+":");
		
		for (int i = 0; i < movesMadeByPlayer.size(); i++) {
			
			if (movesMadeByPlayer.get(i) == 66) {
				sb.append("(-1|");
			}
			else {
				sb.append("("+movesMadeByPlayer.get(i)+"|");
			}
			
			i++;
			
			if (movesMadeByPlayer.get(i) == 66) {
				sb.append("-1)");
			}
			else {
				sb.append(movesMadeByPlayer.get(i)+")");
			}
			
			
			if (i == movesMadeByPlayer.size()-1) {
				sb.append(";");
			}
			else {
				sb.append(",");
			}
		}
		move = sb.toString();
		socketOut.println(move);
	}
	
	/*
	 * Asks the user to input the IP address and the port number
	 * of the server socket they want to connect to.
	 */
	private static void getServerIPAndPort() {
		Scanner consoleReader = new Scanner(System.in);
		String userInput;
		boolean validInput = false;
		
		System.out.print("Input the Server's IP: ");
		userInput = consoleReader.nextLine();
		serverIP = userInput;
		
		while (validInput == false) {
			System.out.print("Input the Server's port (Default: "+defaultPort+"): ");
			userInput = consoleReader.nextLine();
			
			try {
				serverPort = Integer.valueOf(userInput);
				validInput = true;
			} catch (NumberFormatException e) {
				System.out.println("Please input a valid port number!!!!");
			}
			
		}
		
		System.out.print("\nPress Enter to connect to the Server ("+serverIP+":"+serverPort+")...");
		consoleReader.nextLine();
	
		System.out.println(DOTTED_LINE);
	}
	
	/*
	 * Checks to see if the server has sent a message to
	 * the client socket.
	 */	
	private static String getMessageFromServer() {
		try {
			String serverMessage = socketIn.readLine();
			
			if (serverMessage == null) {
				/*
				 * If the message received is null, then it means the
				 * client has been disconnected from the server socket.
				 */
				System.out.println("You have been disconnected from the server!");
				userConnectedToServer = false;
				//The program then closes the connection with the server socket.
				connectionWithServer.close();
			}
			else {
				return serverMessage;
			}
			
	
		} catch (SocketTimeoutException e) {
			/*
			 * If the socket simply times out, this means the client
			 * is still connected to the server socket, but the 
			 * server hasn't sent a message yet.
			 */
			
		} catch (IOException ex) {
			
		}
		
		return null;
	}

	private static void hostGame(){
		//Sets up the server socket the user will host.
		setupServer();
		
		//Uses a buffered reader instead of a scanner so that the program can use the ready() method.
		BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
		
		boolean quit = false;
		boolean recievingMessageFromClient = true;//If this is false, it is the server's turn to send a message.
		boolean readyToPlayGame = false;
		boolean clientReadyToPlayGame = false;
		boolean setupGame = false;
		
		//When gameOver = true, one of the players has won the game.
		boolean gameOver = false;
		
		while (quit == false) {
			
			//Checks to see if a user has connected to the server socket.
			checkForConnection();
			
			try {
				//If the user has inputed something into the console...
				if (consoleReader.ready()) {
					String userInput = consoleReader.readLine();
					
					if (userInput.equalsIgnoreCase("quit")) {
						//Stops the loop and closes the server socket.
						quit = true;
					}
					else if (userInput.equalsIgnoreCase("ready")) {
						//If no one is connected to the server, the user can't say that they are ready yet.
						if (userConnectedToServer == false) {
							System.out.println("Nobody is connected to the server!");
						}
						/*
						 * If someone is connected to the server but they haven't confirmed that they are ready,
						 * the program will wait for them to confirm that they are ready before starting the game.
						 */
						else if (readyToPlayGame == false && currentlyInGame == false && clientReadyToPlayGame == false) {
							readyToPlayGame = true;
							System.out.println("Now waiting for the other player to say that they are ready.");
						}
						/*
						 * If the user connected to the server socket has already confirmed they are ready
						 * the program will immediately start setting up the game.
						 */
						else if (readyToPlayGame == false && currentlyInGame == false && clientReadyToPlayGame == true) {
							readyToPlayGame = true;
						}
						//If the user has already confirmed that they are ready.
						else if (readyToPlayGame == true && currentlyInGame == false) {
							System.out.println("You have already specified that you are ready to start a new game of backgammon!");							
						}
						//If the user is already in a game, they can't say that they are ready to play.
						else if (currentlyInGame == true) {
							System.out.println("You are already in a game!");
						}
						
					}
					/*
					 * If the user inputs "reject" at any time and there is a user connected to the server socket
					 * the program closes the connection between the server and the client, but keeps the server socket
					 * open so that a different user can connect to the server socket and play a game.  
					 */
					else if (userInput.equalsIgnoreCase("reject")) {
						if (clientReadyToPlayGame == true && currentlyInGame == false && userConnectedToServer == true) {
							socketOut.println("reject");
							connectionWithClient.close();
							userConnectedToServer = false;
							clientReadyToPlayGame = false;
							readyToPlayGame = false;
							setupGame = false;
							System.out.println("Kicked the user from the server...");
						}
						else if (clientReadyToPlayGame == false && currentlyInGame == false && userConnectedToServer == true) {
							socketOut.println("bye");
							connectionWithClient.close();
							userConnectedToServer = false;
							System.out.println("Kicked the user from the server...");
						}
						else if (currentlyInGame == true && userConnectedToServer == true) {
							socketOut.println("bye");
							connectionWithClient.close();
							userConnectedToServer = false;
							currentlyInGame = false;
							displayOnlineGameBoard = true;
							clientReadyToPlayGame = false;
							readyToPlayGame = false;
							setupGame = false;
							System.out.println("Kicked the user from the server...");
						}
						//If no one is connected to the server.
						else if (userConnectedToServer == false) {
							System.out.println("No users are connected to the server!");
						}
					}
				}
			} catch (IOException e) {

			}
			//If the program is waiting for a message from the client.
			if (userConnectedToServer == true && recievingMessageFromClient == true) {
				String clientMessage = getMessageFromClient();
				
				//If the message is null, it means that the client hasn't sent a message yet.
				if (clientMessage == null) {
				}
				//If the message is "newgame", it means that the client is ready to start the game.
				else if(clientMessage.equals("newgame")){
					//If the user is also ready to start a game and the server isn't already playing a game...
					if (readyToPlayGame == true && currentlyInGame == false) {
						//Confirms to the client that the server is also ready to start a game.
						socketOut.println("ready");
						System.out.println("Setting up game...");
						//Sets up the players in the player store array list for the game.
						setupThePlayers();
						
						Scanner consoleReader2 = new Scanner(System.in);
						
						boolean validChoice = false;
				        String userChoice = "";
				        
				        //Allows the user to choose whether they will be controlling the player, or whether the program's AI will. 
				        while (validChoice == false) {
				            System.out.print("Will this player be controlled by (1) A Human / (2) The Computer: ");
				            try {
				            	userChoice = consoleReader2.nextLine();  
				            	validChoice = checkIfValidUserIntInput(userChoice, 1, 2);
				    		} catch (Exception e) {
				    		}
						} 			        
	
				    	if (Integer.valueOf(userChoice) == 1) {
				        	playerStore.get(0).setAsAI(false);				        	   				    
				    	}
				    	else {
				        	playerStore.get(0).setAsAI(true);
				        	validChoice = false;
				        	userChoice = "";
				        	
				        	while (validChoice == false) {
					            System.out.print("Set AI difficulty (1) Normal / (2) Difficult: ");
					            try {
					            	userChoice = consoleReader2.nextLine();  
					            	validChoice = checkIfValidUserIntInput(userChoice, 1, 2);
					    		} catch (Exception e) {
					    		}
							}
				        	
				        	if (Integer.valueOf(userChoice) == 1) {
								playerStore.get(0).setAsEasyAI(true);
							}
				        	else{
				        		playerStore.get(0).setAsEasyAI(false);
				        	}		  
				    	}
				    	//Sets up the board for the game
						onlineBoard = new Board();
						//Tells the user which player they are in the game.
						System.out.println("\nYou will be Player 1 in this game.");
						System.out.println("Now waiting to see who will have the first turn...");
						System.out.println("\nIf Player 2 gets to move first, you will also have to wait for them\nto make their move...");
						System.out.println();
						clientReadyToPlayGame = true;							
						setupGame = true;
						recievingMessageFromClient = true;					
					}
					/*
					 * If the user isn't ready to start the game and the server isn't already playing a game,
					 * but the client is ready to play the game, the program tells the user that the client
					 * has confirmed that they are ready to play the game.
					 */
					else if (readyToPlayGame == false && currentlyInGame == false) {
						clientReadyToPlayGame = true;
						System.out.println("The user ("+clientIPAddress+") is ready to play a game!");
						recievingMessageFromClient = false;
					}
				//If the message is "bye", it means the client has disconnected from the server socket.	
				} else if (clientMessage.equals("bye")) {
					try {
						//Closes the connection between the server socket and the client.
						connectionWithClient.close();
					} catch (IOException e) {
					}
					/*
					 * resets all of these variables, so that the program is ready for the next
					 * player who connects to the server socket.
					 */
					userConnectedToServer = false;
					clientReadyToPlayGame = false;
					readyToPlayGame = false;
					setupGame = false;
					//Tells the user that the client has disconnected from the server socket.
					System.out.println("The user disconnected from the server...");
				}
				//If the message is "pass" it means that the server gets to play the first turn.
				else if (clientMessage.equals("pass")) {
					if (setupGame == true && currentlyInGame == false) {
						turn = 0;
						System.out.println("Player 1 gets the first turn!");
						System.out.print("Press Enter to start the game...");
						try {
							consoleReader.readLine();
						} catch (IOException e) {
	
						}
						System.out.println(DOTTED_LINE);
						//Starts the game.
						currentlyInGame = true;
					}
				/*
				 * If the message from the client is a valid turn command, it processes the client's move
				 * and then performs it. 	
				 */
				} else if (readMove(clientMessage) == true && (currentlyInGame == true||setupGame == true)) {
					System.out.println("\nProcessing Player 2's move...");
					
					locationInMovesMadeByPlayer = 0;
					turn = 1;
					setupGame = false;
					currentlyInGame = true;
					turnProcess(onlineBoard, MULTIPLAYER);
					
					//Checks to see if the client has won the game.
					gameOver = isGameOver(onlineBoard, true);
					
					/*
					 * If the client has won, the program tells the client that 
					 * they have won and then the server socket is closed.
					 */
					if (gameOver == true) {
						socketOut.println("you-win; bye");
						System.out.println("\nPress Enter to end the game and close the server...");
						try {
							System.in.read();
						} catch (Exception e) {
						}
						quit = true;
						
					}
					//If the client hasn't won, the game continues as normal.
					else {
						turnChanger();
						displayOnlineGameBoard = true;	
						recievingMessageFromClient = false;
					}
					
				}
			}
			//If the program needs to send a message to the client
			else if (userConnectedToServer == true && recievingMessageFromClient == false) {
				//If both the client and the user is ready to start the game...
				if (readyToPlayGame == true && clientReadyToPlayGame == true && setupGame == false && currentlyInGame == false) {
					//The program tells the client that it is ready to start the game.
					socketOut.println("ready");
					System.out.println("Setting up game...");
					//Sets up the players in the player store array list for the game.
					setupThePlayers();
					
					Scanner consoleReader2 = new Scanner(System.in);
					
					boolean validChoice = false;
			        String userChoice = "";
			        
					//Allows the user to choose whether they will be controlling the player, or whether the program's AI will. 
			        while (validChoice == false) {
			            System.out.print("Will this player be controlled by (1) A Human / (2) The Computer: ");
			            try {
			            	userChoice = consoleReader2.nextLine();  
			            	validChoice = checkIfValidUserIntInput(userChoice, 1, 2);
			    		} catch (Exception e) {
			    		}
					} 			        

			    	if (Integer.valueOf(userChoice) == 1) {
			        	playerStore.get(0).setAsAI(false);			        		     				    
			    	}
			    	else {
			        	playerStore.get(0).setAsAI(true);
			        	validChoice = false;
			        	userChoice = "";
			        	
			        	while (validChoice == false) {
				            System.out.print("Set AI difficulty (1) Normal / (2) Difficult: ");
				            try {
				            	userChoice = consoleReader2.nextLine();  
				            	validChoice = checkIfValidUserIntInput(userChoice, 1, 2);
				    		} catch (Exception e) {
				    		}
						}
			        	
			        	if (Integer.valueOf(userChoice) == 1) {
							playerStore.get(0).setAsEasyAI(true);
						}
			        	else{
			        		playerStore.get(0).setAsEasyAI(false);
			        	}	
			    	}
			    	//Sets up the board for the game
					onlineBoard = new Board();
					//Tells the user which player they are in the game.
					System.out.println("\nYou will be Player 1 in this game.");
					System.out.println("Now waiting to see who will have the first turn...");
					System.out.println("\nIf Player 2 gets to move first, you will also have to wait for them\nto make their move...");
					System.out.println();
					clientReadyToPlayGame = true;							
					setupGame = true;
					recievingMessageFromClient = true;
				}
				
			}
			
			if (currentlyInGame == true) {
				//If it is the user's turn...
				if (turn == 0) {
					if (displayOnlineGameBoard == true) {
						
						System.out.println(DOTTED_LINE);
						
						
						if (playerStore.get(turn).getAI() == true) {
							turnProcess(onlineBoard,AI);
						} else {
							turnProcess(onlineBoard,REAL_PLAYER);
						}
						
						//Checks to see if the user has won the game.
						gameOver = isGameOver(onlineBoard, true);
						
						/*
						 * If the user has won the game, the program sends the client the winning move
						 * made by the user and then closes the server socket.
						 */
						if (gameOver == true) {
							sendMoves();
							System.out.println("\nPress Enter to end the game and close the server...");
							
							try {
								System.in.read();
							} catch (Exception e) {
							}
							quit = true;
						//If the user hasn't won, the game continues as normal.	
						} else {
							turnChanger();
							sendMoves();
							displayOnlineGameBoard = true;	
						}
											
					}
				}
				//If it is the client's turn.
				else if (turn == 1) {
					if (displayOnlineGameBoard == true) {
						onlineBoard.displayBoard();
						System.out.println("Waiting for Player 2 to make their move...");
						displayOnlineGameBoard = false;
						//Waits for the client to send a valid turn command.
						recievingMessageFromClient = true;
					}					
				}				
			}
			
		}
		
		//If a user is still connected to the server socket, it sends the protocol for terminating a session ("bye")
		if (userConnectedToServer == true) {
			 socketOut.println("bye");
		}
		
		try {
			//Closes the server socket being hosted.
			hostServerSocket.close();
		} catch (IOException e) {

		}
		
		/*
		 * Sets all of these to their original starting values,
		 * so that the user can re-host a server without any
		 * problems.
		 */
		userConnectedToServer = false;
		currentlyInGame = false;
		displayOnlineGameBoard = true;
		
		//Tells the user that the server has been stopped.
		System.out.println("Server has been stopped...");
		
	}
	
	/*
	 * Checks to see if the client has sent a message to
	 * the server socket.
	 */	
	private static String getMessageFromClient() {
		try {
			String clientMessage = socketIn.readLine();
			
			if (clientMessage == null) {
				/*
				 * If the message received is null, then it means the
				 * client has disconnected from the socket.
				 */
				clientHasDisconnected();
			}
			else {
				return clientMessage;
			}
			
	
		} catch (SocketTimeoutException e) {
			/*
			 * If the socket simply times out, this means the client
			 * is still connected, but hasn't sent a message yet.
			 */
		} catch (IOException ex) {
			/*
			 * If an IO Exception occurs, then it means the
			 * client has disconnected from the socket.
			 */
			clientHasDisconnected();
		}
		
		return null;
	}
	
	/*
	 * Tells the user hosting the server socket, that the user has disconnected
	 * from the server and then closes the connection.
	 */
	private static void clientHasDisconnected() {
		System.out.println("\nThe user ("+clientIPAddress+") has disconnected from the server!");
		try {
			userConnectedToServer = false;
			connectionWithClient.close();
		} catch (IOException e) {
			System.out.println("An IOException has occured!");
		}
	}
	
	/*
	 * Checks to see if anyone is trying to connect to
	 * the server socket being hosted.
	 */
	private static void checkForConnection() {
		Socket connection = null;
		boolean userConnected = true;
		
		try {
			connection = hostServerSocket.accept();
			connection.setSoTimeout(timeout);
			connection.setTcpNoDelay(true);	
			socketIn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			socketOut = new PrintWriter(connection.getOutputStream(),true);
			
		} catch (Exception e) {
			/*
			 * If the socket connection times out then it means that no one is correctly
			 * connected to the server socket. 
			 */			
			userConnected = false;
		}
		
		//If a client has successfully connected to the server socket.
		if (userConnected == true) {
			setupConnection(connection);
		}		
	}
	
	/*
	 * Checks to see if the client attempting to connect to the server socket is
	 * using the correct connection protocols. If they are, the connection is
	 * then correctly set up.
	 */
	private static void setupConnection(Socket connection) {
		
		try {
			String clientMessage = socketIn.readLine();
			InetAddress userInetAddress = connection.getInetAddress();
			
			if (clientMessage.equals("hello")) {
				if (userConnectedToServer == false) {
					
					//Tells the user that someone has connected to the server and what their IP address is.
					System.out.println("\nA user ("+userInetAddress.getHostAddress()+") has connected to the server!");
					System.out.println("\nType 'ready' when you're ready to start a game of backgammon...");
					
					userConnectedToServer = true;
					connectionWithClient = connection;
					
					//Sets socketIn, socketOut and the clientIPAddress using the connection created with the new client.
					socketIn = new BufferedReader(new InputStreamReader(connectionWithClient.getInputStream()));
					socketOut = new PrintWriter(connectionWithClient.getOutputStream(),true);
					clientIPAddress = userInetAddress.getHostAddress();
					
					socketOut.println("hello");						
				}
				else {
					/*
					 * If a user is already connected to the server, the server tells the new user that the server is busy
					 * and then closes the connection.
					 */
					socketOut.println("busy");
					//Tells the user that a user attempted to join the server and what their IP address was.
					System.out.println("A user ("+userInetAddress.getHostAddress()+") has attempted to join this full server.");
					connection.close();
				}
			} else {
				/*
				 * If the user connected isn't using the correct protocol and doesn't send "hello" when connecting
				 * to the server socket, the connection is immediately closed
				 */
				connection.close();
				socketIn = new BufferedReader(new InputStreamReader(connectionWithClient.getInputStream()));
				socketOut = new PrintWriter(connectionWithClient.getOutputStream(),true);
			}
			
		} catch (IOException e) {
			//Tells the user that an IOEXCEPTION has just occured
			System.out.println("IOEXCEPTION HAS OCCURED");
		}
		
	}
	
	/*
	 * Sets up and starts the server socket that the user will be hosting
	 */
	private static void setupServer() {
		setServerPort();
		System.out.println("Setting up server...");
		
		try {
			hostServerSocket = new ServerSocket(serverPort);
			hostServerSocket.setSoTimeout(timeout);			
			InetAddress hostIP = InetAddress.getLocalHost();
			
			//Tells the user the details about the server, which they can then give to another player so that they can join their server.
			System.out.println("\nHost name: "+hostIP.getHostName());
			System.out.println("Host IP address: "+hostIP.getHostAddress());
			System.out.println("Port: "+serverPort);
			
			System.out.println("\nServer has been set up!");
			
			//Tells the user which commands they can use while hosting the server socket.
			System.out.println("Input 'quit' at any time to close server \nInput 'reject' to kick any user connected to the server.");

		} catch (Exception e) {
			/*
			 * Tells the user if it can't host the server socket and suggets a common reason
			 * for why the server socket couldn't be hosted.
			 */
			System.out.println("\nError! Could not start the server!");
			System.out.println("There may be a server on this machine using the same port ("+serverPort+")");
			System.out.print("\nPress Enter to return to the menu...");
			try {
				System.in.read();
				hostServerSocket.close();
			} catch (IOException e1) {
			}
			//Returns the user to the online menu.
			displayOnlineMenu();
		}
		
		System.out.println("\nWaiting for someone to join...");
		
		
		
	}
	
	//Takes a user input to set the port of the server the user is hosting.
	private static void setServerPort() {
		Scanner consoleReader = new Scanner(System.in);
		String userInput;
		boolean validInput = false;

		while (validInput == false) {
			//Tells the user what the default port is, in case the user doesn't know what port to use.
			System.out.print("Input the port the Server will use (Default: "+defaultPort+"): ");
			userInput = consoleReader.nextLine();
			
			//If the port isn't just a number, then it isn't a valid port.
			try {
				serverPort = Integer.valueOf(userInput);
				validInput = true;
			} catch (NumberFormatException e) {
				System.out.println("Please input a valid port number!!!!");
			}
			
		}
		
		System.out.print("\nPress Enter to start the Server...");
		consoleReader.nextLine();
	
		System.out.println(DOTTED_LINE);
		
	}
	
	/*
	 * Reads a string received from the socket, character by character, and checks whether it is a valid turn command.
	 * 
	 * If it is a valid turn command, the dice values rolled by the online player are stored in
	 * the array list diceValuesRolledByPlayer and the starting and ending position of each of the 
	 * online player's moves is stored in the array list movesMadeByPlayer.
	 */
	public static boolean readMove(String move) {
		/*
		 * Clears these arrays so that it can store the dice values rolled by the online player
		 * and the moves made by the online player.
		 */
		diceValuesRolledByPlayer.clear();
		movesMadeByPlayer.clear();
		boolean validMove = true;
		
		int numberOfMoves;
		int dice1 = 0,dice2 = 0;
		
		ArrayList<Integer> startPositions = new ArrayList<Integer>();
		ArrayList<Integer> endPositions = new ArrayList<Integer>();
		
		char[] moveAsCharacters = move.toCharArray();
		
		
		/*
		 * Based on the format of the turn commands, the shortest possible turn command is 10 characters long.
		 * If the string is less than 10 characters long then it cannot be a move.
		 */
		if (moveAsCharacters.length < 10) { 
			validMove = false;
		}
		else{
			String singleChar;
			
			//Reads and stores the dice values rolled by the online player
			try {
				singleChar = moveAsCharacters[0]+"";
				dice1 = Integer.valueOf(singleChar);
				
				singleChar = moveAsCharacters[1]+"";
				
				if (singleChar.equals("-")) {
					singleChar = moveAsCharacters[2]+"";
					dice2 = Integer.valueOf(singleChar);
				}
				else {
					validMove = false;
				}
				
				singleChar = moveAsCharacters[3]+"";
				
				if (!(dice1 > 0 && dice2 > 0 && dice1 < 7 && dice2 < 7 && singleChar.equals(":"))) {
					validMove = false;
				}	
				
			} catch (Exception e) {
				validMove = false;
			}
			
			//Reads all of the moves done by the online player and stores the starting and ending positions of every move.
			if (validMove == true) {
				try {
					String tempDigit1,tempDigit2 ="",positionAsString;
					boolean reachedEndOfMove = false; // When the program reaches the semi-colon at the end of the turn command.
					int i = 4;
					//Loops until the end of the move has been reached, or if the turn command is not in the correct format.
					while (reachedEndOfMove == false && validMove == true) {
						tempDigit1 = "";
						tempDigit2 = "";
						positionAsString = "";
						
						singleChar = moveAsCharacters[i]+"";
						
						if (singleChar.equals("(")) {
							i++;
							tempDigit1 = moveAsCharacters[i]+"";
							
							if (tempDigit1.equals("-")) {
			
							}
							else{
								Integer.valueOf(tempDigit1);//Checks that tempDigit1 is a number.
							}					
						
							i++;
							singleChar = moveAsCharacters[i]+"";
							
							if (!(singleChar.equals("|"))) {
								tempDigit2 = singleChar;
								Integer.valueOf(tempDigit2);
								
								if (tempDigit1.equals("-")) {
									if (Integer.valueOf(tempDigit2) != 1) {
										validMove = false;
									}
								}
								
								i++;
								singleChar = moveAsCharacters[i]+"";									
							}
							
							if (singleChar.equals("|")) {
								if (tempDigit1.equals("-")) {
									Integer.valueOf(tempDigit2);//Makes sure that tempDigit2 is a number.
									positionAsString = "66";//Sets a blank move position as 66.
								}
								else {
									positionAsString = tempDigit1 + "" + tempDigit2;
									
									if (Integer.valueOf(positionAsString) >= 0 && Integer.valueOf(positionAsString) <= 25) {
										
									}
									else{
										validMove = false;
										System.out.println(positionAsString);
									}
								}
								
								//Blank moves are ignored as the program detects whether the online player can make a move or not.
								if (positionAsString.equals("66")) {
									
								} else {
									startPositions.add(Integer.valueOf(positionAsString));
								}

								tempDigit1 = "";
								tempDigit2 = "";
								
								i++;
								tempDigit1 = moveAsCharacters[i]+"";
								
								if (!(tempDigit1.equals("-"))) {
									Integer.valueOf(tempDigit1);
								}									
								
								i++;
								singleChar = moveAsCharacters[i]+"";
								
								if (!(singleChar.equals(")"))) {
									tempDigit2 = singleChar;
									Integer.valueOf(tempDigit2);
									
									if (tempDigit1.equals("-1")) {
										if (Integer.valueOf(tempDigit2)!=1) {
											validMove = false;
										}
									}
									i++;
									singleChar = moveAsCharacters[i]+"";									
								}
								
								if (singleChar.equals(")")) {
									if (tempDigit1.equals("-")) {
										Integer.valueOf(tempDigit2);//Makes sure that tempDigit2 is a number.
										positionAsString = "66";//Sets a blank move position as 66.
									}
									else {
										positionAsString = tempDigit1+""+tempDigit2;
										
										if (Integer.valueOf(positionAsString) >= 0 && Integer.valueOf(positionAsString) <= 25) {
											
										}
										else{
											validMove = false;
										}
									}
									//Blank moves are ignored as the program detects whether the online player can make a move or not.
									if (positionAsString.equals("66")) {
										
									} else {
										endPositions.add(Integer.valueOf(positionAsString));
									}
									
									i++;
									singleChar = moveAsCharacters[i]+"";
									
									if (singleChar.equals(";")) {
										reachedEndOfMove = true;
									} else if (singleChar.equals(",")) {
										i++;
									}
								}
								else {
									validMove = false;
								}
							}
							else{
								validMove = false;
							}
						}
						else {
							validMove = false;
						}						
					}
				} catch (Exception e) {
					validMove = false;
				}
			}			
		}
			
		if (validMove == true) {
			
			diceValuesRolledByPlayer.clear();
			movesMadeByPlayer.clear();
			
			//Stores the dice values rolled by the online player
			diceValuesRolledByPlayer.add(dice1);
			diceValuesRolledByPlayer.add(dice2);
			
			//Stores the moves made by the online player.
			for (int j = 0; j < startPositions.size(); j++) {				
				movesMadeByPlayer.add(startPositions.get(j));
				movesMadeByPlayer.add(endPositions.get(j));
			}
		}	
		return validMove;		
	}
	
	public static void main(String args[]) {
    	System.out.println(DOTTED_LINE);
        printMenu(); //Prints the main menu of the game
        System.exit(0);
    }
}