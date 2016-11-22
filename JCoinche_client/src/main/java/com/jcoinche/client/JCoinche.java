package com.jcoinche.client;

import com.jcoinche.client.core.JCoincheClient;
import io.netty.channel.nio.NioEventLoopGroup;

import javax.net.ssl.SSLContext;

public class JCoinche {

    public static void main(String [] args) {
        try {
            new JCoincheClient().startClient();
        } catch (Exception e) {
            System.out.println("Veuillez lancez le serveur avant le client.");
        }
    }

    public static int Program(int a, int b) {
        return a + b;
    }
}
