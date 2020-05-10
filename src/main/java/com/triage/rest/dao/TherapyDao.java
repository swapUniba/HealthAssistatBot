package com.triage.rest.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.triage.rest.models.users.Therapy;
import com.triage.rest.models.users.TherapyType;

public class TherapyDao {
	public TherapyDao(){}
	
	/**
	 * Restituisce tutte le terapie contenute nel database.
	 */
	public ArrayList<Therapy> getAllTherapies(int userID){
		Connection conn = TriageBotConnection.getConn();
		ArrayList<Therapy> therapies = new ArrayList<Therapy>();
		String query = "SELECT * FROM Therapy WHERE userid=? AND done=1 AND visible=1 "
						+ "ORDER BY last_update DESC";
		String query1 = "SELECT * FROM Therapy_Hour WHERE therapy=?";
		String query2 = "SELECT * FROM Therapy_Day WHERE therapy=?";
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userID);
			
			ResultSet result= pstmt.executeQuery();
			while(result.next()){
				Therapy therapy = new Therapy(result.getInt(1), result.getString(3),
						result.getString(4), result.getDate(6));
				
				Date startDate = result.getDate(5);
				if (!result.wasNull()) {
					therapy.setStartTime(startDate);;
				}
				
				String type = result.getString(7);
				if (!result.wasNull()) {
					therapy.setType(TherapyType.valueOf(type));
				}
				
				String drugName = result.getString(12);
				if(drugName != null && !result.wasNull() ) {
					therapy.setDrugName(drugName);
					}
				
				int interval = result.getInt(13);
				if (!result.wasNull()) {
					therapy.setIntervalDays(interval);
			    }
				
				
				PreparedStatement pstmt1 = conn.prepareStatement(query1);
				pstmt1.setInt(1, therapy.getId());
				
				ResultSet result1= pstmt1.executeQuery();
				while(result1.next()){
					therapy.setSingleHour(result1.getString(3));
				}
				result1.close();
				pstmt1.close();
				
				if(therapy.getType() == TherapyType.SOME_DAY){
					PreparedStatement pstmt2 = conn.prepareStatement(query2);
					pstmt2.setInt(1, therapy.getId());
					
					ResultSet result2= pstmt2.executeQuery();
					while(result2.next()){
						therapy.setSingleDay(result2.getString(3));
					}
					result2.close();
					pstmt2.close();
				}
				
				therapies.add(therapy);
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
		
		return therapies;
	}
	
