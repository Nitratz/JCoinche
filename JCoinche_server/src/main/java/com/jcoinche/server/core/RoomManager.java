package com.jcoinche.server.core;

import com.jcoinche.server.game.Game;

import java.util.HashMap;

public class RoomManager {

    private HashMap<Integer, Game> mRooms = new HashMap<>();
    private static RoomManager INSTANCE = null;

    private RoomManager() {}

    public static synchronized RoomManager getInstance()
    {
        if (INSTANCE == null) {
            INSTANCE = new RoomManager();
        }
        return INSTANCE;
    }

    public HashMap<Integer, Game> getmRooms() {
        return mRooms;
    }

    public void setmRooms(HashMap<Integer, Game> mRooms) {
        this.mRooms = mRooms;
    }

}
