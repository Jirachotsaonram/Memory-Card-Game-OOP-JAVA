public class GameTimer {
    private long startTime;
    private long endTime;

    public void start() { startTime = System.currentTimeMillis(); }
    public void stop() { endTime = System.currentTimeMillis(); }
    public long getElapsedTime() { return (endTime - startTime) / 1000; }
}
