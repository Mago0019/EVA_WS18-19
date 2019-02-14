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

	public GameField(int width, int hight) {
		if (width < 16 && hight < 16) {
			this.field = new int[width][hight];
			this.width = width;
			this.hight = hight;
		} else {
			this.field = new int[15][15];
			this.width = 15;
			this.hight = 15;
		}

	}

	public boolean setStone(int collumn, int player) {
		for (int i = this.hight - 1; i >= 0; i--) {
			if (this.field[i][collumn] == 0) {
				this.field[i][collumn] = player;
				return true;
			}

		}
		return false;
	}

	public int[][] getField() {
		return this.field;
	}

	public boolean checkWin(int collumn, int player) {
		int row = hight - 1;
		int counter = 0;

		for (int i = this.hight - 1; i >= 0; i--) {
			if (this.field[i][collumn] == 0) {
				row = i + 1;
			}
		}

		// Senkrechte prüfen

		for (int i = this.hight - 1; i >= 0; i--) {

			if (this.field[i][collumn] == player) {
				counter++;
				if (counter == 4) {
					return true;
				}
			} else {
				counter = 0;
			}
		}

		// Waagrechte überprüfen

		for (int i = 0; i <= this.width; i++) {

			if (this.field[row][i] == player) {
				counter++;
				if (counter == 4) {
					return true;
				}
			} else {
				counter = 0;
			}
		}

		// Diagonale von links-unten nach rechts-oben prüfen

		if (collumn + row < hight) // sorgt dafür, das der Start nicht nach links raus läuft
		{
			for (int i = row + collumn; i >= 0; i--) {
				for (int j = 0; j <= this.width - 1; i++) {
					if (this.field[i][j] == player) {
						counter++;
						if (counter == 4) {
							return true;
						} else {
							counter = 0;
						}
					}
				}
			}
		} else // sorgt dafür dass der Start nicht nach unten raus läuft
		{
			for (int i = row + (this.hight - 1 - row); i >= 0; i--) {
				for (int j = collumn - (this.hight - 1 - row); j <= this.width - 1; i++) {
					if (this.field[i][j] == player) {
						counter++;
						if (counter == 4) {
							return true;
						} else {
							counter = 0;
						}
					}
				}
			}
		}

		// Diagonale von links-oben nach rechts-unten prüfen

		if (collumn - row >= 0) // sorgt dafür, das der Start nicht nach oben raus läuft
		{
			for (int i = 0; i <= this.hight - 1; i++) {
				for (int j = collumn - row; j <= this.width - 1; i++) {
					if (this.field[i][j] == player) {
						counter++;
						if (counter == 4) {
							return true;
						} else {
							counter = 0;
						}
					}
				}
			}
		} else // sorgt dafür dass der Start nicht nach links raus läuft
		{
			for (int i = row - collumn; i >= 0; i--) {
				for (int j = 0; j <= this.width - 1; i++) {
					if (this.field[i][j] == player) {
						counter++;
						if (counter == 4) {
							return true;
						} else {
							counter = 0;
						}
					}
				}
			}
		}

		return false;
	}

}
