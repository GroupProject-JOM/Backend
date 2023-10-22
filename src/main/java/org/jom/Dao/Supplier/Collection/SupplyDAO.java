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
            String sql = "SELECT c.id,p.pickup_date ,p.pickup_time , c.init_amount ,c.status,c.final_amount,c.value \n" +
                    "FROM collections c\n" +
                    "INNER JOIN pickups p ON c.id = p.collection_id\n" +
                    "WHERE c.sup_id = ?\n" +
                    "UNION\n" +
                    "SELECT c.id,d.delivery_date,d.delivery_time,c.init_amount ,c.status,c.final_amount,c.value\n" +
                    "FROM collections c\n" +
                    "INNER JOIN deliveries d ON c.id = d.collec_id\n" +
                    "WHERE c.sup_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,id);
            preparedStatement.setInt(2,id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int collection_id = resultSet.getInt(1);
                String date = resultSet.getString(2);
                String time = resultSet.getString(3);
                int initial_amount = resultSet.getInt(4);
                int status = resultSet.getInt(5);
                int final_amoount = resultSet.getInt(6);
                int value = resultSet.getInt(7);

                SupplyModel supply = new SupplyModel(collection_id,date,time,initial_amount,status,final_amoount,value);
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
