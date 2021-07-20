package com.company.javasockets;

import javax.swing.*;
import java.net.*;
import java.io.*;

public class Client {
    public static final String SERVER_IP = "10.0.0.5";
    public static final int PORT = 9090;
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(SERVER_IP, PORT);
        ServerConnection serverConn = new ServerConnection(socket);

        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        new Thread(serverConn).start();
        while(true){
            System.out.println("> ");
            String command = keyboard.readLine();
            if(command.equals("quit")) break;
            out.println(command);
        }
        socket.close();
        System.exit(0);
    }
}
