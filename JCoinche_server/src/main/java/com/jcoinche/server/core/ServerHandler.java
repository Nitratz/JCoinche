package com.jcoinche.server.core;

import com.jcoinche.protocol.CardGame;
import com.jcoinche.server.game.Game;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;

public class ServerHandler extends SimpleChannelInboundHandler<CardGame.CardClient> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CardGame.CardClient msg) throws Exception {
        CardGame.CardServer req;
        HashMap<Integer, Game> rooms = RoomManager.getInstance().getmRooms();
        req = MessageHandler.handleMessage(rooms, msg, ctx);
        ctx.writeAndFlush(req);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        HashMap<Integer, Game> rooms = RoomManager.getInstance().getmRooms();
        MessageHandler.handleDisconnection(rooms, ctx.channel());
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
