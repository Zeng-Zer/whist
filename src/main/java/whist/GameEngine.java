package whist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameEngine {
    private Trump masterTrump;
    private Trump roundTrump;

    private List<Player> players = new ArrayList<>();
    private List<Card> deck = new ArrayList<>();

    GameEngine() {
        resetGame();
    }

    public void resetRound() {
        for (int i = 1; i < 5; ++i) {
            players.add(new Player("p" + i));
        }

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 13; ++j) {
                deck.add(new Card(Trump.values()[i] , Value.values()[j]));
            }
        }

        Collections.shuffle(deck);
        distribute();
    }

    public void resetGame() {
        players.clear();
        resetRound();
    }

    private void distribute() {
        for (int i = 0; i < 4; ++i) {
            players.get(i).getDeck().addAll(deck.subList(i * 13, i * 13 + 13));
        }
        for (Player player : players) {
            System.out.println();
            for (Card card : player.getDeck()) {
                System.out.println(card);
            }
        }
    }
}
