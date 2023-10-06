package org.jom.Dao.Supplier;

import org.jom.Database.ConnectionPool;
import org.jom.Model.EstateModel;

import java.sql.*;

public class EstateDAO {
    public int addEstate(EstateModel estate){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int estateId = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "INSERT INTO estates (name,location,area,supplier_id) VALUES (?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1,estate.getEstate_name());
            preparedStatement.setString(2,estate.getEstate_location());
            preparedStatement.setString(3,estate.getArea());
            preparedStatement.setInt(4,estate.getSupplier_id());

            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                estateId = resultSet.getInt(1);
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
        return estateId;
    }
}
