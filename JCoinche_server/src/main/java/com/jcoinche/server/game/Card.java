package com.jcoinche.server.game;

public class Card {
    private String mValue;
    private String mColor;

    public Card(String color, String value) {
        this.mValue = value;
        this.mColor = color;
    }

    public String getmValue() {
        return mValue;
    }

    public String getmColor() {
        return mColor;
    }

    @Override
    public String toString() {
        return mColor + " " + mValue;
    }
}
