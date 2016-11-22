package com.jcoinche.client.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class JCoincheClient {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 4242;

    public void startClient() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientInitializer());
            Channel c = bootstrap.connect(HOST, PORT).sync().channel();
            ClientHandler handle = c.pipeline().get(ClientHandler.class);

            handle.startClient();
        } finally {
            group.shutdownGracefully();
        }
    }
}
