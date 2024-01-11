package org.jom.Socket;

import org.jom.Dao.Supplier.Collection.CollectionDAO;
import org.jom.Dao.UserDAO;
import org.jom.Model.UserModel;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@ServerEndpoint("/verify-amount/{user}")
public class VerifyCoconutAmount {
    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private static final Map<String, Queue<String>> pendingNotifications = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("user") int user) {
        int authenticatedUser = authenticateUser(session, user);
        if (authenticatedUser != 0) {
            sessions.put(Integer.toString(authenticatedUser), session);
            Queue<String> notifications = pendingNotifications.get(Integer.toString(user));
            if (notifications != null && !notifications.isEmpty()) {
                for (String notification : notifications) {
                    sendNotification(session, notification);
                }
                pendingNotifications.remove(Integer.toString(user));
            }
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        String[] parts = message.split(":");
        if (parts.length == 3) {
            int sender_id = Integer.parseInt(parts[0].trim());
            String content = parts[1].trim();
            int collection_id = Integer.parseInt(parts[2].trim());
            CollectionDAO collectionDAO = new CollectionDAO();

            if (!content.startsWith("O") && !content.startsWith("D")) {
                int supplier = collectionDAO.getSupplierId(collection_id, sender_id);
                Session recipientSession = sessions.get(Integer.toString(supplier));
                if (recipientSession != null)
                    sendNotification(recipientSession, content + ":" + collection_id);
                else
                    savePendingNotification(Integer.toString(supplier), content + ":" + collection_id);

            } else {
                int collector = collectionDAO.getCollectorId(collection_id, sender_id);
                Session recipientSession = sessions.get(Integer.toString(collector));

                if (recipientSession != null)
                    sendNotification(recipientSession, content + ":" + collection_id);
                else {
                    savePendingNotification(Integer.toString(collector), content + ":" + collection_id);

                    // to admin
                    Session recipientSession2 = sessions.get("7");
                    if (recipientSession2 != null)
                        sendNotification(recipientSession2, content + ":" + collection_id);
                    else
                        savePendingNotification("7", content + ":" + collection_id);

                }
            }
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("user") String user) {
        sessions.remove(user);
    }


    private static void sendNotification(Session session, String message) {
        try {
            session.getAsyncRemote().sendText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void savePendingNotification(String senderId, String notification) {
        pendingNotifications.computeIfAbsent(senderId, k -> new ConcurrentLinkedQueue<>()).add(notification);
    }

    private int authenticateUser(Session session, int user_id) {
        if (user_id == 0) {
            sendNotification(session, "Invalid user.");
            return 0;
        }

        UserDAO userDAO = new UserDAO();
        UserModel user = userDAO.getUserById(user_id);

        if (user.getRole().equals("collector"))
            return user_id;
        else if (user.getRole().equals("supplier"))
            return user_id;
        else if (user.getRole().equals("admin"))
            return user_id;
        else {
            sendNotification(session, "Invalid user.");
            return 0;
        }

    }
}
