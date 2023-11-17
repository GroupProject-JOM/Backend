package org.jom.Controller.Socket;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/websocket/{userId}")
public class Notification {

//    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();
//
//    @OnOpen
//    public void onOpen(Session session, @PathParam("userId") String userId) {
//        sessions.put(userId, session);
//
//    }
//
//    @OnMessage
//    public void onMessage(String message, Session session) {
//        // Handle messages received from clients
//        if (message.equals("CollectorEnteredAmount")) {
//            sendNotificationToSuppliers("Collector entered collected amount.");
//        }
//        sendNotificationToSuppliers(message);
//    }
//
//    @OnClose
//    public void onClose(Session session, @PathParam("userId") String userId) {
//        sessions.remove(userId);
//    }
//
//    public static void sendNotificationToSuppliers(String message) {
//        for (Session session : sessions.values()) {
//            if (session.isOpen()) {
//                session.getAsyncRemote().sendText(message);
//            }
//        }
//    }

    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private static final Map<String, Queue<String>> pendingNotifications = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        sessions.put(userId, session);

        // Check for and send pending notifications
        Queue<String> notifications = pendingNotifications.get(userId);
        if (notifications != null && !notifications.isEmpty()) {
            for (String notification : notifications) {
                sendNotification(session, notification);
//                System.out.println(notification.toString());
            }
            pendingNotifications.remove(userId);

            System.out.println("Over\n");
        }

    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // Handle messages received from clients
        if (message.equals("CollectorEnteredAmount")) {
            sendNotificationToSuppliers("Collector entered collected amount.");
        }
        sendNotificationToSuppliers(message);

        if (message.startsWith("SaveNotification")) {
            // Example message format: SaveNotification:supplier456:Collector entered collected amount.
            String[] parts = message.split(":");
            if (parts.length >= 3) {
                String recipientId = parts[1];
                String notification = parts[2];
                savePendingNotification(recipientId, notification);
            }
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("userId") String userId) {
        sessions.remove(userId);
    }

    public static void sendNotificationToSuppliers(String message) {
        for (Session session : sessions.values()) {
            if (session.isOpen()) {
                sendNotification(session, message);
            }
        }
    }

    private static void sendNotification(Session session, String message) {
        System.out.println(message+" - Out");
        try {
            session.getAsyncRemote().sendText(message);
            System.out.println(message+" - In");
        } catch (Exception e) {
            // Handle exceptions if unable to send message to the session
            e.printStackTrace();
        }
    }

    public static void savePendingNotification(String userId, String notification) {
        pendingNotifications.computeIfAbsent(userId, k -> new ConcurrentLinkedQueue<>()).add(notification);
    }
}
