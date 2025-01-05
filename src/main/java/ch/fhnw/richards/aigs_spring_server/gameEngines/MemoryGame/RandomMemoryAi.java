package ch.fhnw.richards.aigs_spring_server.gameEngines.MemoryGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import ch.fhnw.richards.aigs_spring_server.game.Game;

public class RandomMemoryAi implements MemoryAi {

	@Override
	public void makeMove(Game game, MemoryGame engine) {
		long[][] cardStates = game.getCardStates();
		// Sammle alle verdeckten Karten (state = 0)
		List<int[]> verdeckteKarten = new ArrayList<>();
		for (int r = 0; r < engine.getROWS(); r++) {
			for (int c = 0; c < engine.getCOLS(); c++) {
				if (cardStates[r][c] == 0) {
					verdeckteKarten.add(new int[]{r, c});
				}
			}
		}
		//TEST ob nur karten mit 0 gespeichert werden
		/*
		int count = 0;
		for(int [] verdeckteKarte: verdeckteKarten) {
			count++;
			for(int i : verdeckteKarte){
				System.out.print(i+", ");
			}
			System.out.println("count: " + count);
		}
		 */

		// Prüfen, ob es überhaupt noch mindestens 2 verdeckte Karten gibt
		if (verdeckteKarten.size() < 2) {
			return;
		}

		// Zufällig zwei verschiedene Karten aus der Liste auswählen
		Random rand = new Random();
		int index1 = rand.nextInt(verdeckteKarten.size());
		int[] karte1 = verdeckteKarten.remove(index1); // aus Liste entfernen, damit wir nicht nochmal dieselbe Karte ziehen
		int index2 = rand.nextInt(verdeckteKarten.size());
		int[] karte2 = verdeckteKarten.get(index2);    // zweite Karte

		// Parameter für move(...) vorbereiten
		HashMap<String, String> moveMap = new HashMap<>();
		moveMap.put("row1", String.valueOf(karte1[0]));
		moveMap.put("col1", String.valueOf(karte1[1]));
		moveMap.put("row2", String.valueOf(karte2[0]));
		moveMap.put("col2", String.valueOf(karte2[1]));
		moveMap.put("isAi", "true");

		game.setAiMove(0);

		// Move ausführen (Karten aufdecken)
		engine.move(game, moveMap);
	}
}
