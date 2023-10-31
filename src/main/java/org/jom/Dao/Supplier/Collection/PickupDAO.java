package org.jom.Dao.Supplier.Collection;

import org.jom.Database.ConnectionPool;
import org.jom.Model.Collection.CollectionModel;
import org.jom.Model.Collection.PickupModel;

import java.sql.*;

public class PickupDAO {
    public int addPickup(PickupModel pickup){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int pickupId = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql;
            if(pickup.getAccount_id() == 0){
                sql = "INSERT INTO pickups (pickup_date,pickup_time,s_id,collection_id,estate_id) VALUES (?,?,?,?,?)";
            }else {
                sql = "INSERT INTO pickups (pickup_date,pickup_time,s_id,collection_id,estate_id,account_id) VALUES (?,?,?,?,?,?)";
            }
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1,pickup.getDate());
            preparedStatement.setString(2,pickup.getTime());
            preparedStatement.setInt(3,pickup.getSupplier_id());
            preparedStatement.setInt(4,pickup.getCollection_id());
            preparedStatement.setInt(5,pickup.getEstate_id());
            if(pickup.getAccount_id() != 0){
                preparedStatement.setInt(6,pickup.getAccount_id());
            }

            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                pickupId = resultSet.getInt(1);
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
        return pickupId;
    }
}
