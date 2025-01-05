package ch.fhnw.richards.aigs_spring_server.gameEngines.MemoryGame;

import java.util.*;

import ch.fhnw.richards.aigs_spring_server.game.Game;
import ch.fhnw.richards.aigs_spring_server.gameEngines.GameEngine;

public class MemoryGame implements GameEngine {

	private final int ROWS = 5;   // 5 Reihen
	private final int COLS = 4;   // 4 Spalten
	private final int PAIRS = 10; // Kartenwerte von 1..10

	// Verwaltung der Züge
	private int flipsInProgress = 0; // Wieviele Karten sind in diesem Zug schon aufgedeckt?
	private int firstRow = -1;
	private int firstCol = -1;
	private int currentPlayer = 1;

	// Punkte
	private int scorePlayer1 = 0;
	private int scorePlayer2 = 0;

	// AI-Referenzen
	private MemoryAi aiEasy = new RandomMemoryAi();
	private MemoryAi aiSmart = new SmartMemoryAi();

	// << GETTER für AI und Felder (damit die AI darauf zugreifen kann) >>
	public int getCurrentPlayer() {
		return currentPlayer;
	}
	public int getROWS() {
		return ROWS;
	}
	public int getCOLS() {
		return COLS;
	}
	public int getFlipsInProgress() {
		return flipsInProgress;
	}
	public int getFirstRow() {
		return firstRow;
	}
	public int getFirstCol() {
		return firstCol;
	}
	@Override
	public Game newGame(Game game) {
		// Arrays für Werte und Zustände erzeugen
		long[][] cardValues = new long[ROWS][COLS];
		long[][] cardStates = new long[ROWS][COLS];
		/*
		States legende:
		0= verdeckt
		1= aufgedeckt
		2= kartenpaar gefunden
		 */

		// Kartendeck erstellen und mischen
		List<Integer> deck = createShuffledDeck(PAIRS);

		// Werte in cardValues kopieren, alles anfangs verdeckt (0)
		int idx = 0;
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				cardValues[r][c] = deck.get(idx);
				cardStates[r][c] = 0;  // 0 = verdeckt
				idx++;
			}
		}

		// Im Game-Objekt hinterlegen
		game.setCardValues(cardValues);
		game.setCardStates(cardStates);

		// Zustände zurücksetzen
		flipsInProgress = 0;
		firstRow = -1;
		firstCol = -1;
		currentPlayer = 1;
		scorePlayer1 = 0;
		scorePlayer2 = 0;

		// Spiel noch nicht beendet
		game.setResult(false);
		game.setGameMessage("Spiel wurde erstellt");
		return game;
	}


	@Override
	public Game move(Game game, HashMap<String, String> move) {

		if (game.getResult()) return game;

	System.out.println("ZUG New");
		//Nachricht nur zurücksetzen wenn dieser von Spieler kommt
		// damit AI und spieler move in message enthalten ist
		if(move.get("isAi").equals("false")){
			game.setGameMessage("");
		}

		// Das aktuelle Karten-Layout aus dem Game-Objekt holen
		long[][] cardStates = game.getCardStates();
		long[][] cardValues = game.getCardValues();

		int row = Integer.parseInt(move.get("row1"));
		int col = Integer.parseInt(move.get("col1"));
		int row2 = Integer.parseInt(move.get("row2"));
		int col2 = Integer.parseInt(move.get("col2"));

		//System.out.println("NewMove\n" + "Position 1: " +row +" "+ col +" Karte: " + "\n position 2: " +row2 +" "+ col2 +" Karte: ");

        /*
        int requestedPlayer = Integer.parseInt(move.get("player"));
        if (requestedPlayer != currentPlayer) {
            // Falscher Spieler am Zug -> ignorieren oder Exception
            return game;
        }
        */

		// Check ob row und col valide sind
		if (row < 0 || row >= ROWS || col < 0 || col >= COLS ||
				row2 < 0 || row2 >= ROWS || col2 < 0 || col2 >= COLS) {
			//System.out.println("Ungültige col und row");
			game.setGameMessage("Ungültige col und row");
			return game;
		}

		// Karte nur aufdecken, wenn sie noch verdeckt ist (state=0)
		if (cardStates[row][col] != 0) {
			// schon aufgedeckt oder entfernt -> ignorieren
			game.setGameMessage("Karte bereits aufgedeckt oder entfernt");
			// System.out.println("Karte bereits aufgedeckt oder entfernt");
			return game;
		}

		//if (flipsInProgress == 0) {
			// Karte aufdecken (state=1)
			cardStates[row][col] = 1;
			firstRow = row;
			firstCol = col;
			flipsInProgress = 1;

		//} else if (flipsInProgress == 1) {
			// Nicht dieselbe Karte?
			if (row2 == firstRow && col2 == firstCol) {
				// Gleicher Klick -> ignorieren
				game.setGameMessage("es wurden zwei gleiche positionen gegeben");
				//System.out.println("es wurde zwei gleiche positionen gegeben");
				return game;
			}

			// Aufdecken
			cardStates[row2][col2] = 1;
			flipsInProgress = 2;

			ArrayList<Map<String, Integer>> knownCards = game.getKnownCards();
			// Werte vergleichen
			long valFirst = cardValues[firstRow][firstCol];
			long valSecond = cardValues[row2][col2];
			if (valFirst == valSecond) {
				//Gefundenes Kartenpaar aus Knowncards nehmen oder wenn nicht vorhanden ignorieren
				//rückwährt damit löschen von liste nicht zu fehlern führt
				for (int i = knownCards.size() - 1; i >= 0; i--) {
					int val = knownCards.get(i).get("value");
					if (val == valFirst) {
						System.out.println("Wert Entfernt: " + val);
						knownCards.remove(i);
					}
				}

				// -> Gleiche Werte, also entfernen (state=2)
				cardStates[firstRow][firstCol] = 2;
				cardStates[row2][col2] = 2;
				String temp = move.get("isAi");
				if(move.get("isAi").equals("false")){
					int score = game.getScore()+1;
					game.setScore(score);
					// System.out.println("log: karten waren gleich punkt player");
					game.setGameMessage("Paar gefunden von Player: " + valFirst);
				} else {
					int score = game.getScoreAI() + 1;
					game.setScoreAI(score);
					// System.out.println("log: karten waren gleich punkt AI");
					game.setGameMessage("AI hat ein Paar gefunden: " + valFirst);
				}

			} else {
				//SmartAI speicher füllen
				Map<String, Integer> knownCard1 = new HashMap<>();
				Map<String, Integer> knownCard2 = new HashMap<>();
				boolean card1Validate = false;
				boolean card2Validate = false;
				//überprüfen ob karte nicht schon gespeichert
				for(int i = 0; i < knownCards.size(); i++){
					Map<String, Integer> validate = knownCards.get(i);
					//kommt erste oder zweite karte in liste vor?
					if(validate.get("row") == row && validate.get("col") == col){
						card1Validate = true;
					}
					if(validate.get("row") == row2 && validate.get("col") == col2){
						card2Validate = true;
					}
				}
				//wenn karte nicht in liste dann speichern
				if(!card1Validate) {
					knownCard1.put("row", row);
					knownCard1.put("col", col);
					knownCard1.put("value", (int) valFirst);
					knownCards.add(knownCard1);
				}
				if(!card2Validate) {
					knownCard2.put("row", row2);
					knownCard2.put("col", col2);
					knownCard2.put("value", (int) valSecond);
					knownCards.add(knownCard2);
				}
				game.setKnownCards(knownCards);

				//cardstates zurücksetzen
				cardStates[firstRow][firstCol] = 0;
				cardStates[row2][col2] = 0;

				if(move.get("isAi").equals("false")){
					game.setGameMessage(game.getGameMessage()+"\n Keine gleichen paare gefunden.\n" +
							"Position Player: " +row +" "+ col +" Karte: "+ valFirst +
							"\n position Player: " +row2 +" "+ col2 +" Karte: "+ valSecond
					);
					//System.out.println("log: karten waren nicht gleich"+
					//		"Position Player: " +row +" "+ col +" Karte: "+ valFirst +
					//		"\n position Player: " +row2 +" "+ col2 +" Karte: "+ valSecond);
				} else {
					game.setGameMessage(game.getGameMessage()+"\n Keine gleichen paare gefunden.\n" +
							"Position AI: " +row +" "+ col +" Karte: "+ valFirst +
							"\n position AI: " +row2 +" "+ col2 +" Karte: "+ valSecond
					);
					//System.out.println("log: karten waren nicht gleich"+
					//		"Position AI: " +row +" "+ col +" Karte: "+ valFirst +
					//		"\n position AI: " +row2 +" "+ col2 +" Karte: "+ valSecond
					//);
				}


				// Spieler wechseln
				//currentPlayer = (currentPlayer == 1) ? 2 : 1;
			}

			// Auf den nächsten Zug warten
			//flipsInProgress = 0;
			//firstRow = -1;
			//firstCol = -1;
		//}

		// Prüfen, ob das Spiel fertig ist
		if (isGameFinished(game)) {
			if(game.getScoreAI() < game.getScore()){
				game.setGameMessage(game.getGameMessage() +"\nGame ist zu ende du hast gewonnen mit " + game.getScore()+" Punkten");
			}else{
				game.setGameMessage("Game ist zu ende AI hat gewonnen mit " + game.getScoreAI()+" Punkten");
			}
			game.setResult(true);
		}
		//System.out.println("VOR ai aufruf");
		if (!game.getResult() && game.getAiMove()== 1) {
			//System.out.println("IM ai aufruf");
			MemoryAi ai = (game.getDifficulty() <= 1) ? new RandomMemoryAi() : new SmartMemoryAi();
			ai.makeMove(game, this);
		}
		return game;
	}


	private boolean isGameFinished(Game game) {
        return game.getScore() == 10 || game.getScoreAI() == 10;
	}




	private List<Integer> createShuffledDeck(int numberOfPairs) {
		List<Integer> deck = new ArrayList<>();
		for (int i = 1; i <= numberOfPairs; i++) {
			deck.add(i);
			deck.add(i);
		}
		Collections.shuffle(deck);
		return deck;
	}
}
