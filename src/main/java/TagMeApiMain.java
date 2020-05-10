import com.triage.logic.TagMeApi;

public class TagMeApiMain {
	
	public static void main(String[] args) {
		TagMeApi tma = new TagMeApi("ho nausea e vomito");
		System.out.println(tma.getResult());
		System.out.println(tma.getResult().getWikipediaLinks());
	}
}
