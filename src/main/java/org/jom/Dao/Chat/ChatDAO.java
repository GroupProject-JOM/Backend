package org.jom.Dao.Chat;

import org.jom.Database.ConnectionPool;
import org.jom.Model.AccountModel;
import org.jom.Model.ChatModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatDAO {
    public void saveChatMessage(int sender, int recipient, String content) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "INSERT INTO chat_messages (sender, recipient, content) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, sender);
            preparedStatement.setInt(2, recipient);
            preparedStatement.setString(3, content);

            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ChatModel> loadChat(int sender, int recipient) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        ArrayList<ChatModel> messages = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT c.sender,c.recipient,c.content FROM jom_db.chat_messages c where c.sender=? OR c.recipient=?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, sender);
            preparedStatement.setInt(2, recipient);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int sender_id = resultSet.getInt(1);
                int receiver_id = resultSet.getInt(2);
                String content = resultSet.getString(3);

                ChatModel chat = new ChatModel(sender_id,receiver_id,content);
                messages.add(chat);
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
}
