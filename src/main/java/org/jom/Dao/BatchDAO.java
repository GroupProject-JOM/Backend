package org.jom.Dao;

import org.jom.Database.ConnectionPool;
import org.jom.Model.BatchModel;
import org.jom.Model.ProductionModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BatchDAO {
    //create new production batch
    public int createProductionBatch(BatchModel batchModel) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int productionId = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "INSERT INTO batches (amount,requests,amount_by,products) VALUES (?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, batchModel.getAmount());
            preparedStatement.setString(2, batchModel.getRequests());
            preparedStatement.setString(3, batchModel.getAmount_by());
            preparedStatement.setString(4, batchModel.getProducts());

            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                productionId = resultSet.getInt(1);
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
        return productionId;
    }

    // get all production batches
    public List<BatchModel> getAllBatches() {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<BatchModel> batches = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    id, amount, products, created_date, status\n" +
                    "FROM\n" +
                    "    jom_db.batches;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                int amount = resultSet.getInt(2);
                String products = resultSet.getString(3);
                String created_by = resultSet.getString(4);
                int status = resultSet.getInt(5);

                BatchModel batchModel = new BatchModel(id,amount,products,created_by,status);
                batches.add(batchModel);
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
        return batches;
    }

    // get all ongoing batches
    public List<BatchModel> getAllOngoingBatches() {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<BatchModel> batches = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    id, amount, products, created_date, status\n" +
                    "FROM\n" +
                    "    jom_db.batches\n" +
                    "WHERE\n" +
                    "    status = 1;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                int amount = resultSet.getInt(2);
                String products = resultSet.getString(3);
                String created_by = resultSet.getString(4);
                int status = resultSet.getInt(5);

                BatchModel batchModel = new BatchModel(id,amount,products,created_by,status);
                batches.add(batchModel);
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
        return batches;
    }
}
