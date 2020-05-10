package com.triage.rest.models.messages;
import com.triage.rest.models.users.Exam;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class OCRExam {
    private int photoID;
    private int trackingID;
    private String text;
    private List<Exam> exams = new ArrayList<Exam>();

    public OCRExam(int photoID, int trackingID, List<Exam> exams) {
        this.photoID = photoID;
        this.trackingID = trackingID;
        this.exams = exams;
    }

    public int getPhotoID() {
        return photoID;
    }

    public int getTrackingID() {
        return trackingID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Exam> getExams() {
        return exams;
    }

}
