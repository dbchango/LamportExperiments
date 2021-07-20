package com.company.javasockets;

import jdk.swing.interop.SwingInterOpUtils;

import java.net.*;
import java.io.*;
import java.nio.file.ProviderNotFoundException;
import java.util.Date;

public class DateServer {
    public static  final int PORT = 9090;

    public static void main(String[] args) throws IOException{
        ServerSocket listener = new ServerSocket(PORT);
        System.out.println("[SERVER] Waiting for client connection ...");
        Socket client = listener.accept();
        System.out.println("[SERVER] Connected to client!");
        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        out.println( (new Date()).toString() );

        System.out.println("[SERVER] Sent date. Closing.");
        client.close();
        listener.close();

    }
}
