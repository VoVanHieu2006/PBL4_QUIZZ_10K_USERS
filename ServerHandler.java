package com.quiz.lab;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;


public class ServerHandler
        extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(
            ChannelHandlerContext ctx
    ) {

        System.out.println(
                "CLIENT CONNECTED"
                        + "\n  Channel ID: "
                        + ctx.channel().id().asShortText()
                        + "\n  Remote: "
                        + ctx.channel().remoteAddress()
                        + "\n  EventLoop: "
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
                    "\nMESSAGE RECEIVED"
                            + "\n  Channel ID: "
                            + ctx.channel().id().asShortText()
                            + "\n  Message: "
                            + message
                            + "\n  EventLoop: "
                            + Thread.currentThread().getName()
            );

        //     String response =
        //             "Server received from "
        //                     + ctx.channel().id().asShortText()
        //                     + ": "
        //                     + message;

        //     ctx.executor().schedule(() -> {
        //     if (ctx.channel().isActive()) {
        //             ctx.writeAndFlush(
        //                     Unpooled.copiedBuffer(
        //                             response,
        //                             StandardCharsets.UTF_8
        //                     )
        //             );
        //     }
        //     }, 5, TimeUnit.SECONDS);

            String response =
                    "Server received from "
                            + ctx.channel().id().asShortText()
                            + ": "
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
                "CLIENT DISCONNECTED"
                        + "\n  Channel ID: "
                        + ctx.channel().id().asShortText()
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