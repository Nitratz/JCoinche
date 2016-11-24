package com.jcoinche.server.core;

import com.jcoinche.protocol.CardGame;
import com.jcoinche.server.game.Game;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;

public class MessageHandler {

    public static CardGame.CardServer handleMessage(HashMap<Integer, Game> rooms, CardGame.CardClient msg, ChannelHandlerContext ctx) {
        CardGame.CardServer.Builder req = CardGame.CardServer.newBuilder()
                .setType(CardGame.CardServer.SERVER_TYPE.FAILED).setName("You must create a room before");
        Game room;
        boolean isStarted = false;
        if (msg.getValue() == -1)
            return req.build();
        room = rooms.get(msg.getValue());
        if (room != null && room.isStarted())
            isStarted = true;
        CardGame.CardClient.CLIENT_TYPE type = msg.getType();
        if (type != CardGame.CardClient.CLIENT_TYPE.CONNEXION && type != CardGame.CardClient.CLIENT_TYPE.ROOM &&
                type != CardGame.CardClient.CLIENT_TYPE.START) {
            if (!isStarted) {
                req.setName("The game must be started");
                return req.build();
            }
        } else if (isStarted && type == CardGame.CardClient.CLIENT_TYPE.START) {
            req.setName("The game is already running");
            return req.build();
        }
        switch (msg.getType()) {
            case CONNEXION:
                req = welcomeClient(rooms);
                break;
            case ROOM:
                req = addInRoom(rooms, msg.getValue(), ctx.channel());
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
                req = rooms.get(msg.getValue()).playerCall(ctx.channel(), msg.getName().toLowerCase());
                break;
            case LIAR:
                req = rooms.get(msg.getValue()).playerLiar(ctx.channel());
                break;
            default:
                req = CardGame.CardServer.newBuilder().setType(CardGame.CardServer.SERVER_TYPE.FAILED);
                break;
        }
        return req.build();
    }

    private static CardGame.CardServer.Builder welcomeClient(HashMap<Integer, Game> rooms) {
        CardGame.CardServer.Builder req = CardGame.CardServer.newBuilder();
        req.setType(CardGame.CardServer.SERVER_TYPE.WELCOME);
        StringBuilder str = new StringBuilder();
        str.append("Welcome Client! Choose or create a room [0-999]\n");
        if (rooms.size() != 0) {
            for (HashMap.Entry<Integer, Game> entry : rooms.entrySet()) {
                str.append("Room ");
                str.append(entry.getKey());
                str.append(" : ");
                str.append(entry.getValue().getNbPlayers());
                str.append("/");
                str.append("4\n");
            }
        } else
            str.append("No room were found.");
        req.setName(str.toString().substring(0, str.length() - 1));
        return req;
    }

    private static CardGame.CardServer.Builder addInRoom(HashMap<Integer, Game> rooms, int room, Channel ch) {
        CardGame.CardServer.Builder req = CardGame.CardServer.newBuilder();
        if (rooms.get(room) == null) {
            rooms.put(room, new Game());
            rooms.get(room).setNewChannel(true, ch);
            req.setType(CardGame.CardServer.SERVER_TYPE.ROOM)
                    .setValue(room)
                    .setName("You've just created the new game on room " + room);
        }
        else {
            Game game = rooms.get(room);
            req.setType(CardGame.CardServer.SERVER_TYPE.FAILED);
            if (game.getNbPlayers() < 4) {
                rooms.get(room).setNewChannel(false, ch);
                req.setType(CardGame.CardServer.SERVER_TYPE.ROOM);
                req.setName("You've just joined the game on room " + room);
            }
            else
                req.setName("The room " + room + " is already full.");
        }
        return req;
    }


}
