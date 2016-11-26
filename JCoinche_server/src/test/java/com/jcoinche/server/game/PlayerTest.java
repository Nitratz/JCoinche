package com.jcoinche.server.game;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;

import java.util.ArrayList;

public class PlayerTest extends TestCase {

    Player mPlayer;

    @Before
    public void setUp() throws Exception {
        mPlayer = new Player(true, null);
        mPlayer.setCall("Hello world !");
        mPlayer.setmCards(new ArrayList<Card>());
        mPlayer.addCard(new Card("spades", "9"));
        mPlayer.addCard(new Card("hearts", "10"));
    }

    public void testIsOwner() {
        assertTrue(mPlayer.isOwner());
    }

    public void testIsPlaying() {
        assertTrue(!mPlayer.isPlaying());
    }

    public void testGetCall() {
        String test = mPlayer.getCall();
        assertEquals("Hello world !", test);
    }

    public void testIndexByValue() {
        int index = mPlayer.indexByValue("hearts", "10");
        assertEquals(1, index);
    }

    public void testIndexByValues() {
        int index = mPlayer.indexByValue("diamonds", "5");
        assertEquals(-1, index);
    }

    @After
    public void tearDown() throws Exception {

    }

}