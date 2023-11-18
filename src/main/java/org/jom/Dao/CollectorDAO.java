package org.jom.Dao;

import org.jom.Database.ConnectionPool;
import org.jom.Model.EmployeeModel;

import java.sql.*;

public class CollectorDAO {
    public boolean register(int user_id){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "INSERT INTO collectors (u_id) VALUES (?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1,user_id);

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

    public boolean updateTodayAmount(int amount,int user_id){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE\n" +
                    " collectors \n" +
                    "SET\n" +
                    " today_amount = today_amount + ? \n" +
                    "WHERE\n" +
                    " u_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,amount);
            preparedStatement.setInt(2,user_id);

            int x = preparedStatement.executeUpdate();
            if(x !=0){
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
