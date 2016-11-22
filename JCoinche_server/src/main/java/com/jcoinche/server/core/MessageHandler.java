package com.jcoinche.server.core;

import com.jcoinche.server.CardGame;
import com.jcoinche.server.game.Game;
import io.netty.channel.Channel;

import java.util.HashMap;

public class MessageHandler {

    public static CardGame.CardServer welcomeClient(HashMap<Integer, Game> rooms) {
        CardGame.CardServer.Builder req = CardGame.CardServer.newBuilder();
        req.setType(CardGame.CardServer.SERVER_TYPE.WELCOME);
        StringBuilder str = new StringBuilder();
        str.append("Welcome Client! Choose or create a room [0-999]\n");
        if (rooms.size() != 0) {
            for (HashMap.Entry<Integer, Game> entry : rooms.entrySet()) {
                str.append("Room : ");
                str.append(entry.getKey());
                str.append(" ");
                str.append(entry.getValue().getNbPlayers());
                str.append("/");
                str.append("4\n");
            }
        } else
            str.append("No room were found.");
        req.setName(str.toString().substring(0, str.length() - 1));
        return req.build();
    }

    public static CardGame.CardServer addInRoom(HashMap<Integer, Game> rooms, int room, Channel ch) {
        CardGame.CardServer.Builder req = CardGame.CardServer.newBuilder();
        if (rooms.get(room) == null) {
            rooms.put(room, new Game());
            rooms.get(room).setNewChannel(true, ch);
            req.setType(CardGame.CardServer.SERVER_TYPE.WELCOME)
                    .setName("You've just created the new game on room " + room);
        }
        else {
            Game game = rooms.get(room);
            if (game.getNbPlayers() < 4) {
                rooms.get(room).setNewChannel(false, ch);
                req.setType(CardGame.CardServer.SERVER_TYPE.WELCOME);
                req.setName("You've just joined the game on room " + room);
            }
            else
                req.setName("The room " + room + " is already full.");
        }
        return req.build();
    }
}
