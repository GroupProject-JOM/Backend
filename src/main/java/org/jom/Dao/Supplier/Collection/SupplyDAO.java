package org.jom.Dao.Supplier.Collection;

import org.jom.Database.ConnectionPool;
import org.jom.Model.Collection.CollectionModel;
import org.jom.Model.Collection.CollectorModel;
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
            String sql = "SELECT c.id,u.first_name,p.pickup_date, c.init_amount ,c.s_method,c.status\n" +
                    "FROM collections c\n" +
                    "INNER JOIN pickups p ON c.id = p.collection_id INNER JOIN suppliers s ON c.sup_id = s.id INNER JOIN users u ON u.id=s.user_id \n" +
                    "WHERE c.delete=0 AND (c.status=1 OR c.status=2)\n" +
                    "UNION\n" +
                    "SELECT c.id,u.first_name,d.delivery_date,c.init_amount ,c.s_method,c.status\n" +
                    "FROM collections c\n" +
                    "INNER JOIN deliveries d ON c.id = d.collec_id INNER JOIN suppliers s ON c.sup_id = s.id INNER JOIN users u ON u.id=s.user_id \n" +
                    "WHERE c.delete=0 AND c.status=1 ORDER BY id;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int collection_id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                String date = resultSet.getString(3);
                int amount = resultSet.getInt(4);
                String method = resultSet.getString(5);
                int status = resultSet.getInt(6);

                SupplyModel supply = new SupplyModel(collection_id, date, amount, name, method, status);
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

    //for relevant supplier dashboard ongoing table
    public List<SupplyModel> getAllOngoing(int supplier_id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<SupplyModel> supplies = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    c.id,\n" +
                    "    p.pickup_date,\n" +
                    "    p.pickup_time,\n" +
                    "    c.init_amount,\n" +
                    "    c.s_method,\n" +
                    "    c.status\n" +
                    "FROM\n" +
                    "    collections c\n" +
                    "        INNER JOIN\n" +
                    "    pickups p ON c.id = p.collection_id\n" +
                    "WHERE\n" +
                    "    c.sup_id = ? AND c.delete = 0\n" +
                    "        AND (c.status = 1 OR c.status = 2\n" +
                    "        OR c.status = 3\n" +
                    "        OR c.status = 4) \n" +
                    "UNION SELECT \n" +
                    "    c.id,\n" +
                    "    d.delivery_date,\n" +
                    "    d.delivery_time,\n" +
                    "    c.init_amount,\n" +
                    "    c.s_method,\n" +
                    "    c.status\n" +
                    "FROM\n" +
                    "    collections c\n" +
                    "        INNER JOIN\n" +
                    "    deliveries d ON c.id = d.collec_id\n" +
                    "WHERE\n" +
                    "    c.sup_id = ? AND c.delete = 0\n" +
                    "        AND (c.status = 1 OR c.status = 2\n" +
                    "        OR c.status = 4)\n" +
                    "ORDER BY pickup_date;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, supplier_id);
            preparedStatement.setInt(2, supplier_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int collection_id = resultSet.getInt(1);
                String date = resultSet.getString(2);
                String time = resultSet.getString(3);
                int initial_amount = resultSet.getInt(4);
                String method = resultSet.getString(5);
                int status = resultSet.getInt(6);

                SupplyModel supply = new SupplyModel(collection_id, date, initial_amount, time, method, status);
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

    //for relevant supplier dashboard past table
    public List<SupplyModel> getAllPast(int supplier_id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<SupplyModel> supplies = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    c.id,\n" +
                    "    p.collected_date,\n" +
                    "    c.final_amount,\n" +
                    "    c.s_method,\n" +
                    "    c.value,\n" +
                    "    c.status\n" +
                    "FROM\n" +
                    "    collections c\n" +
                    "        INNER JOIN\n" +
                    "    pickups p ON c.id = p.collection_id\n" +
                    "WHERE\n" +
                    "    c.sup_id = ? AND c.delete = 0\n" +
                    "        AND (c.status = 5 OR c.status = 6) \n" +
                    "UNION SELECT \n" +
                    "    c.id,\n" +
                    "    d.delivered_time,\n" +
                    "    c.final_amount,\n" +
                    "    c.s_method,\n" +
                    "    c.value,\n" +
                    "    c.status\n" +
                    "FROM\n" +
                    "    collections c\n" +
                    "        INNER JOIN\n" +
                    "    deliveries d ON c.id = d.collec_id\n" +
                    "WHERE\n" +
                    "    c.sup_id = ? AND c.delete = 0\n" +
                    "        AND (c.status = 5 OR c.status = 6)\n" +
                    "ORDER BY collected_date DESC;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, supplier_id);
            preparedStatement.setInt(2, supplier_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int collection_id = resultSet.getInt(1);
                String date = resultSet.getString(2);
                int final_amount = resultSet.getInt(3);
                String method = resultSet.getString(4);
                int value = resultSet.getInt(5);
                int status = resultSet.getInt(6);

                SupplyModel supply = new SupplyModel(collection_id, date, status, final_amount, value, method);
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
            String sql = "SELECT \n" +
                    "    c.id,\n" +
                    "    u_supplier.first_name,\n" +
                    "    u_supplier.last_name,\n" +
                    "    u_supplier.phone,\n" +
                    "    c.s_method,\n" +
                    "    p.pickup_date,\n" +
                    "    p.pickup_time,\n" +
                    "    c.init_amount,\n" +
                    "    c.p_method,\n" +
                    "    e.address,\n" +
                    "    e.location,\n" +
                    "    e.area,\n" +
                    "    c.status,\n" +
                    "    u_collector.first_name,\n" +
                    "    u_collector.last_name,\n" +
                    "    u_collector.phone,\n" +
                    "    c.final_amount,\n" +
                    "    p.collected_date\n" +
                    "FROM\n" +
                    "    collections c\n" +
                    "        INNER JOIN\n" +
                    "    pickups p ON c.id = p.collection_id\n" +
                    "        INNER JOIN\n" +
                    "    suppliers s ON c.sup_id = s.id\n" +
                    "        INNER JOIN\n" +
                    "    users u_supplier ON u_supplier.id = s.user_id\n" +
                    "        INNER JOIN\n" +
                    "    estates e ON e.id = p.estate_id\n" +
                    "        LEFT JOIN\n" +
                    "    employees emp ON emp.id = p.collector\n" +
                    "        LEFT JOIN\n" +
                    "    users u_collector ON u_collector.id = emp.user_id_\n" +
                    "WHERE\n" +
                    "    c.delete = 0\n" +
                    "        AND (c.status = 1 OR c.status = 2\n" +
                    "        OR c.status = 3\n" +
                    "        OR c.status = 4\n" +
                    "        OR c.status = 5\n" +
                    "        OR c.status = 6)\n" +
                    "        AND c.id = ? \n" +
                    "UNION SELECT \n" +
                    "    c.id,\n" +
                    "    u_supplier.first_name,\n" +
                    "    u_supplier.last_name,\n" +
                    "    u_supplier.phone,\n" +
                    "    c.s_method,\n" +
                    "    d.delivery_date,\n" +
                    "    d.delivery_time,\n" +
                    "    c.init_amount,\n" +
                    "    c.p_method,\n" +
                    "    d.collec_id,\n" +
                    "    d.collec_id,\n" +
                    "    d.acc_id,\n" +
                    "    c.status,\n" +
                    "    c.status,\n" +
                    "    c.status,\n" +
                    "    c.status,\n" +
                    "    c.final_amount,\n" +
                    "    d.delivered_time\n" +
                    "FROM\n" +
                    "    collections c\n" +
                    "        INNER JOIN\n" +
                    "    deliveries d ON c.id = d.collec_id\n" +
                    "        INNER JOIN\n" +
                    "    suppliers s ON c.sup_id = s.id\n" +
                    "        INNER JOIN\n" +
                    "    users u_supplier ON u_supplier.id = s.user_id\n" +
                    "WHERE\n" +
                    "    c.delete = 0\n" +
                    "        AND (c.status = 1 OR c.status = 2\n" +
                    "        OR c.status = 4\n" +
                    "        OR c.status = 5\n" +
                    "        OR c.status = 6)\n" +
                    "        AND c.id = ?;";
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
                supply.setAddress(resultSet.getString(10));
                supply.setLocation(resultSet.getString(11));
                supply.setArea(resultSet.getString(12));
                supply.setStatus(resultSet.getInt(13));
                supply.setC_fName(resultSet.getString(14));
                supply.setC_lName(resultSet.getString(15));
                supply.setC_phone(resultSet.getString(16));
                supply.setFinal_amount(resultSet.getInt(17));
                supply.setCollected_date(resultSet.getString(18));
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

    //Collector names and their collection count
    public List<CollectorModel> getCollectionCount(String date) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<CollectorModel> collectors = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    u.first_name AS Name,\n" +
                    "    e.id AS Employee_ID,\n" +
                    "    COALESCE(COUNT(p.id), 0) AS Row_Count\n" +
                    "FROM\n" +
                    "    jom_db.users u\n" +
                    "        INNER JOIN\n" +
                    "    employees e ON u.id = e.user_Id_\n" +
                    "        LEFT JOIN\n" +
                    "    pickups p ON p.collector = e.id AND p.pickup_date = ?\n" +
                    "        LEFT JOIN\n" +
                    "    collections c ON c.id = p.collection_id\n" +
                    "WHERE\n" +
                    "    u.role = 'collector'\n" +
                    "GROUP BY\n" +
                    "    e.id;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, date);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString(1);
                int employee_id = resultSet.getInt(2);
                int count = resultSet.getInt(3);

                CollectorModel collector = new CollectorModel(employee_id, name, count);
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

    //collection for relevant date
    public List<SupplyModel> getCollectionByDay(int collector, String pickup_date) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<SupplyModel> supplies = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    p.pickup_date,\n" +
                    "    p.pickup_time,\n" +
                    "    p.collection_id,\n" +
                    "    u.first_name,\n" +
                    "    u.last_name,\n" +
                    "    u.phone,\n" +
                    "    e.location,\n" +
                    "    e.area,\n" +
                    "    c.init_amount\n" +
                    "FROM\n" +
                    "    jom_db.pickups p\n" +
                    "        INNER JOIN\n" +
                    "    suppliers s ON s.id = p.s_id\n" +
                    "        INNER JOIN\n" +
                    "    users u ON s.user_id = u.id\n" +
                    "        INNER JOIN\n" +
                    "    collections c ON p.collection_id = c.id AND c.delete = 0\n" +
                    "        AND c.status = 3\n" +
                    "        INNER JOIN\n" +
                    "    estates e ON e.id = p.estate_id\n" +
                    "WHERE\n" +
                    "    p.pickup_date = ?\n" +
                    "        AND p.collector = ?;        ";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, pickup_date);
            preparedStatement.setInt(2, collector);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String date = resultSet.getString(1);
                String time = resultSet.getString(2);
                int collection_id = resultSet.getInt(3);
                String fist_name = resultSet.getString(4);
                String last_name = resultSet.getString(5);
                String phone = resultSet.getString(6);
                String location = resultSet.getString(7);
                String area = resultSet.getString(8);
                int initial_amount = resultSet.getInt(9);

                SupplyModel supply = new SupplyModel(collection_id, date, time, initial_amount, fist_name, last_name, phone, location, area);
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

    // upcoming collection for next two days
    public List<SupplyModel> getUpcomingCollections(int collector, String pickup_date, String day_after_tomorrow) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<SupplyModel> supplies = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    p.pickup_date,\n" +
                    "    p.pickup_time,\n" +
                    "    p.collection_id,\n" +
                    "    u.first_name,\n" +
                    "    u.last_name,\n" +
                    "    u.phone,\n" +
                    "    e.location,\n" +
                    "    e.area,\n" +
                    "    c.init_amount\n" +
                    "FROM\n" +
                    "    jom_db.pickups p\n" +
                    "        INNER JOIN\n" +
                    "    suppliers s ON s.id = p.s_id\n" +
                    "        INNER JOIN\n" +
                    "    users u ON s.user_id = u.id\n" +
                    "        INNER JOIN\n" +
                    "    collections c ON p.collection_id = c.id AND c.delete = 0\n" +
                    "        AND c.status = 3\n" +
                    "        INNER JOIN\n" +
                    "    estates e ON e.id = p.estate_id\n" +
                    "WHERE\n" +
                    "    p.pickup_date > ? AND p.pickup_date <= ?\n" +
                    "        AND p.collector = ?;        ";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, pickup_date);
            preparedStatement.setString(2, day_after_tomorrow);
            preparedStatement.setInt(3, collector);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String date = resultSet.getString(1);
                String time = resultSet.getString(2);
                int collection_id = resultSet.getInt(3);
                String fist_name = resultSet.getString(4);
                String last_name = resultSet.getString(5);
                String phone = resultSet.getString(6);
                String location = resultSet.getString(7);
                String area = resultSet.getString(8);
                int initial_amount = resultSet.getInt(9);

                SupplyModel supply = new SupplyModel(collection_id, date, time, initial_amount, fist_name, last_name, phone, location, area);
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

    //Get All collection count for relevant date
    public int getCollectionCount(int collector, String pickup_date) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        int count = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    COUNT(*) AS Row_Count\n" +
                    "FROM\n" +
                    "    jom_db.pickups p\n" +
                    "        INNER JOIN\n" +
                    "    suppliers s ON s.id = p.s_id\n" +
                    "        INNER JOIN\n" +
                    "    users u ON s.user_id = u.id\n" +
                    "        INNER JOIN\n" +
                    "    collections c ON p.collection_id = c.id AND c.delete = 0\n" +
                    "        AND (c.status = 3 OR c.status = 5 OR c.status = 6)\n" +
                    "        INNER JOIN\n" +
                    "    estates e ON e.id = p.estate_id\n" +
                    "WHERE\n" +
                    "    p.pickup_date = ?\n" +
                    "        AND p.collector = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, pickup_date);
            preparedStatement.setInt(2, collector);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                count = resultSet.getInt(1);
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

    //get relevant collection
    public SupplyModel getCollection(int collection_id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        SupplyModel supply = new SupplyModel();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT c.id,u.first_name,u.last_name,u.phone,p.pickup_date,p.pickup_time ,c.init_amount,c.p_method,e.address,e.location,e.area\n" +
                    "                    FROM collections c\n" +
                    "                    INNER JOIN pickups p ON c.id = p.collection_id INNER JOIN suppliers s ON c.sup_id = s.id INNER JOIN users u ON u.id=s.user_id INNER JOIN estates e ON e.id=p.estate_id\n" +
                    "                    WHERE c.delete=0 AND c.status=3 AND c.id=?\n;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, collection_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                supply.setId(resultSet.getInt(1));
                supply.setName(resultSet.getString(2));
                supply.setLast_name(resultSet.getString(3));
                supply.setPhone(resultSet.getString(4));
                supply.setDate(resultSet.getString(5));
                supply.setTime(resultSet.getString(6));
                supply.setAmount(resultSet.getInt(7));
                supply.setPayment_method(resultSet.getString(8));
                supply.setAddress(resultSet.getString(9));
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

    //get payout
    public SupplyModel getPayout(int collection_id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        SupplyModel supply = new SupplyModel();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    c.id,\n" +
                    "    u.first_name,\n" +
                    "    u.last_name,\n" +
                    "    u.phone,\n" +
                    "    c.s_method,\n" +
                    "    p.collected_date,\n" +
                    "    c.final_amount,\n" +
                    "    c.value,\n" +
                    "    c.p_method,\n" +
                    "    a.name,\n" +
                    "    a.account_num,\n" +
                    "    a.bank,\n" +
                    "    c.status\n" +
                    "FROM\n" +
                    "    jom_db.users u\n" +
                    "        INNER JOIN\n" +
                    "    suppliers s ON u.id = s.user_id\n" +
                    "        INNER JOIN\n" +
                    "    collections c ON c.sup_id = s.id\n" +
                    "        INNER JOIN\n" +
                    "    pickups p ON c.id = p.collection_id\n" +
                    "        LEFT JOIN\n" +
                    "    accounts a ON a.id = p.account_id\n" +
                    "WHERE\n" +
                    "    c.id = ? AND (c.status = 5 OR c.status = 6)\n" +
                    "        AND c.delete = 0 \n" +
                    "UNION SELECT \n" +
                    "    c.id,\n" +
                    "    u.first_name,\n" +
                    "    u.last_name,\n" +
                    "    u.phone,\n" +
                    "    c.s_method,\n" +
                    "    d.delivered_time,\n" +
                    "    c.final_amount,\n" +
                    "    c.value,\n" +
                    "    c.p_method,\n" +
                    "    a.name,\n" +
                    "    a.account_num,\n" +
                    "    a.bank,\n" +
                    "    c.status\n" +
                    "FROM\n" +
                    "    jom_db.users u\n" +
                    "        INNER JOIN\n" +
                    "    suppliers s ON u.id = s.user_id\n" +
                    "        INNER JOIN\n" +
                    "    collections c ON c.sup_id = s.id\n" +
                    "        INNER JOIN\n" +
                    "    deliveries d ON c.id = d.collec_id\n" +
                    "        LEFT JOIN\n" +
                    "    accounts a ON a.id = d.acc_id\n" +
                    "WHERE\n" +
                    "    c.id = ? AND (c.status = 5 OR c.status = 6)\n" +
                    "        AND c.delete = 0;";
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
                supply.setFinal_amount(resultSet.getInt(7));
                supply.setValue(resultSet.getInt(8));
                supply.setPayment_method(resultSet.getString(9));
                supply.setH_name(resultSet.getString(10));
                supply.setAccount(resultSet.getString(11));
                supply.setBank(resultSet.getString(12));
                supply.setStatus(resultSet.getInt(13));
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

    //get payouts
    public List<SupplyModel> getPayouts() {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<SupplyModel> supplies = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    c.id,\n" +
                    "    u.first_name,\n" +
                    "    u.last_name,\n" +
                    "    c.p_method,\n" +
                    "    c.value,\n" +
                    "    c.status\n" +
                    "FROM\n" +
                    "    jom_db.users u\n" +
                    "        INNER JOIN\n" +
                    "    suppliers s ON u.id = s.user_id\n" +
                    "        INNER JOIN\n" +
                    "    collections c ON c.sup_id = s.id\n" +
                    "WHERE\n" +
                    "    (c.status = 5 OR c.status = 6)\n" +
                    "        AND c.delete = 0;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String fName = resultSet.getString(2);
                String lName = resultSet.getString(3);
                String payment = resultSet.getString(4);
                int value = resultSet.getInt(5);
                int status = resultSet.getInt(6);

                SupplyModel supply = new SupplyModel(id, status, value, fName, lName, payment);
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

    //get collections by date
    public List<SupplyModel> getCollectionsByDate(String date) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<SupplyModel> supplies = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    c.id,\n" +
                    "    u_supplier.first_name AS supplier_first_name,\n" +
                    "    u_supplier.last_name AS supplier_last_name,\n" +
                    "    c.init_amount,\n" +
                    "    c.s_method,\n" +
                    "    u_collector.first_name AS collector_first_name,\n" +
                    "    u_collector.last_name AS collector_last_name,\n" +
                    "    p.pickup_time\n" +
                    "FROM\n" +
                    "    collections c\n" +
                    "        INNER JOIN\n" +
                    "    pickups p ON c.id = p.collection_id\n" +
                    "        INNER JOIN\n" +
                    "    suppliers s ON c.sup_id = s.id\n" +
                    "        INNER JOIN\n" +
                    "    users u_supplier ON u_supplier.id = s.user_id\n" +
                    "        INNER JOIN\n" +
                    "    employees e ON e.id = p.collector\n" +
                    "        LEFT JOIN\n" +
                    "    users u_collector ON u_collector.id = e.user_id_\n" +
                    "WHERE\n" +
                    "    c.delete = 0 AND c.status = 3\n" +
                    "        AND p.pickup_date = ? \n" +
                    "UNION SELECT \n" +
                    "    c.id,\n" +
                    "    u.first_name,\n" +
                    "    u.last_name,\n" +
                    "    c.init_amount,\n" +
                    "    c.s_method,\n" +
                    "    c.s_method,\n" +
                    "    c.s_method,\n" +
                    "    d.delivery_time\n" +
                    "FROM\n" +
                    "    collections c\n" +
                    "        INNER JOIN\n" +
                    "    deliveries d ON c.id = d.collec_id\n" +
                    "        INNER JOIN\n" +
                    "    suppliers s ON c.sup_id = s.id\n" +
                    "        INNER JOIN\n" +
                    "    users u ON u.id = s.user_id\n" +
                    "WHERE\n" +
                    "    c.delete = 0 AND c.status = 2\n" +
                    "        AND d.delivery_date = ?\n" +
                    "ORDER BY pickup_time;\n";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, date);
            preparedStatement.setString(2, date);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String fName = resultSet.getString(2);
                String lName = resultSet.getString(3);
                int amount = resultSet.getInt(4);
                String method = resultSet.getString(5);
                String c_fName = resultSet.getString(6);
                String c_lName = resultSet.getString(7);
                String time = resultSet.getString(8);

                SupplyModel supply = new SupplyModel(id, amount, fName, method, lName, c_fName, c_lName, time);
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

    //get pickups by date
    public List<SupplyModel> getPickupsByDate(String date) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<SupplyModel> supplies = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    c.id,\n" +
                    "    u_supplier.first_name AS supplier_first_name,\n" +
                    "    u_supplier.last_name AS supplier_last_name,\n" +
                    "    c.init_amount,\n" +
                    "    u_collector.first_name AS collector_first_name,\n" +
                    "    u_collector.last_name AS collector_last_name,\n" +
                    "    p.pickup_time\n" +
                    "FROM\n" +
                    "    collections c\n" +
                    "        INNER JOIN\n" +
                    "    pickups p ON c.id = p.collection_id\n" +
                    "        INNER JOIN\n" +
                    "    suppliers s ON c.sup_id = s.id\n" +
                    "        INNER JOIN\n" +
                    "    users u_supplier ON u_supplier.id = s.user_id\n" +
                    "        INNER JOIN\n" +
                    "    employees e ON e.id = p.collector\n" +
                    "        INNER JOIN\n" +
                    "    users u_collector ON u_collector.id = e.user_id_\n" +
                    "WHERE\n" +
                    "    c.delete = 0 AND c.status = 3\n" +
                    "        AND p.pickup_date = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, date);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String fName = resultSet.getString(2);
                String lName = resultSet.getString(3);
                int amount = resultSet.getInt(4);
                String c_fName = resultSet.getString(5);
                String c_lName = resultSet.getString(6);
                String time = resultSet.getString(7);

                SupplyModel supply = new SupplyModel(id, amount, fName, lName, c_fName, c_lName, time);
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

    //Get collector's user id by collection id
    public int getCollectorUserIDByCollectionID(int collectin_id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        int collector_id = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    u.id\n" +
                    "FROM\n" +
                    "    collections c\n" +
                    "        INNER JOIN\n" +
                    "    pickups p ON p.collection_id = c.id\n" +
                    "        INNER JOIN\n" +
                    "    employees e ON e.id = p.collector\n" +
                    "        INNER JOIN\n" +
                    "    users u ON u.id = e.user_Id_\n" +
                    "WHERE\n" +
                    "    c.id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, collectin_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                collector_id = resultSet.getInt(1);
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
        return collector_id;
    }

    //collection for relevant date
    public List<SupplyModel> getAllCollectionByDate(int collector, String pickup_date) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<SupplyModel> supplies = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    c.id,\n" +
                    "    sup.first_name,\n" +
                    "    sup.last_name,\n" +
                    "    sup.phone,\n" +
                    "    est.area,\n" +
                    "    c.final_amount,\n" +
                    "    c.status\n" +
                    "FROM\n" +
                    "    jom_db.pickups p\n" +
                    "        INNER JOIN\n" +
                    "    collections c ON c.id = p.collection_id\n" +
                    "        INNER JOIN\n" +
                    "    employees e ON e.id = p.collector\n" +
                    "        INNER JOIN\n" +
                    "    users u ON u.id = e.user_Id_\n" +
                    "        INNER JOIN\n" +
                    "    suppliers s ON s.id = c.sup_id\n" +
                    "        INNER JOIN\n" +
                    "    users sup ON sup.id = s.user_id\n" +
                    "        INNER JOIN\n" +
                    "    estates est ON est.id = p.estate_id\n" +
                    "WHERE\n" +
                    "    p.pickup_date = ?\n" +
                    "        AND u.id = ?\n" +
                    "        AND c.delete = 0;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, pickup_date);
            preparedStatement.setInt(2, collector);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String fist_name = resultSet.getString(2);
                String last_name = resultSet.getString(3);
                String phone = resultSet.getString(4);
                String area = resultSet.getString(5);
                int amount = resultSet.getInt(6);
                int status = resultSet.getInt(7);

                SupplyModel supply = new SupplyModel(id, amount, status, fist_name, last_name, phone, area);
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

    //get all accepted collections
    public List<SupplyModel> getAccepted() {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<SupplyModel> supplies = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    c.id,\n" +
                    "    u.first_name,\n" +
                    "    u.last_name,\n" +
                    "    c.init_amount,\n" +
                    "    c.s_method,\n" +
                    "    p.pickup_date,\n" +
                    "    u_collec.first_name AS collec_fname,\n" +
                    "    u_collec.last_name AS collec_lname\n" +
                    "FROM\n" +
                    "    collections c\n" +
                    "        INNER JOIN\n" +
                    "    suppliers s ON c.sup_id = s.id\n" +
                    "        INNER JOIN\n" +
                    "    users u ON u.id = s.user_id\n" +
                    "        INNER JOIN\n" +
                    "    pickups p ON p.collection_id = c.id\n" +
                    "        INNER JOIN\n" +
                    "    employees e ON e.id = p.collector\n" +
                    "        INNER JOIN\n" +
                    "    users u_collec ON u_collec.id = e.user_Id_\n" +
                    "WHERE\n" +
                    "    c.status < 4 AND c.status > 0 \n" +
                    "UNION SELECT \n" +
                    "    c.id,\n" +
                    "    u.first_name,\n" +
                    "    u.last_name,\n" +
                    "    c.init_amount,\n" +
                    "    c.s_method,\n" +
                    "    d.delivery_date,\n" +
                    "    d.delivery_date,\n" +
                    "    d.delivery_date\n" +
                    "FROM\n" +
                    "    collections c\n" +
                    "        INNER JOIN\n" +
                    "    suppliers s ON c.sup_id = s.id\n" +
                    "        INNER JOIN\n" +
                    "    users u ON u.id = s.user_id\n" +
                    "        INNER JOIN\n" +
                    "    deliveries d ON d.collec_id = c.id\n" +
                    "WHERE\n" +
                    "    c.status < 4 AND c.status > 0\n" +
                    "ORDER BY pickup_date DESC;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String fist_name = resultSet.getString(2);
                String last_name = resultSet.getString(3);
                int initial_amount = resultSet.getInt(4);
                String method = resultSet.getString(5);
                String date = resultSet.getString(6);
                String collec_fist_name = resultSet.getString(7);
                String collec_last_name = resultSet.getString(8);

                SupplyModel supply = new SupplyModel(id, initial_amount, fist_name, method, last_name, collec_fist_name, collec_last_name, date);
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

    //get all rejected collections
    public List<SupplyModel> getRejected() {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<SupplyModel> supplies = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    c.id,\n" +
                    "    u.first_name,\n" +
                    "    u.last_name,\n" +
                    "    c.init_amount,\n" +
                    "    c.s_method,\n" +
                    "    p.pickup_date\n" +
                    "FROM\n" +
                    "    collections c\n" +
                    "        INNER JOIN\n" +
                    "    suppliers s ON s.id = c.sup_id\n" +
                    "        INNER JOIN\n" +
                    "    users u ON u.id = s.user_id\n" +
                    "        INNER JOIN\n" +
                    "    pickups p ON p.collection_id = c.id\n" +
                    "WHERE\n" +
                    "    c.status = 4 \n" +
                    "UNION SELECT \n" +
                    "    c.id,\n" +
                    "    u.first_name,\n" +
                    "    u.last_name,\n" +
                    "    c.init_amount,\n" +
                    "    c.s_method,\n" +
                    "    d.delivery_date\n" +
                    "FROM\n" +
                    "    collections c\n" +
                    "        INNER JOIN\n" +
                    "    suppliers s ON s.id = c.sup_id\n" +
                    "        INNER JOIN\n" +
                    "    users u ON u.id = s.user_id\n" +
                    "        INNER JOIN\n" +
                    "    deliveries d ON d.collec_id = c.id\n" +
                    "WHERE\n" +
                    "    c.status = 4\n" +
                    "ORDER BY pickup_date DESC;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String fist_name = resultSet.getString(2);
                String last_name = resultSet.getString(3);
                int initial_amount = resultSet.getInt(4);
                String method = resultSet.getString(5);
                String date = resultSet.getString(6);

                SupplyModel supply = new SupplyModel(id, date, initial_amount, fist_name, method, last_name);
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

    //get all completed collections
    public List<SupplyModel> getCompleted() {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<SupplyModel> supplies = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    c.id,\n" +
                    "    u.first_name,\n" +
                    "    u.last_name,\n" +
                    "    c.final_amount,\n" +
                    "    c.s_method,\n" +
                    "    p.collected_date,\n" +
                    "    u_collec.first_name AS collec_fname,\n" +
                    "    u_collec.last_name AS collec_lname\n" +
                    "FROM\n" +
                    "    collections c\n" +
                    "        INNER JOIN\n" +
                    "    suppliers s ON c.sup_id = s.id\n" +
                    "        INNER JOIN\n" +
                    "    users u ON u.id = s.user_id\n" +
                    "        INNER JOIN\n" +
                    "    pickups p ON p.collection_id = c.id\n" +
                    "        INNER JOIN\n" +
                    "    employees e ON e.id = p.collector\n" +
                    "        INNER JOIN\n" +
                    "    users u_collec ON u_collec.id = e.user_Id_\n" +
                    "WHERE\n" +
                    "    c.status > 4 \n" +
                    "UNION SELECT \n" +
                    "    c.id,\n" +
                    "    u.first_name,\n" +
                    "    u.last_name,\n" +
                    "    c.final_amount,\n" +
                    "    c.s_method,\n" +
                    "    d.delivered_time,\n" +
                    "    d.delivered_time,\n" +
                    "    d.delivered_time\n" +
                    "FROM\n" +
                    "    collections c\n" +
                    "        INNER JOIN\n" +
                    "    suppliers s ON c.sup_id = s.id\n" +
                    "        INNER JOIN\n" +
                    "    users u ON u.id = s.user_id\n" +
                    "        INNER JOIN\n" +
                    "    deliveries d ON d.collec_id = c.id\n" +
                    "WHERE\n" +
                    "    c.status > 4\n" +
                    "ORDER BY collected_date DESC;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String fist_name = resultSet.getString(2);
                String last_name = resultSet.getString(3);
                int final_amount = resultSet.getInt(4);
                String method = resultSet.getString(5);
                String date = resultSet.getString(6);
                String collec_fist_name = resultSet.getString(7);
                String collec_last_name = resultSet.getString(8);

                SupplyModel supply = new SupplyModel(id, final_amount, fist_name, method, last_name, collec_fist_name, collec_last_name, date);
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
