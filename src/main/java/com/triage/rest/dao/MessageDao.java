package com.triage.rest.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.triage.rest.models.messages.Message;
import com.vdurmont.emoji.EmojiParser;;

public class MessageDao {
	
	public MessageDao(){}
	
	public ArrayList<Message> getAllChats(){
		Connection conn = TriageBotConnection.getConn();
		ArrayList<Message> msgs = new ArrayList<Message>();
		String query = "SELECT * FROM Message";
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			ResultSet result= pstmt.executeQuery();
			
			while(result.next()){
				Message m = new Message(result.getInt(1), result.getInt(2), result.getInt(3),
										result.getBoolean(4), result.getLong(5), 
										result.getString(6));
				msgs.add(m);
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
		
		return msgs;
	}
	
	public void saveNewMessage(Message m){
		Connection conn = TriageBotConnection.getConn();
		String query = "INSERT INTO Message(chatid, messageid, isbot, date, text) "
				+ "VALUES(?,?,?,?,?)";
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, m.getChatId());
			if(m.getMessageId() == -1){
				pstmt.setNull(2, java.sql.Types.INTEGER); //Is a bot message
			}else{
				pstmt.setInt(2, m.getMessageId()); //Is a user message
			}
			pstmt.setBoolean(3, m.isBot());
			pstmt.setLong(4, m.getDateMs());
			pstmt.setString(5, EmojiParser.removeAllEmojis(m.getText()));
			
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
	
	public String getLastBotMessage(int chatid){
		Connection conn = TriageBotConnection.getConn();
		String text = null;
		String query = "SELECT text FROM Message WHERE chatid=? AND isbot=True "
				+ "ORDER BY DATE DESC LIMIT 1";
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, chatid);
			ResultSet result= pstmt.executeQuery();
			
			while(result.next()){
				text = result.getString(1);
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
		
		return text;
	}
	
	public String getLastKUserMessage(int chatid, int index){
		Connection conn = TriageBotConnection.getConn();
		String text = null;
		String query = "SELECT text FROM ("
							+ "SELECT * FROM Message "
							+ "WHERE chatid=? AND isbot=False "
							+ "ORDER BY DATE DESC LIMIT ?) AS m "
					 + "ORDER BY id LIMIT 1";
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, chatid);
			pstmt.setInt(2, index);
			ResultSet result= pstmt.executeQuery();
			
			while(result.next()){
				text = result.getString(1);
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
		
		return text;
	}
}
