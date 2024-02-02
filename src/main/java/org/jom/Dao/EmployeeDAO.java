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
    public int register(EmployeeModel employee) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int employeeId = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "INSERT INTO employees (dob,nic,gender,user_id_) VALUES (?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, employee.getDob());
            preparedStatement.setString(2, employee.getNic());
            preparedStatement.setString(3, employee.getGender());
            preparedStatement.setInt(4, employee.getId());

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
            String sql = "SELECT \n" +
                    "    e.id,\n" +
                    "    u.first_name,\n" +
                    "    u.last_name,\n" +
                    "    u.role,\n" +
                    "    u.phone,\n" +
                    "    u.add_line_3\n" +
                    "FROM\n" +
                    "    employees e\n" +
                    "        INNER JOIN\n" +
                    "    users u\n" +
                    "WHERE\n" +
                    "    u.id = e.user_id_\n" +
                    "        AND u.role <> 'admin' AND u.delete=0;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String first_name = resultSet.getString(2);
                String last_name = resultSet.getString(3);
                String role = resultSet.getString(4);
                String phone = resultSet.getString(5);
                String city = resultSet.getString(6);

                EmployeeModel employee = new EmployeeModel(id, first_name, last_name, phone, city, role);
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

    // get all previous employees
    public List<EmployeeModel> getAllPrevious() {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<EmployeeModel> employees = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    e.id,\n" +
                    "    u.first_name,\n" +
                    "    u.last_name,\n" +
                    "    u.role,\n" +
                    "    u.phone,\n" +
                    "    u.add_line_3\n" +
                    "FROM\n" +
                    "    employees e\n" +
                    "        INNER JOIN\n" +
                    "    users u\n" +
                    "WHERE\n" +
                    "    u.id = e.user_id_\n" +
                    "        AND u.role <> 'admin' AND u.delete=1;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String first_name = resultSet.getString(2);
                String last_name = resultSet.getString(3);
                String role = resultSet.getString(4);
                String phone = resultSet.getString(5);
                String city = resultSet.getString(6);

                EmployeeModel employee = new EmployeeModel(id, first_name,last_name, phone, city, role);
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
            String sql = "SELECT \n" +
                    "    u.first_name,\n" +
                    "    u.last_name,\n" +
                    "    u.email,\n" +
                    "    u.phone,\n" +
                    "    u.add_line_1,\n" +
                    "    u.add_line_2,\n" +
                    "    u.add_line_3,\n" +
                    "    u.delete,\n" +
                    "    e.dob,\n" +
                    "    e.nic,\n" +
                    "    e.gender,\n" +
                    "    u.role,\n" +
                    "    e.id\n" +
                    "FROM\n" +
                    "    employees e\n" +
                    "        INNER JOIN\n" +
                    "    users u\n" +
                    "WHERE\n" +
                    "    u.id = e.user_id_ AND e.id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                employee.setFirst_name(resultSet.getString(1));
                employee.setLast_name(resultSet.getString(2));
                employee.setEmail(resultSet.getString(3));
                employee.setPhone(resultSet.getString(4));
                employee.setAdd_line_1(resultSet.getString(5));
                employee.setAdd_line_2(resultSet.getString(6));
                employee.setAdd_line_3(resultSet.getString(7));
                employee.setDelete(resultSet.getInt(8));
                employee.setDob(resultSet.getString(9));
                employee.setNic(resultSet.getString(10));
                employee.setGender(resultSet.getString(11));
                employee.setRole(resultSet.getString(12));
                employee.seteId(resultSet.getInt(13));
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

    public boolean updateEmployee(EmployeeModel employee) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE employees SET dob=?,nic=?,gender=? WHERE user_id_=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, employee.getDob());
            preparedStatement.setString(2, employee.getNic());
            preparedStatement.setString(3, employee.getGender());
            preparedStatement.setInt(4, employee.getId());

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

    public int getUserId(int id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        int userId = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT user_id_ FROM employees where id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
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

    public boolean deleteUser(int id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;
        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE users u SET u.delete=1 WHERE id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);

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

    public boolean nicExists(String nic) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT nic FROM employees WHERE nic = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, nic);
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

    public int getEId(String nic) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int eId = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT id FROM employees WHERE nic = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, nic);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                eId = resultSet.getInt(1);
                ;
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
        return eId;
    }

    public int rowCount() {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int count = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT COUNT(*) AS Row_Count FROM employees; ";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt(1);
                ;
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

    public int getEIdById(int userId) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int employeeId = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT id FROM employees WHERE user_id_ = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                employeeId = resultSet.getInt(1);
                ;
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

    //get user
    public EmployeeModel getUser(int id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        EmployeeModel employee = new EmployeeModel();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    u.id,\n" +
                    "    u.first_name,\n" +
                    "    u.last_name,\n" +
                    "    u.email,\n" +
                    "    u.phone,\n" +
                    "    CASE\n" +
                    "        WHEN u.role = 'supplier' THEN NULL\n" +
                    "        ELSE e.nic\n" +
                    "    END AS nic,\n" +
                    "    CASE\n" +
                    "        WHEN u.role = 'supplier' THEN NULL\n" +
                    "        ELSE e.dob\n" +
                    "    END AS dob,\n" +
                    "    CASE\n" +
                    "        WHEN u.role = 'supplier' THEN NULL\n" +
                    "        ELSE e.gender\n" +
                    "    END AS gender,\n" +
                    "    u.add_line_1,\n" +
                    "    u.add_line_2,\n" +
                    "    u.add_line_3,\n" +
                    "    u.role\n" +
                    "FROM\n" +
                    "    users u\n" +
                    "        LEFT JOIN\n" +
                    "    employees e ON u.id = e.user_id_\n" +
                    "        AND u.role != 'supplier'\n" +
                    "WHERE\n" +
                    "    u.id = ?;\n";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                employee.setId(resultSet.getInt(1));
                employee.setFirst_name(resultSet.getString(2));
                employee.setLast_name(resultSet.getString(3));
                employee.setEmail(resultSet.getString(4));
                employee.setPhone(resultSet.getString(5));
                employee.setNic(resultSet.getString(6));
                employee.setDob(resultSet.getString(7));
                employee.setGender(resultSet.getString(8));
                employee.setAdd_line_1(resultSet.getString(9));
                employee.setAdd_line_2(resultSet.getString(10));
                employee.setAdd_line_3(resultSet.getString(11));
                employee.setRole(resultSet.getString(12));
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
}
