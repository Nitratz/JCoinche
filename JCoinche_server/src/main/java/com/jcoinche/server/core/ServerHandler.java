package com.jcoinche.server.core;

import com.jcoinche.server.CardGame;
import com.jcoinche.server.game.Game;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;

public class ServerHandler extends SimpleChannelInboundHandler<CardGame.CardClient> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CardGame.CardClient msg) throws Exception {
        CardGame.CardServer req;
        HashMap<Integer, Game> rooms = RoomManager.getInstance().getmRooms();
        switch (msg.getType()) {
            case CONNEXION:
                req = MessageHandler.welcomeClient(rooms);
                break;
            case ROOM:
                req = MessageHandler.addInRoom(rooms, msg.getValue(), ctx.channel());
                break;
            default:
                req = CardGame.CardServer.newBuilder().setType(CardGame.CardServer.SERVER_TYPE.UNRECOGNIZED).build();
                break;
        }
        ctx.write(req);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        CardGame.CardServer.Builder req = CardGame.CardServer.newBuilder();
        req.setName("lol");
        ChannelFuture cf = ctx.write(req.build());

        ctx.flush();
        if (!cf.isSuccess()) {
            System.out.println("Send failed: " + cf.cause());
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
