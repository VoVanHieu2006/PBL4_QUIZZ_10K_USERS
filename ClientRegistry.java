package com.quiz.lab;

import io.netty.channel.Channel;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientRegistry {

    private static volatile Channel teacherChannel; // volatile to prevent cache, đồng bộ giữa các thread

    private static final List<Channel> studentChannels =
            new CopyOnWriteArrayList<>();
    // final ngăn chặn việc reference tới một cái mới studentChannels = new CopyOnWriteArrayList<>();
    // CopyOnWriteArrayList là copy mảng khi ghi phù hợp với nhiều thread chạy song song
    // Ví dụ mảng A B C D, Thread 1 đang đọc, Thread 2 xóa C
    // Nếu như vậy rất có thể Thread 1 sẽ bị lỗi do mảng chỉ còn A B D
    // Còn khi sử dụng CopyOnWriteArrayList, thi mảng là mảng A B C D sẽ giữ nguyên, và copy ra mảng mới rồi mới xóa C
    private ClientRegistry() {
    }

    public static void registerTeacher(Channel channel) {

        teacherChannel = channel;

        System.out.println(
                "Teacher registered: "
                        + channel.id().asShortText()
        );
    }

    public static void registerStudent(Channel channel) {

        studentChannels.add(channel);

        System.out.println(
                "Student registered: "
                        + channel.id().asShortText()
        );
    }

    public static void remove(Channel channel) {

        if (channel.equals(teacherChannel)) {
            teacherChannel = null;
        }

        studentChannels.remove(channel);
    }

    public static Channel getTeacherChannel() {

        return teacherChannel;
    }

    public static List<Channel> getStudentChannels() {

        return studentChannels;
    }
}