package com.triage.push_notification;

import com.triage.logic.managers.TrackingManager;
import com.triage.rest.dao.TrackingDao;
import com.triage.rest.dao.UserDao;
import com.triage.rest.enummodels.RangeLimit;
import com.triage.rest.models.messages.Response;
import com.triage.rest.models.users.Exam;
import com.triage.rest.models.users.ExamReminder;
import com.triage.rest.models.users.Tracking;
import com.triage.utils.NLPUtils;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.quartz.SchedulerException;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class TrackingPushNotifications {

    public TrackingPushNotifications(){

    }
    public void sendNotifications_examsoutofranges(ArrayList<Response> examsoutofranges){
        if (!examsoutofranges.isEmpty()) {
            for (Response res : examsoutofranges) {
                new PushNotifications().sendResponseObject(res);
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendNotifications_examsperiodicities(ArrayList<Response> examsperiodicities,String user_id){
        if (!examsperiodicities.isEmpty()) {
            String message = "Abbiamo scoperto che i seguenti esami sono effettuati con una certa periodicita'.\n\n";
            for (Response res : examsperiodicities) {
                if (!res.getResponse().isEmpty())
                   message += res.getResponse();
            }
            message += "Abbiamo impostato automaticamente le notifiche per ogni esame a cui non era già " +
                    "associata una notifica attiva.\n\n" +
                    "*Le notifiche gia' impostate e non ancora attivate non sono state modificate.*" +
                    "\n\n" +
                    "Ti ricordo che, nella sezione <Notifiche esami>, è possibile visualizzare tutte le notifiche degli esami e apportarne le modifiche desiderate.\n";
            new PushNotifications().sendResponseObject(new Response(Integer.valueOf(user_id), message));
        }
    }
    public Response createNotificationsAboutDatePattern(int user_id, Exam exam) {
        TrackingDao tdao = new TrackingDao();
        UserDao udao= new UserDao();
        //recupera le date in cui questo esame è stato fatto. OVvviamente crea un set di String di tipo data
        Set<Date> dates = tdao.getExamDates(exam, user_id);
        //se ci sono almeno 2 date per un certo esame
        if (dates.size()>2) {
            Iterator<Date> itr = dates.iterator();
            List<Period> periods = new ArrayList<Period>();

            //calcola le differenze, a due a due
            Date last = null;
            while (itr.hasNext()) {
                Date dtt1 = null;
                if (last == null)
                    dtt1 = itr.next();
                else
                    dtt1 = last;

                if (itr.hasNext()) {
                    Date dtt2 = itr.next();
                    periods.add(NLPUtils.difference(dtt1, dtt2));
                    last = dtt2;
                }
            }

            //ora fa le medie
            int yearsum = 0;
            int monthsum = 0;
            int daysum = 0;
            for (Period p : periods) {
                yearsum += p.getYears();
                monthsum += p.getMonths();
                daysum += p.getDays();
            }

            if (periods.size() > 0) {
                String year="anni";
                String months="mesi";
                int averageyears = yearsum / periods.size();
                int averagemonths = monthsum / periods.size();
                int averagedays = daysum / periods.size();
                boolean set=false;
                String message ="- *" + exam.getName()+"* viene rilevato *mediamente* ogni ";
                if (averagemonths>0 && averageyears>0){
                    if (averagemonths==1)
                        months="mese";
                    if (averageyears==1) {
                        year = "*anno*";
                        message+=   year + " e *"+ averagemonths+"* " + months +".\n";
                    }
                    else
                        message += "*"+averageyears + " " + year + "* e *" + averagemonths + " " + months + "*.\n";
                    set=true;
                }
                else if(averagemonths==0 && averageyears>0){
                    if (averageyears==1) {
                        year = "*anno*";
                        message+=   year + ".\n";
                    }
                    else
                        message += "*"+averageyears + " " + year + "*.\n";
                    set=true;
                }
                else if( averagemonths>0 && averageyears==0){
                    if (averagemonths==1){
                        months="*mese*";
                        message+=   months +".\n";
                    }
                    else
                        message+=  "*"+ averagemonths + " "+ months + "*.\n";
                    set=true;
                }
                /*
                else if( averagedays>0 && averagemonths>0 && averageyears==0) {
                    message += +averagemonths + "* mesi e *" + averagedays + "* giorni.\n";
                }
                else if( averagedays>0 && averagemonths==0 && averageyears==0)
                    message+=  + averagedays + "* giorni. \n";*/

                if (set){
                    LocalDate dt = LocalDate.now().plusYears(averageyears);
                    dt = dt.plusMonths(averagemonths);
                    dt= dt.plusDays(averagedays);
                    message += "(Data notifica suggerita: *" + dt.getDayOfMonth() + "/" + dt.getMonthOfYear() + "/" + dt.getYear() + "*)\n\n";
                    //aggiungi un alert tra che tra X tempo avviserà l'utente.
                    exam.setDate(NLPUtils.parseDate(dt.getDayOfMonth() + "/" + dt.getMonthOfYear() + "/" + dt.getYear()));
                    //save on db
                    boolean added = tdao.addReminder(new ExamReminder(exam.getName(), exam.getDate(), "10:15", user_id));
                    //save on quartz
                    if (added) {
                        TrackingManager tm = new TrackingManager(udao.getUser(user_id));
                        try {
                            ExamReminder exm = new ExamReminder(exam.getName(), dt.toDate(), "10:15");
                            tm.addReminder(exm, exm);
                        } catch (SchedulerException e) {
                            e.printStackTrace();
                        }
                    }
                    Response resp = new Response(user_id, message);
                    return resp;
                }
            }
        }
        return null;
    }

    public Response createNotificationsAboutRanges(int user_id,int photo_id,Exam exam) {
        TrackingDao tdao = new TrackingDao();
        //Invia una notifica se in un esame compare un valore superiore o inferiore al limite.
        Tracking t = tdao.getTrackingByImageID(user_id, photo_id);
        String message = null;
        if (exam.getOutofrange() == RangeLimit.superior) {
            double diff = (double) Math.round((exam.getResult() - exam.getMax()) * 100) / 100;
            message = "Attenzione, dal monitoraggio *" + t.getName() + "*, è emerso che il risultato per l'esame *" + exam.getName() + "* supera la soglia massima:\n"
                    + "- Risultato: " + exam.getResult() + "\n"
                    + "- Soglia massima: " + exam.getMax() + "\n"
                    + "- Scostamento: " + diff + "\n"
                    + "ALTRE INFO:\n"
                    + "- Soglia minima: " + exam.getMin() + "\n"
                    + "- U. di misura: " + exam.getUnit() + "\n\n" +
                    "*Nota: se noti qualche valore errato, puoi correggerlo manualmente accedendo alla sezione <Visualiza monitoraggi>.*\n" ;


            List<Tracking> trs = tdao.getTrackingsByExamOutOfRange(exam, user_id);
            if (!trs.isEmpty()) {
                message = message + "Inoltre, ha superato la soglia massima anche nei seguenti monitoraggi: \n";
                String list = "";
                for (Tracking tt : trs) {
                    if(!tt.getName().equals(t.getName())) {
                        double difff = (double) Math.round((exam.getResult() - exam.getMax()) * 100) / 100;
                        list = list + " -" + tt.getName() + " (Scostamento +" + difff + ")" + "\n";
                    }
                }
                if (!list.isEmpty())
                    message = message + list;
                else
                    message="";
            }
        } else if (exam.getOutofrange() == RangeLimit.inferior) {
            double diff = (double) Math.round((exam.getMin() - exam.getResult()) * 100) / 100;
            message = "Attenzione, dal monitoraggio  *" + t.getName() + "*, è emerso che il risultato per l'esame  *" + exam.getName() + "* è inferiore alla soglia minima:\n"
                    + "- Risultato: " + exam.getResult() + "\n"
                    + "- Soglia minima: " + exam.getMin() + "\n"
                    + "- Scostamento: " + diff + "\n"
                    + "ALTRE INFO:\n"
                    + "- Soglia massima: " + exam.getMax() + "\n"
                    + "- U. di misura: " + exam.getUnit() + "\n\n"+
                    "*Nota: se noti qualche valore errato, puoi correggerlo manualmente accedendo alla sezione <Visualiza monitoraggi>.*\n" ;


            //nota: supponiamo che l'esame, per esempio Glicemia, sia UNIVOCO in un monitoraggio: non ci deve essere due volte, insomma.
            List<Tracking> trs = tdao.getTrackingsByExamOutOfRange(exam, user_id);
            if (!trs.isEmpty()) {
                message = message + "Inoltre, risulta essere inferiore alla soglia minima anche nei seguenti monitoraggi: \n";
                String list = "";
                for (Tracking tt : trs) {
                    if(!tt.getName().equals(t.getName())) {
                        double difff = (double) Math.round((exam.getMin() - exam.getResult()) * 100) / 100;
                        list = list + " - " + tt.getName() + " (Scostamento +" + difff + ")" + "\n";
                    }
                }
                if (!list.isEmpty())
                    message = message + list;
                else
                    message="";
            }
        }
        if (message != null) {
            Response resp = new Response(user_id, message);
            //new PushNotifications().sendResponseObject(resp);
            return resp;
        }
        return null;
    }

    public void send_tracking_news_notification(int userid, Tracking t){
        String message = "Ci sono novita' riguardo al monitoraggio *"+ t.getName()+ "* .\n Accedi alla sezione <Visualizza monitoraggi> per saperne di piu'.";
        Response resp = new Response(userid,message);
        new PushNotifications().sendResponseObject(resp);
    }
}
