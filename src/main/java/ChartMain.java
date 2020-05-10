import com.triage.logic.OCRChart;
import com.triage.utils.JsonUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.*;

import static com.sun.javafx.PlatformUtil.isLinux;
import static com.sun.javafx.PlatformUtil.isWindows;

public class ChartMain {
    public static void main(String[] args) {
        double x = (30.00*45)/100;
        if (isLinux())
            System.out.println("hello");
      /*  SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                OCRChart example = new OCRChart("Time Series Chart",);
                example.setSize(800, 400);
                example.setLocationRelativeTo(null);
                example.setVisible(true);
                example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            }
        });*/
    //   System.out.println( new JsonUtils().read_json_from_file("C:/Users/frank/IdeaProjects/triagebotrestserver/src/main/java/OCRexamsData.json"));
    }
}
