package org.jom.Dao;

import org.jom.Database.ConnectionPool;
import org.jom.Model.BatchModel;
import org.jom.Model.ProductionModel;

import java.sql.*;

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
}
