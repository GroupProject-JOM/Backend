package org.jom.Model;

public class ChatModel {
    private int id;
    private int sender;
    private int receiver;
    private String content;
    private String fist_name;
    private String last_name;

    public ChatModel(int sender, int receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    public ChatModel(int sender, int receiver, String content, String fist_name, String last_name) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.fist_name = fist_name;
        this.last_name = last_name;
    }
}
