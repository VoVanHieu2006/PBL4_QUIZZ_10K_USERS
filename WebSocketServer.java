package com.quiz.lab;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class WebSocketServer {

    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {

        EventLoopGroup bossGroup =
                new MultiThreadIoEventLoopGroup(
                        1,
                        NioIoHandler.newFactory()
                );

        EventLoopGroup workerGroup =
                new MultiThreadIoEventLoopGroup(
                        2,
                        NioIoHandler.newFactory()
                );

        try {

            ServerBootstrap bootstrap =
                    new ServerBootstrap();

            bootstrap
                    .group(bossGroup, workerGroup)

                    .channel(NioServerSocketChannel.class)

                    .childOption(
                            ChannelOption.SO_KEEPALIVE,
                            true
                    )

                    .childHandler(
                            new ChannelInitializer<SocketChannel>() {

                                @Override
                                protected void initChannel(
                                        SocketChannel channel
                                ) {

                                    channel.pipeline().addLast(
                                            new HttpServerCodec()
                                    );

                                    channel.pipeline().addLast(
                                            new HttpObjectAggregator(
                                                    64 * 1024
                                            )
                                    );

                                    channel.pipeline().addLast(
                                            new WebSocketServerProtocolHandler( // biến kết nối từ HTTP => WebSocket
                                                    "/ws"
                                            )
                                    );

                                    channel.pipeline().addLast(
                                            new WebSocketFrameHandler()
                                    );
                                }
                            }
                    );

            Channel channel =
                    bootstrap
                            .bind(PORT)
                            .sync()
                            .channel();

            System.out.println(
                    "WebSocket server started at:"
            );

            System.out.println(
                    "ws://localhost:" + PORT + "/ws"
            );

            channel
                    .closeFuture()
                    .sync();

        } finally {

            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}