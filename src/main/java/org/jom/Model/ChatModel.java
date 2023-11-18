package org.jom.Model;

public class ChatModel {
    private int id;
    private int sender;
    private int reciver;
    private String content;

    public ChatModel(int sender, int reciver, String content) {
        this.sender = sender;
        this.reciver = reciver;
        this.content = content;
    }
}
