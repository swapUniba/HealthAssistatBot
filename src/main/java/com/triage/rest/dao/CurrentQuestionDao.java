package com.triage.rest.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.triage.logic.questions.BaseBotQuestions;
import com.triage.logic.questions.MedicalDictionaryQuestions;
import com.triage.logic.questions.SuggestDoctorQuestions;
import com.triage.logic.questions.SurveyQuestions;
import com.triage.logic.questions.SymptomCheckerQuestions;
import com.triage.logic.questions.TherapyQuestions;
import com.triage.logic.questions.TrackingQuestions;
import com.triage.rest.models.messages.Question;

public class CurrentQuestionDao {
	//private Connection conn;
	
	public CurrentQuestionDao(){}
	
	public Question getLastQuestion(int userid){
		Connection conn = TriageBotConnection.getConn();
		String query = "SELECT questionid, sectionmenu FROM CurrentQuestion WHERE userid=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userid);
			ResultSet result= pstmt.executeQuery();
			while(result.next()){
				String qName = result.getString(1);
				String sectionMenu = result.getString(2);
				//BaseBotQuestions
				if(sectionMenu.equalsIgnoreCase("Menu") ||
						sectionMenu.equalsIgnoreCase("UserProfile")){
					BaseBotQuestions bbq = new BaseBotQuestions();
					Question q = bbq.getQuestion(qName);
					if(q != null)
						return q;
				}
				
				//SuggestDoctorQuestions
				if(sectionMenu.equalsIgnoreCase("SuggestDoctor")){
					SuggestDoctorQuestions sdq = new SuggestDoctorQuestions();
					Question q = sdq.getQuestion(qName);
					if(q != null)
						return q;
				}
				
				//SuggestDoctorQuestions
				if(sectionMenu.equalsIgnoreCase("SymptomChecker")){
					SymptomCheckerQuestions scq = new SymptomCheckerQuestions();
					Question q = scq.getQuestion(qName);
					if(q != null)
						return q;
				}
				
				//MedicalDictionaryQuestions
				if(sectionMenu.equalsIgnoreCase("Dictionary")){
					MedicalDictionaryQuestions mdq = new MedicalDictionaryQuestions();
					Question q = mdq.getQuestion(qName);
					if(q != null)
						return q;
				}
				
				//TherapyQuestions
				if(sectionMenu.equalsIgnoreCase("Therapy")){
					TherapyQuestions tq = new TherapyQuestions();
					Question q = tq.getQuestion(qName);
					if(q != null)
						return q;
				}
				
				//TrackingQuestions
				if(sectionMenu.equalsIgnoreCase("Tracking")){
					TrackingQuestions tq = new TrackingQuestions();
					Question q = tq.getQuestion(qName);
					if(q != null)
						return q;
				}
				
				//SurveyQuestions
				if(sectionMenu.equalsIgnoreCase("Survey")){
					SurveyQuestions sq = new SurveyQuestions();
					Question q = sq.getQuestion(qName);
					if(q != null)
						return q;
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
		return null;
	}
	
	public void saveCurrentQuestion(int userid, String questionName){
		Connection conn = TriageBotConnection.getConn();
		String query = "UPDATE CurrentQuestion SET questionid=? WHERE userid=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, questionName);
			pstmt.setInt(2, userid);
			
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
	
	public String getLastSectionMenu(int userid){
		Connection conn = TriageBotConnection.getConn();
		String query1 = "SELECT sectionmenu FROM CurrentQuestion WHERE userid=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(query1);
			pstmt.setInt(1, userid);
			ResultSet result= pstmt.executeQuery();
			while(result.next()){
				System.out.println("Last section menu: " + result.getString(1));
				return result.getString(1);
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
		return null;
	}
	
	public void saveCurrentSectionMenu(int userid, String sectionmenu){
		Connection conn = TriageBotConnection.getConn();
		String query = "UPDATE CurrentQuestion SET sectionmenu=? WHERE userid=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, sectionmenu);
			pstmt.setInt(2, userid);
			
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
}
