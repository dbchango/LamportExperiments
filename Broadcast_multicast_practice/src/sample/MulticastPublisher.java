package sample;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MulticastPublisher {
    private DatagramSocket socket = null;
    private InetAddress group;
    private byte[] buffer;

    public void multicast(
            String multicastMessage
    ) throws IOException {
        socket = new DatagramSocket();
        group = InetAddress.getByName("230.0.0.0");
        buffer = multicastMessage.getBytes();
        DatagramPacket packet = new DatagramPacket(
                buffer, buffer.length, group, 4446
        );
        socket.send(packet);
        socket.close();
    }

}
