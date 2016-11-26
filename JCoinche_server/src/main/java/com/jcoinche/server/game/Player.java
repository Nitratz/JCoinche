package com.jcoinche.server.game;

import io.netty.channel.Channel;

import java.util.ArrayList;

public class Player {

    private ArrayList<Card> mCards;
    private Channel mChannel;
    private boolean isOwner;
    private String mCall;

    private boolean isPlaying;

    public Player(boolean owner, Channel ch) {
        this.isOwner = owner;
        mChannel = ch;
        mCall = "";
        mCards = null;
        isPlaying = false;
    }

    public String getCall() {
        return mCall;
    }

    public void setCall(String mCall) {
        this.mCall = mCall;
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

    public void addCard(Card card) { this.mCards.add(card); }

    public boolean isOwner() {
        return isOwner;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public Card drawCard(int index) {return mCards.remove(index);}

    public int indexByValue(String color, String value) {
        for (int i = 0; i < mCards.size(); i++) {
            Card c = mCards.get(i);
            if (color.equals(c.getmColor().toLowerCase()) && value.equals(c.getmValue().toLowerCase()))
                return i;
        }
        return -1;
    }
}
