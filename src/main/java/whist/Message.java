package whist;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {
    private Command command;
    // SERVER
    private List<Card> playedCards;
    private Trump trump;
    private Player player;
    private List<Card> deck;
    // CLIENT
    private Card card;
    private int indexPlayer;
    private int whosHand;

    /**
     * Server Message constructor
     */
    public Message(Command command, List<Card> playedCards, List<Card> deck, Trump trump, Player player) {
        this.command = command;
        this.playedCards = playedCards;
        this.deck = deck;
        this.trump = trump;
        this.player = player;
      //  this.whosHand = whosHand;
    }

    public Message(Command command, List<Card> deck, int index) {
        this.command = command;
        this.deck = deck;
        this.indexPlayer = index;
    }

    public Message(Command command, int whosHand) {
        this.command = command;
        this.whosHand = whosHand;
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

    public int getWhosHand() {
        return whosHand;
    }
}
