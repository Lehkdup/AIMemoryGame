package ch.fhnw.richards.aigs_spring_server.gameEngines.MemoryGame;

import ch.fhnw.richards.aigs_spring_server.game.Game;

public interface MemoryAi {
	/**
	 * Die AI führt (mindestens) einen Zug aus.
	 * Je nach Umsetzung kann sie – falls sie ein Paar findet – gleich
	 * nochmal ziehen. Das passiert üblicherweise in einer Schleife, bis
	 * sie kein Paar mehr findet oder das Spiel vorbei ist.
	 *
	 * @param game   Das Game-Objekt mit States und Values
	 * @param engine Referenz auf das MemoryGame selbst, um z.B. Methoden aufzurufen
	 */
	void makeMove(Game game, MemoryGame engine);
}
