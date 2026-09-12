package com.quiz.lab;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

public class ServerHandler
        extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(
            ChannelHandlerContext ctx
    ) {

        System.out.println(
                "Client connected: "
                        + ctx.channel().remoteAddress()
        );

        System.out.println(
                "Channel ID: "
                        + ctx.channel().id()
        );

        System.out.println(
                "EventLoop: "
                        + ctx.channel().eventLoop()
        );

        System.out.println(
                "Thread: "
                        + Thread.currentThread().getName()
        );
    }

    @Override
    public void channelRead(
            ChannelHandlerContext ctx,
            Object msg
    ) {

        ByteBuf buffer = (ByteBuf) msg;

        try {

            String message =
                    buffer.toString(
                            StandardCharsets.UTF_8
                    );

            System.out.println(
                    "Server received: "
                            + message
            );

            String response =
                    "Hello Client, I received: "
                            + message;

            ByteBuf responseBuffer =
                    ctx.alloc().buffer();

            responseBuffer.writeCharSequence(
                    response,
                    StandardCharsets.UTF_8
            );

            ctx.writeAndFlush(responseBuffer);

        } finally {

            buffer.release();
        }
    }

    @Override
    public void channelInactive(
            ChannelHandlerContext ctx
    ) {

        System.out.println(
                "Client disconnected"
        );
    }

    @Override
    public void exceptionCaught(
            ChannelHandlerContext ctx,
            Throwable cause
    ) {

        cause.printStackTrace();

        ctx.close();
    }
}