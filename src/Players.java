import java.util.ArrayList;

public class Players {
	private boolean player1or2;
	private boolean canBearOff;
	private boolean isAI;
	private boolean isEasyAI;

	public void setPlayerIdentity(boolean player) {
		this.player1or2 = player;
	}
	
	public void setPlayerToBearOff(boolean bearOff) {
		this.canBearOff = bearOff;
	}
	
	public void setAsAI(boolean isPlayerAI) {
		this.isAI = isPlayerAI;
	}
	
	public void setAsEasyAI(boolean easyAI) {
		this.isEasyAI = easyAI;
	}
	
	public boolean getPlayerIdentity() {
		return this.player1or2;
	}
	
	public boolean getBearOff() {
		return this.canBearOff;
	}
	
	public boolean getAI() {
		return this.isAI;
	}
	
	public boolean getEasyAI() {
		return this.isEasyAI;
	}
	
	public void moveScanner(ArrayList<Moves> moveStore, Board gameBoard, ArrayList<Dice> DieStore) {
		for (int i = 0; i < moveStore.size(); i++) {
			int moveValue = 0;
			int colChoice = moveStore.get(i).getCurrentCol();
			int destChoice = moveStore.get(i).getDestCol();
			int piece = Piece.CIRCLES;
			int enemyPiece = Piece.SOLIDS;
			int startPosition = 0;
			int endPosition = 19;
			int bearOffPrecedence = 6;
			
			if (this.getPlayerIdentity() == false) {
				piece = Piece.SOLIDS;
				enemyPiece = Piece.CIRCLES;
				startPosition = 6;
				endPosition = gameBoard.BOARDCOLS;
				for (int h = startPosition; h >= 0; h--) { // precedence decreases as piece closes into own bearOff
					if (gameBoard.pieceStore[destChoice].getPieceOrientation() == piece) {
						moveValue = bearOffPrecedence;
					}
					bearOffPrecedence--;
				}			
			}
			if (this.getPlayerIdentity() == true) {
				for (int h = startPosition; h >= 0; h--) { // precedence decreases as piece closes into own bearOff
					if (gameBoard.pieceStore[destChoice].getPieceOrientation() == piece) {
						moveValue = bearOffPrecedence; 
					}
					bearOffPrecedence--;
				}
			}
			if (colChoice > startPosition && colChoice < endPosition) { //If piece not in player's quadrant, higher precedence
    			if (gameBoard.pieceStore[colChoice].getPieceOrientation() == piece) {
    				moveValue = moveValue + 6;
    			}
    		}
			if (destChoice == 0 || destChoice == 25) { //If able to bear off, much higher precedence
				moveValue = moveValue + 6;
			}	
			else {
				if (gameBoard.pieceStore[destChoice].getPieceOrientation() == piece) { //if destination is own piece
					if (gameBoard.pieceStore[destChoice].getNumberOfPieces() == 1) { //and piece is vulnerable
						moveValue = moveValue + 4; //takes a high precedence
					}
					if (gameBoard.pieceStore[colChoice].getNumberOfPieces() == 1) { //if current column is vulnerable
						moveValue = moveValue + 4; //takes even higher precedence
					}
					else {
						moveValue = moveValue + 3; //otherwise still a solid move
					}
				}
				else if (gameBoard.pieceStore[destChoice].getPieceOrientation() == enemyPiece) { //if destination is enemy
					moveValue++; //fairly good move 
				}
				if (this.isEasyAI == false) {
					for (int b = 0; b < moveStore.size(); b++) { //looks for another piece that can go to same column in different move
						if (destChoice == moveStore.get(b).getDestCol() && colChoice != moveStore.get(b).getCurrentCol()) {
							if (gameBoard.pieceStore[destChoice].getPieceOrientation() == piece) {
								if (gameBoard.pieceStore[destChoice].getNumberOfPieces() == 1) { //if destination piece is vulnerable
									moveValue = moveValue + 4; //strong move
								}
								else {
									moveValue = moveValue + 3; //otherwise good move
								}
							}
							if (gameBoard.pieceStore[destChoice].getPieceOrientation() == enemyPiece) {
								moveValue = moveValue + 6; //very strong move
							}
							else {
								moveValue = moveValue + 3; //otherwise still fairly good move
							}
						}
					}
					if (gameBoard.pieceStore[colChoice].getNumberOfPieces() == 2) { //if both piece can be moved to same destination
						if (DieStore.size() > 1) {
							if (DieStore.get(0).getRollValue() == DieStore.get(1).getRollValue()) {
								if (gameBoard.pieceStore[destChoice].getPieceOrientation() == piece) {
									if (gameBoard.pieceStore[destChoice].getNumberOfPieces() == 1) {
										moveValue = moveValue + 4;
									}
									else {
										moveValue = moveValue + 3;
									}
								}
								if (gameBoard.pieceStore[destChoice].getPieceOrientation() == enemyPiece) {
									moveValue = moveValue + 6;
								}
								else {
									moveValue = moveValue + 5;
								}
							}
						}
					}
					else {
						moveValue++;
					}
				}
			}
			moveStore.get(i).setMoveValue(moveValue);
		}	
	}
}