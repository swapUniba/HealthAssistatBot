package com.triage.rest;

import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/register")
public class RegisterClientService {
	
	@GET
	/*
	 * Registra un nuovo cliente e restituisce una chiave alfanumerica
	 */
	public String displayRegisterForm(){
		UUID apikey = UUID.randomUUID();
		
		return apikey.toString();
	}
	
	@POST
	public String saveRegisterForm(){
		UUID apikey = UUID.randomUUID();
		
		return apikey.toString();
	}
}
