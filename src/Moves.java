public class Moves {
	
	private int CurrentCol;	
	private int DestCol;
	private int moveValue;
	
	public void setCurrentCol(int i) {
		this.CurrentCol = i;
	}

	public void setDestCol(int potentialMove) {
		this.DestCol = potentialMove;
	}
	
	public void setMoveValue(int movePoints) {
		this.moveValue = movePoints;
	}
	
	public int moveValue() {
		return this.moveValue;
	}
	
	public int getCurrentCol() {
		return this.CurrentCol;
	}
	
	public int getDestCol() {
		return this.DestCol;
	}
	
	public Moves(int i, int potentialMove) {
		CurrentCol = i;
		DestCol = potentialMove;
	}
}
