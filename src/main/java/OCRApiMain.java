import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.triage.logic.OCRApi;
import com.triage.rest.models.users.Exam;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

public class OCRApiMain {

	public static void main(String[] args) throws FileNotFoundException, JSONException {
		//InputStream is = new FileInputStream("C:/Users/frank/IdeaProjects/triagebotrestserver/src/1.png");
		OCRApi ocra = new OCRApi();
		JSONArray arr = new JSONArray(ocra.get_result("testsdf"));
		String error = arr.getJSONObject(0).getString("Error");
		System.out.println(error);
		//Create OCRText object
		if (arr.length()>1) {
			String text = arr.getJSONObject(1).getString("Text");
		}
		//	OCRText ocr = new OCRText(Integer.toString(imageID), text);
		if (error.isEmpty()) {
			List<Exam> exams = new ArrayList<Exam>();
			for (int i = 2; i < arr.length(); i++) {
				org.codehaus.jettison.json.JSONObject l = arr.getJSONObject(i);
				//Exam x = new Exam(l.getString("Exam"), l.getDouble("Result"), l.getDouble("Min"), l.getDouble("Max"), l.getString("Unit"));
				//exams.add(x);
			}
		}
	}

}
