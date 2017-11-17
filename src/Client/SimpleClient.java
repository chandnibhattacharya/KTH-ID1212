/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;


import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimpleClient extends JPanel {

    private JButton connectButton;
    private JButton reverseButton;
    private JButton newGameButton;
    private JButton quitGameButton;
    private JLabel resultLabel = new JLabel();
    private JLabel scoreLabel = new JLabel();
    private JLabel attemptLabel = new JLabel();
    private ServerConnection connection;
    public boolean always = false;
    public JPanel menuPanel, outputPanel, connectPanel, reversePanel;
    static JFrame frame;
    static int prevValue = 0;

    // Creates a new instance and builds the gui.
    SimpleClient() {
        buildGui();
    }

//The main method of the client. Starts the gui.
    public static void main(String[] args) {
        frame = new JFrame("Hangman Game");

      //  frame.setResizable(false);
        frame.setSize(600, 950);
        frame.setContentPane(new SimpleClient());
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void buildGui() {
        setLayout(new GridLayout(2, 1));
        add(createConnectPanel());
        add(createReversePanel());
        add(createMenuPanel());
        add(createOutputPanel());

    }

    private Component createConnectPanel() {
        connectPanel = new JPanel();
        connectPanel.setBorder(
                new TitledBorder(new EtchedBorder(), "Connection"));

        connectPanel.add(new JLabel("Host:"));
        final JTextField hostField = new JTextField("localhost");
        connectPanel.add(hostField);

        connectPanel.add(new JLabel("Port:"));
        final JTextField portField = new JTextField("5555");
        connectPanel.add(portField);

        connectButton = new JButton("Connect");
        connectPanel.add(connectButton);
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String host = hostField.getText();
                int port = Integer.parseInt(portField.getText());
                connectButton.setEnabled(false);
                connection
                        = new ServerConnection(SimpleClient.this, host, port);
                new Thread(connection).start(); //multithreaded
                //connection.connect(); //single threaded
            }
        });
        return connectPanel;
    }

    private Component createReversePanel() {
        reversePanel = new JPanel();
        reversePanel.setBorder(new TitledBorder(new EtchedBorder(), "Guess"));

        reversePanel.add(new JLabel("guess:"));
        final JTextField reverseField = new JTextField(10);
        reversePanel.add(reverseField);

        reverseButton = new JButton("Submit");
        //reverseButton.setEnabled(true);
        reversePanel.add(reverseButton);
        reverseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                connection.reverse(reverseField.getText());
                reverseField.setText("");
                
            }
        });
        reversePanel.setVisible(false);

        return reversePanel;
    }

    private Component createOutputPanel() {
        outputPanel = new JPanel();
        outputPanel.setBorder(new TitledBorder(new EtchedBorder(), "Result"));

        outputPanel.add(new JLabel("Hidden View: "));
        outputPanel.add(resultLabel);
        outputPanel.add(new JLabel("   Remaining Attempt:"));
        outputPanel.add(attemptLabel);

        outputPanel.setVisible(false);
        return outputPanel;
    }

    private Component createMenuPanel() {
        menuPanel = new JPanel();
        menuPanel.setBorder(new TitledBorder(new EtchedBorder(), "Menu"));
        quitGameButton = new JButton("Stop Game");
        newGameButton = new JButton("New Game");

        menuPanel.add(newGameButton);
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                outputPanel.setVisible(true);
                reversePanel.setVisible(true);
                reverseButton.setEnabled(true);
                
            }
        });

        quitGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                frame.dispose();
            }
        });
        quitGameButton.setEnabled(true);
        menuPanel.add(quitGameButton);
        menuPanel.add(new JLabel("Score:"));
        menuPanel.add(scoreLabel);
        menuPanel.setVisible(false);
        return menuPanel;
    }

    public void connected() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {                
                menuPanel.setVisible(true);
            }
        });
    }

    public void showResult(final String result) {
        SwingUtilities.invokeLater(new Runnable() {
            String win = result.substring(result.length() - 2, result.length());
            String result1 = result.substring(0, result.length() - 2);
            int attempt = Integer.parseInt(result1.substring(result1.length() - 1));

            @Override
            public void run() {
                resultLabel.setText(formatString(result.substring(0, result.length() - 3)));
                scoreLabel.setText(win);
                if (attempt == 0) {
                    attemptLabel.setText(Integer.toString(9));
                    outputPanel.setVisible(false);
                    reverseButton.setEnabled(false);
                } else {
                    attemptLabel.setText(Integer.toString(attempt));
                }
            }

        });
    }

    private String formatString(String s) {
        char[] str = new char[2 * s.length()];
        for (int i = 0; i < s.length(); i++) {
            str[2 * i] = s.charAt(i);
            str[2 * i + 1] = ' ';

        }
        return new String(str);
    }

}