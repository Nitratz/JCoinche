package com.jcoinche.server.core;

import com.jcoinche.protocol.CardGame;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();

        p.addLast(new ProtobufDecoder(CardGame.CardClient.getDefaultInstance()));
        p.addLast(new ProtobufEncoder());
        p.addLast(new ServerHandler());
    }

}
