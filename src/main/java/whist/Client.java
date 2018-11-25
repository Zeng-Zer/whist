package whist;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        JFrame f;

        System.out.println("Connecting to server");
        f = new JFrame();
        f.setTitle("Whist");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setPreferredSize(new Dimension(250, 180));
        f.setLocationRelativeTo(null);
        f.setContentPane(new StartInterface(f));
        f.pack();
        f.setVisible(true);
    }
}
