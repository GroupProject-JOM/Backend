package org.jom.Dao;

import org.jom.Database.ConnectionPool;
import org.jom.Model.DistributionModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DistributionDAO {
    // get all remaining products form product_distribution table from relevant product id
    public List<DistributionModel> getRemaining(int product_id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<DistributionModel> distributions = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    d.id AS distributor_id,\n" +
                    "    u.first_name,\n" +
                    "    u.last_name,\n" +
                    "    pd.quantity\n" +
                    "FROM\n" +
                    "    distributors d\n" +
                    "        INNER JOIN\n" +
                    "    users u ON d.user_id = u.id\n" +
                    "        INNER JOIN\n" +
                    "    product_distribution pd ON d.id = pd.distributor_id\n" +
                    "        AND pd.product_id = ?\n" +
                    "ORDER BY pd.quantity DESC;;\n";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, product_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String first_name = resultSet.getString(2);
                String last_name = resultSet.getString(3);
                int remaining = resultSet.getInt(4);

                DistributionModel distribution = new DistributionModel(id, first_name, last_name, remaining);
                distributions.add(distribution);
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
        return distributions;
    }

    // Update distributor distribution product amount
    public boolean UpdateDistributorAmount(int quantity, int product, int distributor) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE product_distribution pd SET pd.quantity = quantity + ? WHERE pd.product_id = ? AND pd.distributor_id=? ";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, quantity);
            preparedStatement.setInt(2, product);
            preparedStatement.setInt(3, distributor);

            int x = preparedStatement.executeUpdate();
            if (x != 0) {
                status = true;
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
        return status;
    }
}
