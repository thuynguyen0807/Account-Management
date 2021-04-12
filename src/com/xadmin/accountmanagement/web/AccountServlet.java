package com.xadmin.accountmanagement.web;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xadmin.accountmanagement.bean.Account;
import com.xadmin.accountmanagement.dao.AccountDao;

/**
 * Servlet implementation class AccountServlet
 */
@WebServlet("/")
public class AccountServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private AccountDao accountDao;

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init() throws ServletException {
		accountDao = new AccountDao();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getServletPath();
		switch (action) {
			case "/new":
				showNewForm(request, response);
				break;
			case "/insert":
				try {
					insertAccount(request, response);
				} catch (ServletException | IOException | SQLException e) {
					e.printStackTrace();
				}
				break;
			case "/delete":
				deleteAccount(request, response);
				break;
			case "/edit":
				try {
					showEditForm(request, response);
				} catch (ServletException | IOException | SQLException e) {
					e.printStackTrace();
				}
				break;
			case "/update":
				try {
					System.out.println("Update");
					updateAccount(request, response);
				} catch (SQLException | IOException e) {
					e.printStackTrace();
				}
				break;
			default:
				try {
					listAccount(request, response);
				} catch (ServletException | IOException | SQLException e) {
					e.printStackTrace();
				}
				break;
		}
	}

	private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher("account-form.jsp");
		dispatcher.forward(request, response);
	}
	
	// insert account
	private void insertAccount(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
		String name = request.getParameter("name");
		String email = request.getParameter("email");
		String country = request.getParameter("country");
		Account newAccount = new Account(name, email, country);
		accountDao.insertAccount(newAccount);
		response.sendRedirect("list");
	}
	
	// delete account
	private void deleteAccount(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int id = Integer.parseInt(request.getParameter("id"));
		System.out.println(id);
		try {
			accountDao.deleteAccount(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.sendRedirect("list");
	}

	// edit account
	private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
		int id = Integer.parseInt(request.getParameter("id"));
		System.out.println(id);
		Account existingAccount;
		try {
			existingAccount = accountDao.selectAccountById(id);
			RequestDispatcher dispatcher = request.getRequestDispatcher("account-form.jsp");
			request.setAttribute("account", existingAccount);
			dispatcher.forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void updateAccount(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException{
		int id = Integer.parseInt(request.getParameter("id"));
		System.out.println(id);
		String name = request.getParameter("name");
		String email = request.getParameter("email");
		String country = request.getParameter("country");
		Account account = new Account(id, name, email, country);
		accountDao.updateAccount(account);
		response.sendRedirect("list");
	}

	// default
	private void listAccount(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
		try {
			List<Account> listAccount = accountDao.selectAllAccounts();
			request.setAttribute("listAccount", listAccount);
			RequestDispatcher dispatcher = request.getRequestDispatcher("account-list.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
}
