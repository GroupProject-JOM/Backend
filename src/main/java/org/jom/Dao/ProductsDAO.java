package org.jom.Dao;

import org.jom.Database.ConnectionPool;
import org.jom.Model.ProductModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductsDAO {
    //get products list
    public List<ProductModel> getProducts() {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<ProductModel> products = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    *\n" +
                    "FROM\n" +
                    "    jom_db.products p\n" +
                    "WHERE\n" +
                    "    p.delete = 0;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String type = resultSet.getString(2);
                String category = resultSet.getString(3);
                String price = resultSet.getString(4);
                int status = resultSet.getInt(5);

                ProductModel product = new ProductModel(id, type, category, price, status);
                products.add(product);
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
        return products;
    }

    // Add product
    public int addProduct(ProductModel product) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int productId = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "INSERT INTO products (type,category,price,status) VALUES (?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, product.getType());
            preparedStatement.setString(2, product.getCategory());
            preparedStatement.setString(3, product.getPrice());
            preparedStatement.setInt(4, product.getStatus());

            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                productId = resultSet.getInt(1);
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
        return productId;
    }

    // delete product
    public boolean deleteProduct(int id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE products SET jom_db.products.delete=1 WHERE id = ?";
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

    //get all products
    public ProductModel getProduct(int id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ProductModel product = new ProductModel();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT * FROM products WHERE id = ? AND jom_db.products.delete=0";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                product.setId(resultSet.getInt(1));
                product.setType(resultSet.getString(2));
                product.setCategory(resultSet.getString(3));
                product.setPrice(resultSet.getString(4));
                product.setStatus(resultSet.getInt(5));
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
        return product;
    }

    // update product
    public boolean updateProduct(ProductModel product) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE products p SET p.type=?,p.category=?,p.price=?,p.status=? WHERE p.id = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, product.getType());
            preparedStatement.setString(2, product.getCategory());
            preparedStatement.setString(3, product.getPrice());
            preparedStatement.setInt(4, product.getStatus());
            preparedStatement.setInt(5, product.getId());

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

    //get production released products list
    public List<ProductModel> getProductionProducts(List<Integer> idList) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<ProductModel> products = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();

            // Construct the SQL query with the appropriate number of placeholders
            StringBuilder sqlBuilder = new StringBuilder("SELECT p.id, p.type, p.category FROM jom_db.products p WHERE p.id IN (");
            for (int i = 0; i < idList.size(); i++) {
                sqlBuilder.append("?");
                if (i < idList.size() - 1) {
                    sqlBuilder.append(", ");
                }
            }
            sqlBuilder.append(")");

            PreparedStatement preparedStatement = connection.prepareStatement(sqlBuilder.toString());

            // Set the parameters
            for (int i = 0; i < idList.size(); i++) {
                preparedStatement.setInt(i + 1, idList.get(i));
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String type = resultSet.getString(2);
                String category = resultSet.getString(3);

                ProductModel product = new ProductModel(id, type, category);
                products.add(product);
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
        return products;
    }

    // check if unverified products available
    public boolean checkUnverified() {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    *\n" +
                    "FROM\n" +
                    "    jom_db.products p\n" +
                    "WHERE\n" +
                    "    p.status = 0 AND p.delete = 0;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                status = true;
            }
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

    //get accepted products list
    public List<ProductModel> getAcceptedProducts() {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<ProductModel> products = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    *\n" +
                    "FROM\n" +
                    "    jom_db.products p\n" +
                    "WHERE\n" +
                    "    p.delete = 0 AND p.status=1;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String type = resultSet.getString(2);
                String category = resultSet.getString(3);
                String price = resultSet.getString(4);
                int status = resultSet.getInt(5);

                ProductModel product = new ProductModel(id, type, category, price, status);
                products.add(product);
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
        return products;
    }

    //get accepted product count
    public int acceptedProductCount() {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int count = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT COUNT(*) AS Row_Count FROM products WHERE status=1;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt(1);
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
