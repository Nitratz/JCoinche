package com.jcoinche.server.game;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Game {
    private ArrayList<Player> mPlayers;

    private static final String[] mColors = {"Carreaux","Coeur", "Pique", "Trefle"};
    private static final String[] mValues = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "V", "D", "R"};
    private ArrayList<Card> mDeck;

    public Game() {
        mDeck = new ArrayList<>();
        mPlayers = new ArrayList<>();
        for (String value : mValues) {
            for (String color : mColors) {
                mDeck.add(new Card(color, value));
                Collections.shuffle(mDeck);
            }
        }
    }

    public Card drawCard() {
        return mDeck.remove(mDeck.size() - 1);
    }

    public void setNewChannel(boolean owner, Channel ch) {
        mPlayers.add(new Player(owner, ch));
    }

    public int getNbPlayers() {
        return mPlayers.size();
    }
}
