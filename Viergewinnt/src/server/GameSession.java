package server;

import java.util.concurrent.ThreadLocalRandom;

public class GameSession {

	ClientThread player1;
	ClientThread player2;
	String gameName;
	GameField gameField;
	int playerTurn;

	public GameSession(ClientThread  player1) {
		this.player1 = player1;
		this.gameName = player1.client.name + "'s Game";
		this.gameField = new GameField();
		this.playerTurn = 0;
	}

	public boolean setStone(int collumn, Player player) {
		if(player.equals(this.player1.client)) {
			if (this.gameField.setStone(collumn, 1)) {
		//		playerTurn *= -1;
				return true;
			} else {
				return false;
			}
		} else {
			if (this.gameField.setStone(collumn, -1)) {
	//			playerTurn *= -1;
				return true;
			} else {
				return false;
			}
		}
	}
	
	public String gameFieldToString() {
		int[][] field = gameField.getField();
		StringBuilder sb = new StringBuilder();
		for(int row = 0; row <7; row++) {
			for(int col = 0; col<6; col++) {
				sb.append(field[row][col] + ",");
			}
		}
		return sb.toString().substring(0, sb.length() - 1);
	}
	
	public void randomStart() {
		if (ThreadLocalRandom.current().nextBoolean()) {
			this.playerTurn = 1; // Spieler 1 darf starten
		} else {
			this.playerTurn = -1; // Spieler 2 darf starten
		}
	}

}
