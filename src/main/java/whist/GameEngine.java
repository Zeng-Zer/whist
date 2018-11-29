package whist;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameEngine extends Thread {
    private Trump masterTrump;
    private Trump roundTrump = Trump.NOTHING;

    private List<Player> players = new ArrayList<>();
    private List<Card> deck = new ArrayList<>();
    private Card strongestCard;

    private Player topPlayer = null;
    private Map<Integer, Integer> teamScore = new HashMap<>();

    public Map<Trump, Map<Value, String>> mainDeck;

    GameEngine(ServerSocket listener, Map<Trump, Map<Value, String>> deck) throws IOException {
        connectPlayers(listener);
        mainDeck = deck;
        resetGame();
    }

    public void run() {
        while (teamScore.get(0) < 7 && teamScore.get(1) < 7) {
            try {
                playHand();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                resetHand();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Final score Team 0: " + teamScore.get(0));
        System.out.println("Final score Team 1: " + teamScore.get(1));
        for (Player player : players) {
            try {
                player.quit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void playHand() throws Exception {
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

    private void playRound() throws Exception {
        Player player;
        int i = topPlayer.getIndex();
        Card[] playedCards = {null, null, null, null};

        System.out.println(topPlayer.getName() + " has to play");

        for (int turn = 0; turn < 4; turn++) {
            player = players.get(i);
            Card cardPlayed = player.play(roundTrump);
            playedCards[i] = cardPlayed;

            // Change top player
            if (turn == 0 || cardIsStronger(cardPlayed)) {
                roundTrump = cardPlayed.getTrump();
                strongestCard = cardPlayed;
                topPlayer = player;
            }

            for (Card c : playedCards)
                System.out.println(c);

            sendPlayedCard(playedCards, i);
            i = (i + 1) % 4;

            System.out.println("\t" + player.getName() + "(" + player.getTeam() + ") has played " + cardPlayed.toString() + " - decksize: " + player.getDeck().size());
        }
        roundTrump = Trump.NOTHING;
        System.out.println(topPlayer.getName() + "(" + topPlayer.getTeam() + ") won the round with " + strongestCard.toString() + "\n");
        topPlayer.setPoints(topPlayer.getPoints() + 1);
    }

    private void connectPlayers(ServerSocket listener) throws IOException {
        players.clear();
        for (int i = 0; i < 4; ++i) {
            System.out.println("Waiting for player to connect");
            players.add(new Player(listener.accept(), "player: " + (i + 1), i));
            System.out.println("Player " + (i + 1) + " connected");
            //players.get(i).connected(players.get(i).getDeck(), i, masterTrump);
        }
    }

    public void resetGame() {
        masterTrump = Trump.NOTHING;

        // Reset score
        teamScore.clear();
        teamScore.put(0, 0);
        teamScore.put(1, 0);

        // Create deck
        deck.clear();
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 13; ++j) {
                String resource = mainDeck.get(Trump.values()[i]).get(Value.values()[j]);
                deck.add(new Card(Trump.values()[i], Value.values()[j], resource));
            }
        }

        try {
            resetHand();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetHand() throws IOException {
        int indexTrump = masterTrump.ordinal();

        // Change MasterTrump following the round
        if (masterTrump.equals(Trump.NOTHING)) {
            masterTrump = Trump.HEART;
        } else {
            masterTrump = Trump.values()[indexTrump + 1];
        }

        System.out.println("Master trump is: " + masterTrump);

        // Reset points
        for (Player player : players) {
            player.setPoints(0);
        }

        // Set hand to a random player
        int whosHand = (int) (Math.random() * 4);
        topPlayer = players.get(whosHand);

        // Split deck
        Collections.shuffle(deck);
        for (int i = 0; i < 4; ++i) {
            players.get(i).getDeck().addAll(deck.subList(i * 13, i * 13 + 13));
            players.get(i).connected(players.get(i).getDeck(), i, masterTrump, whosHand);
        }
    }

    private void sendPlayedCard(Card[] playedCards, int whoHasPlayed) throws ClassNotFoundException {
        for (Player p : players) {
            try {
                p.sendPlayedCard(playedCards, whoHasPlayed);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean cardIsStronger(Card card) {
        return (card.getTrump().equals(roundTrump) &&
                card.getValue().ordinal() > strongestCard.getValue().ordinal()) ||
                (roundTrump != masterTrump && card.getTrump().equals(masterTrump));
    }
    
}
