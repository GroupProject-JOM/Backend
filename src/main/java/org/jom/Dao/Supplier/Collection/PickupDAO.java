package org.jom.Dao.Supplier.Collection;

import org.jom.Database.ConnectionPool;
import org.jom.Model.Collection.CollectionModel;
import org.jom.Model.Collection.PickupModel;

import java.sql.*;

public class PickupDAO {
    public int addPickup(PickupModel pickup) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int pickupId = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql;
            if (pickup.getAccount_id() == 0) {
                sql = "INSERT INTO pickups (pickup_date,pickup_time,s_id,collection_id,estate_id) VALUES (?,?,?,?,?)";
            } else {
                sql = "INSERT INTO pickups (pickup_date,pickup_time,s_id,collection_id,estate_id,account_id) VALUES (?,?,?,?,?,?)";
            }
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, pickup.getDate());
            preparedStatement.setString(2, pickup.getTime());
            preparedStatement.setInt(3, pickup.getSupplier_id());
            preparedStatement.setInt(4, pickup.getCollection_id());
            preparedStatement.setInt(5, pickup.getEstate_id());
            if (pickup.getAccount_id() != 0) {
                preparedStatement.setInt(6, pickup.getAccount_id());
            }

            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                pickupId = resultSet.getInt(1);
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
        return pickupId;
    }

    // update collected date by collection id
    public boolean updateCollectedDate(int collection_id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE pickups p SET p.collected_date = NOW() WHERE p.collection_id=?; ";
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

    //get pickup
    public PickupModel getPickup(int collection_id, int supplier_id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        PickupModel pickup = new PickupModel();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    *\n" +
                    "FROM\n" +
                    "    jom_db.pickups p\n" +
                    "WHERE\n" +
                    "    p.s_id = ? AND p.collection_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, supplier_id);
            preparedStatement.setInt(2, collection_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                pickup.setId(resultSet.getInt(1));
                pickup.setDate(resultSet.getString(2));
                pickup.setTime(resultSet.getString(3));
                pickup.setSupplier_id(resultSet.getInt(5));
                pickup.setCollection_id(resultSet.getInt(6));
                pickup.setEstate_id(resultSet.getInt(7));
                pickup.setAccount_id(resultSet.getInt(8));
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
        return pickup;
    }

    //update collection
    public boolean updateCollection(PickupModel pickup) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql;

            if (pickup.getAccount_id() == 0) {
                sql = "UPDATE pickups p SET p.pickup_date = ?,p.pickup_time=?,p.estate_id=?,p.collector=0 WHERE p.collection_id=? AND p.s_id=?; ";
            } else {
                sql = "UPDATE pickups p SET p.pickup_date = ?,p.pickup_time=?,p.estate_id=?,p.account_id=?,p.collector=0 WHERE p.collection_id=? AND p.s_id=?; ";
            }

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, pickup.getDate());
            preparedStatement.setString(2, pickup.getTime());
            preparedStatement.setInt(3, pickup.getEstate_id());

            if (pickup.getAccount_id() != 0) {
                preparedStatement.setInt(4, pickup.getAccount_id());
                preparedStatement.setInt(5, pickup.getCollection_id());
                preparedStatement.setInt(6, pickup.getSupplier_id());
            } else {
                preparedStatement.setInt(4, pickup.getCollection_id());
                preparedStatement.setInt(5, pickup.getSupplier_id());
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

    //delete pickup
    public boolean deletePickup(int collection_id, int supplier_id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "DELETE FROM pickups WHERE s_id = ? AND collection_id = ? ";
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
}
