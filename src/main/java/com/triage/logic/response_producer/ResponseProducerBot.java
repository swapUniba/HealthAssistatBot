package com.triage.logic.response_producer;

import java.util.ArrayList;

import org.apache.velocity.VelocityContext;

import com.triage.logic.managers.BotManager;
import com.triage.logic.questions.BaseBotQuestions;
import com.triage.logic.questions.MedicalDictionaryQuestions;
import com.triage.logic.questions.SuggestDoctorQuestions;
import com.triage.logic.questions.SymptomCheckerQuestions;
import com.triage.logic.questions.TherapyQuestions;
import com.triage.logic.questions.TrackingQuestions;
import com.triage.rest.dao.TherapyDao;
import com.triage.rest.dao.TrackingDao;
import com.triage.rest.models.messages.Answer;
import com.triage.rest.models.messages.Question;
import com.triage.rest.models.messages.QuestionTemplate;
import com.triage.rest.models.messages.Response;
import com.triage.rest.models.messages.RestRequestInput;
import com.triage.rest.models.users.User;
import com.vdurmont.emoji.EmojiParser;

public class ResponseProducerBot extends ResponseProducerAbstract{
	
	BotManager bmanager;
	
	public ResponseProducerBot(RestRequestInput restRequest, User user, Question lastq) {
		super(restRequest, user, lastq);
		this.setQstdao(new BaseBotQuestions());
		this.bmanager = new BotManager(this.getUser());
	}

