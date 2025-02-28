import java.util.*;

public class RankManager {
    private List<Player> rankings = new ArrayList<>();

    public void addPlayer(Player player) {
        rankings.add(player);
        rankings.sort(Comparator.comparingLong(Player::getTime));
    }

    public void showRankings() {
        System.out.println("🏆 RANKINGS 🏆");
        for (int i = 0; i < rankings.size(); i++) {
            System.out.println((i + 1) + ". " + rankings.get(i).getName() + " - " + rankings.get(i).getTime() + "s");
        }
    }
}
