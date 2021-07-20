package sample;

import java.io.*;
import java.net.*;
public class MulticastReceiver {
    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[256];

    public void run(){
        try{
            socket = new MulticastSocket(4446);
            InetAddress group;
           // group = new InetAddress.getByName("230.0.0.0");
            //socket.joinGroup(group);
            while(true){
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(
                        packet.getData(), 0, packet.getLength()
                );
                if("end".equals(received)){
                    break;
                }
            }
            //socket.leaveGroup(group);
            socket.close();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
}
