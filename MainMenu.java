import javax.swing.*;

public class MainMenu extends JFrame {
    public MainMenu() {
        setTitle("Memory Card Game");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new java.awt.GridLayout(6, 1));

        JButton easyBtn = new JButton("Easy");
        JButton normalBtn = new JButton("Normal");
        JButton hardBtn = new JButton("Hard");
        JButton nightmareBtn = new JButton("Nightmare");
        JButton rankBtn = new JButton("Ranks");
        JButton exitBtn = new JButton("Exit");

        easyBtn.addActionListener(e -> new GameUI(4, 4));
        normalBtn.addActionListener(e -> new GameUI(6, 6));
        hardBtn.addActionListener(e -> new GameUI(8, 8));
        nightmareBtn.addActionListener(e -> new GameUI(10, 10));
        exitBtn.addActionListener(e -> System.exit(0));

        add(easyBtn);
        add(normalBtn);
        add(hardBtn);
        add(nightmareBtn);
        add(rankBtn);
        add(exitBtn);

        setVisible(true);
    }
}
