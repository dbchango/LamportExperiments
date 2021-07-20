package com.company.singleton;

import java.io.*;
import java.net.*;

public class Server {
    private static int PORT = 9090;
        public static void main(String[] args){
            try{
                ServerSocket listener = new ServerSocket(6666);
                Socket s = listener.accept(); // acepta la peticion del cliente
                DataInputStream dis = new DataInputStream(s.getInputStream());
                String str = (String) dis.readUTF();
                System.out.println("Clients says: " + str);
                listener.close();

            }catch(IOException ex){
                ex.printStackTrace();
            }

        }
}
