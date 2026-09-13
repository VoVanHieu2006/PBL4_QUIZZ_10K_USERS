package com.quiz.lab;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class WebSocketFrameHandler
        extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper();

    private String role;

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

        try {

            JsonNode json =
                    OBJECT_MAPPER.readTree(message);

            String type =
                    json.path("type").asText(); // lấy ra type

            // =========================
            // REGISTER
            // =========================

            if (type.equals("REGISTER")) {

                String requestedRole =
                        json.path("role").asText(); // lấy ra role

                handleRegister(
                        ctx.channel(),
                        requestedRole
                );

                return;
            }

            // =========================
            // TEACHER → START QUIZ
            // =========================

            if (type.equals("START_QUIZ")) {

                if (!"TEACHER".equals(role)) {

                    sendError(
                            ctx.channel(),
                            "Only teacher can start quiz"
                    );

                    return;
                }

                broadcastToStudents(
                        """
                        {"type":"QUIZ_STARTED"}
                        """
                );

                return;
            }

            // =========================
            // UNKNOWN MESSAGE
            // =========================

            sendError(
                    ctx.channel(),
                    "Unknown message type"
            );

        } catch (Exception e) {

            e.printStackTrace();

            sendError(
                    ctx.channel(),
                    "Invalid JSON"
            );
        }
    }

    private void handleRegister(
            Channel channel,
            String requestedRole
    ) {

        if ("TEACHER".equals(requestedRole)) {

            role = "TEACHER";

            ClientRegistry.registerTeacher(channel);

            channel.writeAndFlush(
                    new TextWebSocketFrame(
                            """
                            {"type":"REGISTERED","role":"TEACHER"}
                            """
                    )
            );

            return;
        }

        if ("STUDENT".equals(requestedRole)) {

            role = "STUDENT";

            ClientRegistry.registerStudent(channel);

            channel.writeAndFlush(
                    new TextWebSocketFrame(
                            """
                            {"type":"REGISTERED","role":"STUDENT"}
                            """
                    )
            );

            return;
        }

        sendError(
                channel,
                "Invalid role"
        );
    }

    private void broadcastToStudents(
            String message
    ) {

        System.out.println(
                "Broadcasting to "
                        + ClientRegistry.getStudentChannels().size()
                        + " students"
        );

        for (Channel studentChannel :
                ClientRegistry.getStudentChannels()) {

            studentChannel.writeAndFlush(
                    new TextWebSocketFrame(message)
            );
        }
    }

    private void sendError(
            Channel channel,
            String message
    ) {

        channel.writeAndFlush(
                new TextWebSocketFrame(
                        """
                        {"type":"ERROR","message":"%s"}
                        """
                                .formatted(message)
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

        ClientRegistry.remove(
                ctx.channel()
        );
    }

    @Override
    public void exceptionCaught(
            ChannelHandlerContext ctx,
            Throwable cause
    ) {

        cause.printStackTrace();

        ClientRegistry.remove(
                ctx.channel()
        );

        ctx.close();
    }
}