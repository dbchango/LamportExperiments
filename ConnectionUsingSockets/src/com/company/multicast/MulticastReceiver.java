package com.company.multicast;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.*;

public class MulticastReceiver extends Thread{
    protected MulticastSocket socket = null;
    protected byte[] buffer = new byte[256];

    @Override
    public void run() {
        super.run();
        try {
            socket = new MulticastSocket(4446);
            InetAddress group = InetAddress.getByName("230.0.0.0");
            socket.joinGroup(group);
            while (true){
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String received = new String(
                        packet.getData(), 0, packet.getLength());
                System.out.println(received);
                if("end".equals(received)) break;
            }
            socket.leaveGroup(group);
            socket.close();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
}
