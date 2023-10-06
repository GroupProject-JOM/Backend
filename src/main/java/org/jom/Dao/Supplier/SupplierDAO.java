package org.jom.Dao.Supplier;

import org.jom.Database.ConnectionPool;
import org.jom.Model.EstateModel;

import java.sql.*;

public class SupplierDAO {
    public int createSupplier(int user_id){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int supplier_id = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "INSERT INTO suppliers (user_id) VALUES (?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1,user_id);

            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                supplier_id = resultSet.getInt(1);
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
        return supplier_id;
    }
}
