package com.jcoinche.server;

import com.jcoinche.server.core.JCoincheServer;

public class JCoinche {

    public static void main(String[] args) {
        try {
            new JCoincheServer().startServer();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
