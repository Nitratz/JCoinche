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
        switch (msg.getType()) {
            case CONNEXION:
                req = MessageHandler.welcomeClient(rooms);
                break;
            case ROOM:
                req = MessageHandler.addInRoom(rooms, msg.getValue(), ctx.channel());
                break;
            case START:
                req = rooms.get(msg.getValue()).startGame(ctx.channel());
                break;
            case CARDS:
                req = rooms.get(msg.getValue()).displayCards(ctx.channel());
                break;
            case DRAW:
                req = rooms.get(msg.getValue()).playerDraw(ctx.channel(), msg.getName().toLowerCase());
                break;
            case CALL:
                rooms.get(msg.getValue()).playerCall(ctx.channel(), msg.getName().toLowerCase());
                return;
            case LIAR:
                req = rooms.get(msg.getValue()).playerLiar(ctx.channel());
                break;
            default:
                req = CardGame.CardServer.newBuilder().setType(CardGame.CardServer.SERVER_TYPE.FAILED).build();
                break;
        }
        ctx.writeAndFlush(req);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        //TODO Handle disconnection
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
