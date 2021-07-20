package com.company.javasockets;

import jdk.swing.interop.SwingInterOpUtils;

import java.net.*;
import java.io.*;
import java.nio.Buffer;
import java.nio.file.ProviderNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static String[] names = { "Wily", "Felix", "Carlsbad", "Hobob" };
    private static String[] adjs = { "the gentle", "the un-gentle", "the overwrought", "the urbane" };
    public static  final int PORT = 9090;

    private static ArrayList<ClientHandler> clients = new ArrayList<>();

    private static ExecutorService pool = Executors.newFixedThreadPool(4);

    public static void main(String[] args) throws IOException{
        ServerSocket listener = new ServerSocket(PORT);

        while(true){
            System.out.println("[SERVER] Waiting for client connection ...");
            Socket client = listener.accept();
            System.out.println("[SERVER] Connected to client!");
            ClientHandler clientThread = new ClientHandler(client, clients);
            clients.add(clientThread);
            pool.execute(clientThread);
        }

        // PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        // BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));



    }

    public static String getRandomName(){
        String name = names[(int) (Math.random())* names.length];
        String adj = adjs[(int) (Math.random() * adjs.length)];
        return name + " " + adj;
    }
}
