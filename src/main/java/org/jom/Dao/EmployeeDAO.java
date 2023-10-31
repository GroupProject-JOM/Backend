package org.jom.Dao;

import org.jom.Database.ConnectionPool;
import org.jom.Model.EmployeeModel;
import org.jom.Model.EstateModel;
import org.jom.Model.OutletModel;
import org.jom.Model.UserModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    public int register(EmployeeModel employee){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int employeeId = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "INSERT INTO employees (dob,nic,user_id_) VALUES (?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1,employee.getDob());
            preparedStatement.setString(2,employee.getNic());
            preparedStatement.setInt(3,employee.getId());

            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                employeeId = resultSet.getInt(1);
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
        return employeeId;
    }

    public List<EmployeeModel> getAll() {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<EmployeeModel> employees = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT employees.id, users.first_name, users.role, users.phone, users.add_line_3 FROM employees inner join users where users.id = employees.user_id_ ;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String first_name = resultSet.getString(2);
                String role = resultSet.getString(3);
                String phone = resultSet.getString(4);
                String city = resultSet.getString(5);

                EmployeeModel employee = new EmployeeModel(id,first_name,phone,city,role);
                employees.add(employee);
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
        return employees;
    }

    public EmployeeModel getEmployee(int id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        EmployeeModel employee = new EmployeeModel();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT users.first_name,users.last_name, users.email, users.phone,users.add_line_1,users.add_line_2, users.add_line_3,employees.dob,employees.nic,users.role,employees.id FROM employees inner join users where users.id = employees.user_id_ and  employees.id= ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                employee.setFirst_name(resultSet.getString(1));
                employee.setLast_name(resultSet.getString(2));
                employee.setEmail(resultSet.getString(3));
                employee.setPhone(resultSet.getString(4));
                employee.setAdd_line_1(resultSet.getString(5));
                employee.setAdd_line_2(resultSet.getString(6));
                employee.setAdd_line_3(resultSet.getString(7));
                employee.setDob(resultSet.getString(8));
                employee.setNic(resultSet.getString(9));
                employee.setRole(resultSet.getString(10));
                employee.seteId(resultSet.getInt(11));
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
        return employee;
    }

    public boolean updateEmployee(EmployeeModel employee){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE employees SET dob=?,nic=? WHERE user_id_=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,employee.getDob());
            preparedStatement.setString(2,employee.getNic());
            preparedStatement.setInt(3,employee.geteId());

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

    public int getUserId(int id){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        int userId = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT user_id_ FROM employees where id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                userId =  resultSet.getInt(1);
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

    public boolean deleteUser(int id){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;
        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "DELETE FROM users WHERE id = ? ";
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
}
