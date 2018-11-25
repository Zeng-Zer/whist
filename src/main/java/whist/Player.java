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
import java.util.List;

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
    private boolean hasToPlay = false;

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
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        /* for (int i = 0; i < 20; ++i) {
            JLabel b = new JLabel("                   ");
            b.setMinimumSize(new Dimension(60, 58));
            c.gridx = i;
            mainPanel.add(b, c);
        }
        c.gridx = 0;
        for (int i = 0; i < 15; ++i) {
            JLabel b = new JLabel("                    ");
            b.setMinimumSize(new Dimension(60, 58));
            c.gridy = i;
            mainPanel.add(b, c);
        }*/
       /* for (int i = 0; i < 13; ++i) {
            JButton b = new JButton("Card example");
            b.setPreferredSize(new Dimension(141, 100));
            c.gridy = i;
            mainPanel.add(b, c);
        }*/
        mainPanel.setBackground(new Color(0, 102, 0));
        playerCard.setLayout(null);
        playerCard.setBackground(new Color(0, 102, 0));
        c.ipadx = 700;
        c.ipady = 141;
        mainPanel.add(playerCard, c);
        // playerCard.setSize(650, 150);
       // createCards();
       // mainPanel.add(playerCard, BorderLayout.SOUTH);
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

    // Client calls this function
    public void run() throws IOException, ClassNotFoundException {
        while (true) {
            System.out.println("Waiting for command");
           // try {
            Message message = (Message) is.readObject();
           /* } catch(SocketTimeoutException e) {
                e.printStackTrace();
                return;
            }
*/
            // Read command
            switch (message.getCommand()) {
                case CONNECT:
                    deck = message.getDeck();
                    index = message.getIndexPlayer();
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

                    // random ia
                    //int i = (int) (Math.random() * deck.size());
                    //Card randomCard = deck.get(i);

                  /*  if (isTrumpInDeck(roundTrump) && card.getTrump() != roundTrump) {
                        System.out.println("You have " + String.valueOf(roundTrump) + "in your deck, you should play it");
                        break;*/

                        /*while (!randomCard.getTrump().equals(roundTrump)) {
                            i = (int) (Math.random() * deck.size());
                            randomCard = deck.get(i);
                        }
                    }*/
                   /* deck.remove(deck.indexOf(card));
                    System.out.println("Player " + name + " played: " + card.toString() + ", card left: " + deck.size());
                    Message response = new Message(card);
                    os.writeObject(response);*/
                    break;

                case QUIT:
                    System.out.println("Quit");
                    socket.close();
                    System.exit(0);
                default:
                    break;
            }
            drawCard();
        }
    }

    public void createCards() {
        if (deck.isEmpty()) {
            return ;
        }
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
                    drawCard();
                    hasToPlay = false;
                }
            });
       /* for (int i = 0; i < 13; i++) {
            Card c = new Card(Trump.CLUB, Value.KING);
            JPanel p = playerCard;
            int height = p.getSize().height - 150;
            System.out.println(height);
            c.button.setSize(100, 141);
            c.button.setLocation(i * 50, 0);
            p.add(c.button);
        }*/
        }
    }

    public void drawCard() {
        JPanel p = playerCard;

        p.removeAll();
        createCards();
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

    public void connected(List<Card> deck, int index) throws IOException {
        List<Card> newDeck = new ArrayList<>(deck);
        Message message = new Message(Command.CONNECT, newDeck, index);

        os.writeObject(message);
    }

    public void whosHand(int index) throws IOException, ClassNotFoundException {
        Message message = new Message(Command.HAND, index);
        os.writeObject(message);

  /*      Message response = (Message) is.readObject();
        whosHand = response.getWhosHand();
*/
    }
}
