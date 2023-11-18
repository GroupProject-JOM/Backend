package org.jom.Controller.Socket;

import org.jom.Dao.Chat.ChatDAO;
import org.jom.Database.ConnectionPool;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ServerEndpoint("/chat-web")
public class ChatWebSocket {

    private static final Map<String, Session> sessions = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        Map<String, List<String>> params = session.getRequestParameterMap();
        String username = params.get("username").get(0);
        String password = params.get("password").get(0);

        System.out.println(username);
        System.out.println(password);


        String authenticatedUser = authenticateUser(session, username, password);

        if (authenticatedUser != null) {
            sessions.put(authenticatedUser, session);
            broadcast("User " + authenticatedUser + " connected");
        }
    }

    @OnMessage
    public void onMessage(String message, Session senderSession) {
        String[] parts = message.split(":");
        if (parts.length == 2) {
            String recipientId = parts[0].trim();
            String content = parts[1].trim();

            Session recipientSession = sessions.get(recipientId);
            if (recipientSession != null) {
                sendMessageTo(recipientSession, generateUserId(senderSession) + ": " + content);
                ChatDAO chatDAO = new ChatDAO();
//                chatDAO.saveChatMessage(generateUserId(senderSession), recipientId, content);
            } else {
                sendMessageTo(senderSession, "User " + recipientId + " not found");
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        String userId = generateUserId(session);
        sessions.remove(userId);
        broadcast("User " + userId + " disconnected");
    }

    private String authenticateUser(Session session, String username, String password) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        try{
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                // User authentication successful
                return username;
            } else {
                // User authentication failed
                sendMessageTo(session, "Authentication failed. Invalid username or password.");
                session.close(); // Close the WebSocket connection for failed authentication
                return null;
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void sendMessageTo(Session recipientSession, String message) {
        try {
            recipientSession.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcast(String message) {
        for (Session session : sessions.values()) {
            sendMessageTo(session, message);
        }
    }

    private String generateUserId(Session session) {
        return session.getId();
    }

}
