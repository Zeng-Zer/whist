package whist;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(8080);
        try {
            while (true) {
                System.out.println("Starting game");
                GameEngine engine = new GameEngine(listener);
                engine.start();
            }
        } finally {
            listener.close();
        }
    }
}
