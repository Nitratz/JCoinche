package com.jcoinche.server.core;

import com.jcoinche.protocol.CardGame;
import com.jcoinche.server.game.Game;
import io.netty.channel.embedded.EmbeddedChannel;
import junit.framework.TestCase;
import org.junit.Ignore;

import java.util.HashMap;

public class MessageHandlerTest extends TestCase {

    HashMap<Integer, Game> mRooms;
    EmbeddedChannel ch;

    @Override
    public void setUp() throws Exception {
        mRooms = new HashMap<>();
        ch = new EmbeddedChannel();

        mRooms.put(15, new Game());
        MessageHandler.addInRoom(mRooms, 15, ch);
    }

    @Ignore
    public void testWelcomeClientWithoutRooms() throws Exception {
        CardGame.CardServer req;
        mRooms = new HashMap<>();

        req = MessageHandler.welcomeClient(mRooms).build();
        String welcome = req.getName();

        assertEquals("Welcome Client! Choose or create a room [0-999]\nNo room were found", welcome);
    }

    public void testWelcomeClient() throws Exception {
        CardGame.CardServer req;

        req = MessageHandler.welcomeClient(mRooms).build();
        String welcome = req.getName();

        assertEquals("Welcome Client! Choose or create a room [0-999]\nRoom 15 : 1/4", welcome);
    }


    public void testAddInRoomAlreadyAdded() throws Exception {
        CardGame.CardServer req;

        req = MessageHandler.addInRoom(mRooms, 0, ch).build();
        String add = req.getName();

        assertEquals("You are already in a room", add);
    }

    public void testAddInRoom() {
        CardGame.CardServer req;

        req = MessageHandler.addInRoom(mRooms, 12, new EmbeddedChannel()).build();
        String add = req.getName();

        assertEquals("You've just created the new game on room 12", add);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }
}