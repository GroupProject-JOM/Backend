package org.jom.Dao;

import org.jom.Database.ConnectionPool;
import org.jom.Model.AccountModel;
import org.jom.Model.EstateModel;
import org.jom.Model.OutletModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OutletDAO {
    public int addOutlet(OutletModel outlet){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int outletId = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "INSERT INTO outlets (name,email,phone,address1,street,city,registerd_by) VALUES (?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1,outlet.getName());
            preparedStatement.setString(2,outlet.getEmail());
            preparedStatement.setString(3,outlet.getPhone());
            preparedStatement.setString(4,outlet.getAddress1());
            preparedStatement.setString(5,outlet.getStreet());
            preparedStatement.setString(6,outlet.getCity());
            preparedStatement.setInt(7,outlet.getEmp_id());

            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                outletId = resultSet.getInt(1);
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
        return outletId;
    }

    public List<OutletModel> getAll() {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<OutletModel> outlets = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT * FROM outlets WHERE jom_db.outlets.delete=0";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                String email = resultSet.getString(3);
                String phone = resultSet.getString(4);
                String address1 = resultSet.getString(5);
                String street = resultSet.getString(6);
                String city = resultSet.getString(7);

                OutletModel outlet = new OutletModel(id,name,email,phone,address1,street,city);
                outlets.add(outlet);
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
        return outlets;
    }

    public OutletModel getOutlet(int id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        OutletModel outlet = new OutletModel();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT * FROM outlets WHERE id = ? AND jom_db.outlets.delete=0";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                outlet.setId(resultSet.getInt(1));
                outlet.setName(resultSet.getString(2));
                outlet.setEmail(resultSet.getString(3));
                outlet.setPhone(resultSet.getString(4));
                outlet.setAddress1(resultSet.getString(5));
                outlet.setStreet(resultSet.getString(6));
                outlet.setCity(resultSet.getString(7));
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
        return outlet;
    }

    public boolean updateOutlet(OutletModel outlet){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE outlets SET name=?,email=?,phone=?,address1=?,street=?,city=? WHERE id = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,outlet.getName());
            preparedStatement.setString(2,outlet.getEmail());
            preparedStatement.setString(3,outlet.getPhone());
            preparedStatement.setString(4,outlet.getAddress1());
            preparedStatement.setString(5,outlet.getStreet());
            preparedStatement.setString(6,outlet.getCity());
            preparedStatement.setInt(7,outlet.getId());

            int x = preparedStatement.executeUpdate();
            if(x !=0){
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

    public boolean deleteOutlet(int id){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE outlets SET jom_db.outlets.delete=1 WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,id);

            int x = preparedStatement.executeUpdate();
            if(x !=0){
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

    public int rowCount(){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int count = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT COUNT(*) AS Row_Count FROM outlets; ";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt(1);;
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
}
