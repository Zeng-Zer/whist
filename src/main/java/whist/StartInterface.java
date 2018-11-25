package whist;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

public class StartInterface extends JPanel {

    JTextField name;
    JTextField ip;
    JButton join;

    public StartInterface(JFrame f) {
        this.name = new JTextField();
        //name.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.name.setPreferredSize(new Dimension(200, 50));
        this.ip = new JTextField();
        //ip.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.ip.setPreferredSize(new Dimension(200, 50));
        this.join = new JButton("JOIN");
        this.join.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Player player = null;
                try {
                    player = new Player(new Socket(ip.getText(), 8080), name.getText());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Connected");
         //       f.setVisible(false);
                Player finalPlayer = player;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            finalPlayer.run();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        this.setLayout(new FlowLayout(2));
        this.add(name);
        this.add(ip);
        this.add(join);
        this.setBackground(new Color(0, 102, 0));
    }
}
