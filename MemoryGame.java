import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.ArrayList;

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
    private int gridSize = 4;
    private int mistakes = 0;
    private long startTime;

    public MemoryGame(int size) {
        this.gridSize = size;
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
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 24));
                final int row = i, col = j;
                buttons[i][j].addActionListener(e -> revealCard(row, col));
                panel.add(buttons[i][j]);
            }
        }

        frame.add(panel);
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
                    JOptionPane.showMessageDialog(frame, "You win! Time: " + (endTime - startTime) / 1000 + " sec\nMistakes: " + mistakes);
                }
            } else {
                mistakes++;
                timer = new Timer(500, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        buttons[row][col].setIcon(null);
                        buttons[firstRow][firstCol].setIcon(null);
                        firstCardFlipped = false;
                        timer.stop();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
            firstCardFlipped = false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String[] options = {"Easy (4x4)", "Normal (6x6)", "Hard (8x8)", "Nightmare (10x10)"};
            int choice = JOptionPane.showOptionDialog(null, "Select difficulty:", "Memory Game",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            int size = 4 + (choice * 2);
            new MemoryGame(size);
        });
    }
}
