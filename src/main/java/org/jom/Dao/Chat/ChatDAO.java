package org.jom.Dao.Chat;

import org.jom.Database.ConnectionPool;

import javax.websocket.Session;
import java.sql.*;

public class ChatDAO {
    public void saveChatMessage(String sender, String recipient, String content) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        // Insert the chat message into the database using JDBC
        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "INSERT INTO chat_messages (sender, recipient, content) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, sender);
            preparedStatement.setString(2, recipient);
            preparedStatement.setString(3, content);

            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
