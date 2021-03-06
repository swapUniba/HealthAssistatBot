package com.triage.rest.dao;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class MediciItaliaConnection {
	public static Connection getConn(){
		Context initContext;
		try {
			initContext = new InitialContext();
			Context envContext  = (Context)initContext.lookup("java:/comp/env");
			DataSource ds = (DataSource)envContext.lookup("jdbc/MediciItalia");
			return ds.getConnection();
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
