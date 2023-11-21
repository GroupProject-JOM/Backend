package org.jom.Dao.Supplier.Collection;

import org.jom.Database.ConnectionPool;
import org.jom.Model.Collection.PickupModel;
import org.jom.Model.Collection.YardModel;

import java.sql.*;

public class YardDAO {
    public int addYard(YardModel yard) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int yardId = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql;
            if (yard.getAccount_id() == 0) {
                sql = "INSERT INTO deliveries (delivery_date,delivery_time,supp_id,collec_id) VALUES (?,?,?,?)";
            } else {
                sql = "INSERT INTO deliveries (delivery_date,delivery_time,supp_id,collec_id,acc_id) VALUES (?,?,?,?,?)";
            }
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, yard.getDate());
            preparedStatement.setString(2, yard.getTime());
            preparedStatement.setInt(3, yard.getSupplier_id());
            preparedStatement.setInt(4, yard.getCollection_id());
            if (yard.getAccount_id() != 0) {
                preparedStatement.setInt(5, yard.getAccount_id());
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

    public boolean deleteYard(int collection_id, int supplier_id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "DELETE FROM deliveries WHERE supp_id = ? AND collec_id = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, supplier_id);
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

    //get pickup
    public YardModel getYard(int collection_id, int supplier_id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        YardModel yard = new YardModel();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    *\n" +
                    "FROM\n" +
                    "    jom_db.deliveries d\n" +
                    "WHERE\n" +
                    "    d.supp_id = ? AND d.collec_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, supplier_id);
            preparedStatement.setInt(2, collection_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                yard.setId(resultSet.getInt(1));
                yard.setDate(resultSet.getString(2));
                yard.setTime(resultSet.getString(3));
                yard.setSupplier_id(resultSet.getInt(5));
                yard.setCollection_id(resultSet.getInt(6));
                yard.setAccount_id(resultSet.getInt(7));
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
        return yard;
    }

    //update collection
    public boolean updateCollection(YardModel yard) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql;

            if (yard.getAccount_id() == 0) {
                sql = "UPDATE deliveries d SET d.delivery_date = ?,d.delivery_time=? WHERE d.collec_id=? AND d.supp_id=?; ";
            } else {
                sql = "UPDATE deliveries d SET d.delivery_date = ?,d.delivery_time=?,d.acc_id=? WHERE d.collec_id=? AND d.supp_id=?; ";
            }
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, yard.getDate());
            preparedStatement.setString(2, yard.getTime());

            if (yard.getAccount_id() != 0) {
                preparedStatement.setInt(3, yard.getAccount_id());
                preparedStatement.setInt(4, yard.getCollection_id());
                preparedStatement.setInt(5, yard.getSupplier_id());
            }else{

                preparedStatement.setInt(3, yard.getCollection_id());
                preparedStatement.setInt(4, yard.getSupplier_id());
            }

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
