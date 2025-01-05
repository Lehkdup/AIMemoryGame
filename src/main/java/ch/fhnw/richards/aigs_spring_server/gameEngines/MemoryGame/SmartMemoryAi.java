package ch.fhnw.richards.aigs_spring_server.gameEngines.MemoryGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.fhnw.richards.aigs_spring_server.game.Game;

public class SmartMemoryAi implements MemoryAi {

	@Override
	public void makeMove(Game game, MemoryGame engine) {
		ArrayList<Map<String, Integer>> knownCards = game.getKnownCards();
		long[][] cardStates = game.getCardStates();
		// Sammle alle verdeckten Karten  = 0
		List<int[]> verdeckteKarten = new ArrayList<>();
		for (int r = 0; r < engine.getROWS(); r++) {
			for (int c = 0; c < engine.getCOLS(); c++) {
				if (cardStates[r][c] == 0) {
					verdeckteKarten.add(new int[]{r, c});
				}
			}
		}
		//TEST ob nur karten mit 0 gespeichert werden
		int count = 0;
		for(int [] verdeckteKarte: verdeckteKarten) {
			count++;
			for(int i : verdeckteKarte){
				//System.out.print(i+", ");
			}
			//System.out.println("count: " + count);
		}

		//Prüfen ob karten gespeichert werden
		/*
		for (int i = 0; i < knownCards.size(); i++) {
			Map<String, Integer> card = knownCards.get(i);
			// Werte direkt aus dem Map ziehen
			int row = card.get("row");
			int col = card.get("col");
			int value = card.get("value");

			System.out.println("Karte " + i + ":");
			System.out.println("  row = " + row);
			System.out.println("  col = " + col);
			System.out.println("  value = " + value);
			System.out.println("-----------------");
		}
		*/
		// Prüfen, ob es überhaupt noch mindestens 2 verdeckte Karten gibt
		if (verdeckteKarten.size() < 2) {
			return;
		}
		boolean smartMove = false;
		HashMap<String, String> moveMap = new HashMap<>();

		//löschen!
		boolean isVerdeckt = false;
		boolean rowValidate = false;
		boolean colValicate = false;
		Map<String, Integer> card2 = new HashMap<String, Integer>();
		Map<String, Integer> card = new HashMap<String, Integer>();

		//prüfen ob in knowncards bei gleiche values sind wenn ja diese zwei positionen aufdecken
		for (int i = 0; i < knownCards.size(); i++) {
			card = knownCards.get(i);
			// Werte direkt aus dem Map ziehen
			int value = card.get("value");
			for (int n = 0; n < knownCards.size(); n++) {
				card2 = knownCards.get(n);
				if(value == card2.get("value") && n != i){
					//System.out.println("AI findet Paar: " + value +" count i: "+ i +" count n: "+ n);
					moveMap.put("row1", String.valueOf(card2.get("row")));
					moveMap.put("col1", String.valueOf(card2.get("col")));
					moveMap.put("row2", String.valueOf(card.get("row")));
					moveMap.put("col2", String.valueOf(card.get("col")));
					moveMap.put("isAi", "true");
					//prüfen ob gefundenekarte unter verdeckte karten ist
					/*
					for(int [] verdeckteKarte: verdeckteKarten) {
						int r = -1;
						int c = -1;
						System.out.println("hier");
						for(int o = 0 ; o<verdeckteKarte.length; o++){
							if(o == 0){
								c = verdeckteKarte[o];
							}
							if(o == 1){
								r = verdeckteKarte[o];
							}
						}
						if(r == card.get("row") && c == card.get("col")){
							System.out.println("row und col ist nicht gefunden: " + " row: "+ r +" col: "+ c);
							smartMove = true;
						}
					}
					 */
					smartMove = true;
					break;
				}
			}
		}

		//wenn keine gleichen vorahnden
		// Zufällig zwei verschiedene Karten aus der Liste auswählen
		if(!smartMove) {
			Random rand = new Random();
			int index1 = rand.nextInt(verdeckteKarten.size());
			int[] karte1 = verdeckteKarten.remove(index1); // aus Liste entfernen, damit wir nicht nochmal dieselbe Karte ziehen
			int index2 = rand.nextInt(verdeckteKarten.size());
			int[] karte2 = verdeckteKarten.get(index2);    // zweite Karte

			// Parameter für move(...) vorbereiten
			moveMap.put("row1", String.valueOf(karte1[0]));
			moveMap.put("col1", String.valueOf(karte1[1]));
			moveMap.put("row2", String.valueOf(karte2[0]));
			moveMap.put("col2", String.valueOf(karte2[1]));
			moveMap.put("isAi", "true");
		}

		game.setAiMove(0);
		// Move ausführen (Karten aufdecken)
		engine.move(game, moveMap);
	}
}
