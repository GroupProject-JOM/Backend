package org.jom.Dao;

import org.jom.Database.ConnectionPool;
import org.jom.Model.Collection.SupplyModel;
import org.jom.Model.UserModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public int register(UserModel user){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int userId = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "INSERT INTO users (first_name,last_name,email,password,phone,add_line_1,add_line_2,add_line_3,role,validity) VALUES (?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1,user.getFirst_name());
            preparedStatement.setString(2,user.getLast_name());
            preparedStatement.setString(3,user.getEmail());
            preparedStatement.setString(4,user.getPassword());
            preparedStatement.setString(5,user.getPhone());
            preparedStatement.setString(6,user.getAdd_line_1());
            preparedStatement.setString(7,user.getAdd_line_2());
            preparedStatement.setString(8,user.getAdd_line_3());
            preparedStatement.setString(9,user.getRole());
            preparedStatement.setInt(10,user.getValidity());

            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                userId = resultSet.getInt(1);
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
        return userId;
    }

    public boolean emailExists(String Email){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT email FROM users WHERE email = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,Email);
            ResultSet resultSet = preparedStatement.executeQuery();

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

    public static UserModel getUserByEmail(String Email){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        UserModel user = new UserModel();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT * FROM users WHERE email = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,Email);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                user.setId(resultSet.getInt(1));
                user.setFirst_name(resultSet.getString(2));
                user.setLast_name(resultSet.getString(3));
                user.setEmail(resultSet.getString(4));
                user.setPassword(resultSet.getString(5));
                user.setPhone(resultSet.getString(6));
                user.setAdd_line_1(resultSet.getString(7));
                user.setAdd_line_2(resultSet.getString(8));
                user.setAdd_line_3(resultSet.getString(9));
                user.setRole(resultSet.getString(10));
                user.setValidity(resultSet.getInt(11));
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
        return user;
    }

    public static UserModel getUserById(int id){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        UserModel user = new UserModel();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT * FROM users WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                user.setId(resultSet.getInt(1));
                user.setFirst_name(resultSet.getString(2));
                user.setLast_name(resultSet.getString(3));
                user.setEmail(resultSet.getString(4));
                user.setPassword(resultSet.getString(5));
                user.setPhone(resultSet.getString(6));
                user.setAdd_line_1(resultSet.getString(7));
                user.setAdd_line_2(resultSet.getString(8));
                user.setAdd_line_3(resultSet.getString(9));
                user.setRole(resultSet.getString(10));
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
        return user;
    }

    public static void updateValidity(int id){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE users SET validity = '1' WHERE id = ?";
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

    // update with role from admin
    public boolean updateUser(UserModel user){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE users SET first_name=?,last_name=?,phone=?,add_line_1=?,add_line_2=?,add_line_3=?,role=? WHERE id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,user.getFirst_name());
            preparedStatement.setString(2,user.getLast_name());
            preparedStatement.setString(3,user.getPhone());
            preparedStatement.setString(4,user.getAdd_line_1());
            preparedStatement.setString(5,user.getAdd_line_2());
            preparedStatement.setString(6,user.getAdd_line_3());
            preparedStatement.setString(7,user.getRole());
            preparedStatement.setInt(8,user.getId());

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

    public boolean updateEmail(String Email,int user_id){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE users SET email = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,Email);
            preparedStatement.setInt(2,user_id);

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

    public static boolean updateSeen(int stat,int id){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE users SET seen = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,stat);
            preparedStatement.setInt(2,id);
            preparedStatement.executeUpdate();

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

    //Get distributors (TEMP)
    public List<UserModel> getDistributors(){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        ArrayList<UserModel> distributors = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    u.id, u.first_name, u.last_name, u.phone\n" +
                    "FROM\n" +
                    "    users u\n" +
                    "WHERE\n" +
                    "    u.role = 'distributor';";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String fName = resultSet.getString(2);
                String lName = resultSet.getString(3);
                String phone = resultSet.getString(4);

                UserModel user = new UserModel(id, fName, lName, phone);
                distributors.add(user);
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
        return distributors;
    }

    // Update password
    public static boolean updatePassword(int id,String password){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE users SET password = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,password);
            preparedStatement.setInt(2,id);
            preparedStatement.executeUpdate();

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

    // update without role from them self
    public boolean updateProfile(UserModel user){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE users SET first_name=?,last_name=?,phone=?,add_line_1=?,add_line_2=?,add_line_3=? WHERE id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,user.getFirst_name());
            preparedStatement.setString(2,user.getLast_name());
            preparedStatement.setString(3,user.getPhone());
            preparedStatement.setString(4,user.getAdd_line_1());
            preparedStatement.setString(5,user.getAdd_line_2());
            preparedStatement.setString(6,user.getAdd_line_3());
            preparedStatement.setInt(7,user.getId());

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

    //get few user data for supplier invoice
    public UserModel reportData(int id){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        UserModel user = new UserModel();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    u.first_name,\n" +
                    "    u.last_name,\n" +
                    "    u.add_line_1,\n" +
                    "    u.add_line_2,\n" +
                    "    u.add_line_3,\n" +
                    "    u.phone,\n" +
                    "    u.email\n" +
                    "FROM\n" +
                    "    jom_db.users u\n" +
                    "WHERE\n" +
                    "    id = ? AND u.role = 'supplier';";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                user.setFirst_name(resultSet.getString(1));
                user.setLast_name(resultSet.getString(2));
                user.setAdd_line_1(resultSet.getString(3));
                user.setAdd_line_2(resultSet.getString(4));
                user.setAdd_line_3(resultSet.getString(5));
                user.setPhone(resultSet.getString(6));
                user.setEmail(resultSet.getString(7));
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
        return user;
    }
}
