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
        Player player;
        int i = whoHasTheHand();

        for (int turn = 0; turn < 4; ++turn) {
            player = players.get(i);
            player.setHand(false);
            cardPlayed = player.play(roundTrump);
            if (turn == 0 || cardIsStronger(cardPlayed)) {
                roundTrump = cardPlayed.getTrump();
                strongestCard = cardPlayed;
                for (Player p : players)
                 p.setHand(false);
                player.setHand(true);
            }
            i = (i + 1) % 4;
            System.out.println(player.getName() + " has played " + cardPlayed + " - decksize: " + player.getDeck().size());
        }
        players.get(whoHasTheHand()).points += 1;
        System.out.println(players.get(whoHasTheHand()).getName() + " won the round with " + strongestCard.getValue());
    }

    public void resetRound() {
        int indexTrump = masterTrump.ordinal();

        //Change MasterTrump following the round
        if (masterTrump.equals(Trump.NOTHING)) {
            masterTrump = Trump.HEART;
        } else {
            masterTrump = Trump.values()[indexTrump + 1];
        }

        for (int i = 0; i < 4; ++i) {
            players.get(i).points = 0;
        }

        //Create deck
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 13; ++j) {
                deck.add(new Card(Trump.values()[i], Value.values()[j]));
            }
        }
        //Set hand to a random player
        players.get((int) (Math.random() * 4)).setHand(true);
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
        //split deck
	    for (int i = 0; i < 4; ++i) {
	     players.get(i).getDeck().addAll(deck.subList(i * 13, i * 13 + 13));
	    }
    }

    private int whoHasTheHand() {
        //find the player who had to play
        for (int i = 0; i < players.size(); ++i) {
            if (players.get(i).gotHand()) {
                return i;
            }
        }
        return -1;
    }

    private boolean cardIsStronger(Card card) {
        return (card.getTrump().equals(roundTrump) &&
                card.getValue().ordinal() > strongestCard.getValue().ordinal()) ||
                (roundTrump != masterTrump && card.getTrump().equals(masterTrump));
    }

    public boolean idRoundWon () {
	    return (players.get(0).score + players.get(2).score == 7)
	     || (players.get(1).score + players.get(3).score == 7);
    }
}
