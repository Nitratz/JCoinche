package com.jcoinche.server.game;

import io.netty.channel.Channel;

import java.util.ArrayList;

public class Player {

    ArrayList<Card> mCards;
    Channel mChannel;
    boolean isOwner;
    boolean isPlaying;

    public Player(boolean owner, Channel ch) {
        this.isOwner = owner;
        mChannel = ch;
        isPlaying = false;
    }

    public Channel getmChannel() {
        return mChannel;
    }

    public void setmChannel(Channel mChannel) {
        this.mChannel = mChannel;
    }

    public ArrayList<Card> getmCards() {
        return mCards;
    }

    public void setmCards(ArrayList<Card> mCards) {
        this.mCards = mCards;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
