package whist;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

public class StartInterface extends JPanel {


    public StartInterface() {
        JTextField name = new JTextField();
        JTextField ip = new JTextField();
        JButton join = new JButton("JOIN");
        JLabel Lname = new JLabel("Enter your name");
        JLabel Lserver = new JLabel("Server ip");
        JLabel title = new JLabel("WHIST");
        JLabel errors = new JLabel("");
        JPanel form = new JPanel();

        title.setFont(new Font(null, Font.LAYOUT_NO_START_CONTEXT, 60));
        title.setForeground(Color.WHITE);
        Lname.setFont(new Font(null, Font.PLAIN, 15));
        Lserver.setFont(new Font(null, Font.PLAIN, 15));

        errors.setFont(new Font(null, Font.BOLD, 15));
        errors.setForeground(Color.RED);
        name.setPreferredSize(new Dimension(200, 50));
        ip.setPreferredSize(new Dimension(200, 50));

        join.setBackground(new Color(0xD1c30D));
        join.setForeground(Color.WHITE);
        join.setFont(new Font(null, Font.PLAIN, 30));
        join.setPreferredSize(new Dimension(0, 50));
        join.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Player player = null;
                try {
                    if (name.getText().trim().equals("")) {
                        errors.setText("You should provide your name");
                        return ;
                    }
                    if (ip.getText().trim().equals("")) {
                        errors.setText("The server ip you trying to connect to is not valid");
                        return ;
                    }
                    player = new Player(new Socket(ip.getText(), 8080), name.getText());
                } catch (IOException e) {
                    errors.setText("Server can't be reached");
                    return;
                }
                errors.setText("");
                ip.setEnabled(false);
                name.setEnabled(false);
                join.setEnabled(false);
                join.setText("Waiting for players...");
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

        this.setLayout(new BorderLayout());
        form.setLayout(new GridBagLayout());
        form.setBackground(null);

        form.add(title, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE, new Insets(-30, 0, 60, 0), 0, 0));

        form.add(Lname, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 200, 0));

        form.add(name, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(0, 0, 30, 0), 200, 0));

        form.add(Lserver, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 200, 0));

        form.add(ip, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(0, 0, 30, 0), 200, 0));

        form.add(errors, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        this.add(form, BorderLayout.CENTER);
        this.add(join, BorderLayout.SOUTH);

        this.setBackground(new Color(0, 102, 0));
    }
}
