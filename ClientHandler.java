package com.quiz.lab;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

public class ClientHandler
        extends ChannelInboundHandlerAdapter {

        @Override
        public void exceptionCaught(
                        ChannelHandlerContext ctx,
                        Throwable cause
        ) {

                cause.printStackTrace();
                ctx.close();
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
                    "Client received: "
                            + response
            );

        } finally {

            buffer.release();

            ctx.close();
        }
    }
}