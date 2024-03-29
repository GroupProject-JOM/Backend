package org.jom.Dao;

import org.jom.Database.ConnectionPool;
import org.jom.Model.DistributionModel;
import org.jom.Model.ProductModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DistributionDAO {
    // get all remaining products form product_distribution table from relevant product id
    public List<DistributionModel> getRemaining(int product_id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<DistributionModel> distributions = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    d.id AS distributor_id,\n" +
                    "    u.first_name,\n" +
                    "    u.last_name,\n" +
                    "    pd.quantity\n" +
                    "FROM\n" +
                    "    distributors d\n" +
                    "        INNER JOIN\n" +
                    "    users u ON d.user_id = u.id\n" +
                    "        INNER JOIN\n" +
                    "    product_distribution pd ON d.id = pd.distributor_id\n" +
                    "        AND pd.product_id = ?\n" +
                    "ORDER BY pd.quantity DESC;;\n";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, product_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String first_name = resultSet.getString(2);
                String last_name = resultSet.getString(3);
                int remaining = resultSet.getInt(4);

                DistributionModel distribution = new DistributionModel(id, first_name, last_name, remaining);
                distributions.add(distribution);
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
        return distributions;
    }

    // Update distributor distribution product amount
    public boolean UpdateDistributorAmount(int quantity, int product, int distributor) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE product_distribution pd SET pd.quantity = quantity + ? WHERE pd.product_id = ? AND pd.distributor_id=? ";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, quantity);
            preparedStatement.setInt(2, product);
            preparedStatement.setInt(3, distributor);

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

    // Distributor's products remaining amount
    public List<DistributionModel> DistributorsRemaining(int distributor) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<DistributionModel> distributions = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    pd.id, pd.product_id, p.type, p.category, pd.quantity\n" +
                    "FROM\n" +
                    "    product_distribution pd\n" +
                    "        INNER JOIN\n" +
                    "    products p ON pd.product_id = p.id\n" +
                    "        INNER JOIN\n" +
                    "    distributors d ON d.id = pd.distributor_id\n" +
                    "        INNER JOIN\n" +
                    "    users u ON u.id = d.user_id\n" +
                    "WHERE\n" +
                    "    u.id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, distributor);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                int product = resultSet.getInt(2);
                String type = resultSet.getString(3);
                String category = resultSet.getString(4);
                int remaining = resultSet.getInt(5);

                DistributionModel distribution = new DistributionModel(id, remaining, type, category, product);
                distributions.add(distribution);
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
        return distributions;
    }

    // Distributor's products remaining (Only remaining)
    public List<DistributionModel> DistributorsOnlyRemaining(int distributor) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<DistributionModel> distributions = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    pd.product_id, p.category, p.type, pd.quantity, p.price\n" +
                    "FROM\n" +
                    "    jom_db.product_distribution pd\n" +
                    "        INNER JOIN\n" +
                    "    products p ON p.id = pd.product_id\n" +
                    "        INNER JOIN\n" +
                    "    distributors d ON d.id = pd.distributor_id\n" +
                    "        INNER JOIN\n" +
                    "    users u ON u.id = d.user_id\n" +
                    "WHERE\n" +
                    "    u.id = ? AND pd.quantity <> 0;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, distributor);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int product = resultSet.getInt(1);
                String category = resultSet.getString(2);
                String type = resultSet.getString(3);
                int remaining = resultSet.getInt(4);
                String price = resultSet.getString(5);

                DistributionModel distribution = new DistributionModel(remaining, type, category, product, price);
                distributions.add(distribution);
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
        return distributions;
    }

    //get accepted product count allocated to distributor
    public int allocatedAcceptedProductCount(int distributor) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int count = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    COUNT(*) AS Row_Count\n" +
                    "FROM\n" +
                    "    product_distribution pd\n" +
                    "        INNER JOIN\n" +
                    "    distributors d ON d.id = pd.distributor_id\n" +
                    "        INNER JOIN\n" +
                    "    users u ON u.id = d.user_id\n" +
                    "        INNER JOIN\n" +
                    "    products p ON p.id = pd.product_id\n" +
                    "WHERE\n" +
                    "    u.id = ? AND pd.quantity <> 0\n" +
                    "        AND p.status = 1;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, distributor);
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

    // Add record for distributions table
    public int addDistributionRecord(DistributionModel distribution) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int distributionId = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "INSERT INTO distributions (product,distributor,quantity,price,outlet) VALUES (?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, distribution.getProduct());
            preparedStatement.setInt(2, distribution.getDistributor());
            preparedStatement.setInt(3, distribution.getRemaining());
            preparedStatement.setString(4, distribution.getPrice());
            preparedStatement.setInt(5, distribution.getOutlet());

            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                distributionId = resultSet.getInt(1);
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
        return distributionId;
    }

    // Decrement distributor distributed product amount
    public boolean decrementDistributorAmount(int quantity, int product, int distributor) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        boolean status = false;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "UPDATE product_distribution pd inner join distributors d on d.id=pd.distributor_id SET pd.quantity = quantity - ? WHERE pd.product_id = ? AND d.user_id=? ";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, quantity);
            preparedStatement.setInt(2, product);
            preparedStatement.setInt(3, distributor);

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

    // Distribution records since year
    public List<DistributionModel> DistributionRecordsFromYear(int distributor) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<DistributionModel> distributions = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    DATE(date) AS visit_date,\n" +
                    "    COUNT(DISTINCT outlet) AS total_visits\n" +
                    "FROM\n" +
                    "    distributions\n" +
                    "WHERE\n" +
                    "    distributor = ?\n" +
                    "        AND DATE(date) BETWEEN CURDATE() - INTERVAL 1 YEAR AND CURDATE()\n" +
                    "GROUP BY distributor , visit_date";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, distributor);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String date = resultSet.getString(1);
                int count = resultSet.getInt(2);

                DistributionModel distribution = new DistributionModel(date, count);
                distributions.add(distribution);
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
        return distributions;
    }

    // Get today distribution count via distributor's user id
    public int todayDistributionCount(int distributor) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int count = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    COUNT(DISTINCT outlet) AS total_visits\n" +
                    "FROM\n" +
                    "    distributions\n" +
                    "WHERE\n" +
                    "    distributor = ?\n" +
                    "        AND DATE(date) = CURDATE();";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, distributor);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
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

    // Get last seven days visits count
    public List<DistributionModel> lastSevenDaysVisits(int distributor) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<DistributionModel> visits = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    generated_dates.date AS date,\n" +
                    "    DAYNAME(generated_dates.date) AS day,\n" +
                    "    COUNT(DISTINCT d.outlet) AS total_visits_last_seven_days\n" +
                    "FROM\n" +
                    "    (SELECT \n" +
                    "        CURDATE() - INTERVAL seq DAY AS date\n" +
                    "    FROM\n" +
                    "        (SELECT 0 AS seq UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6) AS seq_table) AS generated_dates\n" +
                    "        LEFT JOIN\n" +
                    "    distributions d ON DATE(d.date) = generated_dates.date\n" +
                    "        AND d.distributor = ?\n" +
                    "        AND generated_dates.date >= CURDATE() - INTERVAL 7 DAY\n" +
                    "GROUP BY generated_dates.date , DAYNAME(generated_dates.date)\n" +
                    "ORDER BY generated_dates.date;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, distributor);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String date = resultSet.getString(1);
                String day = resultSet.getString(2);
                int count = resultSet.getInt(3);

                DistributionModel visit = new DistributionModel(date, day, count);
                visits.add(visit);
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
        return visits;
    }

}
