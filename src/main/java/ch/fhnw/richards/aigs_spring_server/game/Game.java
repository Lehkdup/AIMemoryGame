package ch.fhnw.richards.aigs_spring_server.game;

import org.hibernate.annotations.CollectionId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.*;
import java.util.Map;

@Entity
@Table(name = "games")
public class Game {
	@Id
	@Column(name = "token")
	private String token;

	@Column(name = "gameType")
	GameType gameType;

	@Column(name = "difficulty")
	Long difficulty;

	@Column(name = "options")
	String options;

	@Column(name = "score", length=2048)
	int score;

	@Column(name = "scoreAI", length=2048)
	int scoreAI;

	@Column(name = "result")
	Boolean result; // true = gewonnen, false = verloren, null = laufend

	@Column(name = "aiMove", length=2048)
	int aiMove;

	@Column(name = "gameMessage")
	String gameMessage;


	@Column(name = "cardStates", length = 2048)
	private long[][] cardStates;


	@Column(name = "cardValues", length = 2048)
	private long[][] cardValues;

	public Game() {
	}

	public Game(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getGameMessage() {
		return gameMessage;
	}

	public void setGameMessage(String gameMessage) {
		this.gameMessage = gameMessage;
	}

	public GameType getGameType() {
		return gameType;
	}

	public void setGameType(GameType gameType) {
		this.gameType = gameType;
	}

	public Long getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(Long difficulty) {
		this.difficulty = difficulty;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getAiMove() {
		return aiMove;
	}

	public void setAiMove(int aiMove) {
		this.aiMove = aiMove;
	}

	public int getScoreAI() {
		return scoreAI;
	}

	public void setScoreAI(int scoreAI) {
		this.scoreAI = scoreAI;
	}

	public Boolean getResult() {
		return result;
	}

	public void setResult(Boolean result) {
		this.result = result;
	}

	public long[][] getCardStates() {
		return cardStates;
	}

	public void setCardStates(long[][] cardStates) {
		this.cardStates = cardStates;
	}

	public long[][] getCardValues() {
		return cardValues;
	}

	public void setCardValues(long[][] cardValues) {
		this.cardValues = cardValues;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Game)) return false;
		Game g = (Game) o;
		return (this.token.equals(g.token));
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.token);
	}

	@Override
	public String toString() {
		return "Game{" + "token=" + this.token + ", gameType= " + gameType + "}";
	}

	//für smartMemoryAI
	@Convert(converter = KnownCardsConverter.class)
	@Column(name = "known_cards", length = 10000) // z.B. 10k Zeichen als Obergrenze
	private ArrayList<Map<String, Integer>> knownCards;

	public ArrayList<Map<String, Integer>> getKnownCards() {
		if (this.knownCards == null) {
			this.knownCards = new ArrayList<>();
		}
		return this.knownCards;
	}

	public void setKnownCards(ArrayList<Map<String, Integer>> knownCards) {
		this.knownCards = knownCards;
	}

	// Oder eine Hilfsmethode fürs Hinzufügen einzelner Maps
	public void addKnownCard(Map<String, Integer> newCard) {
		if (this.knownCards == null) {
			this.knownCards = new ArrayList<>();
		}
		this.knownCards.add(newCard);
	}

	/*
	@Transient
	ArrayList<Map<String, Integer>> knownCards;


	public ArrayList<Map<String, Integer>> getKnownCards() {
		if (this.knownCards == null) {
			this.knownCards = new ArrayList<>();
			return knownCards;
		}else{
			return knownCards;
		}
	}

	public void setKnownCards(Map<String, Integer> newCard) {
		if (this.knownCards == null) {
			this.knownCards = new ArrayList<>();
		}
		this.knownCards.add(newCard);
	}

	 */

}
