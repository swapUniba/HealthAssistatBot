package com.triage.logic.questions;

import java.util.ArrayList;

import com.triage.rest.models.messages.Answer;
import com.triage.rest.models.messages.Question;
import com.triage.rest.models.messages.QuestionTemplate;
import com.triage.rest.models.messages.QuestionText;
import com.vdurmont.emoji.EmojiParser;

public class BaseBotQuestions extends AbstractQuestions{

	public static final String NULL = "NULL";
	public static final String MENU = "MENU";
	
	public static final String USER_DETAIL = "USER_DETAIL";
	public static final String USER_NAME = "USER_NAME";
	public static final String USER_SURNAME = "USER_SURNAME";
	public static final String USER_SEX = "USER_SEX";
	public static final String USER_BIRTH = "USER_BIRTH";
	//public static final String USER_CITY = "USER_CITY";
	public static final String USER_PROVINCE = "USER_PROVINCE";
	public static final String USER_PROVINCE_DISAMBIGUATION = "USER_PROVINCE_DISAMBIGUATION";
	
	public BaseBotQuestions() {}
	
	@Override
	public ArrayList<Question> initializeQuestions() {
		ArrayList<Question> questions = new ArrayList<Question>();
		
		/* Menu */
		Question menu = new QuestionTemplate(MENU, "com/triage/template/baseBot/menu.vm");
		Answer m1 = new Answer("Suggerisci medico " + EmojiParser.parseToUnicode(":male_health_worker:"));
		menu.addSingleAnswer(m1);
		Answer m2 = new Answer("Analisi sintomi " + EmojiParser.parseToUnicode(":hospital:"));
		menu.addSingleAnswer(m2);
		Answer m3 = new Answer("Dizionario medico " + EmojiParser.parseToUnicode(":open_book:"));
		menu.addSingleAnswer(m3);
		Answer m4 = new Answer("Terapie " + EmojiParser.parseToUnicode(":pill:"));
		menu.addSingleAnswer(m4);
		Answer m5 = new Answer("Monitoraggio " + EmojiParser.parseToUnicode(":chart_with_upwards_trend:"));
		menu.addSingleAnswer(m5);
		Answer m7 = new Answer("Profilo utente " + EmojiParser.parseToUnicode(":gear:"));
		menu.addSingleAnswer(m7);
		questions.add(menu);
		
		/* Domande utente */
		Question userName = new QuestionText(USER_NAME, "Digita il tuo nome");
		questions.add(userName);
		
		Question userSurname = new QuestionText(USER_SURNAME, "Digita il tuo cognome");
		questions.add(userSurname);
		
		Question userSer = new QuestionText(USER_SEX, "Qual'è il tuo sesso?");
		Answer us1 = new Answer("Uomo");
		userSer.addSingleAnswer(us1);
		Answer us2 = new Answer("Donna");
		userSer.addSingleAnswer(us2);
		questions.add(userSer);
		
		Question userBirth = new QuestionText(USER_BIRTH, "Digita la tua data di nascita. (Formato GG/MM/AAAA)");
		questions.add(userBirth);
		
		/*Question userCity = new QuestionText(USER_CITY, "Qual'è la tua città di residenza?");
		questions.add(userCity);*/
		
		Question userProvince = new QuestionText(USER_PROVINCE, "Qual'è la tua provincia di residenza?");
		questions.add(userProvince);
		
		Question userProvinceDisambiguation = new QuestionTemplate(USER_PROVINCE_DISAMBIGUATION, "com/triage/template/baseBot/userProvinceDisambiguation.vm");
		Answer upd1 = new Answer("Si");
		userProvinceDisambiguation.addSingleAnswer(upd1);
		Answer upd2 = new Answer("No");
		userProvinceDisambiguation.addSingleAnswer(upd2);
		questions.add(userProvinceDisambiguation);
		
		Question userDetail = new QuestionTemplate(USER_DETAIL, "com/triage/template/baseBot/userDetail.vm");
		Answer ud1 = new Answer("Modifica nome");
		userDetail.addSingleAnswer(ud1);
		Answer ud2 = new Answer("Modifica cognome");
		userDetail.addSingleAnswer(ud2);
		Answer ud3 = new Answer("Modifica sesso");
		userDetail.addSingleAnswer(ud3);
		Answer ud4 = new Answer("Modifica data di nascita");
		userDetail.addSingleAnswer(ud4);
		/*Answer ud5 = new Answer("Modifica città di residenza");
		userDetail.addSingleAnswer(ud5);*/
		Answer ud6 = new Answer("Modifica provincia di residenza");
		userDetail.addSingleAnswer(ud6);
		questions.add(userDetail);
		
		
		return questions;
	}

}
