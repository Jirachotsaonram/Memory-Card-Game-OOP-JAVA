import java.util.*;

public class Deck {
    private List<Card> cards;

    public Deck(int size) {
        cards = new ArrayList<>();
        for (int i = 0; i < size / 2; i++) {
            cards.add(new Card(i));
            cards.add(new Card(i));
        }
        Collections.shuffle(cards); // สุ่มตำแหน่งไพ่
    }

    public List<Card> getCards() { return cards; }
}
