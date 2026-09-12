package com.quiz.lab;

import io.netty.buffer.Unpooled;
import java.nio.charset.StandardCharsets;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {

    private static final String HOST =
            "127.0.0.1";

    private static final int PORT =
            8080;

    public static void main(String[] args)
            throws Exception {

        EventLoopGroup group =
                new MultiThreadIoEventLoopGroup(
                        1,
                        NioIoHandler.newFactory()
                );

        try {

            Bootstrap bootstrap =
                    new Bootstrap();

            bootstrap
                    .group(group)
                    .channel(NioSocketChannel.class)

                    .handler(
                            new ChannelInitializer<SocketChannel>() {

                                @Override
                                protected void initChannel(
                                        SocketChannel channel
                                ) {

                                    channel.pipeline().addLast(
                                            new ClientHandler()
                                    );
                                }
                            }
                    );

            Channel channel =
                    bootstrap
                            .connect(HOST, PORT)
                            .sync()
                            .channel();

            System.out.println("Client connected to " + HOST + ":" + PORT);

            channel.writeAndFlush(
                    Unpooled.copiedBuffer(
                            "Hello Server",
                            StandardCharsets.UTF_8
                    )
            );

            channel.closeFuture()
                   .sync();

        } finally {

            group.shutdownGracefully();
        }
    }
}






