import com.triage.rest.enummodels.ItalianProvince;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class FuzzyWuzzyMain {
	
	
	public static void main(String[] args) {
		System.out.println(FuzzySearch.extractTop("bri", 
				ItalianProvince.mappingProvinces().keySet(), 3));
		

		System.out.println(FuzzySearch.extractTop("bar", 
				ItalianProvince.mappingProvinces().keySet(), 3));
		
		System.out.println(FuzzySearch.extractTop("sfagdf", 
				ItalianProvince.mappingProvinces().keySet(), 3));
		
	}
}
