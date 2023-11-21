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
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (Exception ignore) {
            }
        }
    }

    public List<ChatModel> loadChat(int sender, int recipient) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        ArrayList<ChatModel> messages = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    c.sender,\n" +
                    "    c.recipient,\n" +
                    "    c.content\n" +
                    "FROM\n" +
                    "    jom_db.chat_messages c\n" +
                    "WHERE\n" +
                    "    (c.sender = ? AND c.recipient = ?)\n" +
                    "    OR\n" +
                    "    (c.sender = ? AND c.recipient = ?);\n";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, sender);
            preparedStatement.setInt(2, recipient);
            preparedStatement.setInt(3, recipient);
            preparedStatement.setInt(4, sender);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int sender_id = resultSet.getInt(1);
                int receiver_id = resultSet.getInt(2);
                String content = resultSet.getString(3);

                ChatModel chat = new ChatModel(sender_id, receiver_id, content);
                messages.add(chat);
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (Exception ignore) {
            }
        }
        return messages;
    }

    public List<ChatModel> loadLastChat() {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        ArrayList<ChatModel> messages = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql1 = "SELECT \n" +
                    "    u.id\n" +
                    "FROM\n" +
                    "    users u\n" +
                    "WHERE\n" +
                    "    u.role = 'supplier' AND u.validity = 1;";
            PreparedStatement preparedStatement1 = connection.prepareStatement(sql1);
            ResultSet resultSet1 = preparedStatement1.executeQuery();
            while (resultSet1.next()) {
                int user_id = resultSet1.getInt(1);

                String sql2 = "SELECT \n" +
                        "    COALESCE(c.id, 0) AS id,\n" +
                        "    COALESCE(c.sender, u.id) AS sender,\n" +
                        "    COALESCE(c.recipient, u.id) AS recipient,\n" +
                        "    COALESCE(c.content, 'No messages') AS content,\n" +
                        "    u.first_name,\n" +
                        "    u.last_name\n" +
                        "FROM\n" +
                        "    users u\n" +
                        "LEFT JOIN\n" +
                        "    jom_db.chat_messages c ON \n" +
                        "    ((c.sender = ? AND c.recipient = 3) OR (c.sender = 3 AND c.recipient = ?))\n" +
                        "    AND (u.id = c.sender OR u.id = c.recipient)\n" +
                        "WHERE\n" +
                        "    u.id = ?\n" +
                        "ORDER BY \n" +
                        "    c.id DESC\n" +
                        "LIMIT 1;\n";
                PreparedStatement preparedStatement2 = connection.prepareStatement(sql2);
                preparedStatement2.setInt(1, user_id);
                preparedStatement2.setInt(2, user_id);
                preparedStatement2.setInt(3, user_id);
                ResultSet resultSet2 = preparedStatement2.executeQuery();

                while (resultSet2.next()) {
                    int sender_id = resultSet2.getInt(2);
                    int receiver_id = resultSet2.getInt(3);
                    String content = resultSet2.getString(4);
                    String first_name = resultSet2.getString(5);
                    String last_name = resultSet2.getString(6);

                    ChatModel chat = new ChatModel(sender_id, receiver_id, content,first_name,last_name);
                    messages.add(chat);
                }
                resultSet2.close();
                preparedStatement2.close();
            }
            resultSet1.close();
            preparedStatement1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (Exception ignore) {
            }
        }
        return messages;
    }
}
