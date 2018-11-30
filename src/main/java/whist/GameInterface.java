package whist;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameInterface extends JPanel {

    private Player p;

    private JLabel[] actives = {new JLabel(), new JLabel(), new JLabel(), new JLabel()};
    private JLabel[] trumps = {new JLabel(), new JLabel(), new JLabel(), new JLabel()};
    private JPanel[] othersCardsPanel = {new JPanel(), new JPanel(), new JPanel()};
    private JLabel errorMsg = new JLabel();
    private Map<Trump, String> trumpIcons = new HashMap<>();

    JPanel playerCard = new JPanel();
    JPanel center = new JPanel();

    GameInterface(Player p) {
        GridBagConstraints c = new GridBagConstraints();
        this.p = p;

        this.setLayout(new GridBagLayout());
        this.setBackground(new Color(0, 102, 0));
        c.fill = GridBagConstraints.HORIZONTAL;

        trumpIcons.put(Trump.HEART, "resources/Heart.png");
        trumpIcons.put(Trump.CLUB, "resources/Club.png");
        trumpIcons.put(Trump.DIAMOND, "resources/Diamond.png");
        trumpIcons.put(Trump.SPADE, "resources/Spade.png");

        errorMsg.setBackground(new Color(0xD1c30D));
        errorMsg.setOpaque(true);
        errorMsg.setSize(new Dimension(700, 30));
        errorMsg.setBorder(new EmptyBorder(0, 10, 0, 10));
        errorMsg.setLocation(0, 480);

        playerCard.setLayout(null);
        playerCard.setBackground(null);
        c.gridx = 2;
        c.gridy = 3;
        c.ipady = 141;
        c.ipadx = 700;
        this.add(playerCard, c);

        othersCardsPanel[0].setLayout(null);
        othersCardsPanel[0].setBackground(null);
        c.gridx = 1;
        c.gridy = 1;
        c.ipady = 520;
        c.ipadx = 141;
        this.add(othersCardsPanel[0], c);


        othersCardsPanel[1].setLayout(null);
        othersCardsPanel[1].setBackground(null);
        c.gridx = 2;
        c.gridy = 0;
        c.ipady = 141;
        this.add(othersCardsPanel[1], c);

        othersCardsPanel[2].setLayout(null);
        othersCardsPanel[2].setBackground(null);
        c.gridx = 3;
        c.gridy = 1;
        c.ipady = 520;
        c.ipadx = 141;
        this.add(othersCardsPanel[2], c);

        center.setLayout(null);
        center.setBackground(null);
        center.add(errorMsg);
        c.gridx = 2;
        c.gridy = 1;
        c.ipady = 520;
        c.insets.right = 13;
        this.add(center, c);
    }

    public void createCards() {
        Trump roundTrump = p.getRoundTrump();

        for (Card card : p.getDeck()) {
            card.button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (!p.isHasToPlay()) {
                        errorMsg.setText("It's not your turn to play");
                        return;
                    }
                    if (p.isTrumpInDeck(roundTrump) && card.getTrump() != roundTrump) {
                        errorMsg.setText("You have " + String.valueOf(roundTrump) + " in your deck, you should play it");
                        return;
                    }
                    p.playedCard.set(p.getIndex(), p.getDeck().remove(p.getDeck().indexOf(card)));
                    System.out.println("Player " + p.getName() + " played: " + card.toString() + ", card left: " + p.getDeck().size());
                    Message response = new Message(card);
                    try {
                        p.getOs().writeObject(response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    updateUI();
                    p.setHasToPlay(false);
                }
            });
            card.button.setSize(100, 141);
            card.button.setLocation(p.getDeck().indexOf(card) * 50, 0);
            playerCard.add(card.button);
        }
    }

    private void drawTrump(int i, Trump trump, int anchor, int gridx, int gridy,int top, int right) {
        if (p.getMasterTrump() != trump) {
            trumps[i].setIcon(new ImageIcon(GrayFilter.createDisabledImage(new ImageIcon(trumpIcons.get(trump)).getImage())));
        }
        else {
            trumps[i].setIcon(new ImageIcon(new ImageIcon(trumpIcons.get(trump)).getImage()));
        }
        this.add(trumps[i], new GridBagConstraints(gridx, gridy, 1, 1, 0.1, 0.0, anchor,
                GridBagConstraints.NONE, new Insets(top, 0, 0, right), 0, 0));
    }

    private void drawPlayerIcon(int i, int anchor, int gridx, int gridy, int top, int left, int bottom, int right) {
        if (actives[i].getIcon() == null)
            actives[i].setIcon(new ImageIcon("resources/player.png"));
    /*    actives[i].setText(name + ": " + points);
        actives[i].setHorizontalTextPosition(JLabel.CENTER);
        actives[i].setVerticalTextPosition(JLabel.BOTTOM);
    */    this.add(actives[i],  new GridBagConstraints(gridx, gridy, 1, 1, 0.1, 0.1, anchor,
                GridBagConstraints.NONE, new Insets(top, left, bottom, right), 0, 0));
    }

    public void draw() {
        int index = p.getIndex();

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        int tmpIndex = (index + 1) % 4;

        for (JPanel p : othersCardsPanel)
            p.removeAll();
        playerCard.removeAll();
        center.removeAll();

        drawPlayerIcon(index, GridBagConstraints.LINE_END, 1, 3, 60, 0, 0,10);

        drawTrump(0, Trump.HEART, GridBagConstraints.EAST, 4, 0, -70, 10);
        drawTrump(1, Trump.SPADE, GridBagConstraints.LINE_END, 4, 4, 0, 0);
        drawTrump(2, Trump.DIAMOND, GridBagConstraints.LINE_START, 0, 4, 0, 0);
        drawTrump(3, Trump.CLUB, GridBagConstraints.WEST, 0, 0, -70, 10);

        if (p.playedCard.get(index) != null) {
            p.playedCard.get(index).button.setLocation(300, 320);
            center.add(p.playedCard.get(index).button);
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < p.othersCards[tmpIndex]; j++) {
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
                othersCardsPanel[i].add(img);
            }
            switch (i) {
                case 0:
                    if (p.playedCard.get(tmpIndex) != null)
                        p.playedCard.get(tmpIndex).button.setLocation(100, 160);
                    drawPlayerIcon(tmpIndex, GridBagConstraints.LINE_START, 1, 0, 60, 0, 0,0);
                    break;
                case 1:
                    if (p.playedCard.get(tmpIndex) != null)
                        p.playedCard.get(tmpIndex).button.setLocation(300, 50);
                    drawPlayerIcon(tmpIndex, GridBagConstraints.LINE_START, 3, 0, 60, 10, 0, 0);
                    break;
                case 2:
                    if (p.playedCard.get(tmpIndex) != null)
                        p.playedCard.get(tmpIndex).button.setLocation(500, 160);
                    drawPlayerIcon(tmpIndex, GridBagConstraints.LINE_END,3, 3, 0, 0, 50,0);
                    break;
                default:
                    break;
            }
            if (p.playedCard.get(tmpIndex) != null)
                center.add(p.playedCard.get(tmpIndex).button);
            tmpIndex = (tmpIndex + 1) % 4;
        }
        createCards();
        center.add(errorMsg);

        for (JPanel p : othersCardsPanel) {
            p.revalidate();
            p.repaint();
        }
        center.revalidate();
        center.repaint();
        playerCard.revalidate();
        playerCard.repaint();
    }

    public JLabel[] getActives() {
        return actives;
    }

    public JLabel getErrorMsg() {
        return errorMsg;
    }
}
