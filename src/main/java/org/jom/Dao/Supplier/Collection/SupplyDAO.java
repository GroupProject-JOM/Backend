package org.jom.Dao.Supplier.Collection;

import org.jom.Database.ConnectionPool;
import org.jom.Model.Collection.CollectionModel;
import org.jom.Model.Collection.SupplyModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SupplyDAO {
    //for stock-manager dashboard
    public List<SupplyModel> getAll() {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<SupplyModel> supplies = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT c.id,u.first_name,p.pickup_date, c.init_amount ,c.s_method\n" +
                    "FROM collections c\n" +
                    "INNER JOIN pickups p ON c.id = p.collection_id INNER JOIN suppliers s ON c.sup_id = s.id INNER JOIN users u ON u.id=s.user_id \n" +
                    "WHERE c.delete=0 AND c.status=1\n" +
                    "UNION\n" +
                    "SELECT c.id,u.first_name,d.delivery_date,c.init_amount ,c.s_method\n" +
                    "FROM collections c\n" +
                    "INNER JOIN deliveries d ON c.id = d.collec_id INNER JOIN suppliers s ON c.sup_id = s.id INNER JOIN users u ON u.id=s.user_id \n" +
                    "WHERE c.delete=0 AND c.status=1;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int collection_id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                String date = resultSet.getString(3);
                int amount = resultSet.getInt(4);
                String method = resultSet.getString(5);

                SupplyModel supply = new SupplyModel(collection_id, date, amount, name, method);
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

    //for relevant supplier dashboard
    public List<SupplyModel> getAll(int supplier_id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<SupplyModel> supplies = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT c.id,p.pickup_date ,p.pickup_time , c.init_amount ,c.status,c.final_amount,c.value \n" +
                    "FROM collections c\n" +
                    "INNER JOIN pickups p ON c.id = p.collection_id\n" +
                    "WHERE c.sup_id = ? AND c.delete=0\n" +
                    "UNION\n" +
                    "SELECT c.id,d.delivery_date,d.delivery_time,c.init_amount ,c.status,c.final_amount,c.value\n" +
                    "FROM collections c\n" +
                    "INNER JOIN deliveries d ON c.id = d.collec_id\n" +
                    "WHERE c.sup_id = ? AND c.delete=0 ORDER BY pickup_date;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, supplier_id);
            preparedStatement.setInt(2, supplier_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int collection_id = resultSet.getInt(1);
                String date = resultSet.getString(2);
                String time = resultSet.getString(3);
                int initial_amount = resultSet.getInt(4);
                int status = resultSet.getInt(5);
                int final_amoount = resultSet.getInt(6);
                int value = resultSet.getInt(7);

                SupplyModel supply = new SupplyModel(collection_id, date, time, initial_amount, status, final_amoount, value);
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

    // Get income
    public int getIncome(int supplier_id, String pattern) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        int income = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT SUM(value) AS total\n" +
                    "FROM (\n" +
                    "    SELECT c.id, c.value, c.delete\n" +
                    "    FROM pickups p\n" +
                    "    INNER JOIN collections c ON p.collection_id = c.id\n" +
                    "    WHERE c.sup_id = ? AND c.status=6 AND p.pickup_date LIKE " + "\'" + pattern + "\'" + "\n" +
                    "    \n" +
                    "    UNION\n" +
                    "    \n" +
                    "    SELECT c.id, c.value, c.delete\n" +
                    "    FROM deliveries d\n" +
                    "    INNER JOIN collections c ON d.collec_id = c.id\n" +
                    "    WHERE c.sup_id = ? AND c.status=6 AND d.delivery_date LIKE " + "\'" + pattern + "\'" + "\n" +
                    ") AS subquery;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, supplier_id);
            preparedStatement.setInt(2, supplier_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                income = resultSet.getInt(1);
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
        return income;
    }

    //for relevant supply request
    public SupplyModel getSupply(int collection_id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        SupplyModel supply = new SupplyModel();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT c.id,u.first_name,u.last_name,u.phone,c.s_method,p.pickup_date,p.pickup_time ,c.init_amount,c.p_method,e.location,e.area\n" +
                    "FROM collections c\n" +
                    "INNER JOIN pickups p ON c.id = p.collection_id INNER JOIN suppliers s ON c.sup_id = s.id INNER JOIN users u ON u.id=s.user_id INNER JOIN estates e ON e.id=p.estate_id\n" +
                    "WHERE c.delete=0 AND c.status=1 AND c.id=?\n" +
                    "UNION\n" +
                    "SELECT c.id,u.first_name,u.last_name,u.phone,c.s_method,d.delivery_date,d.delivery_time,c.init_amount,c.p_method,e.location,e.area\n" +
                    "FROM collections c\n" +
                    "INNER JOIN deliveries d ON c.id = d.collec_id INNER JOIN suppliers s ON c.sup_id = s.id INNER JOIN users u ON u.id=s.user_id INNER JOIN estates e ON e.supplier_id=s.id\n" +
                    "WHERE c.delete=0 AND c.status=1 AND c.id=? LIMIT 1;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, collection_id);
            preparedStatement.setInt(2, collection_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                supply.setId(resultSet.getInt(1));
                supply.setName(resultSet.getString(2));
                supply.setLast_name(resultSet.getString(3));
                supply.setPhone(resultSet.getString(4));
                supply.setMethod(resultSet.getString(5));
                supply.setDate(resultSet.getString(6));
                supply.setTime(resultSet.getString(7));
                supply.setAmount(resultSet.getInt(8));
                supply.setPayment_method(resultSet.getString(9));
                supply.setLocation(resultSet.getString(10));
                supply.setArea(resultSet.getString(11));
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
        return supply;
    }

    //Accept supply request
    public boolean acceptSupply(int collection_id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE collections SET status=2 WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, collection_id);

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

    //Reject supply request
    public boolean rejectSupply(int collection_id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE collections SET status=4 WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, collection_id);

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
