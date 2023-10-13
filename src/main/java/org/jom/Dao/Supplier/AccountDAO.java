package org.jom.Dao.Supplier;

import org.jom.Database.ConnectionPool;
import org.jom.Model.AccountModel;
import org.jom.Model.EstateModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public List<AccountModel> getAll(int id) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Connection connection = null;

        ArrayList<AccountModel> accounts = new ArrayList<>();

        try {
            connection = connectionPool.dataSource.getConnection();
            String sql = "SELECT * FROM accounts WHERE supplier_id_ = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int account_id = resultSet.getInt(1);
                String holder_name = resultSet.getString(2);
                String account_number = resultSet.getString(3);
                String bank = resultSet.getString(4);
                int supplier_id = resultSet.getInt(5);

                AccountModel account = new AccountModel(account_id,supplier_id,holder_name,account_number,bank);
                accounts.add(account);
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
        return accounts;
    }
}
