package org.jom.Dao.Supplier.Collection;

import org.jom.Database.ConnectionPool;
import org.jom.Model.Collection.PickupModel;
import org.jom.Model.Collection.YardModel;

import java.sql.*;

public class YardDAO {
    public int addYard(YardModel yard){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int yardId = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql;
            if(yard.getAccount_id() == 0){
                sql = "INSERT INTO deliveries (supp_id,collec_id) VALUES (?,?)";
            }else {
                sql = "INSERT INTO deliveries (supp_id,collec_id,acc_id) VALUES (?,?,?)";
            }
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1,yard.getSupplier_id());
            preparedStatement.setInt(2,yard.getCollection_id());
            if(yard.getAccount_id() != 0){
                preparedStatement.setInt(3,yard.getAccount_id());
            }

            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                yardId = resultSet.getInt(1);
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
        return yardId;
    }
}
