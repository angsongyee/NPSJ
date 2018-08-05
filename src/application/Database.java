package application;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

class Database {
	// SQL
	private final String dbName = "nspj";
	private final String dbUser = "root";
	private final String dbPass = "";
	private Connection con;

	protected boolean init() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try {
				this.con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dbName, dbUser, dbPass);
			} catch (SQLException e) {
				return false;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return true;
	}

	protected void insertUser(User user) {
		try {
			String sql = "INSERT INTO user (code, fName, lName, id, salt, hash, age, job) VALUES (?, ?, ?, ?, SUBSTRING(SHA1(RAND()), 1, 15), SHA1(CONCAT(salt, '"
					+ user.getPass() + "')), ?, ?)";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, user.getCode());
			ps.setString(2, user.getfName());
			ps.setString(3, user.getlName());
			ps.setString(4, user.getId());
			ps.setInt(5, user.getAge());
			ps.setString(6, user.getJob());

			try {
				int rowsInserted = ps.executeUpdate();
				if (rowsInserted > 0) {
					System.out.println("User Added");
				}
			} catch (MySQLIntegrityConstraintViolationException e) {
				System.out.println("User Already Exist");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected User getUser(int code) {
		User user = new User();
		try {
			String sql = "SELECT * FROM user WHERE code = " + code + " LIMIT 1";
			Statement s = con.createStatement();
			ResultSet rs = s.executeQuery(sql);

			while (rs.next()) {
				user = new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
						rs.getString(6), rs.getInt(7), rs.getString(8));
			}
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}

	protected String getHash(String code, String pass) {
		String hash = "";

		try {
			String sql = "SELECT SHA1(CONCAT(salt, '" + pass + "')) FROM user WHERE code = " + code + " LIMIT 1";
			Statement s = con.createStatement();
			ResultSet rs = s.executeQuery(sql);

			if (rs.next()) {
				hash = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return hash;
	}

	protected int getNextCode() {
		int maxCode = -1;

		try {
			String sql = "SELECT MAX(code) FROM user LIMIT 1";
			Statement s = con.createStatement();
			ResultSet rs = s.executeQuery(sql);

			if (rs.next())
				maxCode = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return maxCode + 1;
	}

	protected int getNextIntID(String job) {
		ArrayList<String> IDList = new ArrayList<String>();
		ArrayList<Integer> IDIntList = new ArrayList<Integer>();
		int nxtID = 0;

		try {
			String sql = "SELECT id FROM user WHERE job=" + "'" + job.toUpperCase() + "'";
			Statement s = con.createStatement();
			ResultSet rs = s.executeQuery(sql);

			if (rs == null) {
				System.out.println("hi");
			} else {
				while (rs.next()) {
					IDList.add(rs.getString(1));
					for (String i : IDList) {
						// Extract Integer ID
						Scanner ex = new Scanner(i);
						ex.useDelimiter("[^0-9]+");
						int IDInt = ex.nextInt();
						IDIntList.add(IDInt);
					}
					nxtID = Collections.max(IDIntList) + 1;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return nxtID;
	}

	protected void insertLog(Log log) {
		try {
			String sql = "INSERT INTO log (id, osUser, userLoc, dateTime, osName, hostname, ip) VALUES (?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, log.getId());
			ps.setString(2, log.getOsUser());
			ps.setString(3, log.getUserLoc());
			ps.setString(4, log.getDateTime().toString());
			ps.setString(5, log.getOsName());
			ps.setString(6, log.getHostname());
			ps.setString(7, log.getIp());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected ArrayList<Log> getLog(String id) {
		ArrayList<Log> logs = new ArrayList<Log>();

		try {
			String sql = "SELECT * FROM user WHERE code = " + id + " LIMIT 1";
			Statement s = con.createStatement();
			ResultSet rs = s.executeQuery(sql);

			while (rs.next()) {
				Log log = new Log(rs.getString(1), rs.getString(2), rs.getString(3), rs.getDate(4), rs.getString(5),
						rs.getString(6), rs.getString(7));
				logs.add(log);
			}
			con.close();
		} catch (SQLException | UnknownHostException e) {
			e.printStackTrace();
		}
		return logs;
	}

	protected void close() {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
