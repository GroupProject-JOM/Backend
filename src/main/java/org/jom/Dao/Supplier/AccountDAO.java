package org.jom.Dao.Supplier;

import org.jom.Database.ConnectionPool;
import org.jom.Model.AccountModel;

import java.sql.*;

public class AccountDAO {
    public int addAccount(AccountModel account){
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;
        int accountId = 0;

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "INSERT INTO accounts (account_num,bank,name,supplier_id_) VALUES (?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1,account.getAccount_number());
            preparedStatement.setString(2,account.getBank());
            preparedStatement.setString(3,account.getName());
            preparedStatement.setInt(4,account.getSupplier_id());

            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                accountId = resultSet.getInt(1);
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
        return accountId;
    }
}
