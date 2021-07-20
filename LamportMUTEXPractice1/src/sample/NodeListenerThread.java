package sample;

import java.io.*;
import java.net.*;

public class NodeListenerThread extends Thread{
    //private ServerSocket server;
    private Node node_reference;
    public NodeListenerThread(Node n){
        this.node_reference = n;
    }

    @Override
    public void run() {
        System.out.println("Running listener node");
        try (ServerSocket serv = new ServerSocket(node_reference.getPort())){
            while(true){
                try{
                    Socket conn = serv.accept();
                    InputStream in = conn.getInputStream();
                    ObjectInputStream instream = new ObjectInputStream(in);
                    try{
                        Message msg = (Message) instream.readObject();
                        node_reference.deliver_message(msg);
                    }catch (ClassNotFoundException ex){
                        System.err.println(ex);
                    }
                }catch (IOException ex){
                    System.err.println(ex);
                }
            }
        }catch (IOException ex){
            System.err.println("couldn't start the server");
        }
        super.run();
    }
}
