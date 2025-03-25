import javax.swing.*;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
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
    private boolean isProcessing = false; // เพิ่มตัวแปร isProcessing
    private Timer timer;
    private int revealedPairs = 0;
    private int gridSize;
    private int mistakes = 0;
    private int maxMistakes;
    private long startTime;
    private String difficulty;
    private int rows; // ประกาศตัวแปร rows เป็นตัวแปรของคลาส
    private int cols; // ประกาศตัวแปร cols เป็นตัวแปรของคลาส
    private JLabel timeLabel;
    private JLabel mistakesLabel;
    private JLabel remainingMistakesLabel;
    private JLabel correctPairsLabel;

    public MemoryGame(int rows, int cols, String level) {
        this.rows = rows; // กำหนดค่าให้ตัวแปร rows
        this.cols = cols; // กำหนดค่าให้ตัวแปร cols
        this.gridSize = rows * cols;
        this.difficulty = level;
        this.maxMistakes = (gridSize / 2); // กำหนดค่าสูงสุดของจำนวนครั้งที่เปิดผิดเป็นครึ่งหนึ่งของจำนวนไพ่ทั้งหมด
        frame = new JFrame("Memory Game");
        panel = new JPanel(new GridLayout(rows, cols));
        buttons = new JButton[rows][cols];
        images = new ImageIcon[gridSize / 2];
        board = new int[rows][cols];
        backIcon = new ImageIcon(new ImageIcon("cardback.png").getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)); // โหลดและปรับขนาดรูปหลังไพ่
        loadImages();
        setupBoard(rows, cols);
        startTime = System.currentTimeMillis();
        startTimer();
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
        
        JPanel infoPanel = new JPanel(new GridLayout(1, 4));
        timeLabel = new JLabel("Time: 0 sec");
        mistakesLabel = new JLabel("Mistakes: 0");
        remainingMistakesLabel = new JLabel("Mistakes Left: " + maxMistakes);
        correctPairsLabel = new JLabel("Correct Pairs: 0");
        infoPanel.add(timeLabel);
        infoPanel.add(mistakesLabel);
        infoPanel.add(remainingMistakesLabel);
        infoPanel.add(correctPairsLabel);
        frame.add(infoPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        JButton restartButton = new JButton("Return Game");
        restartButton.addActionListener(e -> restartGame(rows, cols, difficulty));
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

    private void startTimer() {
        timer = new Timer(1000, e -> {
            long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
            timeLabel.setText("Time: " + elapsedTime + " sec");
        });
        timer.start();
    }

    private void revealCard(int row, int col) {
        if (isProcessing || buttons[row][col].getIcon() != backIcon) return; // ตรวจสอบว่าการ์ดยังไม่ถูกเปิดและไม่อยู่ในระหว่างประมวลผล

        buttons[row][col].setIcon(images[board[row][col]]); // แสดงรูปการ์ด

        if (!firstCardFlipped) {
            firstRow = row;
            firstCol = col;
            firstCardFlipped = true;
        } else {
            isProcessing = true; // เริ่มการประมวลผล
            if (board[row][col] == board[firstRow][firstCol]) {
                revealedPairs++;
                correctPairsLabel.setText("Correct Pairs: " + revealedPairs);
                if (revealedPairs == (gridSize / 2)) {
                    long endTime = System.currentTimeMillis();
                    long elapsedTime = (endTime - startTime) / 1000;
                    saveStat(elapsedTime, "Win");
                    JOptionPane.showMessageDialog(frame, "You win! Time: " + elapsedTime + " sec\nMistakes: " + mistakes);
                    timer.stop();
                }
                isProcessing = false; // สิ้นสุดการประมวลผล
            } else {
                mistakes++;
                mistakesLabel.setText("Mistakes: " + mistakes);
                remainingMistakesLabel.setText("Mistakes Left: " + (maxMistakes - mistakes));
                if (mistakes >= maxMistakes) {
                    saveStat((System.currentTimeMillis() - startTime) / 1000, "Lose");
                    JOptionPane.showMessageDialog(frame, "Game Over! You made too many mistakes.");
                    timer.stop();
                    restartGame(rows, cols, difficulty);
                    return;
                }
                Timer flipBackTimer = new Timer(500, e -> {
                    buttons[row][col].setIcon(backIcon); // รีเซ็ตไอคอนกลับเป็นรูปหลังไพ่
                    buttons[firstRow][firstCol].setIcon(backIcon); // รีเซ็ตไอคอนกลับเป็นรูปหลังไพ่
                    firstCardFlipped = false;
                    isProcessing = false; // สิ้นสุดการประมวลผล
                });
                flipBackTimer.setRepeats(false);
                flipBackTimer.start();
            }
            firstCardFlipped = false;
        }
    }

    private void restartGame(int rows, int cols, String level) {
        frame.dispose();
        new MemoryGame(rows, cols, level);
    }

    private void saveStat(long time, String status) {
        try (FileWriter fw = new FileWriter("Stats.txt", true); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(difficulty + "," + time + "," + mistakes + "," + revealedPairs + "," + new Date() + "," + status);
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

        String[] options = {"Easy (4x4)", "Normal (6x6)", "Hard (6x11)", "Nightmare (6x17)", "Statistics", "Exit"};

        for (int i = 0; i < options.length; i++) {
            JButton button = new JButton(options[i]);
            final int choice = i;
            button.addActionListener(e -> {
                if (choice == 4) {
                    showStatsMenu();
                    menuFrame.dispose(); // ปิดเมนูหลังจากเริ่มเมนูสถิติ
                } else if (choice == 5) {
                    System.exit(0); // ปิดโปรแกรมเมื่อกดปุ่ม Exit
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

    private static void showStatsMenu() {
        JFrame statsFrame = new JFrame("Statistics Menu");
        statsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        statsFrame.setSize(300, 300);
        statsFrame.setLayout(new GridLayout(5, 1));

        String[] statsOptions = {"Statistics Easy", "Statistics Normal", "Statistics Hard", "Statistics Nightmare", "Back to Menu"};

        for (int i = 0; i < statsOptions.length; i++) {
            JButton button = new JButton(statsOptions[i]);
            final int choice = i;
            button.addActionListener(e -> {
                if (choice == 4) {
                    statsFrame.dispose();
                    showMenu();
                } else {
                    String difficulty = statsOptions[choice].replace("Statistics ", "");
                    showStatsByDifficulty(difficulty);
                }
            });
            statsFrame.add(button);
        }

        statsFrame.setVisible(true);
    }

    private static void showStatsByDifficulty(String difficulty) {
        String[] columnNames = {"Difficulty", "Time(s)", "Mistakes", "Correct Pairs", "Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        try (BufferedReader br = new BufferedReader(new FileReader("Stats.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(difficulty)) {
                    String[] data = line.split(",");
                    model.addRow(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JTable table = new JTable(model);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // กำหนดตัวเปรียบเทียบสำหรับคอลัมน์ที่ต้องการเรียงลำดับ
        sorter.setComparator(0, Comparator.comparing(String::toString)); // Difficulty
        sorter.setComparator(1, Comparator.comparingInt(o -> Integer.parseInt(o.toString()))); // Time(s)
        sorter.setComparator(2, Comparator.comparingInt(o -> Integer.parseInt(o.toString()))); // Mistakes
        sorter.setComparator(3, Comparator.comparingInt(o -> Integer.parseInt(o.toString()))); // Correct Pairs
        sorter.setComparator(4, Comparator.comparing(String::toString)); // Date
        sorter.setComparator(5, Comparator.comparing(String::toString)); // Status

        JScrollPane scrollPane = new JScrollPane(table);
        JOptionPane.showMessageDialog(null, scrollPane, difficulty + " Statistics", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MemoryGame::showMenu);
    }
}
