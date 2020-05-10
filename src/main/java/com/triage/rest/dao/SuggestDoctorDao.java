package com.triage.rest.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.triage.rest.models.classifier.ClassifierResult;
import com.triage.rest.models.suggestDoctor.Doctor;

public class SuggestDoctorDao {
	public SuggestDoctorDao(){}
	
	public void createNewSuggestDoctor(int userID, String text){
		String query = "INSERT INTO SuggestDoctor(userid, text) VALUES(?,?)";
		
		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userID);
			pstmt.setString(2, text);
			
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
	
	public int getLastSuggestDoctorId(int userID){
		String query = "SELECT * FROM SuggestDoctor WHERE userid=? ORDER BY ID DESC LIMIT 1";
		
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
	
	public void saveClassifierResults(int userID, ArrayList<ClassifierResult> ress){
		int sdID = getLastSuggestDoctorId(userID);
		String query = "INSERT INTO SuggestDoctor_ClassifierResult(suggestDoctor, classResult, score)"
						+ " VALUES(?,?,?)";
		
		Connection conn = TriageBotConnection.getConn();
		for(ClassifierResult res: ress){
			try {
				PreparedStatement pstmt = conn.prepareStatement(query);
				pstmt.setInt(1, sdID);
				pstmt.setString(2, res.getName());
				pstmt.setDouble(3, res.getScore());
				
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
	
	public void saveClassifierResultSearch(int userID, String text){
		int sdID = getLastSuggestDoctorId(userID);
		String query = "INSERT INTO SuggestDoctor_ClassifierResultSearch(suggestDoctor, medicalArea) VALUES(?,?)";
		
		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, sdID);
			pstmt.setString(2, text);

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
	
	public ArrayList<ClassifierResult> getClassifierResults(int userID){
		int sdID = getLastSuggestDoctorId(userID);
		String query = "SELECT * FROM SuggestDoctor_ClassifierResult WHERE suggestDoctor=?";
		
		Connection conn = TriageBotConnection.getConn();
		ArrayList<ClassifierResult> ress = new ArrayList<ClassifierResult>();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, sdID);
			
			ResultSet result= pstmt.executeQuery();
			while(result.next()){
				ress.add(new ClassifierResult(result.getString(3), result.getDouble(4)));
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
		
		return ress;
	}
	
	public String getLastClassifierResultSearch(int userID){
		int sdID = getLastSuggestDoctorId(userID);
		String query = "SELECT * FROM SuggestDoctor_ClassifierResultSearch WHERE suggestDoctor=? "
					+ "ORDER BY id DESC";
		
		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, sdID);
			
			ResultSet result= pstmt.executeQuery();
			while(result.next()){
				return result.getString(3);
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
		
		return null;
	}
	
	public ArrayList<Doctor> getSpecilists(String province, String medicalAreaLink){
		String query = "SELECT d.* FROM Doctor d "
				+ "JOIN Doctor_SpecialtyStandard dss ON d.id=dss.doctor "
				+ "JOIN SpecialtyStandard ss ON ss.id=dss.specialty_standard "
				+ "JOIN ReceptionLocation rp ON rp.doctor=d.id "
				+ "WHERE ss.link=? AND rp.address_region=? "
				+ "GROUP BY d.id ORDER BY d.activity_total DESC LIMIT 3;";
		
		Connection connMI = MediciItaliaConnection.getConn();
		ArrayList<Doctor> doctors = new ArrayList<Doctor>();
		try {
			PreparedStatement pstmt = connMI.prepareStatement(query);
			pstmt.setString(1, medicalAreaLink);
			pstmt.setString(2, province);
			
			ResultSet result= pstmt.executeQuery();
			while(result.next()){
				Doctor doc = new Doctor(result.getString(2), result.getInt(3), 
										result.getString(4),result.getString(5));
				doctors.add(doc);
			}
			result.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				connMI.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return doctors;
	}
	
}
