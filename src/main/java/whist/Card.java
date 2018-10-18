package whist;

public class Card {
    private Trump trump;
    private Value value;

    public Card(Trump trump, Value value) {
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
}
