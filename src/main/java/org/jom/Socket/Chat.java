package org.jom.Socket;

import org.jom.Dao.Chat.ChatDAO;
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

@ServerEndpoint("/chat/{user}")
public class Chat {
    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private static final Map<String, Queue<String>> pendingMessage = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("user") int user) {
        try {
            int authenticatedUser = authenticateUser(session, user);
            if (authenticatedUser != 0) {
                sessions.put(Integer.toString(authenticatedUser), session);
//                Queue<String> notifications = pendingMessage.get(Integer.toString(user));
//                if (notifications != null && !notifications.isEmpty()) {
//                    for (String notification : notifications) {
//                        sendMessage(session, notification);
//                    }
////                    pendingMessage.remove(Integer.toString(user));
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            String[] parts = message.split(":");
            ChatDAO chatDAO = new ChatDAO();
            UserDAO userDAO = new UserDAO();

            if (parts.length == 3) {
                int sender_id = Integer.parseInt(parts[0].trim());
                String content = parts[1].trim();
                int receiver_id = Integer.parseInt(parts[2].trim());

                Session recipientSession = sessions.get(Integer.toString(receiver_id));
                if (recipientSession != null) {
                    sendMessage(recipientSession, content);
                } else {
//                    savePendingMessage(Integer.toString(receiver_id), content);
                }
                chatDAO.saveChatMessage(sender_id, receiver_id, content);
            } else if (parts.length == 2) {
                int sender_id = Integer.parseInt(parts[0].trim());
                String content = parts[1].trim();

                Session recipientSession = sessions.get("3");
                content = content + sender_id;
                if (recipientSession != null) {
                    sendMessage(recipientSession, content);
                } else {
                    userDAO.updateSeen(0, sender_id);
//                    savePendingMessage("3", content);
                }
                content = content.substring(0, content.length() - 1);
                chatDAO.saveChatMessage(sender_id, 3, content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("user") String user) {
        try {
            sessions.remove(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void sendMessage(Session session, String message) {
        try {
            session.getAsyncRemote().sendText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void savePendingMessage(String senderId, String notification) {
        try {
            pendingMessage.computeIfAbsent(senderId, k -> new ConcurrentLinkedQueue<>()).add(notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int authenticateUser(Session session, int user_id) {
        if (user_id == 0) {
            sendMessage(session, "Invalid user.");
            return 0;
        }

        UserDAO userDAO = new UserDAO();
        UserModel user = userDAO.getUserById(user_id);

        if (user.getRole().equals("stock-manager"))
            return user_id;
        else if (user.getRole().equals("supplier"))
            return user_id;
        else {
            sendMessage(session, "Invalid user.");
            return 0;
        }


    }
}
