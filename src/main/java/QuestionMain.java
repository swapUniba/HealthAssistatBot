import com.triage.logic.questions.BaseBotQuestions;
import com.triage.logic.questions.SuggestDoctorQuestions;

public class QuestionMain {
	public static void main(String[] args) {
		BaseBotQuestions bbq = new BaseBotQuestions();
		System.out.println(bbq.getQuestion(BaseBotQuestions.MENU));
		
		SuggestDoctorQuestions sdq = new SuggestDoctorQuestions();
		System.out.println(sdq.getQuestion(SuggestDoctorQuestions.DESCRIBE_HEALT_STATE));
	}
}
