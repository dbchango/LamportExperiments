package sample;

public class LamportClock {
    private int d  = -1000;
    private long clock = System.currentTimeMillis();
    public LamportClock() {}
    public LamportClock(int d) {
        this.d = d;
    }

    public synchronized void local_event() { clock += d; }

    public synchronized void msg_event(long msg_clock) {
        this.local_event();
        if (msg_clock + d >= this.clock) {
            clock = msg_clock + d;
        }
    }
    public int increment() {return d;}
    public long peek() { return clock; }
}