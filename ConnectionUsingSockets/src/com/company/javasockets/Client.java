package com.company.javasockets;

import javax.swing.*;
import java.net.*;
import java.io.*;

public class Client {
    public static final String SERVER_IP = "10.0.0.5";
    public static final int PORT = 9090;
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(SERVER_IP, PORT);
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String serverResponse = input.readLine();

        JOptionPane.showMessageDialog(null, serverResponse);
        socket.close();
        System.exit(0);
    }
}
