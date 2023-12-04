package org.jom.Dao.Supplier.Collection;

import com.google.gson.Gson;
import org.jom.Database.ConnectionPool;
import org.jom.Model.AccountModel;
import org.jom.Model.Collection.CollectionModel;
import org.jom.Model.Collection.CollectionSingleViewModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CollectionDAO {
    public int addCollection(CollectionModel collection) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int collectionId = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "INSERT INTO collections (init_amount,p_method,s_method,sup_id) VALUES (?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, collection.getInitial_amount());
            preparedStatement.setString(2, collection.getPayment_method());
            preparedStatement.setString(3, collection.getSupply_method());
            preparedStatement.setInt(4, collection.getSupplier_id());

            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                collectionId = resultSet.getInt(1);
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
        return collectionId;
    }

    public boolean updateStatus(int status, int id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean isSuccess = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE collections SET status=? WHERE id = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, status);
            preparedStatement.setInt(2, id);

            int x = preparedStatement.executeUpdate();
            if (x != 0) {
                isSuccess = true;
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
        return isSuccess;
    }

    public CollectionSingleViewModel getCollection(int collection_id, int supplier_id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        CollectionSingleViewModel collection = new CollectionSingleViewModel();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    c.id,\n" +
                    "    c.p_method,\n" +
                    "    c.s_method,\n" +
                    "    p.pickup_date,\n" +
                    "    p.pickup_time,\n" +
                    "    c.init_amount,\n" +
                    "    c.status,\n" +
                    "    c.final_amount,\n" +
                    "    c.value,\n" +
                    "    p.estate_id,\n" +
                    "    p.account_id,\n" +
                    "    e.name,\n" +
                    "    e.address,\n" +
                    "    e.location,\n" +
                    "    e.area\n" +
                    "FROM\n" +
                    "    pickups p\n" +
                    "        INNER JOIN\n" +
                    "    collections c ON p.collection_id = c.id\n" +
                    "    inner join estates e on p.estate_id=e.id\n" +
                    "WHERE\n" +
                    "    c.id = ? AND c.delete = 0\n" +
                    "        AND c.sup_id = ? \n" +
                    "UNION SELECT \n" +
                    "    c.id,\n" +
                    "    c.p_method,\n" +
                    "    c.s_method,\n" +
                    "    d.delivery_date,\n" +
                    "    d.delivery_time,\n" +
                    "    c.init_amount,\n" +
                    "    c.status,\n" +
                    "    c.final_amount,\n" +
                    "    c.value,\n" +
                    "    d.acc_id,\n" +
                    "    d.acc_id,\n" +
                    "    d.acc_id,\n" +
                    "    d.acc_id,\n" +
                    "    d.acc_id,\n" +
                    "    d.acc_id\n" +
                    "FROM\n" +
                    "    deliveries d\n" +
                    "        INNER JOIN\n" +
                    "    collections c ON d.collec_id = c.id\n" +
                    "WHERE\n" +
                    "    c.id = ? AND c.delete = 0\n" +
                    "        AND c.sup_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, collection_id);
            preparedStatement.setInt(2, supplier_id);
            preparedStatement.setInt(3, collection_id);
            preparedStatement.setInt(4, supplier_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                collection.setCollection_id(resultSet.getInt(1));
                collection.setpMethod(resultSet.getString(2));
                collection.setsMethod(resultSet.getString(3));
                collection.setDate(resultSet.getString(4));
                collection.setTime(resultSet.getString(5));
                collection.setInit_amount(resultSet.getInt(6));
                collection.setStatus(resultSet.getInt(7));
                collection.setFinal_amount(resultSet.getInt(8));
                collection.setValue(resultSet.getInt(9));
                collection.setEstate(resultSet.getInt(10));
                collection.setAccount(resultSet.getInt(11));
                collection.setEstate_name(resultSet.getString(12));
                collection.setEstate_address(resultSet.getString(13));
                collection.setEstate_location(resultSet.getString(14));
                collection.setEstate_area(resultSet.getString(15));
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
        return collection;
    }

    public int rowCount(int status) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int count = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT COUNT(*) AS Row_Count FROM collections WHERE status=? AND jom_db.collections.delete=0;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, status);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
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

    public boolean deleteCollection(int sId, int id, String min_date, String max_date) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE collections c \n" +
                    "SET \n" +
                    "    c.delete = 1\n" +
                    "WHERE\n" +
                    "    c.sup_id = ? AND c.id = ?\n" +
                    "        AND c.status < 5\n" +
                    "        AND ((c.id IN (SELECT \n" +
                    "            collection_id\n" +
                    "        FROM\n" +
                    "            pickups\n" +
                    "        WHERE\n" +
                    "            (pickup_date < ?\n" +
                    "                OR pickup_date > ?)))\n" +
                    "        OR (c.id IN (SELECT \n" +
                    "            collec_id\n" +
                    "        FROM\n" +
                    "            deliveries\n" +
                    "        WHERE\n" +
                    "            (delivery_date < ?\n" +
                    "                OR delivery_date > ?))));";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, sId);
            preparedStatement.setInt(2, id);
            preparedStatement.setString(3, min_date);
            preparedStatement.setString(4, max_date);
            preparedStatement.setString(5, min_date);
            preparedStatement.setString(6, max_date);

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

    //Assign collector
    public boolean assignCollector(int collection_id, int employee_id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE pickups p inner join collections c ON c.id=p.collection_id SET p.collector=? WHERE c.id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, employee_id);
            preparedStatement.setInt(2, collection_id);

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


    //get supplier id by collection
    public int getSupplierId(int collection_id, int employee_id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int supplier_id = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    u.id\n" +
                    "FROM\n" +
                    "    collections c\n" +
                    "        INNER JOIN\n" +
                    "    pickups p ON p.collection_id = c.id\n" +
                    "        INNER JOIN\n" +
                    "    suppliers s ON s.id=c.sup_id\n" +
                    "        INNER JOIN\n" +
                    "    users u ON u.id = s.user_id\n" +
                    "        INNER JOIN\n" +
                    "    employees e\n" +
                    "WHERE\n" +
                    "    c.id = ? AND e.user_Id_ = ?\n" +
                    "        AND c.delete = 0\n" +
                    "        AND c.s_method = 'pickup'\n" +
                    "        AND c.status = 3;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, collection_id);
            preparedStatement.setInt(2, employee_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                supplier_id = resultSet.getInt(1);
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
        return supplier_id;
    }

    //get supplier id by collection
    public int getCollectorId(int collection_id, int supplier_id) {
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
                    "    users u ON e.user_Id_ = u.id\n" +
                    "        INNER JOIN\n" +
                    "    suppliers s\n" +
                    "WHERE\n" +
                    "    c.id = ? AND s.user_id = ?\n" +
                    "        AND c.delete = 0\n" +
                    "        AND c.s_method = 'pickup'\n" +
                    "        AND c.status = 3;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, collection_id);
            preparedStatement.setInt(2, supplier_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                collector_id = resultSet.getInt(1);
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
        return collector_id;
    }

    //get supplier email by collection
    public String getSupplierEmail(int collection_id, int employee_id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        String supplier_email = null;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    u.email\n" +
                    "FROM\n" +
                    "    collections c\n" +
                    "        INNER JOIN\n" +
                    "    pickups p ON p.collection_id = c.id\n" +
                    "        INNER JOIN\n" +
                    "    suppliers s ON s.id=c.sup_id\n" +
                    "        INNER JOIN\n" +
                    "    users u ON u.id = s.user_id\n" +
                    "        INNER JOIN\n" +
                    "    employees e\n" +
                    "WHERE\n" +
                    "    c.id = ? AND e.user_Id_ = ?\n" +
                    "        AND c.delete = 0\n" +
                    "        AND c.s_method = 'pickup'\n" +
                    "        AND c.status = 3;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, collection_id);
            preparedStatement.setInt(2, employee_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                supplier_email = resultSet.getString(1);
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
        return supplier_email;
    }

    //get supplier name by collection
    public String getSupplierName(int collection_id, int employee_id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        String supplier_name = null;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    u.first_name\n" +
                    "FROM\n" +
                    "    collections c\n" +
                    "        INNER JOIN\n" +
                    "    pickups p ON p.collection_id = c.id\n" +
                    "        INNER JOIN\n" +
                    "    suppliers s ON s.id=c.sup_id\n" +
                    "        INNER JOIN\n" +
                    "    users u ON u.id = s.user_id\n" +
                    "        INNER JOIN\n" +
                    "    employees e\n" +
                    "WHERE\n" +
                    "    c.id = ? AND e.user_Id_ = ?\n" +
                    "        AND c.delete = 0\n" +
                    "        AND c.s_method = 'pickup'\n" +
                    "        AND c.status = 3;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, collection_id);
            preparedStatement.setInt(2, employee_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                supplier_name = resultSet.getString(1);
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
        return supplier_name;
    }

    // update final amount and status
    public boolean updateFinalAmount(int amount, float value, int collection_id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean isSuccess = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE collections c \n" +
                    "SET \n" +
                    "    c.final_amount = ?,\n" +
                    "    c.status = 5\n," +
                    "    c.value = ?\n" +
                    "WHERE\n" +
                    "    c.id = ? AND c.status = 3\n" +
                    "        AND c.delete = 0;  ";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, amount);
            preparedStatement.setFloat(2, value);
            preparedStatement.setInt(3, collection_id);

            int x = preparedStatement.executeUpdate();
            if (x != 0) {
                isSuccess = true;
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
        return isSuccess;
    }

    public boolean updateCollection(CollectionModel collection) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean isSuccess = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE collections c \n" +
                    "SET \n" +
                    "    c.status = 1,\n" +
                    "    c.init_amount = ?,\n" +
                    "    c.p_method = ?,\n" +
                    "    c.s_method = ?\n" +
                    "WHERE\n" +
                    "    c.id = ? AND c.delete = 0\n" +
                    "        AND c.sup_id = ?\n" +
                    "        AND c.status < 5";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, collection.getInitial_amount());
            preparedStatement.setString(2, collection.getPayment_method());
            preparedStatement.setString(3, collection.getSupply_method());
            preparedStatement.setInt(4, collection.getId());
            preparedStatement.setInt(5, collection.getSupplier_id());

            int x = preparedStatement.executeUpdate();
            if (x != 0) {
                isSuccess = true;
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
        return isSuccess;
    }

    public int completedRowCountByDate(String date) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int count = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    (SELECT \n" +
                    "            COUNT(*)\n" +
                    "        FROM\n" +
                    "            collections c\n" +
                    "                INNER JOIN\n" +
                    "            pickups p ON c.id = p.collection_id\n" +
                    "        WHERE\n" +
                    "            (c.status = 5 OR c.status = 6)\n" +
                    "                AND c.delete = 0\n" +
                    "                AND p.pickup_date = ?) + (SELECT \n" +
                    "            COUNT(*)\n" +
                    "        FROM\n" +
                    "            collections c\n" +
                    "                INNER JOIN\n" +
                    "            deliveries d ON c.id = d.collec_id\n" +
                    "        WHERE\n" +
                    "            (c.status = 5 OR c.status = 6)\n" +
                    "                AND c.delete = 0\n" +
                    "                AND d.delivery_date = ?) AS Total_Row_Count;\n";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, date);
            preparedStatement.setString(2, date);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
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

    public int remainingRowCountByDate(String date) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int count = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    (SELECT \n" +
                    "            COUNT(*)\n" +
                    "        FROM\n" +
                    "            collections c\n" +
                    "                INNER JOIN\n" +
                    "            pickups p ON c.id = p.collection_id\n" +
                    "        WHERE\n" +
                    "            c.status = 3\n" +
                    "                AND c.delete = 0\n" +
                    "                AND p.pickup_date = ?) + (SELECT \n" +
                    "            COUNT(*)\n" +
                    "        FROM\n" +
                    "            collections c\n" +
                    "                INNER JOIN\n" +
                    "            deliveries d ON c.id = d.collec_id\n" +
                    "        WHERE\n" +
                    "            c.status = 2\n" +
                    "                AND c.delete = 0\n" +
                    "                AND d.delivery_date = ?) AS Total_Row_Count;\n";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, date);
            preparedStatement.setString(2, date);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
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

    public String getRequestedDateById(int id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        String date = "";

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    c.date\n" +
                    "FROM\n" +
                    "    jom_db.collections c\n" +
                    "WHERE\n" +
                    "    c.id = ? AND c.delete = 0\n" +
                    "        AND (c.status = 2 OR c.status = 3);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                date = resultSet.getString(1);
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
        return date;
    }
}
