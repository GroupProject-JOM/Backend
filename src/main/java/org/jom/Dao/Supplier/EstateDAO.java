package org.jom.Dao.Supplier;

import org.jom.Database.ConnectionPool;
import org.jom.Model.EstateModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstateDAO {
    public EstateModel getEstate(int sId,int id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        EstateModel estate = new EstateModel();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT * FROM estates WHERE supplier_id = ? AND id = ? AND jom_db.estates.delete=0";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,sId);
            preparedStatement.setInt(2,id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                estate.setId(resultSet.getInt(1));
                estate.setEstate_name(resultSet.getString(2));
                estate.setEstate_address(resultSet.getString(3));
                estate.setEstate_location(resultSet.getString(4));
                estate.setArea(resultSet.getString(5));
                estate.setSupplier_id(resultSet.getInt(7));
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
        return estate;
    }

    public int addEstate(EstateModel estate){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int estateId = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "INSERT INTO estates (name,address,location,area,supplier_id) VALUES (?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1,estate.getEstate_name());
            preparedStatement.setString(2,estate.getEstate_address());
            preparedStatement.setString(3,estate.getEstate_location());
            preparedStatement.setString(4,estate.getArea());
            preparedStatement.setInt(5,estate.getSupplier_id());

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

    public List<EstateModel> getAll(int id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<EstateModel> estates = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT * FROM estates WHERE supplier_id = ? AND jom_db.estates.delete=0";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int estate_id = resultSet.getInt(1);
                String estate_name = resultSet.getString(2);
                String estate_address = resultSet.getString(3);
                String estate_location = resultSet.getString(4);
                String estate_area = resultSet.getString(5);
                int supplier_id = resultSet.getInt(7);

                EstateModel estate = new EstateModel(estate_id,supplier_id,estate_name,estate_location,estate_address,estate_area);
                estates.add(estate);
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
        return estates;
    }

    public boolean updateEstate(EstateModel estate){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE estates SET name=?,address=?,location=?,area=? WHERE supplier_id = ? AND id = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,estate.getEstate_name());
            preparedStatement.setString(2,estate.getEstate_address());
            preparedStatement.setString(3,estate.getEstate_location());
            preparedStatement.setString(4,estate.getArea());
            preparedStatement.setInt(5,estate.getSupplier_id());
            preparedStatement.setInt(6,estate.getId());

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

    public boolean deleteEstate(int sId,int id){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE estates SET jom_db.estates.delete=1 WHERE supplier_id = ? AND id = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,sId);
            preparedStatement.setInt(2,id);

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
}
