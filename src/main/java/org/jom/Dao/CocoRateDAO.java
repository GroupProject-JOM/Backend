package org.jom.Dao;

import org.jom.Database.ConnectionPool;
import org.jom.Model.CocoModel;
import org.jom.Model.OTPModel;
import org.jom.Model.OutletModel;

import java.sql.*;

public class CocoRateDAO {
    public CocoModel getLastRecord() {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        CocoModel cocoModel = new CocoModel();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    *\n" +
                    "FROM\n" +
                    "    coco_rate\n" +
                    "ORDER BY id DESC\n" +
                    "LIMIT 1;";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                cocoModel.setId(resultSet.getInt(1));
                cocoModel.setDate(resultSet.getString(2));
                cocoModel.setPrice(resultSet.getString(3));
            }

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (Exception ignore) {
            }
        }
        return cocoModel;
    }

    public int addRate(CocoModel cocoModel) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int rate_id = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "INSERT INTO coco_rate (date,price) VALUES (?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, cocoModel.getDate());
            preparedStatement.setString(2, cocoModel.getPrice());

            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                rate_id = resultSet.getInt(1);
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
        return rate_id;
    }

    public boolean updateRate(CocoModel cocoModel) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE coco_rate SET price=? WHERE date = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, cocoModel.getPrice());
            preparedStatement.setString(2, cocoModel.getDate());

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
