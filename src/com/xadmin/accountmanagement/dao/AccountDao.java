package com.xadmin.accountmanagement.dao;

import java.beans.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.xadmin.accountmanagement.bean.Account;

public class AccountDao {
	private String jdbcURL = "jdbc:sqlserver://localhost;databaseName=account";
	private String username = "sa";
	private String password = "131123Na";
	
	private static final String INSERT_ACCOUNT_SQL = "INSERT INTO accounts" + " (name, email, country) VALUES" + " (?, ?, ?);";
	private static final String SELECT_ACCOUNT_BY_ID = "SELECT * FROM accounts WHERE id = ?;";
	private static final String SELECT_ALL_ACCOUNTS = "SELECT * FROM accounts;";
	private static final String DELETE_ACCOUNTS_SQL = "DELETE FROM accounts WHERE id = ?;";
	private static final String UPDATE_ACCOUNTS_SQL = "UPDATE accounts SET name = ?, email = ?, country = ? WHERE id = ?;";
	
	public AccountDao() {
		
	}

	protected Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			connection = DriverManager.getConnection(jdbcURL, username, password);
			System.out.println("Connected!");
		}
		catch (SQLException | ClassNotFoundException e) {
			System.out.println("Error!");
			e.printStackTrace();
		}
		return connection;
	}
	
	// insert user
	public void insertAccount(Account account) throws SQLException {
		System.out.println(INSERT_ACCOUNT_SQL);
		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ACCOUNT_SQL)) {
			preparedStatement.setString(1, account.getName());
			preparedStatement.setString(2, account.getEmail());
			preparedStatement.setString(3, account.getCountry());
			preparedStatement.executeUpdate();
		} catch(SQLException e) {
			printSQLException(e);
		}
	}
	
	// select account by ID
	public Account selectAccountById(int id) {
		Account account = null;
		// step 1: Establishing a connection
		try (Connection connection = getConnection();
				// step 2: create a statement using connection object
				PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ACCOUNT_BY_ID);) {
			preparedStatement.setInt(1, id);
			System.out.println(preparedStatement);
			// step 3: execute the query or update query
			ResultSet resultSet = preparedStatement.executeQuery();
			// step 4: Process the result set object
			while (resultSet.next()) {
				String name = resultSet.getString("name");
				String email = resultSet.getString("email");
				String country = resultSet.getString("country");
				account = new Account(id, name, email, country);
			}
		} catch (SQLException e) {
			printSQLException(e);
		}
		return account;
	}
	
	// select all users
	public List<Account> selectAllAccounts() {
		List<Account> accounts = new ArrayList<>();
		// step 1: establishing a connection
		try (Connection connection = getConnection();
				// step 2: create a statement using connection object
				PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_ACCOUNTS);) {
			System.out.println(preparedStatement);
			// step 3: execute the query or update query
			ResultSet resultSet = preparedStatement.executeQuery();
			//step 4: process the ResultSet object
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				String email = resultSet.getString("email");
				String country = resultSet.getString("country");
				accounts.add(new Account(id, name, email, country));
			}
		} catch (SQLException e) {
			printSQLException(e);
		}
		return accounts;
	}
	
	// update account
	public boolean updateAccount(Account account) throws SQLException {
		boolean rowUpdated;
		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ACCOUNTS_SQL);) {
			
			preparedStatement.setString(1, account.getName());
			preparedStatement.setString(2, account.getEmail());
			preparedStatement.setString(3, account.getCountry());
			preparedStatement.setInt(4, account.getId());
			System.out.println("Update account: " + preparedStatement);
			
			rowUpdated = preparedStatement.executeUpdate() > 0;
		} 
		return rowUpdated;
	}
	
	// delete account
	public boolean deleteAccount(int id) throws SQLException {
		boolean rowDeleted;
		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ACCOUNTS_SQL);) {
			preparedStatement.setInt(1, id);
			rowDeleted = preparedStatement.executeUpdate() > 0;
		}
		return rowDeleted;
	}
	
	private void printSQLException(SQLException ex) {
		for (Throwable e : ex) {
			if (e instanceof SQLException) {
				e.printStackTrace(System.err);
				System.err.println("SQLState: " + ((SQLException) e).getSQLState());
				System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
				System.err.println("Message: " + e.getMessage());
				Throwable t = ex.getCause();
				while (t != null) {
					System.out.println("Cause: " + t);
					t = t.getCause();
				}
			}
		}
	}
	
	
	
}
