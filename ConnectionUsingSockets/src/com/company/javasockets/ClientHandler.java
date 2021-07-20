package com.company.javasockets;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private ArrayList<ClientHandler> clients;
    public ClientHandler(Socket clientSocket, ArrayList<ClientHandler> clients) throws IOException{
        this.client = clientSocket;
        this.clients = clients;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream());
    }

    @Override
    public void run() {
        try{
            while(true){
                String request = in.readLine();
                if (request.contains("name")) {
                    out.println(Server.getRandomName());
                }else if(request.startsWith("say")) {
                    int firstSpace = request.indexOf(" ");
                    if(firstSpace!= -1){
                        outToAll(request.substring(firstSpace+1));
                    }
                }else{
                    out.println("Type 'tell me a name' to get a random name");
                }
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }finally {
            out.close();
            try {
                in.close();
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }

    private void outToAll(String msg){
        for(ClientHandler aClient:clients){
            aClient.out.println(msg);
        }
    }

}
