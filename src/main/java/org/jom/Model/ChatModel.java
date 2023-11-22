package org.jom.Model;

public class ChatModel implements Comparable {
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

    public ChatModel(int id, int sender, int receiver, String content, String fist_name, String last_name) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.fist_name = fist_name;
        this.last_name = last_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int compareTo(Object comparestu) {
        int compareage=((ChatModel)comparestu).getId();
        return compareage-this.id;
    }

    @Override
    public String toString() {
        return "[ rollno=" + id + ", name=" + sender + ", age=" + receiver + "]";
    }

}
