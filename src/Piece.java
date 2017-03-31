
public class Piece {
	
	public static final int SOLIDS = 1;
    public static final int CIRCLES = 2;
    public static final int EMPTY = 0;

	private int pieceOrientation;
	private int numberOfPieces;
	
	public void setSolids() {
		this.pieceOrientation = SOLIDS;
	}
	
	public void setCircles() {
		this.pieceOrientation = CIRCLES;
	}
	
	public void setEmpty() {
		this.pieceOrientation = EMPTY;
	}
	
	public void setNumberOfPieces(int numPieces) {
		this.numberOfPieces = numPieces;
	}
	
	public void setPieceOrientation(int piece) {
		this.pieceOrientation = piece;
	}
	
	public int getNumberOfPieces() {
		return this.numberOfPieces;
	}
	
	public int getPieceOrientation() {
		return this.pieceOrientation;
	}
	
	public String printPiece() {
		String printPiece = null;
		switch (pieceOrientation) {
		case 0:
			printPiece = "   ";
			break;
		case 1:
			printPiece = " ● ";
			break;
		case 2:
			printPiece = " ○ ";
			break;
		default:
			printPiece = " " +Integer.toString(numberOfPieces) +" ";
		}
		return printPiece;
	}
}
