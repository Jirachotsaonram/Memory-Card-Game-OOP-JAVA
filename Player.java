public class Player {
    private String name;
    private long timeTaken;

    public Player(String name) {
        this.name = name;
        this.timeTaken = 0;
    }

    public void setTime(long time) { this.timeTaken = time; }
    public long getTime() { return timeTaken; }
    public String getName() { return name; }
}
