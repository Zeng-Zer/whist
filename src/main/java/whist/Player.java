package whist;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

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
    private Trump roundTrump;
    private Trump masterTrump;
    private boolean hasToPlay = false;
    public int[] othersCards = {13, 13, 13, 13};
    public List<Integer> scores = Arrays.asList(new Integer[]{0, 0, 0, 0, 0, 0, 0, 0});
    public List<Card> playedCard = Arrays.asList(new Card[]{null, null, null, null});

    JFrame f;
    GameInterface mainPanel;

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
    public Player(Socket socket, String name) throws IOException {
        this.socket = socket;
        this.name = name;
        this.os = new ObjectOutputStream(socket.getOutputStream());
        this.is = new ObjectInputStream(socket.getInputStream());
    }

    public void createGUI() {
        this.f =  Client.f;
        f.setTitle(name);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setPreferredSize(new Dimension(1200, 900));
        mainPanel = new GameInterface(this);

        f.getContentPane().removeAll();
        f.setContentPane(mainPanel);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        f.revalidate();
        f.repaint();
    }

    public boolean isTrumpInDeck(Trump trump) {
        for (Card card : deck) {
            if (card.getTrump() == trump) {
                return true;
            }
        }
        return false;
    }

    private List<Card> sortDeck(List<Card> deck) {
        List<Card> newDeck = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            System.out.println(Trump.values()[i]);
            for (Card card : deck) {
                if (Trump.values()[i] == card.getTrump()) {
                    newDeck.add(card);
                }
            }
        }
        return newDeck;
    }

    // Client calls this function
    public void run() throws IOException, ClassNotFoundException {
        while (true) {
            System.out.println("Waiting for command");
            Message message = (Message) is.readObject();

            // Read command
            switch (message.getCommand()) {
                case CONNECT:
                    othersCards[0] = 13;
                    othersCards[1] = 13;
                    othersCards[2] = 13;
                    othersCards[3] = 13;
                    deck = sortDeck(message.getDeck());
                    index = message.getIndexPlayer();
                    masterTrump = message.getMasterTrump();
                    if (message.isFirstTime())
                        createGUI();
                    break;

                case PLAY:
                    // Initialize variables
                    deck = message.getDeck();
                    name = message.getPlayer().name;
                    team = message.getPlayer().team;
                    points = message.getPlayer().points;
                    hasToPlay = true;
                    mainPanel.getActives()[index].setIcon(new ImageIcon("resources/player-active.png"));
                    roundTrump = message.getTrump();
                    break;

                case CARD_RESPONSE:
                    playedCard = message.getPlayedCards();
                    scores = message.getPoints();
                    int whoHasPlayed = message.getWhoHasPlayed();
                    for (int i = 0; i < 4; i++)
                        mainPanel.getActives()[i].setIcon(new ImageIcon("resources/player.png"));
                    if (!message.isFirstTime() && whoHasPlayed != index)
                        othersCards[whoHasPlayed] -= 1;
                    if ((whoHasPlayed + 1) % 4 == index)
                        mainPanel.getErrorMsg().setText("Your turn");
                    else
                        mainPanel.getErrorMsg().setText("Player " + ((whoHasPlayed + 1) % 4 + 1) + " turn's");
                        mainPanel.getActives()[(whoHasPlayed + 1) % 4].setIcon(new ImageIcon("resources/player-active.png"));
                    break;
                case QUIT:
                    System.out.println("Quit");
                    socket.close();
                    System.exit(0);
                default:
                    break;
            }
            draw();
        }
    }

    private void draw() {
        mainPanel.draw();
    }

    public List<Card> getDeck() {
        return deck;
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

    public boolean isHasToPlay() {
        return hasToPlay;
    }

    public void setHasToPlay(boolean hasToPlay) {
        this.hasToPlay = hasToPlay;
    }

    public Trump getMasterTrump() {
        return masterTrump;
    }

    public Trump getRoundTrump() {
        return roundTrump;
    }

    public ObjectOutputStream getOs() {
        return os;
    }


    // Server calls this function
    public Card play(Trump roundTrump) throws IOException, ClassNotFoundException {
        // copy new deck
        List<Card> newDeck = new ArrayList<>(deck);

        // send play message
        Message message = new Message(Command.PLAY, newDeck, roundTrump, this);
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

    public String connected(List<Card> deck, int index, Trump masterTrump, boolean firstTime) throws IOException {
        List<Card> newDeck = new ArrayList<>(deck);
        Message message = new Message(Command.CONNECT, newDeck, index, masterTrump, firstTime);

        os.writeObject(message);
        return name;
    }

    public void sendPlayedCard(List<Card> playedCards, int whoHasPlayed, List<Integer> points, boolean firstTime) throws IOException {
        List<Card> newPlayedCards = new ArrayList<>(playedCards);
        Message message = new Message(Command.CARD_RESPONSE, newPlayedCards, whoHasPlayed, points, firstTime);
        os.writeObject(message);
    }
}
