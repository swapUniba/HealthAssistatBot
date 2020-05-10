package com.triage.rest.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.triage.rest.enummodels.ResultType;
import com.triage.rest.models.users.SearchResult;

public class MedicalDictionaryDao {
	public MedicalDictionaryDao(){}
	
	public void createNewMedicalDictionary(int userID, String text){
		Connection conn = TriageBotConnection.getConn();
		String query = "INSERT INTO MedicalDictionary(userid, text) VALUES(?,?)";
		
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
	
	public int getLastMedicalDictionaryId(int userID){
		Connection conn = TriageBotConnection.getConn();
		String query = "SELECT * FROM MedicalDictionary WHERE userid=? ORDER BY ID DESC LIMIT 1";
		
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
	
	public void saveSearchResults(int userID, ArrayList<SearchResult> searchResults){
		int mdID = getLastMedicalDictionaryId(userID);
		String query = "INSERT INTO MedicalDictionary_SearchResult"
				+ "(medicaldictionary, name, description, resultType, wikiLink) "
				+ "VALUES(?,?,?,?,?)";
		
		for(SearchResult sr: searchResults){
			Connection conn = TriageBotConnection.getConn();
			try {
				PreparedStatement pstmt = conn.prepareStatement(query);
				pstmt.setInt(1, mdID);
				pstmt.setString(2, sr.getName());
				pstmt.setString(3, sr.getDescription());
				pstmt.setString(4, sr.getSearchType().toString());
				pstmt.setString(5, sr.getWikiLink());
				
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
	
	public ArrayList<SearchResult> getSearchResults(int userID){
		Connection conn = TriageBotConnection.getConn();
		int mdID = getLastMedicalDictionaryId(userID);
		String query = "SELECT * FROM MedicalDictionary_SearchResult "
						+ "WHERE medicaldictionary=?";
		
		ArrayList<SearchResult> results = new ArrayList<SearchResult>();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, mdID);
			
			ResultSet result= pstmt.executeQuery();
			while(result.next()){
				SearchResult sr = new SearchResult(result.getInt(1), result.getString(3), 
									result.getString(4), 
									ResultType.valueOf(result.getString(5).toUpperCase()));
				
				if(result.getString(5).equals("BabelNet")){
					sr.setSearchType(ResultType.BABELNET);
				}else{
					sr.setSearchType(ResultType.ICPC2);
				}
				sr.setWikiLink(result.getString(6));
				
				results.add(sr);
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
		
		return results;
	}
}
