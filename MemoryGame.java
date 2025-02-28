import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public class MemoryGame {
    private JFrame frame;
    private JPanel panel;
    private JButton[][] buttons;
    private ImageIcon[] images;
    private int[][] board;
    private int firstRow = -1, firstCol = -1;
    private boolean firstCardFlipped = false;
    private Timer timer;
    private int revealedPairs = 0;
    private int gridSize;
    private int mistakes = 0;
    private long startTime;
    private String difficulty;

    public MemoryGame(int size, String level) {
        this.gridSize = size;
        this.difficulty = level;
        frame = new JFrame("Memory Game");
        panel = new JPanel(new GridLayout(gridSize, gridSize));
        buttons = new JButton[gridSize][gridSize];
        images = new ImageIcon[gridSize * gridSize / 2];
        board = new int[gridSize][gridSize];
        loadImages();
        setupBoard();
        startTime = System.currentTimeMillis();
    }

    private void loadImages() {
        for (int i = 0; i < images.length; i++) {
            images[i] = new ImageIcon("img" + (i + 1) + ".png");
        }
    }

    private void setupBoard() {
        ArrayList<Integer> cardList = new ArrayList<>();
        for (int i = 0; i < gridSize * gridSize / 2; i++) {
            cardList.add(i);
            cardList.add(i);
        }
        Collections.shuffle(cardList);

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                board[i][j] = cardList.remove(0);
                buttons[i][j] = new JButton();
                final int row = i, col = j;
                buttons[i][j].addActionListener(e -> revealCard(row, col));
                panel.add(buttons[i][j]);
            }
        }

        frame.add(panel, BorderLayout.CENTER);
        JButton restartButton = new JButton("Return Game");
        restartButton.addActionListener(e -> restartGame());
        frame.add(restartButton, BorderLayout.SOUTH);
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void revealCard(int row, int col) {
        if (buttons[row][col].getIcon() != null) return;

        buttons[row][col].setIcon(images[board[row][col]]);

        if (!firstCardFlipped) {
            firstRow = row;
            firstCol = col;
            firstCardFlipped = true;
        } else {
            if (board[row][col] == board[firstRow][firstCol]) {
                revealedPairs++;
                if (revealedPairs == (gridSize * gridSize / 2)) {
                    long endTime = System.currentTimeMillis();
                    long elapsedTime = (endTime - startTime) / 1000;
                    saveRank(elapsedTime);
                    JOptionPane.showMessageDialog(frame, "You win! Time: " + elapsedTime + " sec\nMistakes: " + mistakes);
                }
            } else {
                mistakes++;
                timer = new Timer(500, e -> {
                    buttons[row][col].setIcon(null);
                    buttons[firstRow][firstCol].setIcon(null);
                    firstCardFlipped = false;
                    timer.stop();
                });
                timer.setRepeats(false);
                timer.start();
            }
            firstCardFlipped = false;
        }
    }

    private void restartGame() {
        frame.dispose();
        new MemoryGame(gridSize, difficulty);
    }

    private void saveRank(long time) {
        try (FileWriter fw = new FileWriter("ranks.txt", true); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(difficulty + "," + time + "," + mistakes + "," + new Date());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String[] options = {"Easy (4x4)", "Normal (6x6)", "Hard (8x8)", "Nightmare (10x10)", "Ranks", "Exit"};
            while (true) {
                int choice = JOptionPane.showOptionDialog(null, "Select option:", "Memory Game Menu",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (choice == 5 || choice == JOptionPane.CLOSED_OPTION) {
                    System.exit(0);
                } else if (choice == 4) {
                    showRanks();
                } else {
                    int size = 4 + (choice * 2);
                    new MemoryGame(size, options[choice]);
                }
            }
        });
    }

    private static void showRanks() {
        StringBuilder ranks = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader("ranks.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                ranks.append(line).append("\n");
            }
        } catch (IOException e) {
            ranks.append("No rankings yet.");
        }
        JOptionPane.showMessageDialog(null, ranks.toString(), "Rankings", JOptionPane.INFORMATION_MESSAGE);
    }
}
