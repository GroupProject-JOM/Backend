package org.jom.Dao;

import org.jom.Database.ConnectionPool;
import org.jom.Model.NotificationModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    //Get notifications for relevant users
    public List<NotificationModel> getNotifications(int user) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        ArrayList<NotificationModel> notifications = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    *\n" +
                    "FROM\n" +
                    "    jom_db.notifications\n" +
                    "WHERE\n" +
                    "    receiver = ?\n" +
                    "ORDER BY time DESC\n" +
                    "LIMIT 10;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, user);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String message = resultSet.getString(2);
                int receiver = resultSet.getInt(3);
                String time = resultSet.getString(4);
                int status = resultSet.getInt(5);

                NotificationModel notification = new NotificationModel(id, message, status, time, receiver);
                notifications.add(notification);
            }

            resultSet.close();
            preparedStatement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (Exception ignore) {
            }
        }
        return notifications;
    }

    // Update seen status
    public boolean updateSeenStatus (int user,int notification) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE notifications SET status = 1 WHERE receiver = ? AND id = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, user);
            preparedStatement.setInt(2, notification);

            int x = preparedStatement.executeUpdate();
            if (x != 0) {
                status = true;
            }
            preparedStatement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (Exception ignore) {
            }
        }
        return status;
    }
}
