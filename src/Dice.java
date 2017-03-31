public class Dice {
	private int rollValue;
	
	public int rollDice() {//Use this method in actual backgammon.
		Boolean validDiceRoll = false;
		
		rollValue = 0;
		
		while (validDiceRoll != true) {
			rollValue = (int)(Math.random()*100);
			
			while (rollValue > 10) {
				rollValue = rollValue - 10;			
			}
			
			if (rollValue <= 6 && rollValue != 0) {
				validDiceRoll = true;
			}
		}			

		return rollValue;
	}
	
	public int getRollValue() {
		return this.rollValue;
	}
	
	public void setRollValue(int dieValue) {
		this.rollValue = dieValue;
	}
	
	public Dice() {
		rollDice();
	}

}