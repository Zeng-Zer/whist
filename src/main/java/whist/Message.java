package whist;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {
    private Command command;
    // SERVER
    private List<Card> playedCards;
    private Trump trump;
    private Trump masterTrump;
    private Player player;
    private List<Card> deck;
    // CLIENT
    private Card card;
    private int indexPlayer;
    private int whoHasPlayed;
    private boolean firstTime;
    private List<Integer> points;

    /**
     * Server Message constructor
     */
    public Message(Command command, List<Card> deck, Trump trump, Player player) {
        this.command = command;
        this.deck = deck;
        this.trump = trump;
        this.player = player;
    }

    public Message(Command command, List<Card> deck, int index, Trump masterTrump, boolean firstTime) {
        this.command = command;
        this.deck = deck;
        this.indexPlayer = index;
        this.masterTrump = masterTrump;
        this.firstTime = firstTime;
    }

    public Message(Command command, List<Card> playedCards, int whoHasPlayed, List<Integer> points, boolean firstTime) {
        this.command = command;
        this.playedCards = playedCards;
        this.whoHasPlayed = whoHasPlayed;
        this.firstTime = firstTime;
        this.points = points;
    }

    public Message(Command command) {
        this.command = command;
    }

    /**
     * Client Message constructor
     * @param card
     */
    public Message(Card card) {
        this.card = card;
    }

    public Command getCommand() {
        return command;
    }

    public List<Card> getPlayedCards() {
        return playedCards;
    }

    public List<Card> getDeck() {
        return deck;
    }

    public Trump getTrump() {
        return trump;
    }

    public Player getPlayer() {
        return player;
    }

    public Card getCard() {
        return card;
    }

    public int getIndexPlayer() {
        return indexPlayer;
    }

    public Trump getMasterTrump() {
        return masterTrump;
    }

    public int getWhoHasPlayed() {
        return whoHasPlayed;
    }

    public boolean isFirstTime() {
        return firstTime;
    }

    public List<Integer> getPoints() {
        return points;
    }
}
