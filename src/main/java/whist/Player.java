package whist;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private List<Card> deck = new ArrayList<>();
    private String name;
    private boolean hand = false;

    public int points = 0;
    public int score = 0;

    public Player(String name) {
        this.name = name;
    }

    public List<Card> getDeck() {
        return deck;
    }

    public void setDeck(List<Card> deck) {
        this.deck = deck;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private boolean isTrumpInDeck(Trump trump) {
        for (Card card : deck) {
            if (card.getTrump() == trump) {
                return true;
            }
        }
        return false;
    }

    public Card play(Trump roundTrump) {
        int i = (int) (Math.random() * 13);
        Card randomCard = deck.get(i);

        if (isTrumpInDeck(roundTrump)) {
            while (!randomCard.getTrump().equals(roundTrump)) {
                i = (int) (Math.random() * 13);
                randomCard = deck.get(i);
            }
        }
        return deck.remove(i);
    }

    public boolean gotHand() {
        return hand;
    }

    public void setHand(boolean hand) {
        this.hand = hand;
    }
}
