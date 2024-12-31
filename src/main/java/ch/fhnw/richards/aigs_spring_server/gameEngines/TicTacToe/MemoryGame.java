package ch.fhnw.richards.aigs_spring_server.gameEngines.TicTacToe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import ch.fhnw.richards.aigs_spring_server.game.Game;
import ch.fhnw.richards.aigs_spring_server.gameEngines.GameEngine;

public class MemoryGame implements GameEngine {

	private final int ROWS = 5;   // 5 Reihen
	private final int COLS = 4;   // 4 Spalten
	private final int PAIRS = 10; // Kartenwerte von 1..10

	/**
	 * Zustand einer Karte:
	 *   0 => verdeckt
	 *   1 => aufgedeckt
	 *   2 => entfernt (bereits gefunden)
	 */
	private int[][] cardStates;

	/**
	 * Eigentliche Werte der Karten: 1..PAIRS,
	 * die zufällig gemischt ins Array gelegt werden.
	 */
	private int[][] cardValues;

	// Verwaltung der Züge
	private int flipsInProgress = 0; // Wieviele Karten sind in diesem Zug schon aufgedeckt?
	private int firstRow = -1;
	private int firstCol = -1;
	private int currentPlayer = 1;

	// Punkte
	private int scorePlayer1 = 0;
	private int scorePlayer2 = 0;

	@Override
	public Game newGame(Game game) {
		// Board (5x4) anlegen brauchen es nicht aber wegen gameengine
		long[][] board = new long[ROWS][COLS];
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				board[r][c] = 0; // Dummy
			}
		}
		game.setBoard(board);

		// für board logik
		this.cardValues = new int[ROWS][COLS];
		this.cardStates = new int[ROWS][COLS];

		// Kartendeck erstellen und mischen (z.B. [1,1,2,2,...,10,10])
		List<Integer> deck = createShuffledDeck(PAIRS);

		// Werte in cardValues kopieren, alles anfangs verdeckt (state=0)
		int idx = 0;
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				cardValues[r][c] = deck.get(idx);
				cardStates[r][c] = 0;  // 0 = verdeckt
				idx++;
			}
		}

		// Zustände zurücksetzen
		flipsInProgress = 0;
		firstRow = -1;
		firstCol = -1;
		currentPlayer = 1;
		scorePlayer1 = 0;
		scorePlayer2 = 0;
		game.setResult(false); // Spiel noch nicht beendet

		return game;
	}

	/**
	 * move(Game, HashMap<String, String> move):
	 *   Erwartet:
	 *     move.get("row"), move.get("col"), move.get("player")
	 *   Ablauf:
	 *     - Pro Zug dürfen zwei Karten aufgedeckt werden.
	 *     - Nach der zweiten aufgedeckten Karte:
	 *       -> Falls Werte übereinstimmen: Karten entfernen (state=2),
	 *          Punkt für currentPlayer, und currentPlayer darf nochmals.
	 *       -> Falls nicht: Beide Karten wieder verdecken (state=0),
	 *          Zug wechselt zum nächsten Spieler.
	 */
	@Override
	public Game move(Game game, HashMap<String, String> move) {
		int row = Integer.parseInt(move.get("row"));
		int col = Integer.parseInt(move.get("col"));
		int requestedPlayer = Integer.parseInt(move.get("player"));

		// Safety: Ist der Zug vom richtigen Spieler?
		if (requestedPlayer != currentPlayer) {
			return game;
		}

		// Check ob col und row valide sind
		if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
			return game;
		}

		// Karte nur aufdecken, wenn sie noch verdeckt ist (state=0)
		if (cardStates[row][col] != 0) {
			// schon aufgedeckt oder entfernt -> ignorieren
			return game;
		}

		// --- Erster Klick ---
		if (flipsInProgress == 0) {
			// Karte aufdecken (state=1)
			cardStates[row][col] = 1;
			firstRow = row;
			firstCol = col;
			flipsInProgress = 1;

			// --- Zweiter Klick ---
		} else if (flipsInProgress == 1) {
			// Nicht dieselbe Karte?
			if (row == firstRow && col == firstCol) {
				// Gleicher Klick -> ignorieren
				return game;
			}

			// Aufdecken
			cardStates[row][col] = 1;
			flipsInProgress = 2;

			// Werte vergleichen
			int valFirst = cardValues[firstRow][firstCol];
			int valSecond = cardValues[row][col];
			if (valFirst == valSecond) {
				// -> Gleiche Werte, also entfernen (state=2)
				cardStates[firstRow][firstCol] = 2;
				cardStates[row][col] = 2;

				// Punkt für currentPlayer
				if (currentPlayer == 1) {
					scorePlayer1++;
				} else {
					scorePlayer2++;
				}

				// currentPlayer bleibt dran
			} else {
				// -> Nicht gleich -> beide wieder verdecken
				cardStates[firstRow][firstCol] = 0;
				cardStates[row][col] = 0;

				// Spieler wechseln
				currentPlayer = (currentPlayer == 1) ? 2 : 1;
			}

			// Auf den nächsten Zug warten
			flipsInProgress = 0;
			firstRow = -1;
			firstCol = -1;
		}

		// Prüfen, ob das Spiel fertig ist
		if (isGameFinished()) {
			game.setResult(true);
		} else {
			game.setResult(false);
		}

		return game;
	}

	/**
	 * Liefert true, wenn alle Karten entfernt (state=2) sind.
	 */
	private boolean isGameFinished() {
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				if (cardStates[r][c] != 2) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * getWinner: Wer hat gewonnen?
	 *   null => Spiel läuft noch
	 *   0    => Unentschieden
	 *   1    => Spieler 1 hat mehr Paare
	 *  -1    => Spieler 2 hat mehr Paare
	 */
	public Long getWinner() {
		if (!isGameFinished()) {
			return null; // noch nicht fertig
		}

		if (scorePlayer1 > scorePlayer2) {
			return 1L;
		} else if (scorePlayer2 > scorePlayer1) {
			return -1L;
		} else {
			return 0L;
		}
	}

	/**
	 * Erzeugt eine zufällig gemischte Liste der Paare: [1,1,2,2,...,numberOfPairs,numberOfPairs]
	 */
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
