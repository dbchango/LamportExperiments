package com.company.singleton;

import java.io.*;
import java.net.*;

public class Client {

    public Client(){
        try{
            Socket socket = new Socket("10.0.0.5", 6666);
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            dout.writeUTF("Hello server");
            dout.close();
            socket.close();
        }catch(IOException ex){
            ex.printStackTrace();
        }

    }

}
