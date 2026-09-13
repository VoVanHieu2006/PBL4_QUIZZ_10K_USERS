package com.quiz.lab;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class WebSocketFrameHandler
        extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    public void channelActive(
            ChannelHandlerContext ctx
    ) {

        System.out.println(
                "\n=== CHANNEL ACTIVE ==="
        );

        System.out.println(
                "Channel : "
                        + ctx.channel().id().asShortText()
        );

        System.out.println(
                "Thread  : "
                        + Thread.currentThread().getName()
        );
    }

    @Override
    protected void channelRead0(
            ChannelHandlerContext ctx,
            TextWebSocketFrame frame
    ) {

        String message = frame.text();

        System.out.println(
                "\n=== MESSAGE ==="
        );

        System.out.println(
                "Message : " + message
        );

        System.out.println(
                "Channel : "
                        + ctx.channel().id().asShortText()
        );

        System.out.println(
                "Thread  : "
                        + Thread.currentThread().getName()
        );

        ctx.channel().writeAndFlush(
                new TextWebSocketFrame(
                        "Server received: " + message
                )
        );
    }

    @Override
    public void channelInactive(
            ChannelHandlerContext ctx
    ) {

        System.out.println(
                "\n=== CHANNEL INACTIVE ==="
        );

        System.out.println(
                "Channel : "
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