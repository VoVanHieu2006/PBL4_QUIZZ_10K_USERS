package com.quiz.lab;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;

public class HttpServer {

    private static final int PORT = 8080;

    public static void main(String[] args)
            throws Exception {

        /*
         * Boss:
         * Accept TCP connections.
         */
        EventLoopGroup bossGroup =
                new MultiThreadIoEventLoopGroup(
                        1,
                        NioIoHandler.newFactory()
                );

        /*
         * Worker:
         * Handle I/O of accepted connections.
         */
        EventLoopGroup workerGroup =
                new MultiThreadIoEventLoopGroup(
                        2,
                        NioIoHandler.newFactory()
                );

        try {

            /*
             * CORS:
             * Cho phép frontend localhost:3000
             * gọi backend localhost:8080.
             */
            CorsConfig corsConfig =
                    CorsConfigBuilder
                            .forAnyOrigin()
                            .allowedRequestHeaders("*")
                            .allowedRequestMethods(
                                    io.netty.handler.codec.http.HttpMethod.GET,
                                    io.netty.handler.codec.http.HttpMethod.POST,
                                    io.netty.handler.codec.http.HttpMethod.OPTIONS
                            )
                            .build();

            ServerBootstrap bootstrap =
                    new ServerBootstrap();

            bootstrap
                    .group(
                            bossGroup,
                            workerGroup
                    )
                    .channel(
                            NioServerSocketChannel.class
                    )
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

                                   

                                    /*
                                     * HTTP decoder + encoder
                                     */
                                    channel.pipeline().addLast(
                                            new HttpServerCodec() 
                                    );

                                    /*
                                     * Gom:
                                     *
                                     * HttpMessage
                                     * HttpContent
                                     * LastHttpContent
                                     *
                                     * thành:
                                     *
                                     * FullHttpRequest
                                     */
                                    channel.pipeline().addLast(
                                            new HttpObjectAggregator(
                                                    1024 * 1024 // Tối đa 1M bytes
                                            )
                                    );

                                     channel.pipeline().addLast(
                                            new CorsHandler(corsConfig)
                                    );

                                    /*
                                     * Application logic
                                     */
                                    channel.pipeline().addLast(
                                            new HttpServerHandler()
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
                    "HTTP server started at:"
            );

            System.out.println(
                    "http://localhost:" + PORT
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