package com.jcoinche.client.core;

import com.jcoinche.protocol.CardGame;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientHandler extends SimpleChannelInboundHandler<CardGame.CardServer> {

    private int mRoomNumber = -1;
    private Channel channel;
    private CardGame.CardServer resp;
    private boolean stop;
    private BlockingQueue<CardGame.CardServer> resps = new LinkedBlockingQueue<>();

    public void startClient() {

        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(isr);

        CardGame.CardClient connexion = CardGame.CardClient.newBuilder()
                .setType(CardGame.CardClient.CLIENT_TYPE.CONNEXION)
                .setName("Client connected").build();
        channel.writeAndFlush(connexion);

        String line;
        stop = false;
        while (!stop) {
            try {
                if (isr.ready()) {
                    line = in.readLine();
                    stop = handleRead(line);
                }
                if (resps.size() != 0) {
                    resp = resps.take();
                    handleMessage(resp);
                }
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number.");
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private boolean handleRead(String line) throws NumberFormatException {
        CardGame.CardClient.Builder req = CardGame.CardClient.newBuilder()
                .setValue(mRoomNumber);
        String[] array = line.split(" ");
        switch (array[0].toLowerCase()) {
            // -- BYE
            case "bye":
                channel.close();
                return true;
            // -- QUIT
            case "quit":
                channel.close();
                return true;
            // -- START
            case "start":
                checkCommand(array, 1, CardGame.CardClient.CLIENT_TYPE.START);
                return false;
            // -- LIAR
            case "liar":
                checkCommand(array, 1, CardGame.CardClient.CLIENT_TYPE.LIAR);
                return false;
            // -- CALL
            case "call":
                checkCommand(array, 2, CardGame.CardClient.CLIENT_TYPE.CALL);
                return false;
            // -- DRAW
            case "draw":
                checkCommand(array, 3, CardGame.CardClient.CLIENT_TYPE.DRAW);
                return false;
            // -- CARDS
            case "cards":
                checkCommand(array, 1, CardGame.CardClient.CLIENT_TYPE.CARDS);
                return false;
            // -- ROOM
            case "room":
                checkCommand(array, 2, CardGame.CardClient.CLIENT_TYPE.ROOM);
                return false;
            case "cmd":
                printCommands();
                return false;
            default:
                printCommands();
                return false;
        }
    }

    public boolean checkCommand(String[] array, int len, CardGame.CardClient.CLIENT_TYPE type) {
        CardGame.CardClient.Builder req = CardGame.CardClient.newBuilder();
        if (mRoomNumber == -1 && type == CardGame.CardClient.CLIENT_TYPE.ROOM && array.length == 2) {
            req.setType(type).setValue(Integer.parseInt(array[1]));
            channel.writeAndFlush(req.build());
        } else if (mRoomNumber != -1 && array.length == len) {
            String name = array[0];
            if (len > 1) {
                StringBuilder str = new StringBuilder();
                for (int i = 1; i < array.length; i++) {
                    str.append(array[i]);
                    str.append(" ");
                }
                name = str.toString().substring(0, str.length() - 1);
            }
            req.setName(name).setType(type).setValue(mRoomNumber);
            channel.writeAndFlush(req.build());
        } else {
            System.out.println("Invalid command");
            return false;
        }
        return true;
    }

    private void handleMessage(CardGame.CardServer msg) {
        switch (msg.getType()) {
            case WELCOME:
                System.out.println(msg.getName());
                System.out.println("Type 'CMD' to get all commands to play");
                break;
            case CARDS:
                System.out.println("Your current deck :");
                System.out.print(msg.getName());
                break;
            case ROOM:
                mRoomNumber = msg.getValue();
                System.out.println(msg.getName());
                break;
            default:
                System.out.println(msg.getName());
                break;
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        channel = ctx.channel();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CardGame.CardServer msg) throws Exception {
        resps.add(msg);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("The server has disconnected\nDisconnection...");
        ctx.close();
        stop = true;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof IOException)
            System.out.println("The server has disconnected\nDisconnection...");
        ctx.close();
        stop = true;
    }

    private void printCommands() {
        StringBuilder str = new StringBuilder();
        str.append("List of Commands :\n");
        str.append("ROOM [NUMBER]\n");
        str.append("START [NO ARGS]\n");
        str.append("CARDS [NO ARGS\n");
        str.append("DRAW [CARD_COLOR & CARD_VALUE] (ex: Spades 9)\n");
        str.append("CALL [CARD_COLOR]\n");
        str.append("LIAR [NO ARGS]\n");
        str.append("BYE OR QUIT [NO ARGS]\n");
        System.out.print(str);
    }
}
