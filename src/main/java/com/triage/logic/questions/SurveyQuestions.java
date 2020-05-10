package com.triage.logic.questions;

import java.util.ArrayList;

import com.triage.rest.models.messages.Answer;
import com.triage.rest.models.messages.Question;
import com.triage.rest.models.messages.QuestionText;
import com.vdurmont.emoji.EmojiParser;

public class SurveyQuestions extends AbstractQuestions{
	public SurveyQuestions() {}

	public static final String MENU = "SURVEY_MENU";
	
	public static final String Q1_PRE = "Q1_PRE";
	public static final String Q2_PRE = "Q2_PRE";
	public static final String Q3_PRE = "Q3_PRE";
	public static final String Q1_ALL = "Q1_ALL";
	public static final String Q2_ALL = "Q2_ALL";
	public static final String Q3_ALL = "Q3_ALL";
	public static final String Q4_ALL = "Q4_ALL";
	public static final String Q5_ALL = "Q5_ALL";
	public static final String Q6_ALL = "Q6_ALL";
	public static final String Q7_ALL = "Q7_ALL";
	/*public static final String Q1_SC = "Q1_SC";
	public static final String Q2_SC = "Q2_SC";
	public static final String Q3_SC = "Q3_SC";*/
	public static final String Q1_FIN = "Q1_FIN";
	public static final String Q2_FIN = "Q2_FIN";
	public static final String Q3_FIN = "Q3_FIN";
	public static final String Q4_FIN = "Q4_FIN";
	public static final String Q5_FIN = "Q5_FIN";
	
	public static final String END = "END";
	
	@Override
	public ArrayList<Question> initializeQuestions() {
		ArrayList<Question> questions = new ArrayList<Question>();
		
		Question menu = new QuestionText(MENU, "menu");
		questions.add(menu);
		
		Question q1Pre = new QuestionText(Q1_PRE, "Qual è il tuo sesso?");
		q1Pre.setPreText("Grazie per aver preso parte alla sperimentazione :) Ti chiedo di rispondere a poche semplici domande che ci aiuteranno a migliorare il sistema. Il tempo di compilazione del questionario è di circa 3 minuti.\n\n");
		q1Pre.setAnswers(SexAnswers());
		questions.add(q1Pre);
		
		Question q2Pre = new QuestionText(Q2_PRE, "Qual è la tua fascia d'età?");
		q2Pre.setAnswers(AgesAnswers());
		questions.add(q2Pre);
		
		Question q3Pre = new QuestionText(Q3_PRE, "Qual è il tuo grado di istruzione?");
		q3Pre.setAnswers(EducationAnswers());
		questions.add(q3Pre);
		
		Question q1All = new QuestionText(Q1_ALL, "In che misura l'agente ha soddisfatto le tue esigenze?\n_(1 nessuno dei miei bisogni sono stati soddisfatti, 5 tutti i miei bisogni sono stati soddisfatti)_");
		q1All.setAnswers(OneFiveAnswers());
		questions.add(q1All);
		
		Question q2All = new QuestionText(Q2_ALL, "Consiglieresti il nostro chatbot a un amico?\n_(1 decisamente no, 5 decisamente sì)_");
		q2All.setAnswers(OneFiveAnswers());
		questions.add(q2All);
		
		Question q3All = new QuestionText(Q3_ALL, "Quanto sei soddisfatto dell'aiuto ricevuto?\n_(1 per nulla soddisfatto, 5 molto soddisfatto)_");
		q3All.setAnswers(OneFiveAnswers());
		questions.add(q3All);
		
		Question q4All = new QuestionText(Q4_ALL, "I servizi che hai ricevuto ti hanno aiutato ad affrontare in modo più efficace i tuoi problemi?\n_(1 non hanno aiutato per niente, 5 hanno aiutato molto)_");
		q4All.setAnswers(OneFiveAnswers());
		questions.add(q4All);
		
		Question q5All = new QuestionText(Q5_ALL, "Complessivamente, quanto sei soddisfatto dell'interazione con l'agente?\n_(1 per nulla soddisfatto, 5 molto soddisfatto)_");
		q5All.setAnswers(OneFiveAnswers());
		questions.add(q5All);
		
		Question q6All = new QuestionText(Q6_ALL, "Se dovessi avere di nuovo bisogno di uno dei servizi offerti dall'agente, torneresti di nuovo ad usarlo?\n_(1 decisamente no, 5 decisamente sì)_");
		q6All.setAnswers(OneFiveAnswers());
		questions.add(q6All);
		
		Question q7All = new QuestionText(Q7_ALL, "Le istruzioni fornite dall'agente erano significative e utili?\n_(1 decisamente no, 5 decisamente sì)_");
		q7All.setAnswers(OneFiveAnswers());
		questions.add(q7All);
		
		/*Question q1Sc = new QuestionText(Q1_SC, "Per quanto riguarda la sezione *Analisi Sintomi* come giudichi la qualità delle risposte che hai ricevuto dall'agente?");
		q1Sc.setAnswers(Q1SCAnswers());
		q1Sc.addSingleAnswer(new Answer("Non ho utilizzato il servizio"));
		questions.add(q1Sc);
		
		Question q2Sc = new QuestionText(Q2_SC, "Per quanto riguarda la sezione *Analisi Sintomi* quanto è stato facile far riconoscere i tuoi sintomi?");
		q2Sc.setAnswers(MoltoFacile_PerNullaFacile());
		questions.add(q2Sc);
		
		Question q3Sc = new QuestionText(Q3_SC, "Per quanto riguarda la sezione *Analisi Sintomi* le risposte ricevute dall'agente erano quelle che cercavi?");
		q3Sc.setAnswers(DecisamenteNo_SicuramenteSi());
		questions.add(q3Sc);*/
		
		Question q1Fin = new QuestionText(Q1_FIN, "Qual è il servizio che hai ritenuto più utile?");
		q1Fin.setAnswers(botFuctionAnswers());
		questions.add(q1Fin);
		
		Question q2Fin = new QuestionText(Q2_FIN, "Qual è il servizio che hai ritenuto meno utile?");
		q2Fin.setAnswers(botFuctionAnswers());
		questions.add(q2Fin);
		
		Question q3Fin = new QuestionText(Q3_FIN, "Qual è il servizio che ti ha soddisfatto maggiormente?");
		q3Fin.setAnswers(botFuctionAnswers());
		questions.add(q3Fin);
		
		Question q4Fin = new QuestionText(Q4_FIN, "Qual è il servizio che ti ha soddisfatto di meno?");
		q4Fin.setAnswers(botFuctionAnswers());
		questions.add(q4Fin);
		
		Question q5Fin = new QuestionText(Q5_FIN, "Ora puoi scrivere un commento con suggerimenti o consigli su come migliorare l'agente. Se non hai altri consigli premi No.");
		q5Fin.addSingleAnswer(new Answer("No"));
		questions.add(q5Fin);
		
		Question end = new QuestionText(END, "Grazie per aver preso parte a questo sondaggio :) Premi Menu per ritornare alla schermata principale.");
		end.addSingleAnswer(new Answer("Menu"+EmojiParser.parseToUnicode(":back:")));
		questions.add(end);
		
		
		return questions;
	}
	
