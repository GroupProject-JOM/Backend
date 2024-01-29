package org.jom.Model;

public class NotificationModel {
    private int id;
    private String message;
    private int status;
    private String time;
    private int user;

    public NotificationModel(int id, String message, int status, String time, int user) {
        this.id = id;
        this.message = message;
        this.status = status;
        this.time = time;
        this.user = user;
    }
}
