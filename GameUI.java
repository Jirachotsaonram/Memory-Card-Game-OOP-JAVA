import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameUI extends JFrame {
    private GameBoard gameBoard;
    private GameTimer gameTimer; // ✅ เพิ่มตัวจับเวลา
    private JLabel timerLabel; // ✅ เพิ่ม Label แสดงเวลา
    private Timer swingTimer; // ✅ ใช้ Swing Timer

    public GameUI(int rows, int cols) {
        gameBoard = new GameBoard(rows, cols);
        gameTimer = new GameTimer(); // ✅ เริ่มจับเวลา
        gameTimer.start();

        setTitle("Memory Game");
        setSize(600, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel gridPanel = new JPanel(new GridLayout(rows, cols));
        JButton[][] buttons = new JButton[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JButton btn = new JButton("?");
                int r = i, c = j;
                btn.addActionListener(e -> flipCard(r, c, btn));
                buttons[i][j] = btn;
                gridPanel.add(btn);
            }
        }

        // ✅ เพิ่มแสดงเวลา
        timerLabel = new JLabel("Time: 0 sec", SwingConstants.CENTER);
        add(timerLabel, BorderLayout.NORTH);
        add(gridPanel, BorderLayout.CENTER);

        // ✅ อัปเดตเวลาแบบเรียลไทม์ทุก 1 วินาที
        swingTimer = new Timer(1000, e -> {
            long elapsed = gameTimer.getElapsedTime();
            timerLabel.setText("Time: " + elapsed + " sec");
        });
        swingTimer.start();

        setVisible(true);
    }

    private void flipCard(int row, int col, JButton button) {
        Card card = gameBoard.getCard(row, col);
        if (!card.isFlipped() && !card.isMatched()) {
            card.flip();
            button.setText(String.valueOf(card.getId()));

            checkGameEnd(); // ✅ เช็คว่าเกมจบหรือยัง
        }
    }

    private void checkGameEnd() {
        // ✅ เช็คว่าไพ่ทั้งหมดจับคู่ครบหรือยัง
        boolean allMatched = true;
        for (int i = 0; i < gameBoard.getRows(); i++) {
            for (int j = 0; j < gameBoard.getCols(); j++) {
                if (!gameBoard.getCard(i, j).isMatched()) {
                    allMatched = false;
                    break;
                }
            }
        }

        if (allMatched) {
            gameTimer.stop();
            swingTimer.stop(); // ✅ หยุด Swing Timer

            long finalTime = gameTimer.getElapsedTime();
            JOptionPane.showMessageDialog(this, "🎉 ชนะแล้ว! เวลาที่ใช้: " + finalTime + " วินาที");

            this.dispose(); // ปิดหน้าต่างเกม
        }
    }
}
