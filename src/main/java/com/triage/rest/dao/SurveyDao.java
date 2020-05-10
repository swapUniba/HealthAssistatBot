package com.triage.rest.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SurveyDao {

	public void createNewSurvey(int userID){
		String query = "INSERT INTO SurveyResponse(userid) VALUES(?)";

		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userID);
			
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
	
	public void updateQuestionValue(int userID, String value, String question){
		String query = "UPDATE SurveyResponse SET "+ question + "=? WHERE userid=?";

		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, value);
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
	}
	
	public ArrayList<Integer> getUserNotEndedSurvey(){
		ArrayList<Integer> users = new ArrayList<Integer>();
		String query = "SELECT DISTINCT u.id FROM user u LEFT JOIN SurveyResponse sr "
							+ "ON u.id=sr.userid "
							+ "WHERE sr.Q5_FIN IS NULL AND sr.Q1_PRE IS NULL";
		
		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			
			ResultSet result= pstmt.executeQuery();
			while(result.next()){
				users.add(result.getInt(1));
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
}
