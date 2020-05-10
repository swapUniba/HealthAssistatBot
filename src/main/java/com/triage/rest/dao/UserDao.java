package com.triage.rest.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;

import com.triage.rest.enummodels.Sex;
import com.triage.rest.models.users.User;

public class UserDao {
	
	public UserDao(){}

	/*USER Table*/
	public void addUser(User user){
		Connection conn = TriageBotConnection.getConn();
		String query = "INSERT INTO user(id, firstname, lastname, username) VALUES(?,?,?,?)";
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, user.getId());
			
			if(user.getFirstname() != null)
				pstmt.setString(2, user.getFirstname());
			else
				pstmt.setNull(2, Types.VARCHAR);
			
			if(user.getLastname() != null)
				pstmt.setString(3, user.getLastname());
			else
				pstmt.setNull(3, Types.VARCHAR);
			
			if(user.getUsername() != null)
				pstmt.setString(4, user.getUsername());
			else
				pstmt.setNull(4, Types.VARCHAR);
			
			pstmt.executeUpdate();
			pstmt.close();
			} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		conn = TriageBotConnection.getConn();
		String query1 = "INSERT INTO CurrentQuestion VALUES(?,?,?)";
		try {
			PreparedStatement pstmt = conn.prepareStatement(query1);
			pstmt.setInt(1, user.getId());
			pstmt.setString(2, "MENU");
			pstmt.setString(3, "Menu");
						
			pstmt.executeUpdate();
			pstmt.close();
			} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public User getUser(int userID){
		Connection conn = TriageBotConnection.getConn();
		User user = null;
		String query = "SELECT * FROM user WHERE id=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userID);
			ResultSet result= pstmt.executeQuery();
			
			while(result.next()){
				user = new User(result.getInt(1), result.getString(2),
						result.getString(3), result.getString(4));

				String sex = result.getString(5);
				if (!result.wasNull()) {
					user.setSex(Sex.valueOf(sex.toUpperCase()));
			    }
				
				user.setBirth(result.getDate(6));
				
				String city = result.getString(7);
				if (!result.wasNull()) {
					user.setCity(city);
			    }
				
				String province = result.getString(8);
				if (!result.wasNull()) {
					user.setProvince(province);
			    }
			}
			pstmt.close();
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return user;
	}
	
	public ArrayList<User> getAllUsers(){
		ArrayList<User> users = new ArrayList<User>();
		
		Connection conn = TriageBotConnection.getConn();
		String query = "SELECT * FROM user";
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			ResultSet result= pstmt.executeQuery();
			
			while(result.next()){
				User user = new User(result.getInt(1), result.getString(2),
						result.getString(3), result.getString(4));

				String sex = result.getString(5);
				if (!result.wasNull()) {
					user.setSex(Sex.valueOf(sex.toUpperCase()));
			    }
				
				user.setBirth(result.getDate(6));
				
				String city = result.getString(7);
				if (!result.wasNull()) {
					user.setCity(city);
			    }
				
				String province = result.getString(8);
				if (!result.wasNull()) {
					user.setProvince(province);
			    }
				
				users.add(user);
			}
			pstmt.close();
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return users;
	}
	
	public User getUserOrAdd(int userID, String firstname, String lastname, String username){
		User user = getUser(userID);
		
		//User is new user
		if(user == null){
			user = new User(userID, firstname, lastname, username);
			addUser(user);
		}
			
		return user;
	}
	
	public boolean updateUserFirstname(int userID, String firstname){
		Connection conn = TriageBotConnection.getConn();
		String query = "UPDATE user SET firstname=? WHERE id=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, firstname);
			pstmt.setInt(2, userID);
			
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public boolean updateUserLastname(int userID, String lastname){
		Connection conn = TriageBotConnection.getConn();
		String query = "UPDATE user SET lastname=? WHERE id=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, lastname);
			pstmt.setInt(2, userID);
			
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public boolean updateUserSex(int userID, Sex sex){
		Connection conn = TriageBotConnection.getConn();
		String query = "UPDATE user SET sex=? WHERE id=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, sex.toString());
			pstmt.setInt(2, userID);
			
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public boolean updateUserBirth(int userID, Date birth){
		Connection conn = TriageBotConnection.getConn();
		java.sql.Date date = new java.sql.Date(birth.getTime());
		String query = "UPDATE user SET birth=? WHERE id=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setDate(1, date);
			pstmt.setInt(2, userID);
			
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public boolean updateUserCity(int userID, String city){
		Connection conn = TriageBotConnection.getConn();
		String query = "UPDATE user SET city=? WHERE id=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, city);
			pstmt.setInt(2, userID);
			
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public boolean updateUserProvince(int userID, String province){
		Connection conn = TriageBotConnection.getConn();
		String query = "UPDATE user SET province=? WHERE id=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, province);
			pstmt.setInt(2, userID);
			
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
}