	@Override
	public Response produceResponse() {
		Question nextq = null;
		String lastQuestionName = this.getLastq().getQuestionName();
		
		if(isMenuCommand(this.getText()) || this.getLastq() == null){
			this.getCqdao().saveCurrentSectionMenu(this.getUser().getId(), "Menu");
			
			nextq = this.getQstdao().getQuestion(BaseBotQuestions.MENU);
			nextq = buildMenuQuestion(nextq, this.getUser().getFirstname(), true);
			this.setLinearButton(false); this.setGridButton(true);
		}else{
			//Bot: visualizzazione menu
			if(lastQuestionName.equals(BaseBotQuestions.MENU)){
				//this.text è una sezione del menu
				if(this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(0).getText())
						|| isSuggestDoctorCommand(this.getText())){
					//text = Suggerisci medici
					this.getCqdao().saveCurrentSectionMenu(this.getUser().getId(), "SuggestDoctor");
					
					SuggestDoctorQuestions sdq = new SuggestDoctorQuestions();
					nextq = sdq.getQuestion(SuggestDoctorQuestions.DESCRIBE_HEALT_STATE);
				}
				if(this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(1).getText())
						|| isSymptomCheckerCommand(this.getText())){
					//text = Analisi sintomi
					this.getCqdao().saveCurrentSectionMenu(this.getUser().getId(), "SymptomChecker");
					
					SymptomCheckerQuestions sdq = new SymptomCheckerQuestions();
					nextq = sdq.getQuestion(SymptomCheckerQuestions.DESCRIBE_SYMPTOM);
				}
				if(this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(2).getText())
						|| isMedicalDictionaryCommand(this.getText())){
					//text = Dizionario medico
					this.getCqdao().saveCurrentSectionMenu(this.getUser().getId(), "Dictionary");
					
					MedicalDictionaryQuestions mdq = new MedicalDictionaryQuestions();
					nextq = mdq.getQuestion(MedicalDictionaryQuestions.SEARCH);
				}
				if(this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(3).getText())
						|| isTherapyCommand(this.getText())){
					//text = Terapie
					this.getCqdao().saveCurrentSectionMenu(this.getUser().getId(), "Therapy");
					
					TherapyQuestions tq = new TherapyQuestions();
					nextq = tq.getQuestion(TherapyQuestions.MENU);
					int numTherapies = countTherapies();
					if(numTherapies < 1){
						ArrayList<Answer> answs = new ArrayList<Answer>();
						answs.add(nextq.getAnswers().get(0));
						nextq.setAnswers(answs);
					}
				}
				if(this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(4).getText())
						|| isTrackingCommand(this.getText())){
					//text = Monitoraggio
					this.getCqdao().saveCurrentSectionMenu(this.getUser().getId(), "Tracking");
					
					TrackingQuestions tq = new TrackingQuestions();
					nextq = tq.getQuestion(TrackingQuestions.MENU);
					int numTrackings = countTrackings();
					if(numTrackings < 1){
						ArrayList<Answer> answs = new ArrayList<Answer>();
						answs.add(nextq.getAnswers().get(0));
						nextq.setAnswers(answs);
					}
				}
				if(this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(5).getText())
						|| isUserProfileCommand(this.getText())){
					//text = Profilo Utente
					this.getCqdao().saveCurrentSectionMenu(this.getUser().getId(), "UserProfile");
					
					nextq = this.getQstdao().getQuestion(BaseBotQuestions.USER_DETAIL);
					nextq = buildUserDetailQuestion(nextq, this.getUser(), true);
				}
				if(nextq == null){
					nextq = this.getQstdao().getQuestion(BaseBotQuestions.MENU);
					nextq = buildMenuQuestion(nextq, null, false);
					nextq.setPreText("Voce del menu non esistente.");
					this.setLinearButton(false); this.setGridButton(true);
				}
			}
			
			/* User Profile*/
			//Bot: dettagli utente...
			if(lastQuestionName.equals(BaseBotQuestions.USER_DETAIL)){
				//this.text è una operazione di modifica dell'utente
				if(this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(0).getText())){
					//text = Modifica nome
					nextq = this.getQstdao().getQuestion(BaseBotQuestions.USER_NAME);
				}
				if(this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(1).getText())){
					//text = Modifica cognome
					nextq = this.getQstdao().getQuestion(BaseBotQuestions.USER_SURNAME);
				}
				if(this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(2).getText())){
					//text = Modifica sesso
					nextq = this.getQstdao().getQuestion(BaseBotQuestions.USER_SEX);
				}
				if(this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(3).getText())){
					//text = Modifica data di nascita
					nextq = this.getQstdao().getQuestion(BaseBotQuestions.USER_BIRTH);
				}
				if(this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(4).getText())){
					//text = Modifica provincia di residenza
					nextq = this.getQstdao().getQuestion(BaseBotQuestions.USER_PROVINCE);
				}
				
				if(nextq == null){
					nextq = this.getQstdao().getQuestion(BaseBotQuestions.USER_DETAIL);
					nextq = buildUserDetailQuestion(nextq, this.getUser(), false);
					nextq.setPreText("Comando non riconosciuto.\n");
				}
			}
			
			//Bot: inserisci il tuo nome
			if(lastQuestionName.equals(BaseBotQuestions.USER_NAME)){
				//this.text è il nuovo nome
				this.bmanager.updateFirstname(this.getText());
				this.setUser(this.bmanager.getUser());
							
				nextq = this.getQstdao().getQuestion(BaseBotQuestions.USER_DETAIL);
				nextq = buildUserDetailQuestion(nextq, this.getUser(), false);
				nextq.setPreText("Nome aggiornato. ");
			}
			
			//Bot: inserisci il tuo cognome
			if(lastQuestionName.equals(BaseBotQuestions.USER_SURNAME)){
				//this.text è il nuovo cognome
				this.bmanager.updateLastname(this.getText());
				this.setUser(this.bmanager.getUser());
				
				nextq = this.getQstdao().getQuestion(BaseBotQuestions.USER_DETAIL);
				nextq = buildUserDetailQuestion(nextq, this.getUser(), false);
				nextq.setPreText("Cognome aggiornato. ");
			}
			
			//Bot: inserisci il tuo sesso
			if(lastQuestionName.equals(BaseBotQuestions.USER_SEX)){
				//this.text è il nuovo sesso
				boolean sexSaved = bmanager.updateSex(this.getText());
				if(sexSaved){
					this.setUser(this.bmanager.getUser());
					
					nextq = this.getQstdao().getQuestion(BaseBotQuestions.USER_DETAIL);
					nextq = buildUserDetailQuestion(nextq, this.getUser(), false);
					nextq.setPreText("Sesso aggiornato. ");
				}else{
					nextq = this.getQstdao().getQuestion(BaseBotQuestions.USER_SEX);
					nextq.setPreText("Non ho capito. ");
				}
			}
			
			//Bot: inserisci la data di nascita
			if(lastQuestionName.equals(BaseBotQuestions.USER_BIRTH)){
				//this.text è la nuova data di nascita
				boolean birthSaved = bmanager.updateBirth(this.getText());
				if(birthSaved){
					this.setUser(this.bmanager.getUser());
					
					nextq = this.getQstdao().getQuestion(BaseBotQuestions.USER_DETAIL);
					nextq = buildUserDetailQuestion(nextq, this.getUser(), false);
					nextq.setPreText("Data di nascita aggiornata. ");
				}else{
					nextq = this.getQstdao().getQuestion(BaseBotQuestions.USER_BIRTH);
					nextq.setPreText("Non ho capito. ");
				}
			}
			
			//Bot: inserisci la provincia di residenza
			if(lastQuestionName.equals(BaseBotQuestions.USER_PROVINCE)){
				//this.text è la provincia di residenza
				String province = bmanager.updateProvince(this.getText());
				if(province != null){
					//Parsing automatico effettuato
					this.setUser(this.bmanager.getUser());
					
					nextq = this.getQstdao().getQuestion(BaseBotQuestions.USER_DETAIL);
					nextq = buildUserDetailQuestion(nextq, this.getUser(), false);
					nextq.setPreText("Provincia di residenza aggiornata.");
				}else{
					//Parsing fuzzy. Do you mean <risultato esatto> (disambiguazione)
					String fuzzyProvince = bmanager.getFuzzyProvinceResult(this.getText());
					if(fuzzyProvince != null){
						nextq = this.getQstdao().getQuestion(BaseBotQuestions.USER_PROVINCE_DISAMBIGUATION);
						nextq = buildUserProvinceDisambiguationQuestion(nextq, fuzzyProvince);
					}else{
						nextq = this.getQstdao().getQuestion(BaseBotQuestions.USER_PROVINCE);
						nextq.setPreText("Non ho capito.");
					}
				}
			}
			
			//Bot: Forse intendevi 'fuzzy province result'
			if(lastQuestionName.equals(BaseBotQuestions.USER_PROVINCE_DISAMBIGUATION)){
				if(this.getText().equalsIgnoreCase(this.getLastq().getAnswers().get(0).getText())){
					//this.text è Si
					String oldText = this.getMsgdao().getLastKUserMessage(this.getChatId(), 2);
					String fuzzyProvince = this.bmanager.getFuzzyProvinceResult(oldText);
					this.bmanager.updateProvince(fuzzyProvince);
					this.setUser(this.bmanager.getUser());
					
					nextq = this.getQstdao().getQuestion(BaseBotQuestions.USER_DETAIL);
					nextq = buildUserDetailQuestion(nextq, this.getUser(), false);
					nextq.setPreText("Provincia di residenza aggiornata.");
				}else{
					nextq = this.getQstdao().getQuestion(BaseBotQuestions.USER_PROVINCE);
					nextq.setPreText("Non ho capito.");
				}
			}
		}
	    
		if(nextq == null){
			//some error occurred: send user to menu
			/*nextq = this.getQstdao().getQuestion(BaseBotQuestions.MENU);
			nextq = buildMenuQuestion(nextq, this.getUser().getFirstname(), false);
			nextq.setPreText("C'è stato un problema! :( ");
			this.setLinearButton(false); this.setGridButton(true);*/
			return null;
		}
		this.setNextq(nextq);
		return createResponseObject();
	}
	
	private static Question buildMenuQuestion(Question nextq, String nameUser, boolean sayhi){
		//Set firstname in message
		VelocityContext vc = new VelocityContext();
		if(nameUser != null){
			if(sayhi)
				vc.put("sayhi", "true");
			vc.put("name", nameUser);
		}
		((QuestionTemplate)nextq).setVelocityContex(vc);
		
		return nextq;
	}
	
	private static Question buildUserDetailQuestion(Question nextq, User user, boolean desc){
		//Set firstname in message
		VelocityContext vc = new VelocityContext();
		if(user != null){
			vc.put("user", user);
			if(user.getProvince() != null)
				vc.put("province", user.getProvince());
			if(desc)
				vc.put("desc", desc);
		}
		((QuestionTemplate)nextq).setVelocityContex(vc);
		
		return nextq;
	}
	
	private static Question buildUserProvinceDisambiguationQuestion(Question nextq, String provinceName){
		VelocityContext vc = new VelocityContext();
		vc.put("provinceName", provinceName);
		((QuestionTemplate)nextq).setVelocityContex(vc);
		
		return nextq;
	}

	private int countTherapies(){
		TherapyDao tdao = new  TherapyDao();
		return tdao.getAllTherapies(this.getUser().getId()).size();
	}
	
	private int countTrackings(){
		TrackingDao tdao = new TrackingDao();
		return tdao.getAllTrackings(this.getUser().getId()).size();
	}
	
	private boolean isMenuCommand(String text){
		String[] commands = new String[]{"/start", "/menu", "\\start", "\\menu",
									"start", "menu",
									"menu"+EmojiParser.parseToUnicode(":back:")};
		return checkCommand(text, commands);
	}
	
	private boolean isSuggestDoctorCommand(String text){
		String[] commands = new String[]{"/suggestDoctor", "suggerisci medico", 
									"suggerisci medici", "suggerisci dottore",
									"suggerisci dottori"};
		return checkCommand(text, commands);
	}
	
	private boolean isSymptomCheckerCommand(String text){
		String[] commands = new String[]{"/symptomchecker", "symptom checker", 
									"analisi sintomi", "analisi sintomo"};
		return checkCommand(text, commands);
	}
	
	private boolean isMedicalDictionaryCommand(String text){
		String[] commands = new String[]{"/medicaldictionary", "dizionario medico"};
		return checkCommand(text, commands);
	}
	
	private boolean isTherapyCommand(String text){
		String[] commands = new String[]{"/therapy", "terapia", "terapie", "gestione terapie"};
		return checkCommand(text, commands);
	}
	
	private boolean isTrackingCommand(String text){
		String[] commands = new String[]{"/tracking", "monitoraggio", "monitoraggi",
									"gestione monitoraggi"};
		return checkCommand(text, commands);
	}
	
	private boolean isUserProfileCommand(String text){
		String[] commands = new String[]{"/user", "profilo utente", "utente"};
		return checkCommand(text, commands);
	}
	
	private boolean checkCommand(String text, String[] commands){
		for(int i=0; i<commands.length; i++){
			if(text.equalsIgnoreCase(commands[i]))
				return true;
				
			if(EmojiParser.removeAllEmojis(text).trim().equalsIgnoreCase(commands[i]))
				return true;
		}
		
		return false;
	}
	
}
