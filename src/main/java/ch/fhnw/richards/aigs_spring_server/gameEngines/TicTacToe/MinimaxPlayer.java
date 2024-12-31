package ch.fhnw.richards.aigs_spring_server.gameEngines.TicTacToe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
Kommentare sind manchmal in englisch und manchmal in deutsch kommt bei mir
darauf an wie mein kopf gerade tickt, entschuldige das hin und her
 */
public class MinimaxPlayer implements ttt_ai{

	private Player player1;
	private Player player2;
	private int scoreP1;
	private int scoreP2;

	// Board-Layout
	private int[][] board = {
			{1,2,3,4},
			{1,2,3,4},
			{1,2,3,4},
			{1,2,3,4},
			{1,2,3,4},
	};

	// Kartensatz
	private int[] cards = {
			1,1,
			2,2,
			3,3,
			4,4,
			5,5,
			6,6,
			7,7,
			8,8,
			9,9,
			10,10
	};

	// Gesamtes Spielfeld, wird in prepGame() gefüllt
	ArrayList<GameField> gameField = new ArrayList<>();

	//Hält pro Spieler eine Liste der aufgedeckten Karten (Werte)
	private Map<Player, List<Integer>> revealedCardsMap = new HashMap<>();


	public MinimaxPlayer() {
		// Beim Erzeugen der KI kannst du dieses Mapping initialisieren.
		// Wichtig: Player.none bekommt hier leere Liste, aber eigentlich interessiert uns nur p1 und p2.
		revealedCardsMap.put(Player.none,  new ArrayList<>());
		revealedCardsMap.put(Player.p1, new ArrayList<>());
		revealedCardsMap.put(Player.p2, new ArrayList<>());
	}

	// Mischt das Array "cards" einmal durch und erstellt das vollständige Spielfeld
	public void prepGame() {
		shuffleArray(cards);
		int count = 0;
		for(int i = 0; i < board.length; i++){
			for(int n = 0; n < board[i].length; n++){
				// Initial Player none, weil noch nicht aufgedeckt
				GameField g = new GameField(i, n, cards[count], Player.none);
				gameField.add(g);
				count++;
			}
		}
	}

	// Zufalls-Shuffle für das Kartenset
	private void shuffleArray(int[] arr) {
		// Nur ein sehr einfaches Beispiel
		for (int i = arr.length - 1; i > 0; i--) {
			int j = (int) (Math.random() * (i+1));
			int temp = arr[i];
			arr[i] = arr[j];
			arr[j] = temp;
		}
	}

	// Spieler werden hier gesetzt
	public void setPlayers(Player p1, Player p2) {
		this.player1 = p1;
		this.player2 = p2;
	}

	// Zum Checken, ob Players korrekt zugewiesen wurden
	public String getPlayers() {
		return "Player1: " + player1 + "\nPlayer2: " + player2;
	}

	// Setzt einen Player an eine Position (row,col) diese Karte gehört p1 oder p2
	public void setPlayerAtPosition(Player p, int row, int col) {
		for (int i = 0; i < gameField.size(); i++) {
			GameField field = gameField.get(i);
			if (field.row() == row && field.col() == col) {
				// Neues Record anlegen weil records final sind
				GameField newField = new GameField(field.row(), field.col(), field.card(), p);
				// Altes durch neues ersetzen
				gameField.set(i, newField);
				break;
			}
		}
	}

	/**
	 *  Spieler deckt eine Karte auf (row, col).
	 *  Falls die Karte noch nicht aufgedeckt wurde, wird der Player gesetzt und
	 *  die Karte in die 'revealedCardsMap' dieses Spielers eingetragen.
	 *  Anschließend prüfen wir, ob ein Paar gefunden wurde.
	 */
	public String playerMove(Player p, int row, int col, int card){
		String message = "";

		// Prüfen, ob diese Karte noch unaufgedeckt ist
		if (!checkCard(row, col, card)) {
			message = "Wähle eine unaufgedeckte Karte!";
			return message;
		}

		// Karte "gehört" nun dem Spieler p
		setPlayerAtPosition(p, row, col);

		// In unsere "Aufgedeckt"-Liste eintragen
		revealedCardsMap.get(p).add(card);

		// Überprüfen, ob dieser Spieler mit dieser Karte ein Paar gefunden hat
		if (checkIfPairFound(p, card)) {
			message = "Glückwunsch! " + p + " hat ein Paar gefunden: " + card;

			// Punkte vergeben
			if (p == Player.p1) {
				scoreP1++;
			} else if (p == Player.p2) {
				scoreP2++;
			}
		}

		if (allCardsRevealed()) {
			message += "\nAlle Karten sind aufgedeckt! Spiel ist beendet.\n";
			message += "Endstand: \n"
					+ "P1: " + scoreP1 + " Punkte\n"
					+ "P2: " + scoreP2 + " Punkte\n";

			// Hier könntest du noch weitere Logik einbauen (z. B. wer hat gewonnen?),
			// das Spiel beenden oder ein Ergebnis zurückgeben.
		}

		return message;
	}

