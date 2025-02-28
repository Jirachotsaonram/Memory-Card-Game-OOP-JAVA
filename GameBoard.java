import java.util.*; // ✅ เพิ่มบรรทัดนี้

public class GameBoard {
    private Card[][] board;
    private int rows, cols;

    public GameBoard(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        Deck deck = new Deck(rows * cols);
        board = new Card[rows][cols];
        List<Card> shuffledCards = deck.getCards(); // ✅ ใช้ List<Card> ดังนั้นต้อง import java.util.List;

        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = shuffledCards.get(index++);
            }
        }
    }

    public Card getCard(int row, int col) { return board[row][col]; }
}
