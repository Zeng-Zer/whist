package whist;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable {
    private transient Socket socket;
    private transient ObjectInputStream is;
    private transient ObjectOutputStream os;
    private List<Card> deck = new ArrayList<>();
    private String name;
    // team id
    private int team;
    // points in the round
    private int points = 0;
    private int index;

    // Server constructor
    public Player(Socket socket, String name, int id) throws IOException {
        this.socket = socket;
        this.name = name;
        this.team = id % 2;
        this.index = id;
        this.os = new ObjectOutputStream(socket.getOutputStream());
        this.is = new ObjectInputStream(socket.getInputStream());
    }

    // Client constructor
    public Player(Socket socket) throws IOException {
        this.socket = socket;
        this.os = new ObjectOutputStream(socket.getOutputStream());
        this.is = new ObjectInputStream(socket.getInputStream());
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

    // Client calls this function
    public void run() throws IOException, ClassNotFoundException {
        while (true) {
            System.out.println("Waiting for command");
            Message message = (Message) is.readObject();

            // Read command
            switch (message.getCommand()) {
                case PLAY:
                    // Initialize variables
                    deck = message.getDeck();
                    name = message.getPlayer().name;
                    team = message.getPlayer().team;
                    points = message.getPlayer().points;
                    Trump roundTrump = message.getTrump();

                    // random ia
                    // TODO PLACE USER INPUT HERE
                    int i = (int) (Math.random() * deck.size());
                    Card randomCard = deck.get(i);

                    if (isTrumpInDeck(roundTrump)) {
                        while (!randomCard.getTrump().equals(roundTrump)) {
                            i = (int) (Math.random() * deck.size());
                            randomCard = deck.get(i);
                        }
                    }
                    Card card = deck.remove(i);

                    System.out.println("Player " + name + " played: " + card.toString() + ", card left: " + deck.size());
                    Message response = new Message(card);
                    os.writeObject(response);

                    break;
                case QUIT:
                    System.out.println("Quit");
                    socket.close();
                    System.exit(0);
                default:
                    break;
            }
        }
    }

    // Server calls this function
    public Card play(Trump roundTrump, List<Card> playedCards) throws IOException, ClassNotFoundException {
        // copy new deck
        List<Card> newDeck = new ArrayList<>(deck);

        // send play message
        Message message = new Message(Command.PLAY, playedCards, newDeck, roundTrump, this);
        os.writeObject(message);

        // read response
        Message response = (Message) is.readObject();
        Card cardPlayed = response.getCard();
        deck.remove(cardPlayed);
        return cardPlayed;
    }

    public void quit() throws IOException {
        Message message = new Message(Command.QUIT);
        os.writeObject(message);
    }
}
