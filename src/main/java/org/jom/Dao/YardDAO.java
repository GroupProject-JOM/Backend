package org.jom.Dao;

import org.jom.Database.ConnectionPool;
import org.jom.Model.UserModel;
import org.jom.Model.YardModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class YardDAO {
    //Get yard data
    public List<YardModel> getYards(String yard_name) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        ArrayList<YardModel> yard = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    *\n" +
                    "FROM\n" +
                    "    " + yard_name + "\n" +
                    "ORDER BY days DESC";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                int days = resultSet.getInt(2);
                int count = resultSet.getInt(3);
                String date = resultSet.getString(4);

                YardModel block = new YardModel(id, days, count, date);
                yard.add(block);
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

    //Get relevent block
    public YardModel getBlockData(String yard_name,int block_id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        YardModel block = new YardModel();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    *\n" +
                    "FROM\n" +
                    "    " + yard_name + "\n" +
                    "WHERE\n" +
                    "    id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,block_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                block.setId(resultSet.getInt(1));
                block.setDays(resultSet.getInt(2));
                block.setCount(resultSet.getInt(3));
                block.setDate(resultSet.getString(4));
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
        return block;
    }
}
