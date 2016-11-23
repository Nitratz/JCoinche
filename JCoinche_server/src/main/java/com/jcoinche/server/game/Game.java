package com.jcoinche.server.game;

import com.jcoinche.protocol.CardGame;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Game {
    private ArrayList<Player> mPlayers;

    private static final String[] mColors = {"Diamonds","Hearts", "Spades", "Clubs"};
    private static final String[] mValues = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
    private ArrayList<Card> mDeck;
    private boolean isStarted;

    public Game() {
        mDeck = new ArrayList<>();
        mPlayers = new ArrayList<>();
        isStarted = false;

        for (String value : mValues) {
            for (String color : mColors) {
                mDeck.add(new Card(color, value));
                Collections.shuffle(mDeck);
            }
        }
    }

    private void announceGeneric(String name) {
        CardGame.CardServer.Builder req = CardGame.CardServer.newBuilder()
                .setType(CardGame.CardServer.SERVER_TYPE.STARTED)
                .setName(name);
        for (Player p : mPlayers) {
            p.getmChannel().writeAndFlush(req.build());
        }
    }

    private void announceColor(String color, int player) {
        CardGame.CardServer.Builder req = CardGame.CardServer.newBuilder()
                .setType(CardGame.CardServer.SERVER_TYPE.CALL)
                .setName("Player " + player + " has announced " + color);
        for (Player p : mPlayers) {
            p.getmChannel().writeAndFlush(req.build());
        }
    }

    private Card drawCard() {
        return mDeck.remove(mDeck.size() - 1);
    }

    private void distributeCards() {
        int size = 52 / mPlayers.size();
        for (Player p : mPlayers) {
            ArrayList<Card> cards = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                cards.add(drawCard());
            }
            p.setmCards(cards);
        }
    }

    private void nextPlayerPlaying(int index) {
        CardGame.CardServer.Builder req = CardGame.CardServer.newBuilder()
                .setType(CardGame.CardServer.SERVER_TYPE.TURN)
                .setName("It's your turn");
        int last = mPlayers.size() - 1;
        if (index == last) {
            Player p = mPlayers.get(0);
            p.setPlaying(true);
            p.getmChannel().writeAndFlush(req);
        }
        else {
            Player p = mPlayers.get(index + 1);
            p.setPlaying(true);
            p.getmChannel().writeAndFlush(req);
        }
    }

    public void setNewChannel(boolean owner, Channel ch) {
        mPlayers.add(new Player(owner, ch));
    }

    public int getNbPlayers() {
        return mPlayers.size();
    }

    public CardGame.CardServer startGame(Channel ch) {
        CardGame.CardServer.Builder req = CardGame.CardServer.newBuilder()
                .setType(CardGame.CardServer.SERVER_TYPE.FAILED)
                .setName("The game is already running");
        if (!isStarted) {
            req.setName("Need 1 more player to start");
            if (mPlayers.size() <= 4 && (mPlayers.size() == 2 || mPlayers.size() == 4)) {
                req.setName("You don't have correct rights to start");
                for (Player p : mPlayers) {
                    if (p.getmChannel().equals(ch) && p.isOwner()) {
                        distributeCards();
                        p.setPlaying(true);
                        isStarted = true;
                        announceGeneric("The game has started !");
                        req.setType(CardGame.CardServer.SERVER_TYPE.TURN)
                                .setName("It's your turn !");
                        break;
                    }
                }
            }
        }
        return req.build();
    }

    public CardGame.CardServer displayCards(Channel ch) {
        CardGame.CardServer.Builder req = CardGame.CardServer.newBuilder()
                .setType(CardGame.CardServer.SERVER_TYPE.FAILED)
                .setName("The game must be started");
        if (isStarted) {
            StringBuilder str = new StringBuilder();
            for (Player p : mPlayers) {
                if (p.getmChannel().equals(ch)) {
                    req.setType(CardGame.CardServer.SERVER_TYPE.CARDS);
                    ArrayList<Card> cards = p.getmCards();
                    for (Card c : cards) {
                        str.append(c.toString());
                        str.append(" || ");
                    }
                    req.setName(str.toString() + "\n");
                    break;
                }
            }
        }
        return  req.build();
    }

    public CardGame.CardServer playerDraw(Channel ch, String card) {
        CardGame.CardServer.Builder req = CardGame.CardServer.newBuilder()
                .setType(CardGame.CardServer.SERVER_TYPE.FAILED)
                .setName("The game must be started");
        if (isStarted) {
            for (Player p : mPlayers) {
                if (p.getmChannel().equals(ch)) {
                    req.setName("It's not your turn !");
                    if (p.isPlaying()) {
                        int index = p.indexByValue(card.split(" ")[0], card.split(" ")[1]);
                        if (index != -1) {
                            mDeck.add(p.drawCard(index));
                            req.setType(CardGame.CardServer.SERVER_TYPE.DRAW);
                            req.setName("You must now announce the color");
                            //TODO Test Win ?
                        } else {
                            req.setName("This card does not exists");
                        }
                        break;
                    }
                }
            }
        }
        return req.build();
    }

    public void playerCall(Channel ch, String color) {
        CardGame.CardServer.Builder req = CardGame.CardServer.newBuilder()
                .setType(CardGame.CardServer.SERVER_TYPE.FAILED)
                .setName("The game must be started");
        if (isStarted) {
            for (int i = 0; i < mPlayers.size(); i++) {
                Player p = mPlayers.get(i);
                if (p.getmChannel().equals(ch)) {
                    req.setName("It's not your turn !");
                    if (p.isPlaying()) {
                        for (String s : mColors) {
                            if (s.toLowerCase().equals(color)) {
                                p.setCall(color);
                                p.setPlaying(false);
                                announceColor(color, mPlayers.indexOf(p) + 1);
                                nextPlayerPlaying(mPlayers.indexOf(p));
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    public CardGame.CardServer playerLiar(Channel ch) {
        CardGame.CardServer.Builder req = CardGame.CardServer.newBuilder()
                .setType(CardGame.CardServer.SERVER_TYPE.FAILED)
                .setName("The game must be started");
        if (isStarted) {
            req.setType(CardGame.CardServer.SERVER_TYPE.LIAR);
            for (Player p : mPlayers) {
                if (p.isPlaying()) {
                    int index = mPlayers.indexOf(p);
                    Player last;
                    if (index == 0)
                        last = mPlayers.get(mPlayers.size() - 1);
                    else
                        last = mPlayers.get(index - 1);
                    announceGeneric("Player " + (mPlayers.lastIndexOf(last) + 1) + " is maybe a liar");
                    if (!last.getCall().equals(mDeck.get(0).getmColor().toLowerCase())) {
                        announceGeneric("He was a liar !");
                        req.setName("You are right");
                        addCardPlayer(last.getmChannel());
                    } else {
                        announceGeneric("He was not a liar");
                        req.setName("You are not right");
                        addCardPlayer(ch);
                    }
                    break;
                }
            }
        }
        return req.build();
    }

    private void addCardPlayer(Channel ch) {
        for (Player p : mPlayers) {
            if (p.getmChannel().equals(ch)) {
                for (int i = 0; i < mDeck.size(); i++) {
                    p.addCard(drawCard());
                }
            }
        }
    }
}