	/**
	 * Prüft, ob alle Karten aufgedeckt wurden.
	 */
	private boolean allCardsRevealed() {
		for (GameField g : gameField) {
			if (g.player() == Player.none) {
				// Mindestens eine Karte ist noch nicht aufgedeckt -> Spiel noch nicht fertig
				return false;
			}
		}
		return true;
	}

	/**
	 * checkCard: prüft, ob die Karte an (row,col) überhaupt noch aufgedeckt werden darf.
	 * Sie darf nicht schon einem Player gehören.
	 */
	public boolean checkCard(int row, int col, int card){
		for(GameField g : gameField){
			if(g.row() == row && g.col() == col && g.card() == card){
				// Ist bereits durch Player != none besetzt?
				if (g.player() != Player.none) {
					return false;
				} else {
					return true;
				}
			}
		}
		return false; // falls nicht gefunden
	}

	/**
	 * checkIfPairFound: überprüft, ob im revealedCardsMap des Spielers p
	 * jetzt zweimal derselbe Kartenwert liegt.
	 * In diesem Beispiel wird einfach geschaut, ob der übergebene 'card'-Wert
	 * mindestens zweimal in der Liste vorkommt.
	 */
	private boolean checkIfPairFound(Player p, int cardValue) {
		// Anzahl, wie oft cardValue in der Liste von p vorkommt
		int freq = 0;
		for (int val : revealedCardsMap.get(p)) {
			if (val == cardValue) freq++;
		}
		return (freq >= 2);
	}



//AlterCode
// Helper methods

	//Shuffle Cards um zufällige reihenfolge zu bekommen für ein neues Spiel
	/*
	private void shuffleArray(int[] array) {
		Random random = new Random();
		for (int i = array.length - 1; i > 0; i--) {
			// Zufälligen Index auswählen
			int j = random.nextInt(i + 1);

			// Elemente tauschen
			int temp = array[i];
			array[i] = array[j];
			array[j] = temp;
		}
	}
	//Testen ob karten gemischt sind
	public void printShuffledCards(){
		for (int card : cards) {
			System.out.print(card + " ");
		}
	}


	private long myPiece = -1; // Which player are we?
	
	private enum Evaluation { LOSS, DRAW, WIN };

	private class MoveEval {
		Move move;
		Evaluation evaluation;

		public MoveEval(Move move, Evaluation evaluation) {
			this.move = move;
			this.evaluation = evaluation;
		}
	}

	@Override
	public void makeMove(long[][] board) {
		Move move = findMove(board, myPiece).move;
		board[move.getRow()][move.getCol()] = myPiece;
	}

	private MoveEval findMove(long[][] board, long toMove) {
		Move bestMove = null;
		Evaluation bestEval = Evaluation.LOSS;

		Long result = TicTacToe.getWinner(board);
		if (result != null) { // game is over
			if (result == toMove) return new MoveEval(null, Evaluation.WIN);
			else if (result == (0 - toMove)) return new MoveEval(null, Evaluation.LOSS);
			else return new MoveEval(null, Evaluation.DRAW);
		} else {
			// Find best move for piece toMove
			for (int col = 0; col < 3; col++) {
				for (int row = 0; row < 3; row++) {
					if (board[row][col] == 0) {
						// Possible move found
						long[][] possBoard = copyBoard(board);
						possBoard[row][col] = toMove;
						MoveEval tempMR = findMove(possBoard, (toMove == 1) ? -1: 1);
						Evaluation possEval = invertResult(tempMR.evaluation); // Their win is our loss, etc.
						if (possEval.ordinal() > bestEval.ordinal()) {
							bestEval = possEval;
							bestMove = new Move(toMove, col, row);
						}
					}
				}
			}
			return new MoveEval(bestMove, bestEval); 
		}
	}
	
	private Evaluation invertResult(Evaluation in) {
		if (in == Evaluation.DRAW) return Evaluation.DRAW;
		else if (in == Evaluation.WIN) return Evaluation.LOSS;
		return Evaluation.WIN;
	}
	
	private long[][] copyBoard(long[][] board) {
		long[][] newboard = new long[board.length][];
		for (int i = 0; i < board.length; i++) {
			newboard[i] = new long[board[i].length];
			for (int j = 0; j < board[i].length; j++) {
				newboard[i][j] = board[i][j];
			}
		}
		return newboard;
	}
	*/

}

