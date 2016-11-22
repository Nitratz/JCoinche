package com.jcoinche.server.core;

import com.jcoinche.server.game.Game;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.HashMap;

public class JCoincheServer {
    private static final int PORT = 4242;

    public void startServer() throws Exception {
        EventLoopGroup serverGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootStrap = new ServerBootstrap();
            bootStrap.group(serverGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ServerInitializer());

            bootStrap.bind(PORT).sync().channel().closeFuture().sync();
        } finally {
            serverGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
