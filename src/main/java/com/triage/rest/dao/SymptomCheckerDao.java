package com.triage.rest.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.triage.rest.models.diagnosisApi.DiseaseProbability;

public class SymptomCheckerDao {
	
	public SymptomCheckerDao(){}
	
	public void createNewSymptomChecker(int userID){
		String query = "INSERT INTO SymptomChecker(userid) VALUES(?)";

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
	
	public int getLastSymptomCheckerId(int userID){
		String query = "SELECT * FROM SymptomChecker WHERE userid=? ORDER BY ID DESC LIMIT 1";

		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userID);
			
			ResultSet result= pstmt.executeQuery();
			while(result.next()){
				return result.getInt(1);
			}
			result.close();
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
		
		return -1;
	}
	
	public void addNewSymptom(int userID, String symptomText, String symptomUrl){
		int scID = getLastSymptomCheckerId(userID);
		String query = "INSERT INTO SymptomChecker_Symptoms(symptomChecker, symptom_text, symptom_name)"
				+ " VALUES(?,?,?)";

		Connection conn = TriageBotConnection.getConn();
		try{
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, scID);
			pstmt.setString(2, symptomText);
			pstmt.setString(3, symptomUrl);
			
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
	
	public void removeDiagnosisResults(int userID){
		int scID = getLastSymptomCheckerId(userID);
		String query = "DELETE FROM SymptomChecker_Results WHERE symptomchecker=?;";
		
		Connection conn = TriageBotConnection.getConn();
		try{
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, scID);
			
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
	
	public void saveDiagnosisResults(int userID, ArrayList<DiseaseProbability> diseases){
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		int scID = getLastSymptomCheckerId(userID);
		String query = "INSERT INTO SymptomChecker_Results(symptomChecker, diagnosis_name, diagnosis_accuracy, timestamp)"
				+ " VALUES(?,?,?,?)";

		Connection conn = TriageBotConnection.getConn();
		for(DiseaseProbability dp : diseases){
			try{
				PreparedStatement pstmt = conn.prepareStatement(query);
				pstmt.setInt(1, scID);
				pstmt.setString(2, dp.getName());
				pstmt.setDouble(3, dp.getProbability());
				pstmt.setLong(4, (timestamp.getTime()/1000));
				
				pstmt.executeUpdate();
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> getInquiredSymptoms(int userID){
		int scID = getLastSymptomCheckerId(userID);
		String query = "SELECT DISTINCT symptom_name FROM SymptomChecker_Symptoms "
				+ "WHERE symptomChecker=?";

		Connection conn = TriageBotConnection.getConn();
		ArrayList<String> symptoms = new ArrayList<String>();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, scID);
			
			ResultSet result= pstmt.executeQuery();
			while(result.next()){
				symptoms.add(result.getString(1));
			}
			result.close();
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
		
		return symptoms;
	}
}
