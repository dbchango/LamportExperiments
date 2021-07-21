package sample;

import java.io.Serializable;
import java.util.Comparator;

public class Message implements Serializable, Comparable<Message> {
    final int from, to;
    final long clock;
    final String type;
    final String content;

    public Message(MessageBuilder builder){
        this.from = builder.from; this.to = builder.to;
        this.clock = builder.clock;
        this.type = builder.type;
        this.content = builder.content;
    }

    @Override
    public int compareTo(Message that) {
        if ( this.clock > that.clock ) return +1;
        if ( this.clock < that.clock ) return -1;
        else{
            if ( this.getSender() > that.getSender() ) return +1;
            else return -1;
        }
    }

    @Override
    public String toString() {
        return String.format(from+" "+clock);
    }

    // getters and setters
    public int getSender() { return from; }
    public int getReceiver() { return to; }
    public long getClock() { return clock; }
    public String getType() { return type; }
    public String getContent() { return content; }

    public static class MessageBuilder {
        private int to, from;
        private long clock;
        private String type;
        private String content = "";
        public MessageBuilder(){}
        public MessageBuilder from(int from) {this.from = from; return this;}
        public MessageBuilder to(int to) {this.to = to; return this;}
        public MessageBuilder clock(long clock) {this.clock = clock; return this;}
        public MessageBuilder type(String type) {this.type = type; return this;}
        public MessageBuilder content(String content) {this.content = content; return this;}
        public Message build() {return new Message(this);}

    }

    public static void main(String[] args){
        Message msg = new MessageBuilder()
                .to(1)
                .from(0)
                .clock(11)
                .type("application").build();
        System.out.println(msg.getClock());

    }

}
