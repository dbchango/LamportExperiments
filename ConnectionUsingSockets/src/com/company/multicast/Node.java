package com.company.multicast;

import java.io.IOException;

public class Node {


    public static void main(String[] args) {

        try{
            MulticastReceiver receiver = new MulticastReceiver();
            receiver.start();
            MulticastPublisher publisher = new MulticastPublisher();
            publisher.multicast("Hello");
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

}
