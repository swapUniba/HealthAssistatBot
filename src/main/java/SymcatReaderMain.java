import java.io.File;
import java.net.URL;

import com.triage.logic.SymcatConditionsReader;
import com.triage.logic.SymcatSymptomsReader;

public class SymcatReaderMain {
	public static void main(String[] args) {
		SymcatSymptomsReader ssr = SymcatSymptomsReader.getInstance();
		System.out.println("# symptoms:" + ssr.getSymptoms().length);
		int nrNoDescription = 0;
		for(int i=0; i<ssr.getSymptoms().length; i++){
			if(ssr.getSymptoms()[i].getDescriptions().size() == 0){
				System.out.println(ssr.getSymptoms()[i].getUrl());
				nrNoDescription++;
			}
		}
		System.out.println("# symptoms without desc:" + nrNoDescription);
		System.out.println(ssr.getSymptoms()[0]);
		System.out.println(ssr.getSymptoms()[100]);
		System.out.println(ssr.getIndex().get("http://www.symcat.com/symptoms/abnormal-involuntary-movements") == 0);
		System.out.println(ssr.getIndex().get("http://www.symcat.com/symptoms/abnormal-breathing-sounds") == 100);
		
		
		SymcatConditionsReader scr = SymcatConditionsReader.getInstance();
		System.out.println("# conditions:" + scr.getConditions().length);
		nrNoDescription = 0;
		for(int i=0; i<scr.getConditions().length; i++){
			if(scr.getConditions()[i].getDescriptions().size() == 0){
				System.out.println(scr.getConditions()[i].getUrl());
				nrNoDescription++;
			}
		}
		System.out.println("# conditions without desc:" + nrNoDescription);
		System.out.println(scr.getConditions()[0]);
		System.out.println(scr.getConditions()[166]);
		System.out.println(scr.getIndex().get("http://www.symcat.com/conditions/abdominal-hernia")==0);
		System.out.println(scr.getIndex().get("http://www.symcat.com/conditions/ear-wax-impaction")==166);
		
	}
}
