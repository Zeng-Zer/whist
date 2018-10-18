package whist;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private List<Card> deck = new ArrayList<>();
    private String name;
    // team id
    private int team;
    // points in the round
    private int points = 0;
    private int index;

    public Player(String name, int id) {
        this.name = name;
        this.team = id % 2;
        this.index = id;
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

    public int getTeam() {
        return team;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getIndex() {
        return index;
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
        int i = (int) (Math.random() * deck.size());
        Card randomCard = deck.get(i);

        if (isTrumpInDeck(roundTrump)) {
            while (!randomCard.getTrump().equals(roundTrump)) {
                i = (int) (Math.random() * deck.size());
                randomCard = deck.get(i);
            }
        }
        return deck.remove(i);
    }
}
