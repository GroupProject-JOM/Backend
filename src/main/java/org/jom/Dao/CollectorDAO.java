package org.jom.Dao;

import org.jom.Database.ConnectionPool;
import org.jom.Model.Collection.CollectorModel;
import org.jom.Model.Collection.SupplyModel;
import org.jom.Model.EmployeeModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CollectorDAO {
    public boolean register(int user_id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "INSERT INTO collectors (u_id) VALUES (?)";
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

    public boolean updateTodayAmount(int amount, int user_id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE\n" +
                    " collectors \n" +
                    "SET\n" +
                    " today_amount = today_amount + ? \n" +
                    "WHERE\n" +
                    " u_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, amount);
            preparedStatement.setInt(2, user_id);

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

    //get Collector with collection date
    public List<CollectorModel> getCollectors(String date) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<CollectorModel> collectors = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    u.id,\n" +
                    "    u.first_name,\n" +
                    "    u.last_name,\n" +
                    "    u.phone,\n" +
                    "    COALESCE(c.collection_count, 0) AS collection_count,\n" +
                    "    COALESCE(col.today_total_amount, 0) AS today_total_amount\n" +
                    "FROM\n" +
                    "    users u\n" +
                    "        LEFT JOIN\n" +
                    "    (SELECT \n" +
                    "        e.user_id_, COUNT(c.id) AS collection_count\n" +
                    "    FROM\n" +
                    "        employees e\n" +
                    "    LEFT JOIN pickups p ON e.id = p.collector\n" +
                    "        AND p.pickup_date = ?\n" +
                    "    LEFT JOIN collections c ON p.collection_id = c.id AND c.delete = 0\n" +
                    "        AND c.status = 3\n" +
                    "    GROUP BY e.user_id_) c ON u.id = c.user_id_\n" +
                    "        LEFT JOIN\n" +
                    "    (SELECT \n" +
                    "        u.id AS user_id,\n" +
                    "            COALESCE(SUM(col.today_amount), 0) AS today_total_amount\n" +
                    "    FROM\n" +
                    "        users u\n" +
                    "    LEFT JOIN collectors col ON col.u_id = u.id\n" +
                    "    GROUP BY u.id) col ON u.id = col.user_id\n" +
                    "WHERE\n" +
                    "    u.role = 'collector'\n" +
                    "ORDER BY collection_count DESC;\n";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, date);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String fName = resultSet.getString(2);
                String lName = resultSet.getString(3);
                String phone = resultSet.getString(4);
                int count = resultSet.getInt(5);
                int total = resultSet.getInt(6);

                CollectorModel collector = new CollectorModel(id, fName, lName, phone, count, total);
                collectors.add(collector);
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
        return collectors;
    }
}
