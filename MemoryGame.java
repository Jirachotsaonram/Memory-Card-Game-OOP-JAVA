import javax.swing.*;
import javax.swing.Timer;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public class MemoryGame {
    private JFrame frame;
    private JPanel panel;
    private JButton[][] buttons;
    private ImageIcon[] images;
    private ImageIcon backIcon; // เพิ่มตัวแปรสำหรับรูปหลังไพ่
    private int[][] board;
    private int firstRow = -1, firstCol = -1;
    private boolean firstCardFlipped = false;
    private Timer timer;
    private int revealedPairs = 0;
    private int gridSize;
    private int mistakes = 0;
    private long startTime;
    private String difficulty;

    public MemoryGame(int rows, int cols, String level) {
        this.gridSize = rows * cols;
        this.difficulty = level;
        frame = new JFrame("Memory Game");
        panel = new JPanel(new GridLayout(rows, cols));
        buttons = new JButton[rows][cols];
        images = new ImageIcon[gridSize / 2];
        board = new int[rows][cols];
        backIcon = new ImageIcon(new ImageIcon("cardback.png").getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)); // โหลดและปรับขนาดรูปหลังไพ่
        loadImages();
        setupBoard(rows, cols);
        startTime = System.currentTimeMillis();
    }

    private void loadImages() {
        for (int i = 0; i < images.length; i++) {
            images[i] = new ImageIcon(new ImageIcon("img/" + (i + 1) + ".png").getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)); // โหลดและปรับขนาดรูปภาพ
        }
    }

    private void setupBoard(int rows, int cols) {
        ArrayList<Integer> cardList = new ArrayList<>();
        for (int i = 0; i < gridSize / 2; i++) {
            cardList.add(i);
            cardList.add(i);
        }
        Collections.shuffle(cardList);

        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                board[i][j] = cardList.remove(0);
                buttons[i][j] = new JButton(backIcon); // ตั้งค่าไอคอนเริ่มต้นเป็นรูปหลังไพ่
                final int row = i, col = j;
                buttons[i][j].addActionListener(e -> revealCard(row, col));
                panel.add(buttons[i][j]);
            }
        }

        frame.add(panel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        JButton restartButton = new JButton("Return Game");
        restartButton.addActionListener(e -> restartGame());
        buttonPanel.add(restartButton);

        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> {
            frame.dispose();
            showMenu();
        });
        buttonPanel.add(backButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        // คำนวณขนาดของเฟรมตามขนาดของกริด
        int frameWidth = cols * 100 + 50;
        int frameHeight = rows * 100 + 150;
        frame.setSize(frameWidth, frameHeight);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void revealCard(int row, int col) {
        if (buttons[row][col].getIcon() != backIcon) return; // ตรวจสอบว่าการ์ดยังไม่ถูกเปิด

        buttons[row][col].setIcon(images[board[row][col]]); // แสดงรูปการ์ด

        if (!firstCardFlipped) {
            firstRow = row;
            firstCol = col;
            firstCardFlipped = true;
        } else {
            if (board[row][col] == board[firstRow][firstCol]) {
                revealedPairs++;
                if (revealedPairs == (gridSize / 2)) {
                    long endTime = System.currentTimeMillis();
                    long elapsedTime = (endTime - startTime) / 1000;
                    saveRank(elapsedTime);
                    JOptionPane.showMessageDialog(frame, "You win! Time: " + elapsedTime + " sec\nMistakes: " + mistakes);
                }
            } else {
                mistakes++;
                timer = new Timer(500, e -> {
                    buttons[row][col].setIcon(backIcon); // รีเซ็ตไอคอนกลับเป็นรูปหลังไพ่
                    buttons[firstRow][firstCol].setIcon(backIcon); // รีเซ็ตไอคอนกลับเป็นรูปหลังไพ่
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
        showMenu();
    }

    private void saveRank(long time) {
        try (FileWriter fw = new FileWriter("ranks.txt", true); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(difficulty + "," + time + "," + mistakes + "," + new Date());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void showMenu() {
        JFrame menuFrame = new JFrame("Memory Game Menu");
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setSize(300, 300);
        menuFrame.setLayout(new GridLayout(6, 1));

        String[] options = {"Easy (4x4)", "Normal (6x6)", "Hard (6x11)", "Nightmare (6x17)", "Ranks", "Exit"};

        for (int i = 0; i < options.length; i++) {
            JButton button = new JButton(options[i]);
            final int choice = i;
            button.addActionListener(e -> {
                 if (choice == 4) {
                    showRankMenu();
                } else {
                    int rows = 4 + (choice * 2);
                    int cols = 4 + (choice * 2);
                    if (choice == 2) {
                        rows = 6;
                        cols = 11;
                    } else if (choice == 3) {
                        rows = 6;
                        cols = 17;
                    }
                    new MemoryGame(rows, cols, options[choice]);
                    menuFrame.dispose(); // ปิดเมนูหลังจากเริ่มเกม
                }
            });
            menuFrame.add(button);
        }

        menuFrame.setVisible(true);
    }

    private static void showRankMenu() {
        JFrame rankFrame = new JFrame("Rankings Menu");
        rankFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        rankFrame.setSize(300, 300);
        rankFrame.setLayout(new GridLayout(5, 1));

        String[] rankOptions = {"Rank Easy", "Rank Normal", "Rank Hard", "Rank Nightmare", "Back to Menu"};

        for (int i = 0; i < rankOptions.length; i++) {
            JButton button = new JButton(rankOptions[i]);
            final int choice = i;
            button.addActionListener(e -> {
                if (choice == 4) {
                    rankFrame.dispose();
                    showMenu();
                } else {
                    String difficulty = rankOptions[choice].replace("Rank ", "");
                    showRanksByDifficulty(difficulty);
                }
            });
            rankFrame.add(button);
        }

        rankFrame.setVisible(true);
    }

    private static void showRanksByDifficulty(String difficulty) {
        StringBuilder ranks = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader("ranks.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(difficulty)) {
                    ranks.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            ranks.append("No rankings yet.");
        }
        JOptionPane.showMessageDialog(null, ranks.toString(), difficulty + " Rankings", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MemoryGame::showMenu);
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