	private static ArrayList<Answer> botFuctionAnswers(){
		ArrayList<Answer> ans = new ArrayList<Answer>();
		Answer sm = new Answer("Suggerisci Medici");
		ans.add(sm);
		Answer as = new Answer("Analisi Sintomi");
		ans.add(as);
		Answer dm = new Answer("Dizionario Medico");
		ans.add(dm);
		Answer gt = new Answer("Gestione Terapie");
		ans.add(gt);
		Answer gm = new Answer("Gestione Monitoraggi");
		ans.add(gm);
		
		return ans;
	}
	
	private static ArrayList<Answer> SexAnswers(){
		ArrayList<Answer> ans = new ArrayList<Answer>();
		
		Answer l1 = new Answer("Uomo");
		ans.add(l1);
		Answer l2 = new Answer("Donna");
		ans.add(l2);
		Answer l3 = new Answer("Altro");
		ans.add(l3);
		
		return ans;
	}

	private static ArrayList<Answer> AgesAnswers(){
		ArrayList<Answer> ans = new ArrayList<Answer>();
		
		String[] ages = new String[]{"18-25", "26-35", "36-45", "46-55", "oltre 55"};
		for(int i=0; i<ages.length; i++){
			ans.add(new Answer(ages[i]));
		}
		
		return ans;
	}
	
	private static ArrayList<Answer> EducationAnswers(){
		ArrayList<Answer> ans = new ArrayList<Answer>();

		String[] educIta = new String[]{"Licenza media", "Diploma superiore", "Laurea", "Dottorato", "Altro"};
		for(int i=0; i<educIta.length; i++)
			ans.add(new Answer(educIta[i]));

		return ans;
	}
	
	private static ArrayList<Answer> OneFiveAnswers(){
		ArrayList<Answer> ans = new ArrayList<Answer>();
		for(int i=0; i<5; i++){
			Answer e = new Answer(i + 1 + "");
			ans.add(e);
		}
		
		return ans;
	}
}
