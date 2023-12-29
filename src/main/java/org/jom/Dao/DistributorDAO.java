package org.jom.Dao;

import org.jom.Database.ConnectionPool;

import java.sql.*;

public class DistributorDAO {
//Add distributor
    public boolean register(int user_id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "INSERT INTO user_id (u_id) VALUES (?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, user_id);

            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                status = true;
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
        return status;
    }
}
