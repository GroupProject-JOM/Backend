package org.jom.Dao.Supplier.Collection;

import org.jom.Database.ConnectionPool;
import org.jom.Model.AccountModel;
import org.jom.Model.Collection.CollectionModel;
import org.jom.Model.Collection.CollectionSingleViewModel;

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

    public CollectionSingleViewModel getCollection(int id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        CollectionSingleViewModel collection = new CollectionSingleViewModel();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "select c.id,c.p_method,c.s_method,p.pickup_date ,p.pickup_time , c.init_amount ,c.status,c.final_amount,c.value from pickups p inner join collections c on p.collection_id=c.id where c.id = ? union\n" +
                    "select c.id,c.p_method,c.s_method,d.delivery_date,d.delivery_time,c.init_amount ,c.status,c.final_amount,c.value from deliveries d inner join collections c on d.collec_id=c.id where c.id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,id);
            preparedStatement.setInt(2,id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                collection.setCollection_id(resultSet.getInt(1));
                collection.setpMethod(resultSet.getString(2));
                collection.setsMethod(resultSet.getString(3));
                collection.setDate(resultSet.getString(4));
                collection.setTime(resultSet.getString(5));
                collection.setInit_amount(resultSet.getInt(6));
                collection.setStatus(resultSet.getInt(7));
                collection.setFinal_amount(resultSet.getInt(8));
                collection.setValue(resultSet.getInt(9));
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
        return collection;
    }

    public int rowCount(){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int count = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT COUNT(*) AS Row_Count FROM collections WHERE status=2;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt(1);;
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
        return count;
    }
}
