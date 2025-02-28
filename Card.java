public class Card {
    private int id; // หมายเลขไพ่ (สำหรับจับคู่)
    private boolean isFlipped;
    private boolean isMatched;

    public Card(int id) {
        this.id = id;
        this.isFlipped = false;
        this.isMatched = false;
    }

    public int getId() { return id; }
    public boolean isFlipped() { return isFlipped; }
    public boolean isMatched() { return isMatched; }

    public void flip() { isFlipped = !isFlipped; }
    public void setMatched() { isMatched = true; }
}

