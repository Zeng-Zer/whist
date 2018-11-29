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
    private String name;
    // team id
    private int team;
    // points in the round
    private int points = 0;
    private int index;
    private Trump roundTrump;
    private Trump masterTrump;
    private boolean hasToPlay = false;
    private int[] othersCards = {13, 13, 13, 13};
    private Card[] playedCard = {null, null, null, null};
    private JLabel[] actives = {new JLabel(), new JLabel(), new JLabel(), new JLabel()};
    private Map<Trump, String> trumpIcons = new HashMap<>();

    JFrame f;
    JPanel mainPanel = new JPanel();
    JPanel playerCard = new JPanel();
    JPanel center = new JPanel();

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

        trumpIcons.put(Trump.HEART, "resources/Heart.png");
        trumpIcons.put(Trump.CLUB, "resources/Club.png");
        trumpIcons.put(Trump.DIAMOND, "resources/Diamond.png");
        trumpIcons.put(Trump.SPADE, "resources/Spade.png");
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        mainPanel.setBackground(new Color(0, 102, 0));
        playerCard.setLayout(null);
        playerCard.setBackground(null);
        center.setLayout(null);
        center.setBackground(null);
        c.gridx = 2;
        c.gridy = 3;
        c.ipady = 141;
        c.ipadx = 700;
        mainPanel.add(playerCard, c);
        c.gridx = 2;
        c.gridy = 1;
        c.ipady = 520;
        c.insets.right = 13;
        mainPanel.add(center, c);
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
                    actives[message.getWhosHand()].setIcon(new ImageIcon("resources/player-active.png"));
                    createGUI();
                    break;

                case PLAY:
                    // Initialize variables
                    deck = message.getDeck();
                    name = message.getPlayer().name;
                    team = message.getPlayer().team;
                    points = message.getPlayer().points;
                    hasToPlay = true;
                    actives[index].setIcon(new ImageIcon("resources/player-active.png"));
                    roundTrump = message.getTrump();
                    break;

                case CARD_RESPONSE:
                    playedCard = message.getPlayedCards();
                    int whoHasPlayed = message.getWhoHasPlayed();
                    for (Card c : playedCard) {
                        System.out.println(c);
                    }
                    for (int i = 0; i < 4; i++)
                        actives[i].setIcon(new ImageIcon("resources/player.png"));
                    if (whoHasPlayed != index)
                        othersCards[whoHasPlayed] -= 1;
                    actives[(whoHasPlayed + 1) % 4].setIcon(new ImageIcon("resources/player-active.png"));
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
                    playedCard[index] = deck.remove(deck.indexOf(card));
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
        t = new JLabel();

        if (masterTrump != trump) {
            t.setIcon(new ImageIcon(GrayFilter.createDisabledImage(new ImageIcon(trumpIcons.get(trump)).getImage())));
        }
        else {
            t.setIcon(new ImageIcon(new ImageIcon(trumpIcons.get(trump)).getImage()));
        }
        mainPanel.add(t, new GridBagConstraints(gridx, gridy, 1, 1, 0.1, 0.0, anchor,
                GridBagConstraints.NONE, new Insets(top, 0, 0, right), 0, 0));
    }

    private void drawPlayerIcon(int i, int anchor, int gridx, int gridy, int top, int left, int bottom, int right) {
        if (actives[i].getIcon() == null)
            actives[i].setIcon(new ImageIcon("resources/player.png"));
        mainPanel.add(actives[i],  new GridBagConstraints(gridx, gridy, 1, 1, 0.1, 0.1, anchor,
                GridBagConstraints.NONE, new Insets(top, left, bottom, right), 0, 0));
    }

    private void draw() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        JPanel other;
        JPanel p = playerCard;
        int tmpIndex = (index + 1) % 4;

        p.removeAll();
        center.removeAll();
        for (int i = 0; i < 4; i++)
            System.out.println(actives[i]);
        drawPlayerIcon(index, GridBagConstraints.LINE_END, 1, 3, 60, 0, 0,10);

        drawTrump(Trump.HEART, GridBagConstraints.EAST, 4, 0, -70, 10);
        drawTrump(Trump.SPADE, GridBagConstraints.LINE_END, 4, 4, 0, 0);
        drawTrump(Trump.DIAMOND, GridBagConstraints.LINE_START, 0, 4, 0, 0);
        drawTrump(Trump.CLUB, GridBagConstraints.WEST, 0, 0, -70, 10);

        if (playedCard[index] != null) {
            playedCard[index].button.setLocation(300, 320);
            center.add(playedCard[index].button);
        }
        for (int i = 0; i < 3; i++) {
            other = new JPanel();
            other.setLayout(null);
            other.setBackground(new Color(0, 102, 0));
            for (int j = 0; j < othersCards[tmpIndex]; j++) {
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
                    c.gridy = 1;
                    c.ipady = 520;
                    c.ipadx = 141;
                    if (playedCard[tmpIndex] != null)
                        playedCard[tmpIndex].button.setLocation(100, 160);
                    drawPlayerIcon(tmpIndex, GridBagConstraints.LINE_START, 1, 0, 60, 0, 0,0);
                    break;
                case 1:
                    c.gridx = 2;
                    c.gridy = 0;
                    c.ipady = 141;
                    if (playedCard[tmpIndex] != null)
                        playedCard[tmpIndex].button.setLocation(300, 50);
                    drawPlayerIcon(tmpIndex, GridBagConstraints.LINE_START, 3, 0, 60, 10, 0, 0);
                    break;
                case 2:
                    c.gridx = 3;
                    c.gridy = 1;
                    c.ipady = 520;
                    c.ipadx = 141;
                    if (playedCard[tmpIndex] != null)
                        playedCard[tmpIndex].button.setLocation(500, 160);
                    drawPlayerIcon(tmpIndex, GridBagConstraints.LINE_END,3, 3, 0, 0, 50,0);
                    break;
                default:
                    break;
            }
            if (playedCard[tmpIndex] != null)
                center.add(playedCard[tmpIndex].button);
            mainPanel.add(other, c);
            tmpIndex = (tmpIndex + 1) % 4;
        }
        createCards();
        p.revalidate();
        center.revalidate();
        p.repaint();
        center.repaint();
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
        Message message = new Message(QUIT);
        os.writeObject(message);
    }

    public void connected(List<Card> deck, int index, Trump masterTrump, int whosHand) throws IOException {
        List<Card> newDeck = new ArrayList<>(deck);
        Message message = new Message(Command.CONNECT, newDeck, index, masterTrump, whosHand);

        os.writeObject(message);
    }

    public void sendPlayedCard(Card[] playedCards, int whoHasPlayed) throws IOException {
        Message message = new Message(Command.CARD_RESPONSE, playedCards, whoHasPlayed);
        os.writeObject(message);
    }
}
