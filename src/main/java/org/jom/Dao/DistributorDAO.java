package org.jom.Dao;

import org.jom.Database.ConnectionPool;
import org.jom.Model.DistributionModel;
import org.jom.Model.OutletModel;
import org.jom.Model.UserModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DistributorDAO {
    //Add distributor
    public boolean register(int user_id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "INSERT INTO distributors (user_id) VALUES (?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, user_id);

            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                status = true;
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
        return status;
    }

    //Get distributors and their allocated products with cash on hand
    public List<DistributionModel> getDistributors() {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        ArrayList<DistributionModel> distributors = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    u.id,\n" +
                    "    u.first_name,\n" +
                    "    u.last_name,\n" +
                    "    u.phone,\n" +
                    "    COALESCE(COUNT(DISTINCT pd.product_id), 0) AS allocated_products,\n" +
                    "    d.distributions,\n" +
                    "    d.cash\n" +
                    "FROM\n" +
                    "    distributors d\n" +
                    "        LEFT JOIN\n" +
                    "    (SELECT \n" +
                    "        distributor_id, product_id\n" +
                    "    FROM\n" +
                    "        product_distribution\n" +
                    "    WHERE\n" +
                    "        quantity != 0) pd ON d.id = pd.distributor_id\n" +
                    "        INNER JOIN\n" +
                    "    users u ON u.id = d.user_id\n" +
                    "GROUP BY d.id\n" +
                    "ORDER BY cash DESC;\n";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String fName = resultSet.getString(2);
                String lName = resultSet.getString(3);
                String phone = resultSet.getString(4);
                int products = resultSet.getInt(5);
                int distributions = resultSet.getInt(6);
                int cash = resultSet.getInt(7);

                DistributionModel distributor = new DistributionModel(id, fName, lName, products, Integer.toString(cash), distributions, phone);
                distributors.add(distributor);
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
        return distributors;
    }

    // Update distributor's cash on hand amount
    public boolean updateCashAmount(int cash, int distributor) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE distributors SET cash = cash + ? WHERE user_id = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, cash);
            preparedStatement.setInt(2, distributor);

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

    // Get distributor's cash on hand amount
    public int getCashAmount(int distributor) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int cash = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT cash FROM distributors WHERE user_id = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, distributor);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                cash = resultSet.getInt(1);
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
        return cash;
    }

    // Update distributor's sales and cash
    public boolean updateSales(int cash, int distributor) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE distributors SET cash = cash + ?,distributions = distributions + 1 WHERE user_id = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, cash);
            preparedStatement.setInt(2, distributor);

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
