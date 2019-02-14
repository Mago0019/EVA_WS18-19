package server;

public class GameField {
	int[][] field;
	int width;
	int hight;

	public GameField() {
		this.field = new int[7][6];
		this.width = 7;
		this.hight = 6;
	}

//	public GameField(int width, int hight) {
//		if (width < 16 && hight < 16) {
//			this.field = new int[width][hight];
//			this.width = width;
//			this.hight = hight;
//		} else {
//			this.field = new int[15][15];
//			this.width = 15;
//			this.hight = 15;
//		}
//
//	}

	public boolean setStone(int collumn, int player) {
		if(collumn < 1 || collumn > this.width) {
			return false;
		}
		
		collumn--;
		
		for (int i = this.hight - 1; i >= 0; i--) {
			if (this.field[collumn][i] == 0) {
				this.field[collumn][i] = player;
				return true;
			}

		}
		return false;
	}

	public int[][] getField() {
		return this.field;
	}

	public boolean checkWin(int collumn, int player) {
		int row = 0;
		int counter = 0;

		for (int i = 0; i < hight; i++) {
			if (this.field[collumn][i] == player) {
				row = i;
				break;
			}
		}
		
		System.out.println("CheckWin - col=" + collumn + " row="+row);
		
		// Senkrechte prüfen

		for (int i = this.hight - 1; i >= 0; i--) {

			if (this.field[collumn][i] == player) {
				counter++;
				if (counter == 4) {
					return true;
				}
			} else {
				counter = 0;
			}
		}
		System.out.println("CheckWin - Senkrechte geprüft counter="+counter);
		
		counter = 0;
		// Waagrechte überprüfen

		
		for (int i = 0; i < this.width; i++) {

			if (this.field[i][row] == player) {
				counter++;
				if (counter == 4) {
					return true;
				}
			} else {
				counter = 0;
			}
		}
		System.out.println("CheckWin - Waagrechte geprüft counter="+counter);
		
		counter = 0;
		// Diagonale von links-unten nach rechts-oben prüfen
		
		int startCol = collumn;
		int startRow = row;
		
		while(startCol <= 0 && startRow <= hight-1) {
			startCol--;
			startRow++;
		}
		
		System.out.println("CheckWin - found StartTile:["+startCol+"]["+startRow+"]");
		
		while(startCol <= width-1 && startRow <= 0) {
			if(field[startCol][startRow] == player) {
				counter++;
				if (counter == 4) {
					return true;
				}
			}
			startCol++;
			startRow--;			
		}
		System.out.println("CheckWin - erste Diagonale geprüft counter="+counter);
		counter = 0;
		// Diagonale von links-oben nach rechts-unten prüfen

		startCol = collumn;
		startRow = row;
		
		while(startCol >= 0 && startRow >= 0) { // gehe nach links oben
			startCol--;
			startRow--;
		}
		
		System.out.println("CheckWin - found StartTile:["+startCol+"]["+startRow+"]");
		
		while(startCol <= width-1 && startRow <= hight-1) {
			if(field[startCol][startRow] == player) {
				counter++;
				if (counter == 4) {
					return true;
				}
			}
			startCol++;
			startRow++;			
		}
		System.out.println("CheckWin - zweite Diagonale geprüft counter="+counter);

		return false;
	}

}
