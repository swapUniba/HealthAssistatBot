package com.triage.rest.models.users;

import com.triage.rest.enummodels.RangeLimit;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

public class Exam {
   private double result;
   private String name;
   private double min;
   private double max;
   private String unit;
   private Date date;
   private String tracking_name;
   private RangeLimit outofrange;
   private int id;
   private int tracking_id;
    public Exam( String name,double result,double min, double max,String unit,RangeLimit outofrange,int id) {
        this.result = result;
        this.name = name;
        this.min = min;
        this.max = max;
        this.unit= unit;
        this.tracking_name=tracking_name;
        this.outofrange=outofrange;
        this.id=id;
    }
    public Exam( String name,double result,double min, double max,String unit,String tracking_name,RangeLimit outofrange) {
        this.result = result;
        this.name = name;
        this.min = min;
        this.max = max;
        this.unit= unit;
        this.tracking_name=tracking_name;
        this.outofrange=outofrange;
    }

    public Exam( String name,double result,double min, double max,String unit,RangeLimit outofrange) {
        this.result = result;
        this.name = name;
        this.min = min;
        this.max = max;
        this.unit= unit;
        this.outofrange=outofrange;
    }

    public Exam( String name,double result,double min, double max,String unit,RangeLimit outofrange,int id,int tracking_id) {
        this.result = result;
        this.name = name;
        this.min = min;
        this.max = max;
        this.unit= unit;
        this.outofrange=outofrange;
        this.id = id;
        this.tracking_id=tracking_id;
    }

    public void setId(int id){this.id=id;}
    public void setTracking_id(int id){ this.tracking_id=id;}
    public int getTracking_id(){return  this.tracking_id;}
    public int getId(){ return this.id;}
    public RangeLimit getOutofrange(){ return this.outofrange; }
    public String getTracking_name(){
        return this.tracking_name;
    }
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUnit() {
        return unit;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }


}
