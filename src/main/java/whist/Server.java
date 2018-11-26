package whist;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

public class Server {

    public static Map<Trump, Map<Value, String>> cardDeck;

    public static Map<Trump, Map<Value, String>> createCardDeck() {

        Map<Trump, Map<Value, String>> deck = new HashMap<>();

        Map<Value, String> heartMap = new HashMap<Value, String>();
        Map<Value, String> diamondMap = new HashMap<Value, String>();
        Map<Value, String> spadeMap = new HashMap<Value, String>();
        Map<Value, String> clubMap = new HashMap<Value, String>();

        heartMap.put(Value.ACE, "resources/heart/As.png");
        heartMap.put(Value.TWO, "resources/heart/Two.png");
        heartMap.put(Value.THREE, "resources/heart/Three.png");
        heartMap.put(Value.FOUR, "resources/heart/Four.png");
        heartMap.put(Value.FIVE, "resources/heart/Five.png");
        heartMap.put(Value.SIX, "resources/heart/Six.png");
        heartMap.put(Value.SEVEN, "resources/heart/Seven.png");
        heartMap.put(Value.EIGHT, "resources/heart/Height.png");
        heartMap.put(Value.NINE, "resources/heart/Nine.png");
        heartMap.put(Value.TEN, "resources/heart/Ten.png");
        heartMap.put(Value.JACK, "resources/heart/Jack.png");
        heartMap.put(Value.QUEEN, "resources/heart/Queen.png");
        heartMap.put(Value.KING, "resources/heart/King.png");

        diamondMap.put(Value.ACE, "resources/diamond/As.png");
        diamondMap.put(Value.TWO, "resources/diamond/Two.png");
        diamondMap.put(Value.THREE, "resources/diamond/Three.png");
        diamondMap.put(Value.FOUR, "resources/diamond/Four.png");
        diamondMap.put(Value.FIVE, "resources/diamond/Five.png");
        diamondMap.put(Value.SIX, "resources/diamond/Six.png");
        diamondMap.put(Value.SEVEN, "resources/diamond/Seven.png");
        diamondMap.put(Value.EIGHT, "resources/diamond/Height.png");
        diamondMap.put(Value.NINE, "resources/diamond/Nine.png");
        diamondMap.put(Value.TEN, "resources/diamond/Ten.png");
        diamondMap.put(Value.JACK, "resources/diamond/Jack.png");
        diamondMap.put(Value.QUEEN, "resources/diamond/Queen.png");
        diamondMap.put(Value.KING, "resources/diamond/King.png");

        spadeMap.put(Value.ACE, "resources/spade/As.png");
        spadeMap.put(Value.TWO, "resources/spade/Two.png");
        spadeMap.put(Value.THREE, "resources/spade/Three.png");
        spadeMap.put(Value.FOUR, "resources/spade/Four.png");
        spadeMap.put(Value.FIVE, "resources/spade/Five.png");
        spadeMap.put(Value.SIX, "resources/spade/Six.png");
        spadeMap.put(Value.SEVEN, "resources/spade/Seven.png");
        spadeMap.put(Value.EIGHT, "resources/spade/Height.png");
        spadeMap.put(Value.NINE, "resources/spade/Nine.png");
        spadeMap.put(Value.TEN, "resources/spade/Ten.png");
        spadeMap.put(Value.JACK, "resources/spade/Jack.png");
        spadeMap.put(Value.QUEEN, "resources/spade/Queen.png");
        spadeMap.put(Value.KING, "resources/spade/King.png");

        clubMap.put(Value.ACE, "resources/club/As.png");
        clubMap.put(Value.TWO, "resources/club/Two.png");
        clubMap.put(Value.THREE, "resources/club/Three.png");
        clubMap.put(Value.FOUR, "resources/club/Four.png");
        clubMap.put(Value.FIVE, "resources/club/Five.png");
        clubMap.put(Value.SIX, "resources/club/Six.png");
        clubMap.put(Value.SEVEN, "resources/club/Seven.png");
        clubMap.put(Value.EIGHT, "resources/club/Height.png");
        clubMap.put(Value.NINE, "resources/club/Nine.png");
        clubMap.put(Value.TEN, "resources/club/Ten.png");
        clubMap.put(Value.JACK, "resources/club/Jack.png");
        clubMap.put(Value.QUEEN, "resources/club/Queen.png");
        clubMap.put(Value.KING, "resources/club/King.png");

        deck.put(Trump.HEART, heartMap);
        deck.put(Trump.DIAMOND, diamondMap);
        deck.put(Trump.SPADE, spadeMap);
        deck.put(Trump.CLUB, clubMap);
        return deck;
    }

    public static Map<Trump, Map<Value, String>> getCardDeck() {
        return cardDeck;
    }


    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(8080);
        try {
            while (true) {
                System.out.println("Starting game");
                Server.cardDeck = createCardDeck();
                GameEngine engine = new GameEngine(listener, Server.getCardDeck());
                engine.start();
            }
        } finally {
            listener.close();
        }
    }
}
