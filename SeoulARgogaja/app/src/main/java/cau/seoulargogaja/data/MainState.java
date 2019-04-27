package cau.seoulargogaja.data;

import java.util.ArrayList;

public class MainState {
    static private int planlistid;
    static private String startdate;
    static private String enddate;
    static private ArrayList<String> dates;

    public void MainState(){

    }

    public int getplanlistId() {return planlistid;}
    public void setplanlistId(int planlistid){
        this.planlistid = planlistid;
    }

    public String getStartDate() {return startdate;}
    public void setStartDate(String d){
        this.startdate = d;
    }

    public String getEndDate() {return enddate;}
    public void setEnddate(String d){
        this.enddate = d;
    }

    public ArrayList<String> getdates() {return dates;}
    public void setdates(ArrayList<String> dates){
        this.dates = dates;
    }

}
