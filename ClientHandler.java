package com.quiz.lab;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

public class ClientHandler
        extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(
            ChannelHandlerContext ctx
    ) {

        System.out.println(
                "Connected!"
                        + "\nChannel: "
                        + ctx.channel().id().asShortText()
                        + "\nEventLoop: "
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

            String response =
                    buffer.toString(
                            StandardCharsets.UTF_8
                    );

            System.out.println(
                    "Server response: "
                            + response
            );

        } finally {

            buffer.release();
        }
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