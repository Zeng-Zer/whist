package whist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameEngine {
    private Trump masterTrump;
    private Trump roundTrump = Trump.NOTHING;

    private List<Player> players = new ArrayList<>();
    private List<Card> deck = new ArrayList<>();
    private Card strongestCard;

    private Player topPlayer = null;
    private Map<Integer, Integer> teamScore = new HashMap<>();

    GameEngine() {
        resetGame();
    }

    public void play() {
        while (teamScore.get(0) < 7 && teamScore.get(1) < 7) {
            playHand();
            resetHand();
        }
        System.out.println("Final score Team 0: " + teamScore.get(0));
        System.out.println("Final score Team 1: " + teamScore.get(1));
    }

    private void playHand() {
        while (!topPlayer.getDeck().isEmpty()) {
            playRound();
        }

        // Add score difference to winning team
        int actualScore0 = players.get(0).getPoints() + players.get(2).getPoints();
        int actualScore1 = players.get(1).getPoints() + players.get(3).getPoints();
        System.out.println("Team 0 points: " + actualScore0);
        System.out.println("Team 1 points: " + actualScore1);

        int oldScore0 = teamScore.get(0);
        int oldScore1 = teamScore.get(1);

        teamScore.put(0, oldScore0 + Math.max(actualScore0 - 6, 0));
        teamScore.put(1, oldScore1 + Math.max(actualScore1 - 6, 0));

        System.out.println("Team 0: " + teamScore.get(0));
        System.out.println("Team 1: " + teamScore.get(1));
    }

    private void playRound() {
        Player player;
        int i = topPlayer.getIndex();

        for (int turn = 0; turn < 4; ++turn) {
            player = players.get(i);
            Card cardPlayed = player.play(roundTrump);

            // Change top player
            if (turn == 0 || cardIsStronger(cardPlayed)) {
                roundTrump = cardPlayed.getTrump();
                strongestCard = cardPlayed;
                topPlayer = player;
            }

            i = (i + 1) % 4;

            System.out.println(player.getName() + ", " + player.getTeam() + " has played " + cardPlayed + " - decksize: " + player.getDeck().size());
        }
        System.out.println(topPlayer.getName() + ", " + topPlayer.getTeam() + " won the round with " + strongestCard.getValue());

        topPlayer.setPoints(topPlayer.getPoints() + 1);
    }

    public void resetGame() {
        masterTrump = Trump.NOTHING;

        // Create player
        players.clear();
        for (int i = 0; i < 4; ++i) {
            players.add(new Player("p" + (i + 1), i));
        }

        // Reset score
        teamScore.clear();
        teamScore.put(0, 0);
        teamScore.put(1, 0);

        // Create deck
        deck.clear();
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 13; ++j) {
                deck.add(new Card(Trump.values()[i], Value.values()[j]));
            }
        }

        resetHand();
    }

    private void resetHand() {
        int indexTrump = masterTrump.ordinal();

        // Change MasterTrump following the round
        if (masterTrump.equals(Trump.NOTHING)) {
            masterTrump = Trump.HEART;
        } else {
            masterTrump = Trump.values()[indexTrump + 1];
        }

        // Reset points
        for (Player player : players) {
            player.setPoints(0);
        }

        // Set hand to a random player
        topPlayer = players.get((int) (Math.random() * 4));

        // Split deck
        Collections.shuffle(deck);
        for (int i = 0; i < 4; ++i) {
            players.get(i).getDeck().addAll(deck.subList(i * 13, i * 13 + 13));
        }
    }

    private boolean cardIsStronger(Card card) {
        return (card.getTrump().equals(roundTrump) &&
                card.getValue().ordinal() > strongestCard.getValue().ordinal()) ||
                (roundTrump != masterTrump && card.getTrump().equals(masterTrump));
    }
}
