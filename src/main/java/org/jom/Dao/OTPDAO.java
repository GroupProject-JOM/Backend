package org.jom.Dao;

import org.jom.Database.ConnectionPool;
import org.jom.Model.OTPModel;

import java.sql.*;

public class OTPDAO {
    public int saveOTP(OTPModel otp){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int otpId = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "INSERT INTO otps (user_id,user_email,otp) VALUES (?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1,otp.getUserId());
            preparedStatement.setString(2,otp.getUserEmail());
            preparedStatement.setInt(3,otp.getOtp());

            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                otpId = resultSet.getInt(1);
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
        return otpId;
    }

    public OTPModel getRecord(int id){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        OTPModel record = new OTPModel();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT * FROM otps WHERE id = "+ id +" AND validity = 1";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                record.setId(resultSet.getInt(1));
                record.setUserId(resultSet.getInt(2));
                record.setUserEmail(resultSet.getString(3));
                record.setOtp(resultSet.getInt(4));
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
        return record;
    }

    public static void setValidity(int id){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE otps SET validity = '0' WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,id);
            preparedStatement.executeUpdate();

            preparedStatement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) try {
                connection.close();
            } catch (Exception ignore) {
            }
        }
    }
}
