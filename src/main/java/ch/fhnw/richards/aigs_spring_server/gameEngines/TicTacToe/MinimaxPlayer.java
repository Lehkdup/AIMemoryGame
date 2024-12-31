package ch.fhnw.richards.aigs_spring_server.gameEngines.TicTacToe;

import java.awt.*;
import java.util.*;
/*
Kommentare sind manchmal in englisch und manchmal in deutsch kommt bei mir
darauf an wie mein kopf gerade tickt, entschuldige das hin und her
 */
public class MinimaxPlayer implements ttt_ai{

	private Player player1;
	private Player player2;

	//Represents the Board (fields where memorycards can be placed)
	private int[][] board = {
			{1,2,3,4},
			{1,2,3,4},
			{1,2,3,4},
			{1,2,3,4},
			{1,2,3,4},
	};

	//Represents the set of cards that exssits
	private int[] cards ={
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

	//Should save the gameboard and the exsisting cards in one list
	ArrayList<GameField> gameField = new ArrayList<>();


	public void prepGame(){
		shuffleArray(cards);
		int count = 0;
		for(int i = 0; i< board.length; i++){
			// System.out.println("Board row: "+i);
			for(int n = 0; n < board[i].length; n++){
				//saves the whole gamefield, with the placed cards and sets playerselection to none
				//it saves first the row and col where its placed in which the card is played.
				GameField g = new GameField(i, n, cards[count], Player.none);
				gameField.add(g);
				//to see the list uncomment the sys.out
				// System.out.println("board col: "+ count+" \n Saved Memory Object: "+ g.toString());
				count++;
			}
		}


	}

	//player definieren
	public void setPlayers(Player p1, Player p2){
		this.player1 = p1;
		this.player2 = p2;
	}

	//Zum Checken ob Players korrekt zugewiesen wurden
	public String getPlayers(){
		return "Player1: "+player1+"\n Player2: "+player2;
	}

	//Neue GPT funltion
	public void setPlayerAtPosition(Player p, int row, int col) {
		// Durchläuft die Liste
		for (int i = 0; i < gameField.size(); i++) {
			GameField field = gameField.get(i);
			// Check, ob Zeile und Spalte übereinstimmen
			if (field.row() == row && field.col() == col) {
				// Neues Record erzeugen, da records immutable sind
				GameField newField = new GameField(field.row(), field.col(), field.card(), p);
				// Altes Record in der ArrayList ersetzen
				gameField.set(i, newField);
				// Nach dem Ersetzen ggf. Schleife abbrechen
				break;
			}
		}
	}

	public String playerMove(Player p, int row, int col, int card){
		String message = "";

		//Prüfen ob Karte nicht bereits aufgedeckt ist
		if (!checkCard(row, col, card)) {
			message = "Wähle eine unaufgedeckte Karte";
			return message;
		}
		// Ist der Zug valide dann setze Player
		setPlayerAtPosition(p, row, col);

		//Hier kannst du noch Logik einbauen, ob ein Paar gefunden wurde usw.

		return message;
	}

	public boolean checkCard(int row, int col, int card){
		for(int i = 0; i < gameField.size(); i++){
			GameField g = gameField.get(i);
			if(g.row() == row && g.col() == col && g.card() == card){
				// Wenn diese Karte bereits jemandem gehört -> false
				if (g.player() != Player.none) {
					return false;
				} else {
					return true;
				}
			}
		}
		return false; // falls nicht gefunden
	}


/*
	//Soll wert player in gamefield zum aktivenplayers feld machen
	//Soll prüfen ob zug valide ist
	//soll rüfen ob ein paar gefunden wurde
	public String playerMove(Player p, int row, int col, int card){
		String message = "";
		//Checken ob Karte bereits aufgedeckt ist
		if (!checkCard(row, col, card)) message = "Wähle eine unaufgedeckte Karte";
		
		return message;
	}

	public boolean checkCard(int row, int col, int split){
		boolean result = false;
		for(int i = 0; i < gameField.size(); i++){
			GameField g = gameField.get(i);
			//was ist in g gespeichert
			System.out.println(g.toString());
			if(g.row() == row && i == split && g.player() == Player.none){
				return true;
			}
		}
		return result;
	}
*/



//AlterCode
// Helper methods

	//Shuffle Cards um zufällige reihenfolge zu bekommen für ein neues Spiel
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
}

