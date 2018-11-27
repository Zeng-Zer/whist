package whist;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;
import static javax.swing.SpringLayout.*;
import static whist.Command.QUIT;

public class Player implements Serializable {
    private transient Socket socket;
    private transient ObjectInputStream is;
    private transient ObjectOutputStream os;
    private List<Card> deck = new ArrayList<>();
    private List<Card> playedCard = new ArrayList<>();
    private String name;
    // team id
    private int team;
    // points in the round
    private int points = 0;
    private int index;
    private Trump roundTrump;
    private Trump masterTrump;
    private boolean hasToPlay = false;
    private int[] othersCards = {13, 13, 13};
    private Map<Trump, ImageIcon> trumpIcons = new HashMap<>();

    JFrame f;
    JPanel mainPanel = new JPanel();
    JPanel playerCard = new JPanel();

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
      //  this.socket.setSoTimeout(0);
        this.os = new ObjectOutputStream(socket.getOutputStream());
        this.is = new ObjectInputStream(socket.getInputStream());
    }

    public void createGUI() {
        this.f =  new JFrame();
        f.setTitle(name);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setPreferredSize(new Dimension(1200, 900));

        trumpIcons.put(Trump.HEART, new ImageIcon("resources/Heart.png"));
        trumpIcons.put(Trump.CLUB, new ImageIcon("resources/Club.png"));
        trumpIcons.put(Trump.DIAMOND, new ImageIcon("resources/Diamond.png"));
        trumpIcons.put(Trump.SPADE, new ImageIcon("resources/Spade.png"));
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        mainPanel.setBackground(new Color(0, 102, 0));
        playerCard.setLayout(null);
        playerCard.setBackground(new Color(0, 102, 0));
        c.gridx = 2;
        c.gridy = 3;
        c.ipady = 141;
        c.ipadx = 700;
        mainPanel.add(playerCard, c);
        f.add(mainPanel);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
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

    private List<Card> sortDeck(List<Card> deck) {
        List<Card> newDeck = new ArrayList<>();
        for (int i = 0; i < 4; ++i) {
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
                    deck = sortDeck(message.getDeck());
                    index = message.getIndexPlayer();
                    masterTrump = message.getMasterTrump();
                    createGUI();
                    break;

                case PLAY:
                    // Initialize variables
                    deck = message.getDeck();
                    name = message.getPlayer().name;
                    team = message.getPlayer().team;
                    points = message.getPlayer().points;
                    hasToPlay = true;
                    roundTrump = message.getTrump();
                    break;

                case CARD_RESPONSE:
                    playedCard = message.getPlayedCards();
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

    public void createCards() {
        for (Card card : deck) {
            card.button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (hasToPlay == false) {
                        System.out.println("Not your turn");
                        return ;
                    }
                    if (isTrumpInDeck(roundTrump) && card.getTrump() != roundTrump) {
                        System.out.println("You have " + String.valueOf(roundTrump) + "in your deck, you should play it");
                        return ;
                    }
                    deck.remove(deck.indexOf(card));
                    System.out.println("Player " + name + " played: " + card.toString() + ", card left: " + deck.size());
                    Message response = new Message(card);
                    try {
                        os.writeObject(response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    draw();
                    hasToPlay = false;
                }
            });
            card.button.setSize(100, 141);
            card.button.setLocation(deck.indexOf(card) * 50, 0);
            playerCard.add(card.button);
        }
    }

    private void drawTrump(Trump trump, int anchor, int gridx, int gridy,int top, int right) {
        JLabel t;
        ImageIcon img = trumpIcons.get(trump);
        Image normalImg = img.getImage();

        t = new JLabel(img);
        System.out.println(masterTrump + " " + trump);

        if (masterTrump != trump) {
            t.setIcon(new ImageIcon(GrayFilter.createDisabledImage(normalImg)));
        }
        mainPanel.add(t,  new GridBagConstraints(gridx, gridy, 1, 1, 0.1, 0.0, anchor,
                GridBagConstraints.NONE, new Insets(top, 0, 0, right), 0, 0));
    }

    private void draw() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        JPanel other;
        JPanel p = playerCard;

        p.removeAll();
        drawTrump(Trump.HEART, GridBagConstraints.EAST, 4, 0, -70, 10);
        drawTrump(Trump.SPADE, GridBagConstraints.LINE_END, 4, 4, 0, 0);
        drawTrump(Trump.DIAMOND, GridBagConstraints.LINE_START, 0, 4, 0, 0);
        drawTrump(Trump.CLUB, GridBagConstraints.WEST, 0, 0, -70, 10);

        for (int i = 0; i < 3; i++) {
            other = new JPanel();
            other.setLayout(null);
            other.setBackground(new Color(0, 102, 0));
            for (int j = 0; j < othersCards[i]; j++) {
                JButton img = new JButton();
                img.setBorder(BorderFactory.createEmptyBorder());
                img.setContentAreaFilled(false);
                if (i % 2 != 0) {
                    img.setIcon(new ImageIcon("resources/DosCarte.png"));
                    img.setSize(new Dimension(100, 141));
                    img.setLocation(j * 50, 0);
                } else {
                    img.setIcon(new ImageIcon("resources/DosCarte2.png"));
                    img.setSize(new Dimension(141, 100));
                    img.setLocation(0, j * 35);
                }
                other.add(img);
            }
            switch (i) {
                case 0:
                    c.gridx = 1;
                    c.gridy = 2;
                    c.ipadx = 141;
                    c.ipady = 520;
                    break;
                case 1:
                    c.gridx = 2;
                    c.gridy = 0;
                    c.ipady = 141;
                    break;
                case 2:
                    c.gridx = 3;
                    c.gridy = 2;
                    c.ipadx = 141;
                    c.ipady = 520;
                    break;
                default:
                    break;
            }
            mainPanel.add(other, c);
       }
        createCards();
     /*   c.gridy = 8;
        for (int i = 0; i < playedCard.size(); ++i) {
        //    c.gridx = i + 7;
            System.out.println(playedCard.get(i));
            //  mainPanel.add(playedCard.get(i).button, c);
        }*/
        p.revalidate();
        p.repaint();
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
        Message message = new Message(QUIT);
        os.writeObject(message);
    }

    public void connected(List<Card> deck, int index, Trump masterTrump) throws IOException {
        List<Card> newDeck = new ArrayList<>(deck);
        Message message = new Message(Command.CONNECT, newDeck, index, masterTrump);

        os.writeObject(message);
    }

    public void sendPlayedCard(List<Card> playedCards) throws IOException, ClassNotFoundException {
        Message message = new Message(Command.CARD_RESPONSE, playedCards);
        os.writeObject(message);
    }
}
