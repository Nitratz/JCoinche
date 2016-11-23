package com.jcoinche.client.core;

import com.jcoinche.protocol.CardGame;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
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
                if (mRoomNumber != -1) {
                    req.setType(CardGame.CardClient.CLIENT_TYPE.START);
                    channel.writeAndFlush(req);
                }
                else
                    System.out.println("You need to create a channel before");
                return false;
            // -- LIAR
            case "liar":
                if (mRoomNumber != -1) {
                    req.setType(CardGame.CardClient.CLIENT_TYPE.LIAR);
                    channel.writeAndFlush(req);
                }
                else
                    System.err.println("Invalid command");
                return false;
            // -- CALL
            case "call":
                if (mRoomNumber != -1 && array.length == 2) {
                    req.setType(CardGame.CardClient.CLIENT_TYPE.CALL)
                            .setName(array[1]);
                    channel.writeAndFlush(req);
                }
                else
                    System.err.println("Invalid command");
                return false;
            // -- DRAW
            case "draw":
                if (mRoomNumber != -1 && array.length == 3) {
                    req.setType(CardGame.CardClient.CLIENT_TYPE.DRAW)
                            .setName(array[1] + " " + array[2]);
                    channel.writeAndFlush(req);
                }
                else
                    System.err.println("Invalid command");
                return false;
            // -- CARDS
            case "cards":
                if (mRoomNumber != -1) {
                    req.setType(CardGame.CardClient.CLIENT_TYPE.CARDS);
                    channel.writeAndFlush(req);
                }
                else
                    System.err.println("First, connect or create a room");
                return false;
            // -- ROOM
            case "room":
                if (mRoomNumber == -1 && array.length == 2) {
                    mRoomNumber = Integer.parseInt(array[1].toLowerCase());
                            req.setType(CardGame.CardClient.CLIENT_TYPE.ROOM)
                            .setValue(mRoomNumber);
                    channel.writeAndFlush(req);
                }
                else
                    System.err.println("DRAW [ROOM NUMBER]");
                return false;
            case "cmd":
                // TODO Command list
                return false;
            default:
                return false;
        }
    }

    private void handleMessage(CardGame.CardServer msg) {
        switch (msg.getType()) {
            case CARDS:
                System.out.println("Your current deck :");
                System.out.print(msg.getName());
                break;
            default:
                System.out.println(msg.getName());
                break;
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        channel = ctx.channel();
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
