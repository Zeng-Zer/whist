package whist;

import javax.swing.*;
import java.awt.*;

public class Client {

    static JFrame f;

    public static void main(String[] args) {

        System.out.println("Connecting to server");
        f = new JFrame();
        f.setTitle("Whist");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setPreferredSize(new Dimension(500, 500));
        f.setLocationRelativeTo(null);
        f.setContentPane(new StartInterface());
        f.pack();
        f.setVisible(true);
    }
}
