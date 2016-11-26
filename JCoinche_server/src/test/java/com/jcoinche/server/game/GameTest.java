package com.jcoinche.server.game;

import com.jcoinche.protocol.CardGame;
import io.netty.channel.embedded.EmbeddedChannel;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

public class GameTest extends TestCase {

    Game mGame;

    @Before
    public void setUp() throws Exception {
        mGame = new Game();

        EmbeddedChannel eP = new EmbeddedChannel();
        EmbeddedChannel _eP = new EmbeddedChannel();
        mGame.setNewChannel(true, eP);
        mGame.setNewChannel(false, _eP);
        Player p = mGame.getPlayers().get(0);
        mGame.startGame(p);
    }

    @Ignore
    public void testStartGame() {
        mGame = new Game();
        EmbeddedChannel t = new EmbeddedChannel();
        mGame.setNewChannel(true, t);
        mGame.setNewChannel(false, t);
        CardGame.CardServer.Builder req;

        Player p = mGame.getPlayers().get(0);
        req = mGame.startGame(p);
        String started = req.getName();

        assertEquals("It's your turn !", started);
    }

    @Ignore
    public void testStartGameOther() {
        mGame = new Game();
        EmbeddedChannel t = new EmbeddedChannel();
        mGame.setNewChannel(true, t);
        mGame.setNewChannel(false, t);
        CardGame.CardServer.Builder req;

        Player p = mGame.getPlayers().get(1);
        req = mGame.startGame(p);
        String started = req.getName();

        assertEquals("You don't have correct rights to start", started);
    }

    public void testDisplayCards() {
        CardGame.CardServer.Builder req;

        Player p = mGame.getPlayers().get(0);
        req = mGame.displayCards(p);
        String display = req.getName();

        assertNotSame("The game must be started", display);
    }

    public void testPlayerDraw() {
        CardGame.CardServer.Builder req;
        // Card owned by player 1
        Player p = mGame.getPlayers().get(0);
        Card card = p.getmCards().get(0);
        String sCard = card.toString().toLowerCase();
        req = mGame.playerDraw(p, sCard);
        String draw = req.getName();
        assertEquals("You must now announce the color", draw);
        // Card doest owned by player 1
        Player _p = mGame.getPlayers().get(1);
        Card _card = p.getmCards().get(0);
        String _sCard = card.toString().toLowerCase();
        req = mGame.playerDraw(p, sCard);
        String _draw = req.getName();
        assertEquals("This card does not exists", _draw);
    }

    public void testPlayerCall() {
        CardGame.CardServer.Builder req;

        Player p = mGame.getPlayers().get(0);
        req = mGame.playerCall(p, "hearts");
        String call = req.getName();

        assertEquals("You called hearts", call);
    }

    public void testPlayerLiar() {
        CardGame.CardServer.Builder req;

        // Player 1 is playing
        Player p = mGame.getPlayers().get(0);
        Card card = p.getmCards().get(0);
        String sCard = card.toString().toLowerCase();
        String[] ar = sCard.split(" ");
        mGame.playerDraw(p, sCard);
        mGame.playerCall(p, ar[0]);
        // Player 2 says player 1 is a liar
        Player _p = mGame.getPlayers().get(1);
        req = mGame.playerLiar(_p);
        String liar = req.getName();

        assertEquals("You are not right", liar);
    }

    public void testFindPlayer() {
        Player p = mGame.getPlayers().get(1);
        Player newP = mGame.findPlayerByChannel(p.getmChannel());

        assertEquals(p, newP);
    }

    @After
    public void tearDown() throws Exception {

    }
}