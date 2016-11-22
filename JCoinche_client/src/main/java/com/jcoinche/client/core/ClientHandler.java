package com.jcoinche.client.core;

import com.jcoinche.client.CardGame;
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
                System.out.println("Veuillez entrez un nombre valide.");
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private boolean handleRead(String line) throws NumberFormatException {
        CardGame.CardClient.Builder req = CardGame.CardClient.newBuilder();

        switch (line.split(" ")[0].toLowerCase()) {
            case "bye":
                channel.close();
                return true;
            case "quit":
                channel.close();
                return true;
            case "start":
                        req.setType(CardGame.CardClient.CLIENT_TYPE.ROOM)
                        .setValue(mRoomNumber).build();
                return false;
            default:
                if (mRoomNumber == -1) {
                    mRoomNumber = Integer.parseInt(line);
                            req.setType(CardGame.CardClient.CLIENT_TYPE.ROOM)
                            .setValue(mRoomNumber).build();
                    channel.writeAndFlush(req);
                }
                return false;
        }
    }

    private void handleMessage(CardGame.CardServer msg) {
        switch (msg.getType()) {
            case WELCOME:
                System.out.println(msg.getName());
                break;
            case CARD:
                break;
            case TURN:
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
