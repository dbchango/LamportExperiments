package sample;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class LamportClock {
    private int d  = 1000;
    private long clock = System.currentTimeMillis();
    public LamportClock() {}
    public LamportClock(int d) {
        this.d = d;
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd: HH:mm:ss, SSS", Locale.US);
    public GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("US/Central"));
    public synchronized void local_event() { clock += d; }

    public synchronized void msg_event(long msg_clock) {
        this.local_event();
        if (msg_clock + d >= this.clock) {
            clock = msg_clock + d;
        }
    }
    public int increment() {return d;}
    public long peek() { return clock; }
    public String getHour(){
        calendar.setTimeInMillis(this.clock);
        return sdf.format(calendar.getTime());
    }
}