	/**
	 * Restituisce una terapia selezionadola per nome
	 */
	public Therapy getTherapy(int userID, String therapyName){
		Connection conn = TriageBotConnection.getConn();
		ArrayList<Therapy> ths = getAllTherapies(userID);
		String query = "UPDATE Therapy SET last_visited=? WHERE id=?";

		for(Therapy th : ths){
			if(therapyName.equalsIgnoreCase(th.getName())){ //TODO fuzzy search
				try {
					PreparedStatement pstmt = conn.prepareStatement(query);
					pstmt.setLong(1, System.currentTimeMillis());
					pstmt.setInt(2, th.getId());
					
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
				return th;
			}
		}
		return null;
	}
	
	/**
	 * Restituisce il numero di terapie (attive) con quel nome
	 */
	public boolean repeatedTherapyName(int userID, String name){
		Connection conn = TriageBotConnection.getConn();
		String query = "SELECT COUNT(*) FROM Therapy WHERE "
				+ "userid=? AND name=? AND done=1 AND visible=1";
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userID);
			pstmt.setString(2, name);
			
			ResultSet result= pstmt.executeQuery();
			result.next();
			int numRes = result.getInt(1);
			
			result.close();
			pstmt.close();
			if(numRes>0){
				return true;
			}else{
				return false;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	/**
	 * Aggiunge una nuova terapia settando solo il nome
	 */
	public void addNewTherapy(int userID, String name){
		Connection conn = TriageBotConnection.getConn();
		String query = "INSERT INTO Therapy(userid, name, last_update, last_visited) "
						+ "VALUES(?,?, ?,?)";
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userID);
			pstmt.setString(2, name);
			pstmt.setLong(3, System.currentTimeMillis());
			pstmt.setLong(4, System.currentTimeMillis());
			
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
	
	/**
	 * Restituisce l'ID dell'ultima terapia inserita
	 */
	public int getLastCreatedTherapyID(int userID){
		return getLastCreatedTherapy(userID, false).getId();
	}
	
	/**
	 * Restituisce l'ultima terapia inserita
	 */
	public Therapy getLastCreatedTherapy(int userID, boolean all){
		Connection conn = TriageBotConnection.getConn();
		String query = "SELECT * FROM Therapy WHERE userid=? ORDER BY id DESC LIMIT 1";
		String query1 = "SELECT * FROM Therapy_Hour WHERE therapy=?";
		String query2 = "SELECT * FROM Therapy_Day WHERE therapy=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userID);
			
			ResultSet result= pstmt.executeQuery();
			result.next();
			Therapy therapy = new Therapy(result.getInt(1), result.getString(3),
											result.getString(4), result.getDate(6));
			Date startDate = result.getDate(5);
			if (!result.wasNull()) {
				therapy.setStartTime(startDate);;
		    }
			
			String type = result.getString(7);
			if (!result.wasNull()) {
				therapy.setType(TherapyType.valueOf(type));
		    }
			
			int interval = result.getInt(13);
			if (!result.wasNull()) {
				therapy.setIntervalDays(interval);
		    }
			
			String drugName = result.getString(12);
			if (!result.wasNull()) {
				therapy.setDrugName(drugName);
			}
			
			
			if(all){
				PreparedStatement pstmt1 = conn.prepareStatement(query1);
				pstmt1.setInt(1, therapy.getId());
				
				ResultSet result1= pstmt1.executeQuery();
				while(result1.next()){
					therapy.setSingleHour(result1.getString(3));
				}
				result1.close();
				pstmt1.close();
				
				if(therapy.getType() == TherapyType.SOME_DAY){
					PreparedStatement pstmt2 = conn.prepareStatement(query2);
					pstmt2.setInt(1, therapy.getId());
					
					ResultSet result2= pstmt2.executeQuery();
					while(result2.next()){
						therapy.setSingleDay(result2.getString(3));
					}
					result2.close();
					pstmt2.close();
				}
			}
			result.close();
			pstmt.close();
			
			return therapy;
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
	
	/**
	 * Restituisce l'ID dell'ultima terapia visitata
	 */
	public int getLastVisitedTherapyID(int userID){
		return getLastVisitedTherapy(userID, false).getId();
	}
	
	/**
	 * Restituisce l'ultima terapia visitata
	 */
	public Therapy getLastVisitedTherapy(int userID, boolean all){
		Connection conn = TriageBotConnection.getConn();
		String query = "SELECT * FROM Therapy WHERE userid=? ORDER BY last_visited DESC LIMIT 1";
		String query1 = "SELECT * FROM Therapy_Hour WHERE therapy=?";
		String query2 = "SELECT * FROM Therapy_Day WHERE therapy=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userID);
			
			ResultSet result= pstmt.executeQuery();
			result.next();
			Therapy therapy = new Therapy(result.getInt(1), result.getString(3),
					result.getString(4), result.getDate(6));
			
			Date startDate = result.getDate(5);
			if (!result.wasNull()) {
				therapy.setStartTime(startDate);;
			}
			
			String type = result.getString(7);
			if (!result.wasNull()) {
				therapy.setType(TherapyType.valueOf(type));
			}
			
			int interval = result.getInt(13);
			if (!result.wasNull()) {
				therapy.setIntervalDays(interval);
		    }
			
			if(all){
				PreparedStatement pstmt1 = conn.prepareStatement(query1);
				pstmt1.setInt(1, therapy.getId());
				
				ResultSet result1= pstmt1.executeQuery();
				while(result1.next()){
					therapy.setSingleHour(result1.getString(3));
				}
				
				result1.close();
				pstmt1.close();
				
				if(therapy.getType() == TherapyType.SOME_DAY){
					PreparedStatement pstmt2 = conn.prepareStatement(query2);
					pstmt2.setInt(1, therapy.getId());
					
					ResultSet result2= pstmt2.executeQuery();
					while(result2.next()){
						therapy.setSingleDay(result2.getString(3));
					}
					result2.close();
					pstmt2.close();
				}
			}
			result.close();
			pstmt.close();
			
			return therapy;
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
	
	
	/**
	 * Aggiorna il nome di una terapia
	 */
	public void updateTherapyName(int therapyID, String name){
		Connection conn = TriageBotConnection.getConn();
		String query = "UPDATE Therapy SET name=?, last_update=? WHERE id=?";
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, name);
			pstmt.setLong(2, System.currentTimeMillis());
			pstmt.setInt(3, therapyID);
			
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
	
	/**
	 * Aggiorna il dosaggio della terapia
	 */
	public void updateTherapyDosage(int therapyID, String dosage){
		Connection conn = TriageBotConnection.getConn();
		String query = "UPDATE Therapy SET dosage=?, last_update=? WHERE id=?";
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, dosage);
			pstmt.setLong(2, System.currentTimeMillis());
			pstmt.setLong(3, therapyID);
			
			
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
	
	/**
	 * Aggiorna il tipo di una terapia
	 */
	public void updateTherapyType(int therapyID, TherapyType type){
		Connection conn = TriageBotConnection.getConn();
		String query = "UPDATE Therapy SET type=?, last_update=? WHERE id=?";
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, type.toString());
			pstmt.setLong(2, System.currentTimeMillis());
			pstmt.setLong(3, therapyID);
			
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
	
	public void updateTherapyIntervalDays(int therapyID, int interval){
		Connection conn = TriageBotConnection.getConn();
		String query = "UPDATE Therapy SET interval_days=?, last_update=? WHERE id=?";
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setLong(1, interval);
			pstmt.setLong(2, System.currentTimeMillis());
			pstmt.setLong(3, therapyID);
			
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
	
	
	
	
	public void removeLastTherapyDays(int therapyID){
		Connection conn = TriageBotConnection.getConn();
		String query = "DELETE FROM Therapy_Day WHERE therapy=?";
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, therapyID);
			
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
	
	/**
	 * Aggiunge un nuovo giorno alla terapia.
	 * TODO fare l'update del campo 'last_update' di therapy
	 */
	public void addTherapyDate(int therapyID, String weekDay){
		Connection conn = TriageBotConnection.getConn();
		String query = "INSERT INTO Therapy_Day(therapy, day) VALUES(?,?)";
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, therapyID);
			pstmt.setString(2, weekDay);
			
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
	
	/**
	 * Aggiunge un nuovo giorno della settinama ora nella terapia.
	 * TODO fare l'update del campo 'last_update' di therapy
	 */
	public void addTherapyTime(int therapyID, String time){
		Connection conn = TriageBotConnection.getConn();
		String query = "INSERT INTO Therapy_Hour(therapy, hour) VALUES(?,?)";
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, therapyID);
			pstmt.setString(2, time);
			
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
	
	/**
	 * Aggiorna la data di fine terapia
	 */
	public void updateTherapyStartDate(int therapyID, Date date){
		Connection conn = TriageBotConnection.getConn();
		String query = "UPDATE Therapy SET start_date=?, last_update=?, done=1, visible=1 WHERE id=?";
		java.sql.Date dateSql = new java.sql.Date(date.getTime());
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setDate(1, dateSql);
			pstmt.setLong(2, System.currentTimeMillis());
			pstmt.setInt(3, therapyID);
			
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
	
	/**
	 * Aggiorna la data di fine terapia
	 */
	public void updateTherapyEndDate(int therapyID, Date date){
		Connection conn = TriageBotConnection.getConn();
		String query = "UPDATE Therapy SET end_date=?, last_update=?, done=1, visible=1 WHERE id=?";
		java.sql.Date dateSql = new java.sql.Date(date.getTime());
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setDate(1, dateSql);
			pstmt.setLong(2, System.currentTimeMillis());
			pstmt.setInt(3, therapyID);
			
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
	
	/**
	 * Termina la creazione di una terapia. Setta 'done' e 'visible' a true.
	 */
	public void endTherapyNoEndDate(int therapyID){
		Connection conn = TriageBotConnection.getConn();
		String query = "UPDATE Therapy SET end_date=NULL, last_update=?, done=1, visible=1 WHERE id=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setLong(1, System.currentTimeMillis());
			pstmt.setInt(2, therapyID);
			
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
	
	/**
	 * Rimuove una terapia. Eliminazione logica. Setta visible a false.
	 */
	public void deleteTherapy(int therapyID){
		Connection conn = TriageBotConnection.getConn();
		String query = "UPDATE Therapy SET visible=0 WHERE id=?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, therapyID);
			
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
	
	/**
	 * Aggiunge il nome del medicinale associato a una terapia
	 * @param thID
	 * @param drugName
	 */

	public void updateTherapyDrugName(int thID, String drugName) {
		Connection conn = TriageBotConnection.getConn();
		String query = "UPDATE Therapy SET drug_name=?, last_update=? WHERE id=?";
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, drugName);
			pstmt.setLong(2, System.currentTimeMillis());
			pstmt.setLong(3, thID);
			
			
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
