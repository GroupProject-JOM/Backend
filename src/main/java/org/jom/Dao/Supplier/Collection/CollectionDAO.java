package org.jom.Dao.Supplier.Collection;

import org.jom.Database.ConnectionPool;
import org.jom.Model.AccountModel;
import org.jom.Model.Collection.CollectionModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CollectionDAO {
    public int addCollection(CollectionModel collection){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int collectionId = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "INSERT INTO collections (init_amount,p_method,s_method,sup_id) VALUES (?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1,collection.getInitial_amount());
            preparedStatement.setString(2,collection.getPayment_method());
            preparedStatement.setString(3,collection.getSupply_method());
            preparedStatement.setInt(4,collection.getSupplier_id());

            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                collectionId = resultSet.getInt(1);
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
        return collectionId;
    }

    public boolean updateStatus(int status,int id){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean isSuccess = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE collections SET status=? WHERE id = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,status);
            preparedStatement.setInt(2,id);

            int x = preparedStatement.executeUpdate();
            if(x !=0){
                isSuccess = true;
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
        return isSuccess;
    }
}
