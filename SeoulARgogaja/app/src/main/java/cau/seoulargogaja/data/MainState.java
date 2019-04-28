package cau.seoulargogaja.data;

import java.util.ArrayList;

public class MainState {
    static private int planlistid;
    static private String startdate;
    static private String enddate;
    static private ArrayList<String> dates;
    static private PlanListDTO mainDto;

    public MainState(){

    }

    public MainState(PlanListDTO dto){
        mainDto = dto;
        planlistid = mainDto.getId();
        startdate = mainDto.getStartDate();
        enddate = mainDto.getEndDate();
    }

    public int getplanlistId() {return planlistid;}
    public void setplanlistId(int planlistid){
        this.planlistid = planlistid;
    }

    public String getStartDate() {return startdate;}
    public void setStartDate(String d){
        this.startdate = d;
        this.mainDto.setStartDate(d);
    }

    public String getEndDate() {return enddate;}
    public void setEnddate(String d){
        this.enddate = d;
        this.mainDto.setEnddate(d);
    }

    public ArrayList<String> getdates() {return dates;}
    public void setdates(ArrayList<String> dates){
        this.dates = dates;
    }

    public PlanListDTO getMainDto() {return mainDto;}

}
