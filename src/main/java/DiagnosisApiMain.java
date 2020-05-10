import java.util.ArrayList;

import com.triage.logic.DiagnosisApi;

public class DiagnosisApiMain {
	public static void main(String[] args) {
		ArrayList<String> symptoms = new ArrayList<String>();
		symptoms.add("blindness");
		//symptoms.add("spots-or-clouds-in-vision");
		
		
		DiagnosisApi da = new DiagnosisApi(symptoms);
		System.out.println(da.getResult());
		System.out.println(da.getResult().getDiseases());
	}
}
