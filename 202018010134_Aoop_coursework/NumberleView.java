// NumberleView.java

import javax.sound.midi.Soundbank;
import javax.swing.*;
import java.awt.*;
import java.util.Observer;

public class NumberleView implements Observer {
    private final INumberleModel model;
    private final NumberleController controller;
    private final JFrame frame = new JFrame("Numberle");
    private JPanel[][] lableJpanes = new JPanel[7][7];
    private JLabel[][] lables = new JLabel[7][7];
    private StringBuilder msg[] = new StringBuilder[8];
    private final String[] buttonMsg = {"Back", "+", "-", "*", "/", "=", "Enter", "ReStart"};

    public NumberleView(INumberleModel model, NumberleController controller) {
        this.controller = controller;
        this.model = model;
        this.controller.startNewGame();
        ((NumberleModel) this.model).addObserver(this);
        initializeFrame();
        this.controller.setView(this);
        update((NumberleModel) this.model, null);
    }

    public void initializeFrame() {
        msg[controller.getRemainingAttempts()] = new StringBuilder();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setLayout(new BorderLayout());

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.X_AXIS));
        center.add(new JPanel());

        // Set display area
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 7));

        for (int i = 6; i > 0; i--) {
            for (int j = 0; j < 7; j++) {
                lableJpanes[i][j] = new JPanel(new FlowLayout());
                lableJpanes[i][j].setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                lables[i][j] = new JLabel();
                lables[i][j].setOpaque(true);
                lables[i][j].setBackground(Color.WHITE);
                lables[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                lables[i][j].setPreferredSize(new Dimension(70, 50));
                lableJpanes[i][j].add(lables[i][j]);
                inputPanel.add(lableJpanes[i][j]);
            }
        }
        center.add(inputPanel);
        center.add(new JPanel());
        frame.add(center, BorderLayout.NORTH);


        JPanel keyboardPanel = new JPanel();
        keyboardPanel.setLayout(new BoxLayout(keyboardPanel, BoxLayout.Y_AXIS));
        keyboardPanel.add(new JPanel());
        JPanel numberPanel = new JPanel();
        numberPanel.setLayout(new GridLayout(1, 10));
        keyboardPanel.add(numberPanel, BorderLayout.NORTH);

        // Number button area
        for (int i = 0; i < 10; i++) {
            JButton button = new JButton(Integer.toString(i));
            button.setEnabled(true);
            button.addActionListener(e -> {
                if (msg[controller.getRemainingAttempts()].length() < 7) {
                    lables[controller.getRemainingAttempts()][msg[controller.getRemainingAttempts()].length()].setText(button.getText());
                    msg[controller.getRemainingAttempts()].append(button.getText());
                }
            });
            button.setPreferredSize(new Dimension(50, 20));
            numberPanel.add(button);
        }


        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 8));
        keyboardPanel.add(buttonPanel);

        for (int i = 0; i < buttonMsg.length; i++) {
            JButton button = new JButton(buttonMsg[i]);

            button.setEnabled(true);
            // ActionListener
            button.addActionListener(e -> {
                if (button.getText().equals(buttonMsg[0])) { // backspace
                    if (msg[controller.getRemainingAttempts()].length() > 0) {
                        lables[controller.getRemainingAttempts()][msg[controller.getRemainingAttempts()].length() - 1].setText("");
                        msg[controller.getRemainingAttempts()].deleteCharAt(msg[controller.getRemainingAttempts()].length() - 1);
                    }
                } else if (button.getText().equals(buttonMsg[7])) {
                    if (controller.getRemainingAttempts() < 6) {
                        // reset games
                        restartGame();
                    }
                } else if (button.getText().equals(buttonMsg[6])) {
                    // enter
                    if (msg[controller.getRemainingAttempts()].length() >= 7) {
                        if (!msg[controller.getRemainingAttempts()].toString().contains("=")) {
                            JOptionPane.showMessageDialog(null, "No equal '=' sign",
                                    "Prompt", JOptionPane.INFORMATION_MESSAGE);
                            System.out.println("No equal '=' sign");
                            return;
                        }
                        if (!msg[controller.getRemainingAttempts()].toString().contains("+") && !msg[controller.getRemainingAttempts()].toString().contains("-")
                                && !msg[controller.getRemainingAttempts()].toString().contains("*") && !msg[controller.getRemainingAttempts()].toString().contains("/")) {
                            JOptionPane.showMessageDialog(null,
                                    "There must be at least one sign +-*/",
                                    "Prompt", JOptionPane.INFORMATION_MESSAGE);
                            System.out.println("There must be at least one sign +-*/");
                            return;
                        }
                        if (!controller.processInput(msg[controller.getRemainingAttempts()].toString())) {
                            JOptionPane.showMessageDialog(null,
                                    "The left side is not equal to the right",
                                    "Prompt", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }

                        if (controller.isGameOver()) { // Game over start a new game
                            if (model.isGameWon()){
                                JOptionPane.showMessageDialog(null,
                                        "You won",
                                        "Game Won", JOptionPane.INFORMATION_MESSAGE);
                                System.out.println("You Won");
                            }else {
                                JOptionPane.showMessageDialog(null,
                                        "You lose",
                                        "Game Lose", JOptionPane.INFORMATION_MESSAGE);
                                System.out.println("You Lose");
                            }
                            restartGame();
                            return;
                        }
                        msg[controller.getRemainingAttempts()] = new StringBuilder(); // Initializes a new row

                    }
                } else {
                    // Normal keying
                    if (msg[controller.getRemainingAttempts()].length() < 7) {
                        lables[controller.getRemainingAttempts()][msg[controller.getRemainingAttempts()].length()].setText(button.getText());
                        msg[controller.getRemainingAttempts()].append(button.getText());
                    }
                }
            });

            button.setPreferredSize(new Dimension(50, 20));
            buttonPanel.add(button);
        }
        frame.add(keyboardPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    @Override
    public void update(java.util.Observable o, Object arg) {
        if (controller.getRemainingAttempts() < 6){
            int[] result = controller.verifiedNum();
            for (int j = 0; j < result.length; j++) {
                if (result[j] == 2) {
                    // It's there but not in the right place
                    lables[controller.getRemainingAttempts()+1][j].setBackground(Color.YELLOW);
                } else if (result[j] == 1) {
                    // Stand for correct
                    lables[controller.getRemainingAttempts()+1][j].setBackground(Color.GREEN);
                } else {
                    //Incorrect representation
                    lables[controller.getRemainingAttempts()+1][j].setBackground(Color.GRAY);
                }
            }
        }
    }


    private void restartGame() {
        controller.startNewGame();
        for (int i = 6; i > 0; i--) {
            for (int j = 0; j < 7; j++) {
                lables[i][j].setText("");
                lables[i][j].setBackground(Color.WHITE);
                msg[controller.getRemainingAttempts()] = new StringBuilder();
            }
        }
    }

}