package whist;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private List<Card> deck = new ArrayList<>();
    private String name;

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

    public void play() {

    }
}
