package sample;

import java.io.*;
import java.net.Socket;
import java.security.spec.RSAOtherPrimeInfo;
import java.util.HashMap;
import java.util.*;

public class Node implements Runnable {
    private class NodeLookup{
        public HashMap<String, String> table;
        public NodeLookup(HashMap<String, String> table){
            this.table = table;
        }

        public String getIP(int pid){
            String ip_port = table.get(Integer.toString(pid));
            return ip_port.split(":")[0];
        }

        public int getPort(int pid){
            String ip_port = table.get(Integer.toString(pid));
            return Integer.parseInt(ip_port.split(":")[1]);
        }
    }

    private LamportClock localClock;
    private LamportMutex mutex;
    private int pid;
    private int port;
    private boolean request_crti = false;
    private NodeLookup lookup; // "pid" -> "id:port"
    public final List<Integer> other_pids;
    private HashMap<Integer, Socket> chan_map;

    // data collection stuff
    private int total_application_msgs = 0;
    private int total_protocol_msgs = 0;

    private int count_per_crit = 0;
    private long delay_per_crit = 0;
    private PrintWriter log_writer;

    // total crit section executions - Stop after 20

    private int crit_executions = 0;

    public Node(int pid, String ConfigFile){
        this.pid = pid;
        this.lookup = new NodeLookup(ConfigReader.getLookup(ConfigFile));
        //System.out.println("clock from config: "+ConfigReader.clocks.get(this.pid));
        this.port = lookup.getPort(pid);
        this.localClock = new LamportClock(ConfigReader.clocks.get(this.pid));
        this.other_pids = new ArrayList<Integer>();
        for(String i:lookup.table.keySet()){
            int id = Integer.parseInt(i);
            if(id!=this.pid){
                other_pids.add(id);
            }
        }
        this.mutex = new LamportMutex(this);
        String file_name = "node"+this.pid+".log";
        try{
            this.log_writer = new PrintWriter(file_name, "UTF-8");
            log_writer.println(String.format("%-12s %-12s", "proto_msgs", "delay_duration"));
        } catch (FileNotFoundException|UnsupportedEncodingException ex){
            ex.printStackTrace();
        }
        this.chan_map = new HashMap<Integer,Socket>();
    }

    /*
    * Call init_connections before starting the main node thread
    * wait for 2 seconds on each exception and keep trying to
    * establish all connections before going further
    * */

