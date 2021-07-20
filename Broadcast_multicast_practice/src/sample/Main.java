package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

public class Main extends Application {

    private static DatagramSocket socket = null;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) throws IOException
    {

        System.out.println("Broadcast initialized");
        //broadcast("Hello", InetAddress.getByName("255.255.255.255"));
        //launch(args);
        List<InetAddress> broadcastList = listAllBroadcastAddresses();
    }

    public static void broadcast (
            String broadcastMessage, InetAddress address ) throws IOException {
        // List<InetAddress> broadcastList = listAllBroadcastAddresses();

        socket = new DatagramSocket();
        socket.setBroadcast(true);

        byte[] buffer = broadcastMessage.getBytes();

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 4445);

        socket.send(packet);
        socket.close();
    }
    // iterate all network interfaces to find their broadcast address
    public static List<InetAddress> listAllBroadcastAddresses() throws SocketException {
        List<InetAddress> broadcastList = new ArrayList<>(); // list to save broadcasts
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); // found interfaces
        while (interfaces.hasMoreElements()){ // in this loop we will iterate each interface of the previous line
            NetworkInterface networkInterface = interfaces.nextElement(); // interfaces iteration
            if(networkInterface.isLoopback() || !networkInterface.isUp()){
                continue;
            }
            networkInterface.getInterfaceAddresses().stream()
                    .map(a-> {
                        System.out.println(a.getAddress().getHostName());
                        return a.getBroadcast();
                    })
                    .filter(Objects::nonNull)
                    .forEach(broadcastList::add);
        }
        return broadcastList;
    }

}
