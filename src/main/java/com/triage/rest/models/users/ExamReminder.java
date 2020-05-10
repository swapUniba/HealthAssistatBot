package com.triage.rest.models.users;

import com.triage.utils.NLPUtils;
import org.joda.time.LocalDate;

import java.util.Calendar;
import java.util.Date;

public class ExamReminder {
    private int id;
    private int userid;
    private String exam;
    private Date date;
    private String hour;
    private String dateNewFormat;
    private boolean isPassed;

    public void setIsPassed(boolean ispassed){
        this.isPassed=ispassed;
    }

    public String getDateNewFormat() {
        return dateNewFormat;
    }

    public boolean getisPassed() {
        return isPassed;
    }


    public void setDateNewFormat(String dateNewFormat) {
        this.dateNewFormat = dateNewFormat;
    }

    public ExamReminder(int userid, String exam) {
        this.userid = userid;
        this.exam = exam;
    }

    public ExamReminder(int id, int userid, String exam, Date date, String hour) {
        this.id = id;
        this.userid = userid;
        this.exam = exam;
        this.hour = hour;
        this.setDate(date);
    }

    public ExamReminder(String exam, Date date, String hour) {
        this.exam = exam;
        this.hour = hour;
        this.setDate(date);
    }

    public ExamReminder(int id,String exam, Date date, String hour) {
        this.id=id;
        this.exam = exam;
        this.hour = hour;
        this.setDate(date);
    }

    public ExamReminder(String exam, Date date, String hour,int userid) {
        this.userid=userid;
        this.exam = exam;
        this.hour = hour;
        this.setDate(date);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getExam() {
        return exam;
    }

    public void setExam(String exam) {
        this.exam = exam;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
           if(date!=null) {
               LocalDate dt = new LocalDate(date);
               Calendar cal = Calendar.getInstance();
               if (hour!=null) {
                   String[] hour = this.getHour().split(":");
                   cal.set(dt.getYear(), dt.getMonthOfYear() - 1, dt.getDayOfMonth(), Integer.valueOf(hour[0]), Integer.valueOf(hour[1]));
                   this.isPassed = NLPUtils.isPassedDate(cal.getTime());
                   this.dateNewFormat = NLPUtils.getDateWithAnotherFormat(this.date);
               }
        }
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }
}
