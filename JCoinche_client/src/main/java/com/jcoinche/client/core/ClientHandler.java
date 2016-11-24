package com.jcoinche.client.core;

import com.jcoinche.protocol.CardGame;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientHandler extends SimpleChannelInboundHandler<CardGame.CardServer> {

    private int mRoomNumber = -1;
    private Channel channel;
    private CardGame.CardServer resp;
    private BlockingQueue<CardGame.CardServer> resps = new LinkedBlockingQueue<>();

    public void startClient() {

        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(isr);

        CardGame.CardClient connexion = CardGame.CardClient.newBuilder()
                .setType(CardGame.CardClient.CLIENT_TYPE.CONNEXION)
                .setName("Client connected").build();
        channel.writeAndFlush(connexion);

        String line;
        boolean stop = false;
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
                checkCommand(array, 1, CardGame.CardClient.CLIENT_TYPE.START, 0);
                return false;
            // -- LIAR
            case "liar":
                checkCommand(array, 1, CardGame.CardClient.CLIENT_TYPE.LIAR, 0);
                return false;
            // -- CALL
            case "call":
                checkCommand(array, 2, CardGame.CardClient.CLIENT_TYPE.CALL, 0);
                return false;
            // -- DRAW
            case "draw":
                checkCommand(array, 3, CardGame.CardClient.CLIENT_TYPE.DRAW, 0);
                return false;
            // -- CARDS
            case "cards":
                checkCommand(array, 1, CardGame.CardClient.CLIENT_TYPE.CARDS, 0);
                return false;
            // -- ROOM
            case "room":
                checkCommand(array, 2, CardGame.CardClient.CLIENT_TYPE.ROOM, mRoomNumber);
                return false;
            case "cmd":
                // TODO Command list
                return false;
            default:
                return false;
        }
    }

    private void checkCommand(String[] array, int len, CardGame.CardClient.CLIENT_TYPE type, int value) {
        CardGame.CardClient.Builder req = CardGame.CardClient.newBuilder();
        if (mRoomNumber == -1 && type == CardGame.CardClient.CLIENT_TYPE.ROOM && array.length == 2) {
            req.setType(type).setValue(Integer.parseInt(array[1]));
            channel.writeAndFlush(req.build());
        }
        else if (mRoomNumber != -1 && array.length == len) {
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
        }
        else
            System.out.println("Invalid command");
    }

    private void handleMessage(CardGame.CardServer msg) {
        switch (msg.getType()) {
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
        channel= ctx.channel();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CardGame.CardServer msg) throws Exception {
        resps.add(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
