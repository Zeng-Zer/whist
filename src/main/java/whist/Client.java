package whist;

import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        System.out.println("Connecting to server");
        Player player = new Player(new Socket(args[0], 8080));
        System.out.println("Connected");
        player.run();
    }
}
