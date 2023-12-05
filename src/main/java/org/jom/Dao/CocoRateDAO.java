package org.jom.Dao;

import org.jom.Database.ConnectionPool;
import org.jom.Model.CocoModel;
import org.jom.Model.Collection.SupplyModel;
import org.jom.Model.OTPModel;
import org.jom.Model.OutletModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CocoRateDAO {
    public CocoModel getLastRecord() {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        CocoModel cocoModel = new CocoModel();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    *\n" +
                    "FROM\n" +
                    "    coco_rate\n" +
                    "ORDER BY id DESC\n" +
                    "LIMIT 1;";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                cocoModel.setId(resultSet.getInt(1));
                cocoModel.setDate(resultSet.getString(2));
                cocoModel.setPrice(resultSet.getString(3));
            }

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (Exception ignore) {
            }
        }
        return cocoModel;
    }

    public int addRate(CocoModel cocoModel) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int rate_id = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "INSERT INTO coco_rate (date,price) VALUES (?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, cocoModel.getDate());
            preparedStatement.setString(2, cocoModel.getPrice());

            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                rate_id = resultSet.getInt(1);
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
        return rate_id;
    }

    public boolean updateRate(CocoModel cocoModel) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE coco_rate SET price=? WHERE date = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, cocoModel.getPrice());
            preparedStatement.setString(2, cocoModel.getDate());

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

    //get rate by date
    public CocoModel getRateByDate(String date) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        CocoModel cocoModel = new CocoModel();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    *\n" +
                    "FROM\n" +
                    "    coco_rate c\n" +
                    "WHERE\n" +
                    "    c.date = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, date);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                cocoModel.setId(resultSet.getInt(1));
                cocoModel.setDate(resultSet.getString(2));
                cocoModel.setPrice(resultSet.getString(3));
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
        return cocoModel;
    }

    //get last 6 months records
    public List<CocoModel> getLastSixMonthRecords() {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        ArrayList<CocoModel> cocoModels = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    *\n" +
                    "FROM\n" +
                    "    jom_db.coco_rate\n" +
                    "ORDER BY date DESC\n" +
                    "LIMIT 180;";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String date = resultSet.getString(2);
                String price = resultSet.getString(3);

                CocoModel cocoModel = new CocoModel(id,date,price);
                cocoModels.add(cocoModel);
            }

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (Exception ignore) {
            }
        }
        return cocoModels;
    }

    //get average by month
    public List<Float> getMonthlyAverageRate() {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        ArrayList<Float> avg = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    DATE_FORMAT(date, '%Y-%m') AS month,\n" +
                    "    COUNT(*) AS number_of_records,\n" +
                    "    SUM(price) AS total_price,\n" +
                    "    AVG(price) AS average_price\n" +
                    "FROM \n" +
                    "    coco_rate\n" +
                    "GROUP BY \n" +
                    "    DATE_FORMAT(date, '%Y-%m');\n";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                float val = resultSet.getFloat(4);

                avg.add(val);
            }

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (Exception ignore) {
            }
        }
        return avg;
    }

//    past supply table empty error view in supplier dashboard table
//    chat user id last id show 119 like users in supplier side
//    msg time
//    time past error view
//    password need to clear in cahnge password
}
