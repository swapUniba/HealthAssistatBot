package com.triage.logic.managers;

import java.util.Date;

import com.triage.rest.dao.UserDao;
import com.triage.rest.enummodels.ItalianProvince;
import com.triage.rest.enummodels.Sex;
import com.triage.rest.models.users.User;
import com.triage.utils.NLPUtils;

public class BotManager {
	private User user;
	private UserDao udao;

	public BotManager(User user){
		this.user = user;
		this.udao = new UserDao();
	}
	
	public User getUser(){
		return this.udao.getUser(this.user.getId());
	}
	
	public void updateFirstname(String firstname){
		this.udao.updateUserFirstname(this.user.getId(), firstname);
	}
	
	public void updateLastname(String lastname){
		this.udao.updateUserLastname(this.user.getId(), lastname);
	}
	
	public boolean updateSex(String sex){
		Sex sexEnum = Sex.valueOf(sex.toUpperCase());
		if(sexEnum != null){
			this.udao.updateUserSex(this.user.getId(), sexEnum);
			return true;
		}else
			return false;
	}
	
	public boolean updateBirth(String birth){
		Date birthDate = NLPUtils.parseDate(birth);
		
		if(birthDate != null){
			this.udao.updateUserBirth(this.user.getId(), birthDate);
			return true;
		}else
			return false;
	}
	
	public void updateCity(String city){
		this.udao.updateUserCity(this.user.getId(), city);
	}
	
	public String updateProvince(String province){
		String parsedProvince = ItalianProvince.getProvinceExact(province);
		if(parsedProvince != null){
			this.udao.updateUserProvince(this.user.getId(), parsedProvince);
			return parsedProvince;
		}else{
			return null;
		}
	}
	
	public String getFuzzyProvinceResult(String province){
		return ItalianProvince.getProvinceFuzzy(province);
	}
}
