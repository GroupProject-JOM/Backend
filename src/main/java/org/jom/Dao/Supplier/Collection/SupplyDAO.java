package org.jom.Dao.Supplier.Collection;

import org.jom.Database.ConnectionPool;
import org.jom.Model.Collection.CollectionModel;
import org.jom.Model.Collection.SupplyModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SupplyDAO {
    public List<SupplyModel> getAll(int id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<SupplyModel> supplies = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT pickups.id , pickups.pickup_date, pickups.pickup_time, collections.init_amount ,collections.status\n" +
                    "FROM collections\n" +
                    "INNER JOIN pickups\n" +
                    "ON collections.id = pickups.collection_id AND collections.sup_id = ? ;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int collection_id = resultSet.getInt(1);
                String date = resultSet.getString(2);
                String time = resultSet.getString(3);
                int initial_amount = resultSet.getInt(4);
                int status = resultSet.getInt(5);

                SupplyModel supply = new SupplyModel(collection_id,date,time,initial_amount,status);
                supplies.add(supply);
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
        return supplies;
    }
}
