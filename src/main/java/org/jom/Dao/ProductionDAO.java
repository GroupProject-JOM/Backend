package org.jom.Dao;

import org.jom.Database.ConnectionPool;
import org.jom.Model.EmployeeModel;
import org.jom.Model.OutletModel;
import org.jom.Model.ProductionModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductionDAO {
    //create production request
    public int createProductionRequest(ProductionModel productionModel) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int productionId = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "INSERT INTO productions (yard,block,amount,actual) VALUES (?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, productionModel.getYard());
            preparedStatement.setInt(2, productionModel.getBlock());
            preparedStatement.setInt(3, productionModel.getAmount());
            preparedStatement.setInt(4, productionModel.getAmount());

            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                productionId = resultSet.getInt(1);
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
        return productionId;
    }

    //get request
    public ProductionModel getProductionRequest(int id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ProductionModel productionModel = new ProductionModel();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    *\n" +
                    "FROM\n" +
                    "    productions p\n" +
                    "WHERE\n" +
                    "    p.delete = 0 AND p.id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                productionModel.setId(resultSet.getInt(1));
                productionModel.setYard(resultSet.getInt(2));
                productionModel.setBlock(resultSet.getInt(3));
                productionModel.setAmount(resultSet.getInt(4));
                productionModel.setStatus(resultSet.getInt(5));
                productionModel.setDate(resultSet.getString(7));
                productionModel.setReason(resultSet.getString(8));
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
        return productionModel;
    }

    //get all request
    public List<ProductionModel> getAllProductionRequests() {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<ProductionModel> productionModels = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    *\n" +
                    "FROM\n" +
                    "    productions p\n" +
                    "WHERE\n" +
                    "    p.delete = 0 AND p.status <> 0\n" +
                    "ORDER BY p.date DESC;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                int yard = resultSet.getInt(2);
                int block = resultSet.getInt(3);
                int amount = resultSet.getInt(4);
                int status = resultSet.getInt(5);
                String date = resultSet.getString(7);

                ProductionModel productionModel = new ProductionModel(id, yard, block, amount, status, date);
                productionModels.add(productionModel);
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
        return productionModels;
    }

    // update production request
    public boolean updateProductionRequest(ProductionModel productionModel) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE productions p SET p.yard=?,p.block=?,p.amount=?,p.status=1,p.date=CURRENT_TIMESTAMP,p.actual=? WHERE p.id = ? AND p.delete=0 AND p.status<4";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, productionModel.getYard());
            preparedStatement.setInt(2, productionModel.getBlock());
            preparedStatement.setInt(3, productionModel.getAmount());
            preparedStatement.setInt(4, productionModel.getAmount());
            preparedStatement.setInt(5, productionModel.getId());

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

    // delete production request
    public boolean deleteProductionRequest(int id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE productions SET jom_db.productions.delete=1 WHERE id = ? AND status<4";
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

    //get all pending request for stock manager
    public List<ProductionModel> getAllPendingProductionRequests() {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<ProductionModel> productionModels = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    *\n" +
                    "FROM\n" +
                    "    productions p\n" +
                    "WHERE\n" +
                    "    p.delete = 0 AND p.status=1\n" +
                    "LIMIT 4;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                int yard = resultSet.getInt(2);
                int block = resultSet.getInt(3);
                int amount = resultSet.getInt(4);
                int status = resultSet.getInt(5);
                String date = resultSet.getString(7);

                ProductionModel productionModel = new ProductionModel(id, yard, block, amount, status, date);
                productionModels.add(productionModel);
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
        return productionModels;
    }

    // update status production request
    public boolean updateProductionRequestStatus(int id,int st) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE productions p SET p.status=? WHERE id = ? AND p.delete=0";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, st);
            preparedStatement.setInt(2, id);

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

    public boolean rejectProductionRequest(int id, String reason) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE productions p SET p.status=3,p.reason=? WHERE id = ? AND p.delete=0";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, reason);
            preparedStatement.setInt(2, id);

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

    //get all accepted requests
    public List<ProductionModel> getAllAcceptedRequests() {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<ProductionModel> productionModels = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    *\n" +
                    "FROM\n" +
                    "    productions p\n" +
                    "WHERE\n" +
                    "    p.delete = 0 AND p.status =2 ;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                int yard = resultSet.getInt(2);
                int block = resultSet.getInt(3);
                int amount = resultSet.getInt(4);
                int status = resultSet.getInt(5);
                String date = resultSet.getString(7);
                int actual = resultSet.getInt(9);

                ProductionModel productionModel = new ProductionModel(id, yard, block, amount, status, date, actual);
                productionModels.add(productionModel);
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
        return productionModels;
    }

    // update actual amount of production request
    public boolean updateActualAmount(int id,int amount) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE productions p SET p.actual=? WHERE id = ? AND p.delete=0";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, amount);
            preparedStatement.setInt(2, id);

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
