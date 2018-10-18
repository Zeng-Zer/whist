package whist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameEngine {
    private Trump masterTrump;
    private Trump roundTrump = Trump.NOTHING;

    private List<Player> players = new ArrayList<>();
    private List<Card> deck = new ArrayList<>();
    private Card strongestCard;

    GameEngine() {
        resetGame();
    }

    public void playRound() {
        Card cardPlayed;
        int turn = 0;

        for (Player player : players) {
            player.setHand(false);
            cardPlayed = player.play(roundTrump);
            if (turn == 0 || cardIsStronger(cardPlayed)) {
                roundTrump = cardPlayed.getTrump();
                strongestCard = cardPlayed;
                player.setHand(true);
            }
            turn++;
            System.out.println(player.getName() + " has played " + cardPlayed + " - decksize: " + player.getDeck().size());
        }
        System.out.println("Strongest: " + strongestCard);
    }

    public void resetRound() {
        int indexTrump = masterTrump.ordinal();

        if (masterTrump.equals(Trump.NOTHING)) {
            masterTrump = Trump.HEART;
        } else {
            masterTrump = Trump.values()[indexTrump + 1];
        }

        for (int i = 0; i < 4; ++i) {
            players.get(i).points = 0;
        }

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 13; ++j) {
                deck.add(new Card(Trump.values()[i], Value.values()[j]));
            }
        }

        Collections.shuffle(deck);
        distribute();
    }

    public void resetGame() {
	    masterTrump = Trump.NOTHING;
	    players.clear();
	    for (int i = 1; i < 5; ++i) {
	     players.add(new Player("p" + i));
	    }
	    resetRound();
    }

    private void distribute() {
	    for (int i = 0; i < 4; ++i) {
	     players.get(i).getDeck().addAll(deck.subList(i * 13, i * 13 + 13));
	    }
    }

    private boolean cardIsStronger(Card card) {
        return (card.getTrump().equals(roundTrump) &&
                card.getValue().ordinal() > strongestCard.getValue().ordinal()) ||
                (roundTrump != masterTrump && card.getTrump().equals(masterTrump));
    }

    public boolean isRunning () {
	    return (players.get(0).score + players.get(2).score == 7)
	     || (players.get(1).score + players.get(3).score == 7);
    }
}
