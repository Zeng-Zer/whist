package whist;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {
    private Command command;
    // SERVER
    private Card[] playedCards;
    private Trump trump;
    private Trump masterTrump;
    private Player player;
    private List<Card> deck;
    // CLIENT
    private Card card;
    private int indexPlayer;
    private int whosHand;
    private int whoHasPlayed;

    /**
     * Server Message constructor
     */
    public Message(Command command, List<Card> deck, Trump trump, Player player) {
        this.command = command;
        this.deck = deck;
        this.trump = trump;
        this.player = player;
    }

    public Message(Command command, List<Card> deck, int index, Trump masterTrump, int whosHand) {
        this.command = command;
        this.deck = deck;
        this.indexPlayer = index;
        this.masterTrump = masterTrump;
        this.whosHand = whosHand;
    }

    public Message(Command command, Card[] playedCards, int whoHasPlayed) {
        this.command = command;
        this.playedCards = playedCards;
        System.out.println("FROM MESSAGE: ");
        for (Card c : this.playedCards) {
            System.out.println(c);
        }
        this.whoHasPlayed = whoHasPlayed;
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

    public Card[] getPlayedCards() {
        System.out.println("FROM GETTER: ");
        for (Card c : this.playedCards) {
            System.out.println(c);
        }
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

    public int getWhosHand() {
        return whosHand;
    }
}
