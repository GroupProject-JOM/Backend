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

    // get distribution activity logs data
    public List<DistributionModel> getActivityLogs() {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<DistributionModel> distributions = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    d.price,\n" +
                    "    d.quantity,\n" +
                    "    DATE(d.date) AS distribution_date,\n" +
                    "    DATE_FORMAT(d.date, '%h:%i %p') AS distribution_time,\n" +
                    "    p.type,\n" +
                    "    p.category,\n" +
                    "    u.first_name,\n" +
                    "    u.last_name,\n" +
                    "    o.name AS outlet_name,\n" +
                    "    o.city AS outlet_city\n" +
                    "FROM\n" +
                    "    jom_db.distributions d\n" +
                    "        INNER JOIN\n" +
                    "    products p ON p.id = d.product\n" +
                    "        INNER JOIN\n" +
                    "    users u ON u.id = d.distributor\n" +
                    "        INNER JOIN\n" +
                    "    outlets o ON o.id = d.outlet\n" +
                    "ORDER BY d.id DESC;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String price = resultSet.getString(1);
                int quantity = resultSet.getInt(2);
                String date = resultSet.getString(3);
                String time = resultSet.getString(4);
                String type = resultSet.getString(5);
                String category = resultSet.getString(6);
                String firstName = resultSet.getString(7);
                String lastName = resultSet.getString(8);
                String outletName = resultSet.getString(9);
                String area = resultSet.getString(10);

                DistributionModel distribution = new DistributionModel(firstName, lastName, type, price, outletName, area, date, time, category, quantity);
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

    // get this month revenue of total distributions
    public int getThisMonthRevenue() {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int revenue = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT \n" +
                    "    SUM(price) AS total_price\n" +
                    "FROM\n" +
                    "    jom_db.distributions\n" +
                    "WHERE\n" +
                    "    MONTH(date) = MONTH(CURRENT_DATE())\n" +
                    "        AND YEAR(date) = YEAR(CURRENT_DATE());";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                revenue = resultSet.getInt(1);
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
        return revenue;
    }

    // get last year and this year monthly distribution revenue totals
    public List<DistributionModel> getMonthlyRevenue() {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        ArrayList<DistributionModel> revenue = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "WITH YearMonth AS (\n" +
                    "  SELECT EXTRACT(YEAR FROM date) AS year,\n" +
                    "         EXTRACT(MONTH FROM date) AS month\n" +
                    "  FROM distributions\n" +
                    "),\n" +
                    "ThisYearTotals AS (\n" +
                    "  SELECT EXTRACT(MONTH FROM date) AS month,\n" +
                    "         SUM(price) AS total_price_this_year\n" +
                    "  FROM distributions\n" +
                    "  WHERE EXTRACT(YEAR FROM date) = EXTRACT(YEAR FROM CURRENT_DATE)\n" +
                    "  GROUP BY EXTRACT(MONTH FROM date)\n" +
                    "),\n" +
                    "LastYearTotals AS (\n" +
                    "  SELECT EXTRACT(MONTH FROM date) AS month,\n" +
                    "         SUM(price) AS total_price_last_year\n" +
                    "  FROM distributions\n" +
                    "  WHERE EXTRACT(YEAR FROM date) = EXTRACT(YEAR FROM CURRENT_DATE) - 1\n" +
                    "  GROUP BY EXTRACT(MONTH FROM date)\n" +
                    "),\n" +
                    "AllMonths AS (\n" +
                    "  SELECT 1 AS month UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL\n" +
                    "  SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL\n" +
                    "  SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL\n" +
                    "  SELECT 10 UNION ALL SELECT 11 UNION ALL SELECT 12\n" +
                    ")\n" +
                    "SELECT COALESCE(AM.month, TYT.month, LYT.month) AS month,\n" +
                    "       COALESCE(total_price_this_year, 0) AS total_price_this_year,\n" +
                    "       COALESCE(total_price_last_year, 0) AS total_price_last_year\n" +
                    "FROM AllMonths AM\n" +
                    "LEFT JOIN ThisYearTotals TYT ON AM.month = TYT.month\n" +
                    "LEFT JOIN LastYearTotals LYT ON AM.month = LYT.month\n" +
                    "ORDER BY month;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int thisYear = resultSet.getInt(2);
                int lastYear = resultSet.getInt(3);

                DistributionModel distribution = new DistributionModel(thisYear, lastYear);
                revenue.add(distribution);
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
        return revenue;
    }
}
