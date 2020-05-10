package com.triage.rest.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.triage.rest.enummodels.RangeLimit;
import com.triage.rest.enummodels.Status;
import com.triage.rest.models.users.*;
import com.triage.rest.models.messages.OCRExam;
import com.triage.rest.models.messages.OCRText;
import com.triage.utils.DateComparator;
import com.triage.utils.NLPUtils;

import javax.xml.transform.Result;

public class TrackingDao {

	public TrackingDao() {
	}


	//REMINDERS
	/**
	 * legge l'ultimo reminder visitato dall'utente
	 */
	public ExamReminder getLastVisitedReminder(int userid){
		String query="SELECT id,exam,date,hour FROM Tracking_examreminders WHERE userid=? ORDER BY last_visited DESC LIMIT 1";
		Connection conn = TriageBotConnection.getConn();
		ExamReminder examReminder=null;
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userid);
			ResultSet res = pstmt.executeQuery();
			if(res.next()){
				examReminder=new ExamReminder(res.getInt(1),res.getString(2),res.getDate(3),res.getString(4));
				return examReminder;
			}
			pstmt.close();
			res.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return examReminder;

	}
	/**
	 * Delete specific reminder
	 */
	private void deleteReminder(int reminderid){
		String query1="DELETE FROM Tracking_examreminders WHERE id=?";
		Connection conn = TriageBotConnection.getConn();
		ExamReminder examReminder=null;
		try {
			PreparedStatement pstmt = conn.prepareStatement(query1);
			pstmt.setInt(1, reminderid);
			pstmt.executeUpdate();
			pstmt.close();
			pstmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Delete last visited reminder
	 */
	public ExamReminder deleteLastVisitedReminder(int userid){
		String query="SELECT id,exam,date,hour FROM Tracking_examreminders WHERE userid=? ORDER BY last_visited DESC LIMIT 1 ";
		String query1="DELETE FROM Tracking_examreminders WHERE id=?";
		Connection conn = TriageBotConnection.getConn();
		ExamReminder examReminder=null;
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userid);
			ResultSet res = pstmt.executeQuery();
			if(res.next()){
				examReminder=new ExamReminder(res.getInt(1),res.getString(2),res.getDate(3),res.getString(4));
				PreparedStatement pstmt1 = conn.prepareStatement(query1);
				pstmt1.setInt(1, examReminder.getId());
				pstmt1.executeUpdate();
				pstmt1.close();
				return examReminder;
			}
			pstmt.close();
			res.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return examReminder;
	}
	/** aggiorno data e ora*/
	public void updateReminder(int userid,Date date,String hour){
		//String query="SELECT id,exam,date,hour FROM Tracking_examreminders WHERE userid=? ORDER BY last_visited DESC LIMIT 1 ";
		String query="UPDATE Tracking_examreminders SET date=?, hour=? WHERE id=?";
		Connection conn = TriageBotConnection.getConn();
		ExamReminder examReminder=null;
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, examReminder.getId());
			pstmt.setString(2, hour);
			pstmt.executeUpdate();
			pstmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Aggiorno l'ora dell'ultimo reminder visitato
	 */
	public ExamReminder updateLastVisitedReminderHour(int userid,String hour){
		//String query="SELECT id,exam,date,hour FROM Tracking_examreminders WHERE userid=? ORDER BY last_visited DESC LIMIT 1 ";
		String query="UPDATE Tracking_examreminders SET last_visited=?, hour=? WHERE id=?";
		Connection conn = TriageBotConnection.getConn();
		ExamReminder examReminder=null;
		try {
			examReminder = getLastVisitedReminder(userid);
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setLong(1, System.currentTimeMillis());
			pstmt.setString(2, hour);
			pstmt.setInt(3, examReminder.getId());
			pstmt.executeUpdate();
			pstmt.close();
			return examReminder;

		}
		catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return examReminder;
	}
	/**
	 * Aggiorno la data dell'ultimo reminder visitato
	 */
	public ExamReminder updateLastVisitedReminderDate(int userid,Date date){
	//	String query="SELECT id,exam,date,hour FROM Tracking_examreminders WHERE userid=? ORDER BY last_visited DESC LIMIT 1 ";
		String query="UPDATE Tracking_examreminders SET last_visited=?, date=? WHERE id=?";
		ExamReminder examReminder=null;
		Connection conn = TriageBotConnection.getConn();
		java.sql.Date dateSql = new java.sql.Date(date.getTime());
		try {
			examReminder =getLastVisitedReminder(userid);
			PreparedStatement pstmt= conn.prepareStatement(query);
			pstmt.setLong(1, System.currentTimeMillis());
			pstmt.setDate(2, dateSql);
			pstmt.setInt(3, examReminder.getId());
			pstmt.executeUpdate();
			pstmt.close();
			return examReminder;

		}
		catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return examReminder;
	}
	/**
	 * Aggiorno l'ultimo reminder visitato
	 */
	private void updateLastVisitedReminder(int userid,String examname){
		String query="UPDATE Tracking_examreminders SET last_visited=? WHERE userid=? AND exam=?";
		Connection conn = TriageBotConnection.getConn();
		try{
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setLong(1, System.currentTimeMillis());
			pstmt.setInt(2,userid);
			pstmt.setString(3,examname);
			pstmt.executeUpdate();
			pstmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * recupera i dettagli di un reminder
	 */
	public ExamReminder getReminderDetails(int userid,String examname){
		this.updateLastVisitedReminder(userid,examname);
		String query="SELECT date,hour FROM Tracking_examreminders WHERE userid=? AND exam=?";
		Connection conn = TriageBotConnection.getConn();
		ExamReminder examReminder=null;
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1,userid);
			pstmt.setString(2,examname);
			ResultSet result = pstmt.executeQuery();
			if(result.next())
				 examReminder= new ExamReminder(examname,result.getDate(1), result.getString(2));
			result.close();
			pstmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return examReminder;
	}
	/**
	 * Visualizza tutti i reminders per gli esami
	 */
	public ArrayList<ExamReminder> seeAllExamsReminders(int userid){
		ArrayList<ExamReminder> examsreminders= new ArrayList<>();
		String query="SELECT * FROM  Tracking_examreminders WHERE userid=?";
		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1,userid);
			ResultSet result = pstmt.executeQuery();
			while (result.next()){
				ExamReminder examreminder= new ExamReminder(result.getInt(1),result.getInt(2),
						result.getString(3),result.getDate(4),result.getString(5));
				examsreminders.add(examreminder);
			}
			result.close();
			pstmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return examsreminders;
	}
	private boolean repeatedActiveReminderName(int userid,String name){
		String query= "SELECT id,date FROM Tracking_examreminders WHERE userid=? AND exam=?";
		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userid);
			pstmt.setString(2, name);
			ResultSet res = pstmt.executeQuery();
			if(res.next()){
				Date date=res.getDate(2);
				if(date.before(new Date())) {
					//cancella la notifica già scattata e inserisci quella nuova
					//in quartz è già elminato perchè è stato triggerato.. quindi tranquillo zio :D
					deleteReminder(res.getInt(1));
					return false;
				}
				else
					return true;
			}
			res.close();
			pstmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	/**
	 * aggiunge un reminder per l'utente
	 * @param reminder
	 */
	public boolean addReminder(ExamReminder reminder){
		String query= "INSERT INTO Tracking_examreminders(userid,exam,last_visited) VALUES (?,?,?)";
		String query1= "INSERT INTO Tracking_examreminders(userid,exam,date,hour) VALUES (?,?,?,?)";
		Connection conn = TriageBotConnection.getConn();
		try {
			if (!repeatedActiveReminderName(reminder.getUserid(), reminder.getExam())) {
				if (reminder.getHour() != null && reminder.getDate() != null) {
					java.sql.Date dateSql = new java.sql.Date(reminder.getDate().getTime());
					PreparedStatement pstmt1 = conn.prepareStatement(query1);
					pstmt1.setInt(1, reminder.getUserid());
					pstmt1.setString(2, reminder.getExam());
					pstmt1.setDate(3, dateSql);
					pstmt1.setString(4, reminder.getHour());
					pstmt1.executeUpdate();
					pstmt1.close();
					return true;
				} else {
					PreparedStatement pstmt = conn.prepareStatement(query);
					pstmt.setInt(1, reminder.getUserid());
					pstmt.setString(2, reminder.getExam());
					pstmt.setLong(3, System.currentTimeMillis());
					pstmt.executeUpdate();
					pstmt.close();
					return true;
				}
			}
			else{

			}
		}

		catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	//END REMINDERS


	/**
	 * Aggiorna lo stato dell'immagine associata al tracking
	 */
	public void updateTrackingImageStatus(int imageid,Status s){
		String query="UPDATE Tracking_Images SET status=? WHERE id=?";
		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt= conn.prepareStatement(query);
			pstmt.setString(1, s.toString());
			pstmt.setInt(2, imageid);
			pstmt.executeUpdate();
			pstmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * recupero i monitoraggi per cui l'esame dato in input è risultato outofrange
	 */
	public List<Tracking> getTrackingsByExamOutOfRange(Exam exam,int userid){
		List<Tracking> trackings = new ArrayList<Tracking>();
		String query = "SELECT t.name, ti.result "
				+ "FROM Tracking t JOIN Tracking_ExamsOCR ti ON t.id=ti.tracking_id "
				+ "WHERE  userid=? AND ti.name=? AND t.done=1 AND visible=1 AND ti.outofrange=? AND min=? AND max=? AND unit=? AND ti.active=1  AND ti.done=1";
		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userid);
			pstmt.setString(2, exam.getName());
			pstmt.setString(3, exam.getOutofrange().toString());
			pstmt.setDouble(4, exam.getMin());
			pstmt.setDouble(5, exam.getMax());
			pstmt.setString(6, exam.getUnit());

			ResultSet result = pstmt.executeQuery();
			while(result.next()) {
				trackings.add(new Tracking(result.getString(1),result.getDouble(2)));
			}
			result.close();
			pstmt.close();
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return trackings;
	}
	/**
	 * Aggiorna il nome di un tracking
	 */
	public void updateTrackingDate(int trackingID, Date date) {
		String query = "UPDATE Tracking SET date=?, last_update=? WHERE id=?";

		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setDate(1,new java.sql.Date(date.getTime()));
			pstmt.setLong(2, System.currentTimeMillis());
			pstmt.setInt(3, trackingID);

			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * recupero le date in cui è stato effettuato un certo esame
	 */
	public Set<Date> getExamDates(Exam exam,int userid){
		Set<Date> dates = new TreeSet<Date>(new DateComparator());
		String query = "SELECT t.date "
				+ "FROM Tracking t JOIN Tracking_ExamsOCR ti ON t.id=ti.tracking_id "
				+ "WHERE userid=? AND ti.name=? AND t.done=1 AND visible=1 AND min=? AND max=? AND unit=? AND ti.active=1 AND ti.done=1";
		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userid);
			pstmt.setString(2, exam.getName());
			pstmt.setDouble(3, exam.getMin());
			pstmt.setDouble(4, exam.getMax());
			pstmt.setString(5, exam.getUnit());

			ResultSet result = pstmt.executeQuery();
			while(result.next()) {
				dates.add(result.getDate(1));
			}
			result.close();
			pstmt.close();
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return dates;
	}

	/**
	 * Aggiunge un nuovo esame
	 */

	public void addNewExam(int userid,String name){
		String query= "INSERT INTO Tracking_ExamsOCR (image_id,tracking_id,name,last_visited) VALUES (?,?,?,?)";
		Connection conn = TriageBotConnection.getConn();
		int image_id=getLastVisitedTrackingImage(userid).getId();
		int tracking_id=getTrackingByImageID(userid,image_id).getId();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1,image_id);
			pstmt.setInt(2,tracking_id);
			pstmt.setString(3,name);
			pstmt.setLong(4,System.currentTimeMillis());
			pstmt.executeUpdate();
			pstmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Recupera gli esami presenti nella tabella Tracking_ExamsOCR
	 */
	public Set<String> getExams(){
		Set<String> exams= new HashSet<String>();
		String query = "SELECT name FROM Tracking_ExamsOCR";
		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			ResultSet result = pstmt.executeQuery();
			while (result.next()) {
				exams.add(result.getString(1));
			}
			result.close();
			pstmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		 return exams;
	}

	/**
	 * Recupera gli esami presenti nella tabella Tracking_ExamsOCR
	 */
	public Set<String> getExamsByUser(int userid){
		Set<String> exams= new HashSet<String>();
		String query = "SELECT ti.name FROM Tracking t JOIN Tracking_ExamsOCR ti ON t.id=ti.tracking_id WHERE visible=1 AND t.done=1 AND userid=? AND ti.active=1  AND ti.done=1";
		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1,userid);
			ResultSet result = pstmt.executeQuery();
			while (result.next()) {
				exams.add(result.getString(1));
			}
			result.close();
			pstmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return exams;
	}
	/**
	 * Aggiorna la data dell'ultimo Tracking inserito
	 */
	public void updateLastTrackingDate(int userID,Date date){
		int lastTrackingId= getLastCreatedTrackingID(userID);
		String query = "UPDATE Tracking SET date=? WHERE id=?";
		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setDate(1, new java.sql.Date(date.getTime()));
			pstmt.setInt(2, lastTrackingId);
			pstmt.executeUpdate();
			pstmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Controlla se esiste un esame con stesso nome unità di misura e intervallo per un certo tracking
	 */
	private int repeatedExamForTracking(Exam exam){
		String query="SELECT ti.id FROM Tracking t JOIN Tracking_ExamsOCR ti ON t.id=ti.tracking_id WHERE ti.name=? AND min=? AND max=? AND unit=? AND tracking_id=? AND t.visible=1 AND t.done=1 AND ti.active=1 AND ti.done=1";
		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, exam.getName());
			pstmt.setDouble(2, exam.getMin());
			pstmt.setDouble(3, exam.getMax());
			pstmt.setString(4, exam.getUnit());
			pstmt.setInt(5,exam.getTracking_id());
			ResultSet result = pstmt.executeQuery();
			if(result.next() && exam.getId()==-1) {
				return result.getInt(1);
			}
			else if(result.next() && result.getInt(1)!=exam.getId()){
				return result.getInt(1);
			}
			result.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			}
		}
		return -1;
	}
	/**
	 * Restituisce tutti i trackings
	 */
	public ArrayList<Tracking> getAllTrackings(int userID) {
		ArrayList<Tracking> trackings = new ArrayList<Tracking>();
		String query = "SELECT * FROM Tracking WHERE userid=? AND done=1 AND visible=1 "
				+ "ORDER BY last_update DESC";
		String query1 = "SELECT * FROM Tracking_Images WHERE tracking=?";

		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userID);

			ResultSet result = pstmt.executeQuery();
			while (result.next()) {
				Tracking tracking = new Tracking(result.getInt(1), result.getString(3),result.getDate(4));

				PreparedStatement pstmt1 = conn.prepareStatement(query1);
				pstmt1.setInt(1, tracking.getId());

				ResultSet result1 = pstmt1.executeQuery();
				while (result1.next()) {
					String status = result1.getString(4);
					String text = getPlainTextByImageLink(result1.getInt(1));
					Tracking_image trimg= new Tracking_image(result1.getInt(1),result1.getString(3),Status.valueOf(status.toLowerCase()),text);
					tracking.setSingleImage(trimg);
						//	tracking.setSingleImage(result1.getString(3));
				}

				trackings.add(tracking);

				result1.close();
				pstmt1.close();
			}
			result.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return trackings;
	}
	/**
	 * Restituisce un monitoraggio selezionadola per nome
	 */
	public Tracking getTracking(int userID, String trackingName) {
		ArrayList<Tracking> trackings = getAllTrackings(userID);
		String query = "UPDATE Tracking SET last_visited=? WHERE id=?";

		Connection conn = TriageBotConnection.getConn();
		for (Tracking tracking : trackings) {
			if (trackingName.equalsIgnoreCase(tracking.getName())) { //TODO fuzzy search
				try {
					PreparedStatement pstmt = conn.prepareStatement(query);
					pstmt.setLong(1, System.currentTimeMillis());
					pstmt.setInt(2, tracking.getId());

					pstmt.executeUpdate();
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				return tracking;
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * Restituisce un monitoraggio selezionadolo per l'id di un immagine
	 */
	public Tracking getTrackingByImageID(int userID, int imageID) {
		//Dato l'id di un immagine preleva l'id de
		String query = "SELECT * "
				+ "FROM Tracking t JOIN Tracking_Images ti ON t.id=ti.tracking "
				+ "WHERE ti.id=? AND userid=? AND t.done=1 AND visible=1";
		String query1 = "SELECT * FROM Tracking_Images WHERE tracking=?";

		Connection conn = TriageBotConnection.getConn();
		Tracking tracking = null;
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, imageID);
			pstmt.setInt(2, userID);

			ResultSet result = pstmt.executeQuery();
			while (result.next()) {
				tracking = new Tracking(result.getInt(1), result.getString(3),result.getDate(4));
				break;
			}
			result.close();
			pstmt.close();
			if (tracking != null) {
				PreparedStatement pstmt1 = conn.prepareStatement(query1);
				pstmt1.setInt(1, tracking.getId());

				ResultSet result1 = pstmt1.executeQuery();
				while (result1.next()) {
					String status = result1.getString(4);
					String text = getPlainTextByImageLink(result1.getInt(1));
					Tracking_image trimg= new Tracking_image(result1.getInt(1),result1.getString(3),Status.valueOf(status.toLowerCase()),text);
					tracking.setSingleImage(trimg);
				}
				result1.close();
				pstmt1.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return tracking;
	}
	/**
	 * Restituisce il numero di tracking (attive) con quel nome
	 */
	public boolean repeatedTrackingName(int userID, String name) {
		Connection conn = TriageBotConnection.getConn();
		String query = "SELECT COUNT(*) FROM Tracking WHERE "
				+ "userid=? AND name=? AND done=1 AND visible=1";

		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userID);
			pstmt.setString(2, name);

			ResultSet result = pstmt.executeQuery();
			result.next();
			int numRes = result.getInt(1);

			result.close();
			pstmt.close();
			if (numRes > 0) {
				return true;
			} else {
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return false;
	}
	/**
	 * Aggiunge un nuovo monitoraggio settando solo il nome
	 */
	public void addNewTracking(int userID, String name) {
		String query = "INSERT INTO Tracking(userid, name,last_update, last_visited) "
				+ "VALUES(?,?,?,?)";

		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userID);
			pstmt.setString(2, name);
		//	pstmt.setString(3,date);
			pstmt.setLong(3, System.currentTimeMillis());
			pstmt.setLong(4, System.currentTimeMillis());

			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Restituisce l'ID dell'ultimo monitoraggio inserito
	 */
	public int getLastCreatedTrackingID(int userID) {
		return getLastCreatedTracking(userID, false).getId();
	}
	/**
	 * Restituisce l'ultimo monitoraggio inserito
	 */
	public Tracking getLastCreatedTracking(int userID, boolean all) {
		String query = "SELECT * FROM Tracking WHERE userid=? ORDER BY id DESC LIMIT 1";
		String query1 = "SELECT * FROM Tracking_Images WHERE tracking=?";

		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userID);

			ResultSet result = pstmt.executeQuery();
			result.next();
			Tracking tracking = new Tracking(result.getInt(1), result.getString(3),result.getDate(4));

			if (all) {
				PreparedStatement pstmt1 = conn.prepareStatement(query1);
				pstmt1.setInt(1, tracking.getId());

				ResultSet result1 = pstmt1.executeQuery();
				while (result1.next()) {
					String status = result1.getString(4);
					String text = getPlainTextByImageLink(result1.getInt(1));
					Tracking_image trimg= new Tracking_image(result1.getInt(1),result1.getString(3),Status.valueOf(status.toLowerCase()),text);
					tracking.setSingleImage(trimg);
				}

				result1.close();
				pstmt1.close();
			}
			result.close();
			pstmt.close();

			return tracking;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	/**
	 * aggiorna l'ultima volta di visita della imamgine
	 */
	public void updateLastVisitedImage(int imageID){
		String query = "UPDATE Tracking_Images SET last_visited=? WHERE id=?";
		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setLong(1,System.currentTimeMillis() );
			pstmt.setInt(2, imageID);
			pstmt.executeUpdate();
			pstmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Restituisce l'ID dell'ultima immagine visitata dall'user
	 */
	public Tracking_image getLastVisitedTrackingImage(int userID) {
		String query = "SELECT ti.id,ti.image_link, ti.status FROM Tracking t JOIN Tracking_Images ti WHERE t.userid=? ORDER BY ti.last_visited DESC LIMIT 1";
		Connection conn = TriageBotConnection.getConn();
		Tracking_image trimage= null;
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userID);
			ResultSet result = pstmt.executeQuery();
			if(result.next()){
				String text = getPlainTextByImageLink(result.getInt(1));
				trimage=new Tracking_image(result.getInt(1),result.getString(2),Status.valueOf(result.getString(3)),text);
			}
			return trimage;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return trimage;
	}
	/**
	 * Restituisce l'ID dell'ultimo tracking visitato
	 */
	public int getLastVisitedTrackingID(int userID) {
		return getLastVisitedTracking(userID, false).getId();
	}
	/**
	 * Restituisce l'ultimo monitoraggio visitato
	 */
	public Tracking getLastVisitedTracking(int userID, boolean all) {
		String query = "SELECT * FROM Tracking WHERE userid=? ORDER BY last_visited DESC LIMIT 1";
		String query1 = "SELECT * FROM Tracking_Images WHERE tracking=?";

		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userID);

			ResultSet result = pstmt.executeQuery();
			result.next();
			Tracking tracking = new Tracking(result.getInt(1), result.getString(3),result.getDate(4));

			if (all) {
				PreparedStatement pstmt1 = conn.prepareStatement(query1);
				pstmt1.setInt(1, tracking.getId());

				ResultSet result1 = pstmt1.executeQuery();
				while (result1.next()) {
					String status = result1.getString(4);
					String text = getPlainTextByImageLink(result1.getInt(1));
					Tracking_image trimg= new Tracking_image(result1.getInt(1),result1.getString(3),Status.valueOf(status.toLowerCase()),text);
					tracking.setSingleImage(trimg);
				}
				result1.close();
				pstmt1.close();
			}
			result.close();
			pstmt.close();
			return tracking;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	/**
	 * Aggiunge una nuova immagine al monitoraggio.
	 */
	public int addTrackingImage(int trackingID, String photoID) {
		String query = "INSERT INTO Tracking_Images(tracking, image_link,last_visited) "
				+ "VALUES(?,?,?)";
		String query1 = "UPDATE Tracking SET done=1, visible=1, last_update=? WHERE id=?";
		String query2 = "SELECT id FROM Tracking_Images "
				+ "WHERE tracking=? AND image_link=? "
				+ "ORDER BY id DESC";

		Connection conn = TriageBotConnection.getConn();
		try {
			//Query
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, trackingID);
			pstmt.setString(2, photoID);
			pstmt.setLong(3, System.currentTimeMillis());
			pstmt.executeUpdate();
			pstmt.close();

			//Query 1
			PreparedStatement pstmt1 = conn.prepareStatement(query1);
			pstmt1.setLong(1, System.currentTimeMillis());
			pstmt1.setInt(2, trackingID);

			pstmt1.executeUpdate();
			pstmt1.close();

			//Query 2
			PreparedStatement pstmt2 = conn.prepareStatement(query2);
			pstmt2.setInt(1, trackingID);
			pstmt2.setString(2, photoID);

			ResultSet result = pstmt2.executeQuery();
			result.next();
			return result.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}
	/**
	 * Restituisce l'ultimo monitoraggio visitato
	 */
	public String getImageByImageId(int userID, int imageID) {
		String query = "SELECT image_link FROM Tracking t JOIN Tracking_Images ti "
				+ "ON t.id=ti.tracking WHERE userid=? AND ti.id=?";

		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userID);
			pstmt.setInt(2, imageID);

			ResultSet result = pstmt.executeQuery();
			result.next();
			return result.getString(1);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return null;
	}
	/**
	 * Aggiorna il nome di un tracking
	 */
	public void updateTrackingName(int trackingID, String name) {

		String query = "UPDATE Tracking SET name=?, last_update=? WHERE id=?";

		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, name);
			pstmt.setLong(2, System.currentTimeMillis());
			pstmt.setInt(3, trackingID);

			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Rimuove un monitoraggio. Eliminazione logica. Setta visible a false.
	 */
	public void deleteTracking(int trackingID) {
		String query = "UPDATE Tracking SET visible=0 WHERE id=?";

		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, trackingID);

			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	//END TRACKING

	//ESAMI ESTRATTI DAI REFERTI
	public boolean updateLastVisitedExamByImage(Exam exam){
		String query = "UPDATE Tracking_ExamsOCR SET last_visited=? WHERE id=?";
		Connection conn = TriageBotConnection.getConn();
		try {
				PreparedStatement pstmt = conn.prepareStatement(query);
				pstmt.setLong(1, System.currentTimeMillis());
				pstmt.setInt(2, exam.getId());
				pstmt.executeUpdate();
				pstmt.close();
				return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	/**
	 * Aggiorno il nome dell'esame
	 */
	public boolean updateLastViewedExamName(int userid,String newname){
		Exam exam = getLastVisitedExamByLastVisitedImage(userid);
		String query = "UPDATE Tracking_ExamsOCR SET name=?, last_visited=? WHERE id=?";

		Connection conn = TriageBotConnection.getConn();
		try {

			if (repeatedExamForTracking(exam)<0) {
				PreparedStatement pstmt = conn.prepareStatement(query);
				pstmt.setString(1, newname);
				pstmt.setLong(2, System.currentTimeMillis());
				pstmt.setInt(3, exam.getId());
				pstmt.executeUpdate();
				pstmt.close();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	/**
	 * Aggiorno l'unità dell'esame
	 */
	public boolean updateLastViewedExamUnit(int userid,String newunit){
		//int exam_id = getLastVisitedExamIDByLastVisitedImage(userid);
		Exam exam = getLastVisitedExamByLastVisitedImage(userid);
		String query = "UPDATE Tracking_ExamsOCR SET unit=?, last_visited=? WHERE id=?";
		Connection conn = TriageBotConnection.getConn();
		try {
			if (repeatedExamForTracking(exam)<0) {
				PreparedStatement pstmt = conn.prepareStatement(query);
				pstmt.setString(1, newunit);
				pstmt.setLong(2, System.currentTimeMillis());
				pstmt.setInt(3, exam.getId());
				pstmt.executeUpdate();
				pstmt.close();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	/**
	 * Aggiorno il risultato dell'esame
	 */
	public boolean updateLastViewedExamResult(int userid,double newresult){
		Exam exam = getLastVisitedExamByLastVisitedImage(userid);
		String query = "UPDATE Tracking_ExamsOCR SET result=?, last_visited=?,outofrange=? WHERE id=?";
		String outofrange= checkRange(exam.getMin(),exam.getMax(),newresult);
		Connection conn = TriageBotConnection.getConn();
		try {
			if (outofrange!=null && repeatedExamForTracking(exam)<0) {
				PreparedStatement pstmt = conn.prepareStatement(query);
				pstmt.setDouble(1, newresult);
				pstmt.setLong(2, System.currentTimeMillis());
				pstmt.setString(3, outofrange);
				pstmt.setInt(4, exam.getId());
				pstmt.executeUpdate();
				pstmt.close();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	/**
	 * Aggiorno il minimo dell'ultimo esame visitato
	 */
	public boolean updateLastViewedExamMin(int userid, double newmin){
		Exam exam = getLastVisitedExamByLastVisitedImage(userid);
		String query = "UPDATE Tracking_ExamsOCR SET min=?, last_visited=?, outofrange=? WHERE id=?";
		String outofrange= checkRange(newmin,exam.getMax(),exam.getResult());
		Connection conn = TriageBotConnection.getConn();
		try {
			if (outofrange!=null && repeatedExamForTracking(exam)<0) {
				PreparedStatement pstmt = conn.prepareStatement(query);
				pstmt.setDouble(1, newmin);
				pstmt.setLong(2, System.currentTimeMillis());
				pstmt.setString(3,outofrange);
				pstmt.setInt(4, exam.getId());
				pstmt.executeUpdate();
				pstmt.close();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	/**
	 * Aggiorno il massimo dell'esame
	 */
	public boolean updateLastViewedExamMax(int userid,double newmax){
		Exam exam = getLastVisitedExamByLastVisitedImage(userid);
		String query = "UPDATE Tracking_ExamsOCR SET max=?, last_visited=?, outofrange=?, done=1 WHERE id=?";
		String outofrange= checkRange(exam.getMin(),newmax,exam.getResult());
		Connection conn = TriageBotConnection.getConn();
		try {
			if (outofrange!=null && repeatedExamForTracking(exam)<0) {
				PreparedStatement pstmt = conn.prepareStatement(query);
				pstmt.setDouble(1, newmax);
				pstmt.setLong(2, System.currentTimeMillis());
				pstmt.setString(3,outofrange);
				pstmt.setInt(4, exam.getId());
				pstmt.executeUpdate();
				pstmt.close();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	//public Exam getLastVisitedExamByUser(int userID) {
	//	String query= "SELECT * FROM Tracking_ExamsOCR WHERE image_id=? ORDER BY last_visited DESC LIMIT 1";

//	}
	/**
	 * Ottengo l'ultimo esame richiesto dall'utente nella sezione visualizza monitoraggi, in base all'ultima immagine visitata (di un tracking)

	 */
	public Exam getLastVisitedExamByLastVisitedImage(int userID){
		//ottieni ultima immagine visitata
		Tracking_image trimage= getLastVisitedTrackingImage(userID);
		//ottieni ultimo esame visitato
		String query= "SELECT * FROM Tracking_ExamsOCR WHERE image_id=? ORDER BY last_visited DESC LIMIT 1";
		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, trimage.getId());
			ResultSet result = pstmt.executeQuery();
			result.next();
			String rl= result.getString(8);
			RangeLimit rlt=null;
			if(rl!=null)
				rlt=RangeLimit.valueOf(result.getString(8));
			Exam exam = new Exam(result.getString(7),result.getDouble(1),
					result.getDouble(4),result.getDouble(5),
					result.getString(6),rlt,
					result.getInt(10),result.getInt(3));
			return exam;
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	//END ESAMI ESTRATTI DAI REFERTI


	//CHART
	/**
	 * Controlla se il risultato è nel range o meno
	 * @param max
	 * @param result
	 * @return
	 */
	private String checkRange(double min,double max,double result) {
		String outofrange = "";
		if (result >= min && result <= max && max!=0.0) {
			outofrange = RangeLimit.inrange.toString();
		} else if (result >= min && max==0.0) {
			outofrange = RangeLimit.inrange.toString();
		} else if (result < min) {
			outofrange = RangeLimit.inferior.toString();
		} else if (result > max && max!=0.0) {
			outofrange = RangeLimit.superior.toString();
		}
		return outofrange;
	}
	/**
	 * Salva l'esame richiesto dall'utente
	 */
	public void setRequiredExam(int userid,String exam){
		String query="Insert into Tracking_chart(userid,exam_required) values (?,?)";
		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userid);
			pstmt.setString(2, exam);
			pstmt.executeUpdate();
			pstmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Salva il periodo iniziale
	 */
	public void setInitialPeriod(int userid,Date initialPeriod){
		String query="SELECT id from Tracking_chart where userid=? ORDER BY id DESC LIMIT 1";
		String query1="UPDATE Tracking_chart SET initial_period=? WHERE id=? ";
		int last_examrequired=0;
		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userid);
			ResultSet result =pstmt.executeQuery();
			if (result.next()){
				last_examrequired=result.getInt(1);
			}
			pstmt.close();
			PreparedStatement pstmt1 = conn.prepareStatement(query1);
			if(initialPeriod!=null)
				pstmt1.setDate(1,new java.sql.Date(initialPeriod.getTime()));
			else
				pstmt1.setDate(1,null);
			pstmt1.setInt(2, last_examrequired);
			pstmt1.executeUpdate();
			pstmt1.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Salva il periodo finale
	 */
	public void setFinalPeriod(int userid,Date finalPeriod){
		String query="SELECT id from Tracking_chart where userid=? ORDER BY id DESC LIMIT 1";
		String query1="UPDATE Tracking_chart SET final_period=? WHERE id=? ";
		int last_examrequired=0;
		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userid);
			ResultSet result =pstmt.executeQuery();
			if (result.next()){
				last_examrequired=result.getInt(1);
			}
			pstmt.close();
			PreparedStatement pstmt1 = conn.prepareStatement(query1);
			if(finalPeriod!=null)
				pstmt1.setDate(1,new java.sql.Date(finalPeriod.getTime()));
			else
				pstmt1.setDate(1,null);
			pstmt1.setInt(2, last_examrequired);
			pstmt1.executeUpdate();
			pstmt1.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * ritrova il periodo iniziale di interess
	 * @return
	 */
	public Date getRequiredInitialPeriod(int userid){
		Date date=null;
		String query="SELECT initial_period from Tracking_chart where userid=? ORDER BY id DESC LIMIT 1";
		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1,userid);
			ResultSet result = pstmt.executeQuery();
			if (result.next()){
				date=result.getDate(1);
			}
			pstmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return date;
	}
	/**
	 * ritrova il periodo finale  di interesse
	 * @return
	 */
	public Date getRequiredFinalPeriod(int userid){
		Date date=null;
		String query="SELECT final_period from Tracking_chart where userid=? ORDER BY id DESC LIMIT 1";
		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1,userid);
			ResultSet result = pstmt.executeQuery();
			if (result.next()){
				date=result.getDate(1);
			}
			pstmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return date;
	}

	public ArrayList<Exam> getExamsByImageId(int imageid){
		String query="SELECT * FROM  Tracking_ExamsOCR WHERE image_id=? AND done=1";
		Connection conn = TriageBotConnection.getConn();
		ArrayList<Exam> exams= new ArrayList<>();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1,imageid);
			ResultSet result = pstmt.executeQuery();
			while (result.next()){
				String rl= result.getString(8);
				RangeLimit rlt=null;
				if(rl!=null)
					rlt=RangeLimit.valueOf(result.getString(8));
				Exam exam  = new Exam(result.getString(7),result.getDouble(1),result.getDouble(4),result.getDouble(5),result.getString(6),rlt,result.getInt(10));
				exams.add(exam);
			}
			updateLastVisitedImage(imageid);
			result.close();
			pstmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return exams;
	}
	/**
	 * recupera l'esame richiesto dall'utente da visualizzare nel grafico
	 */
	public String getRequiredExam(int userid){
		String exam=null;
		String query="SELECT exam_required FROM Tracking_chart WHERE userid=? ORDER BY id DESC LIMIT 1";
		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1,userid);
			ResultSet result = pstmt.executeQuery();
			if (result.next()){
				exam=result.getString(1);
			}
			pstmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return exam;
	}
	/**
	 * Recupera gli esami col nome in input estratti dalle fotografie inviati dall'utente,
	 * Seleziona solo gli esami estratti dai referti di un certo anno, se l'utente ha specificato un anno preciso
	 * @param name nome dell'esame di interesse
	 */
	public HashMap<Integer,ArrayList<Exam>> getExamsByName(int chatid,String name,Date initial_period,Date final_period){
		HashMap<Integer,ArrayList<Exam>> exams_byrange = new HashMap<Integer, ArrayList<Exam>>();
		String query = "SELECT t.date,ti.name,min,max,unit,result,t.name,ti.outofrange "
				+ "FROM Tracking t JOIN Tracking_ExamsOCR ti ON t.id=ti.tracking_id "
				+ "WHERE  userid=? AND ti.name=? AND t.done=1 AND visible=1 AND ti.active=1 AND ti.done=1 AND t.date BETWEEN ? AND ?";
		String query1 = "SELECT t.date,ti.name,min,max,unit,result,t.name,ti.outofrange "
				+ "FROM Tracking t JOIN Tracking_ExamsOCR ti ON t.id=ti.tracking_id "
				+ "WHERE  userid=? AND ti.name=? AND t.done=1 AND ti.active=1 AND ti.done=1 AND visible=1";
		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt=null;
			if(initial_period!=null) {
				pstmt = conn.prepareStatement(query);
				pstmt.setInt(1, chatid);
				pstmt.setString(2, name);
				pstmt.setDate(3, new java.sql.Date(initial_period.getTime()));
				pstmt.setDate(4, new java.sql.Date(final_period.getTime()));
			}
			else{
				//all dates
				pstmt= conn.prepareStatement(query1);
				pstmt.setInt(1, chatid);
				pstmt.setString(2, name);
			}
			ResultSet result = pstmt.executeQuery();
			int i=0;
			if (result.next()){
				ArrayList<Exam> exams = null;
				String rl= result.getString(8);
				RangeLimit rlt=null;
				if(rl!=null)
					rlt=RangeLimit.valueOf(result.getString(8));
				Exam exam= new Exam(result.getString(2),result.getDouble(6),
						result.getDouble(3),result.getDouble(4),result.getString(5),result.getString(7), rlt);
				exam.setDate(result.getDate(1));
				exams=new ArrayList<Exam>();
				exams.add(exam);

				//first range
				exams_byrange.put(i, exams);
				i++;
			}
			while (result.next()) {
				String rl= result.getString(8);
				RangeLimit rlt=null;
				if(rl!=null)
					rlt=RangeLimit.valueOf(result.getString(8));
				Exam exam= new Exam(result.getString(2),result.getDouble(6),
						result.getDouble(3),result.getDouble(4),result.getString(5),result.getString(7),rlt);
				exam.setDate(result.getDate(1));
				//String[] date=exam.getDate().split("/");
				boolean added=false;
				//controlla se il range già esiste ed eventualmente inserisce l'esame in quello corrispondente
				for(int s: exams_byrange.keySet()){
					if(exams_byrange.get(s).get(0).getMin()==exam.getMin()
							&& exams_byrange.get(s).get(0).getMax()==exam.getMax()
							&& exams_byrange.get(s).get(0).getUnit().equals(exam.getUnit()) ){
						exams_byrange.get(s).add(exam);
						added=true;
						break;
					}
				}
				//crea un nuovo range
				if (!added){
					ArrayList<Exam> new_range_exam= null;
					new_range_exam=new ArrayList<Exam>();
					new_range_exam.add(exam);
					exams_byrange.put(i,new_range_exam);
					i++;
				}
			}
			result.close();
			pstmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return exams_byrange;
	}
	//END CHART

	//SAVING RESULTS
	/**
	 * Salva i risultati ottenuti dalla fase di OCR. Restituisce l'ID dell'utente a cui
	 * l'immagine appartiene
	 */
	public void saveOCRResult(OCRText ocr) {
		String query = "INSERT INTO Tracking_ImagesOCR(image_link, image_text) VALUES (?, ?)";

		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, ocr.getPhotoID());
			pstmt.setString(2, ocr.getText());

			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Salva i risultati ottenuti dalla fase di OCR. Restituisce l'ID dell'utente a cui
	 * l'immagine appartiene
	 */
	public int saveOCRExamResult(OCRExam ocrexams) {
		String query="INSERT INTO Tracking_ExamsOCR(result,image_id,tracking_id,min,max,name,unit,outofrange,done) VALUES (?,?,?,?,?,?,?,?,1)";
		Connection conn = TriageBotConnection.getConn();
		try {
			for(Exam x: ocrexams.getExams()){
				x.setId(-1);
				x.setTracking_id(ocrexams.getTrackingID());
				int id=repeatedExamForTracking(x);
				if(id>=0) {
					//aggiorna il valore
					updateExamStatus(id);
				}
				PreparedStatement pstmt = conn.prepareStatement(query);
				pstmt.setDouble(1, x.getResult());
				pstmt.setInt(2, ocrexams.getPhotoID());
				pstmt.setInt(3, x.getTracking_id());
				pstmt.setDouble(4, x.getMin());
				pstmt.setDouble(5, x.getMax());
				pstmt.setString(6, x.getName());
				pstmt.setString(7, x.getUnit());
				pstmt.setString(8, x.getOutofrange().toString());
				pstmt.executeUpdate();
				pstmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}
	private void updateExamStatus(int examid){
		String query="UPDATE Tracking_ExamsOCR SET active=0 WHERE id=?";
		Connection conn = TriageBotConnection.getConn();
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, examid);
			pstmt.executeUpdate();
			pstmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Take the plain text extracted
	 * @return
	 */
	public String getPlainTextByImageLink(int imagelink){
		String query="SELECT image_text FROM Tracking_ImagesOCR WHERE image_link=?";
		Connection conn = TriageBotConnection.getConn();
		try{
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, imagelink);
			ResultSet result = pstmt.executeQuery();
			if (result.next()){
				return result.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	//END SAVING RESULTS
}
