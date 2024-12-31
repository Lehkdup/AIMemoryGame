package ch.fhnw.richards.aigs_spring_server.gameEngines.TicTacToe;

public class MainMemory {
    public static void main(String[] args) {
        MinimaxPlayer m = new MinimaxPlayer();
        m.prepGame();
        m.printShuffledCards();

        m.setPlayers(Player.P1, Player.Com);
        System.out.println(m.getPlayers());

    }
}
