import java.util.*;

public class Board {

	public final int BOARDCOLS = 26;
	public Piece[] pieceStore = new Piece[BOARDCOLS];
	
	public void initializeBoard() {
		for (int j = 0; j < BOARDCOLS; j++) {
			if (j == 1 || j == 12 || j == 17 || j == 19) {
				Piece CirclePieces = new Piece();
				CirclePieces.setCircles();
				pieceStore[j] = CirclePieces;
				if (j == 1) {
					CirclePieces.setNumberOfPieces(2);
				}
				if (j == 12 || j == 19) {
					CirclePieces.setNumberOfPieces(5);
				}
				if (j == 17) {
					CirclePieces.setNumberOfPieces(3);
				}
			}
			else if (j == 6 || j == 8 || j == 13 || j == 24) {
				Piece SolidPieces = new Piece();
				SolidPieces.setSolids();
				pieceStore[j] = SolidPieces;
				if (j == 24) {
					SolidPieces.setNumberOfPieces(2);
				}
				if (j == 6 || j == 13) {
					SolidPieces.setNumberOfPieces(5);
				}
				if (j == 8) {
					SolidPieces.setNumberOfPieces(3);
				}
			}
			else {
				Piece EmptyPieces = new Piece();
				EmptyPieces.setEmpty();
				pieceStore[j] = EmptyPieces;
			}
		}
	}
	
	public int maxNumberPiecesInColumn() {
		int maxInCol = 5;
		ArrayList<Integer> maxPieceSorter = new ArrayList<Integer>();
		for (int j = 0; j < BOARDCOLS; j++) {
			maxPieceSorter.add(pieceStore[j].getNumberOfPieces());
		}
		Collections.sort(maxPieceSorter);
		Collections.reverse(maxPieceSorter);
		if (maxInCol < maxPieceSorter.get(0)) {
			maxInCol = maxPieceSorter.get(0);
		}
		return maxInCol;
	}
	
	public void displayBoard() {
		System.out.println();
        System.out.println("  12 11 10  9  8  7  bar  6  5  4  3  2  1  P1 bear");
        for (int x = 0; x < maxNumberPiecesInColumn(); x++) {
        	System.out.print(x +" ");
        	for (int j = BOARDCOLS/2-1; j >= 0; j--) {
        		if (pieceStore[j].getNumberOfPieces() > x) {
        			System.out.print(pieceStore[j].printPiece());
        		}
        		else {
        			System.out.print("   ");
        		}
        		if (j == 7) {
                    System.out.print("|   |");
                }
                if (j == 1) {
                	System.out.print("|");
                }
                if (j == 0) {
                    System.out.println();
                }
        	}
        }
        System.out.println();
        for (int x = maxNumberPiecesInColumn()-1; x >= 0; x--) {
        	System.out.print(x +" ");
        	for (int j = BOARDCOLS/2; j < BOARDCOLS; j++) {
        		if (pieceStore[j].getNumberOfPieces() > x) {
        			System.out.print(pieceStore[j].printPiece());
        		}
        		else {
        			System.out.print("   ");
        		}
        		if (j == 18) {
                	System.out.print("|   |");
                }
                if (j == 12 || j == 24) {
                    System.out.print("|");
                }
                if (j ==12) {
                    System.out.println();
                }
        	}
        	System.out.println();
        }
        System.out.println("  13 14 15 16 17 18  bar 19 20 21 22 23 24  P2 bear");
    	System.out.println();  
	}
	
	public Board() {
		initializeBoard();
		maxNumberPiecesInColumn();
	}
	
}