    public void init_connections(){
        for(int pid: other_pids){
            if(this.chan_map.containsKey(pid)){
                continue;
            }
            String receiver_ip = lookup.getIP(pid);
            int receiver_port = lookup.getPort(pid);
            try (Socket sock = new Socket(receiver_ip, receiver_port)){
                // OutputStram out = sock.getOutputStream();
                chan_map.put(pid, sock);
            }catch(IOException ex){
                System.out.println("Trying to establish connections with "+receiver_ip+":"+receiver_port);
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException exp){
                    System.out.println("Thread.sleep exception: "+exp.getMessage());
                }
                init_connections();
            }
        }
    }
    public int getPid() { return this.pid; }
    public int getPort() { return this.port; }

    public void run_listener(){
        Thread listener = new NodeListenerThread(this);
        listener.start();
    }

    public synchronized void deliver_message(Message msg){
        //System.out.println(msg+"  "+msg.getType()+".from..."+msg.getSender()+"   local clock: "+localClock.peek());
        this.localClock.msg_event(msg.getClock());
        //System.out.println("new local clock: "+localClock.peek());
        if(msg.getType().equals("request")){
            System.out.println(msg.getType()+".from..."+msg);
            mutex.queue_request(msg);

            send_message(msg.getSender(), "reply");
        }
        else if(msg.getType().equals("release")){
            System.out.println(msg.getType()+".from..."+msg);
            mutex.release_request(msg);
        }
        else if(msg.getType().equals("reply")){
            System.out.println(msg.getType()+".from..."+msg);
            this.total_protocol_msgs += 1;
            mutex.reply_request(msg);
        }
    }

    public synchronized void send_message(int receiver,String type){
        // System.out.println("sending message from:"+ pid+" to "+receiver);
        /*
        * Reply messages are never multicasts so
        * we have to increase the local clock in send_message method
        * */
        if(type.equals("reply")){
            this.localClock.local_event();
        }
        // Data collection
        if(type.equals("application")){
            this.total_application_msgs += 1;
        }else {
            if(receiver != this.pid){
                /*
                * The replies you send are not for YOUR
                * crit execution requests.
                * */
                if(!type.equals("reply"))
                    this.total_protocol_msgs += 1;
            }
        }
        Message msg = new Message.MessageBuilder()
                .to(receiver)
                .from(this.pid)
                .clock(this.localClock.peek())
                .type(type).build();
        /*
        * If sending message to self?
        * type should better be "request"
        * */

        if(receiver == this.pid && type.equals("request")){
            this.mutex.queue_request(msg);
        }else{
            String receiver_ip = lookup.getIP(receiver);
            int receiver_port = lookup.getPort(receiver);
            try(Socket sock = new Socket(receiver_ip, receiver_port)){
                OutputStream out = sock.getOutputStream();
                ObjectOutputStream outstream = new ObjectOutputStream(out);
                outstream.writeObject(msg);
                outstream.close();
                out.close();
                sock.close();
            }catch(IOException ex){
                System.err.println("Can't send message: "+ex);
            }
        }
    }

    public synchronized void multicast(String type){
        this.localClock.local_event();
        if(type.equals("request")){
            System.out.println("Sending request at "+localClock.peek());
            send_message(this.pid, type);
        }
        for (String pid_str: lookup.table.keySet()){
            int pid_int = Integer.parseInt(pid_str);
            if(pid_int == this.pid){
                continue;
            }else{
                send_message(pid_int, type);
            }
        }
    }

    private synchronized void execute_crit(){
        write_sharedlog("Entering");
        System.out.println("Executing crit");
        this.crit_executions += 1;
        try{
            Thread.sleep(2000);

        }catch(InterruptedException ex) {
            System.out.println("execute_crit_thread_sleep_exception: "+ex);
        }
        write_sharedlog("Leaving");
    }

    @Override
    public void run() {
        run_listener();
        try{
            Thread.sleep(8000); // till I start other processes;

        }catch(InterruptedException ex){
            System.out.println("Interruption exception - run thread sleep - till other processes start");
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run(){
                cleanup();
            }
        });
        while(true){
            Random rand = new Random();
            int sleepTime = rand.nextInt(101-10)+10;

            try{
                Thread.sleep(sleepTime*10);
            }catch(InterruptedException ex){
                System.err.println(ex);
            }
            int decider = rand.nextInt(101-1)+1;
            if(this.crit_executions>=20){
                System.out.println("I'm done");
            }
            else if (decider >= 1 && decider <= 90){
                multicast("application");
            }
            else{
                long startTime = System.currentTimeMillis();
                int proto_messages_before = this.total_protocol_msgs;
                //multicast("request"); -- do that inside LamportMutex
                while(!mutex.request_crit_section()){
                    // keep checking if we can enter crit or not.
                }
                long endTime = System.currentTimeMillis();
                long duration = (endTime - startTime);
                execute_crit();
                mutex.release_request();
                multicast("release");
                int proto_messages_after = this.total_protocol_msgs;
                this.delay_per_crit = duration;
                this.count_per_crit = proto_messages_after - proto_messages_before;
                log_and_reset();
            }
        }
    }

    public synchronized void log_and_reset(){
        log_writer.println(String.format("%-12s %-12s", this.count_per_crit, this.delay_per_crit));
        this.delay_per_crit = 0;
        this.count_per_crit = 0;
    }

    public synchronized void write_sharedlog(String action){
        try{
            Writer sharedlog_writer = new PrintWriter(new FileWriter("shared.log", true));
            sharedlog_writer.append(
                    String.format("%-5s %-10s %-6s\n", this.pid, action, localClock.peek()));
            sharedlog_writer.close();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public void cleanup() {
        log_writer.println("Total application messages with sent: "+this.total_application_msgs);
        this.log_writer.close();

    }

    public static void main(String[] args){

        Node n = new Node(1, "./src/tmp/foo");
        n.init_connections();
        NodeListenerThread nlistener = new NodeListenerThread(n);
        nlistener.start();

    }
}
