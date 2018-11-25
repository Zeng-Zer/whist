package whist;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.Serializable;
import java.util.Objects;

public class Card implements Serializable {
    private Trump trump;
    private Value value;
    public JButton button;

    public Card(Trump trump, Value value) {

        button = new JButton(String.valueOf(value) + " of " + String.valueOf(trump)/*new ImageIcon("resources/DosCarte.png")*/);
        /*button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        button.setText(String.valueOf(value) + " of " + String.valueOf(trump));
    */    button.setMaximumSize(new Dimension(100, 141));
        this.trump = trump;
        this.value = value;
    }

    public Trump getTrump() {
        return trump;
    }

    public void setTrump(Trump trump) {
        this.trump = trump;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Card{" +
                "trump=" + trump +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return trump == card.trump &&
                value == card.value;
    }

    @Override
    public int hashCode() {

        return Objects.hash(trump, value);
    }
}
