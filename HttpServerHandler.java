package com.quiz.lab;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.nio.charset.StandardCharsets;

public class HttpServerHandler
        extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(
            ChannelHandlerContext ctx,
            FullHttpRequest request
    ) {

        String method =
                request.method().name();

        String uri =
                request.uri();

        System.out.println(
                "\n============================"
        );

        System.out.println(
                "HTTP REQUEST"
        );

        System.out.println(
                "Method : " + method
        );

        System.out.println(
                "URI    : " + uri
        );

        System.out.println(
                "Channel: "
                        + ctx.channel()
                        .id()
                        .asShortText()
        );

        System.out.println(
                "Thread : "
                        + Thread.currentThread()
                        .getName()
        );

        System.out.println(
                "============================"
        );

        String responseBody;

        HttpResponseStatus status;

        /*
         * GET /health
         */
        if (
                method.equals("GET")
                        && uri.equals("/health")
        ) {

            status =
                    HttpResponseStatus.OK;

            responseBody = """
                    {
                      "status": "ok",
                      "service": "netty-quiz-server"
                    }
                    """;
        }

        /*
         * GET /api/quizzes
         */
        else if (
                method.equals("GET")
                        && uri.equals("/api/quizzes")
        ) {

            status =
                    HttpResponseStatus.OK;

            responseBody = """
                    {
                      "quizzes": [
                        {
                          "id": "quiz-001",
                          "title": "Networking Fundamentals"
                        },
                        {
                          "id": "quiz-002",
                          "title": "HTTP Basics"
                        }
                      ]
                    }
                    """;
        }

        /*
         * Unknown route
         */
        else {

            status =
                    HttpResponseStatus.NOT_FOUND;

            responseBody = """
                    {
                      "message": "Route not found"
                    }
                    """;
        }

        byte[] responseBytes =
                responseBody.getBytes(
                        StandardCharsets.UTF_8
                );

        ByteBuf responseBuffer =
                Unpooled.wrappedBuffer(
                        responseBytes
                );

        FullHttpResponse response =
                new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1,
                        status,
                        responseBuffer
                );

        response.headers().set(
                HttpHeaderNames.CONTENT_TYPE,
                "application/json; charset=UTF-8"
        );

        response.headers().set(
                HttpHeaderNames.CONTENT_LENGTH,
                responseBytes.length
        );

        ctx.writeAndFlush(response);
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