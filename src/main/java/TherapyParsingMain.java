import java.util.ArrayList;

import com.triage.rest.models.users.TherapyDay;

public class TherapyParsingMain {
	public static void main(String[] args) {
		String[] toTest = new String[]{
				/*"lunedi",
				"martedì",
				"mercoledì",
				"giovedi",
				"sabato",
				"domenica",
				"mar",
				"merc",
				"mer",
				"dom enica",*/
				/*"lun martedi, merlc, sab - dom",
				"mercol mart ven dom",*/
				"domen lune, meri, maret",
				"l,m,m me ma"
		};
		
		for(int i=0; i<toTest.length; i++){
			ArrayList<String> parsed = TherapyDay.parseDays(toTest[i]);
			System.out.println("Input:" + toTest[i] + ". Parsed: " + parsed);
		}
	}
}
