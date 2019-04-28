package cau.seoulargogaja.data;

public class PlanListDTO {

    private int id;
    private String name;
    private String startdate;
    private String enddate;
    private int budget;
    private String code;

    public PlanListDTO() {

    }

    public PlanListDTO(int id, String name, String startdate, String enddate, int budget, String code){
        this.id = id;
        this.name = name;
        this.startdate = startdate;
        this.enddate = enddate;
        this.budget = budget;
        this.code = code;
    }

    public int getId() {return id;}
    public void setId(int id){
        this.id = id;
    }

    public String getName() {return name;}
    public void setName(String name){
        this.name = name;
    }

    public String getStartDate() {return startdate;}
    public void setStartDate(String d){
        this.startdate = d;
    }

    public String getEndDate() {return enddate;}
    public void setEnddate(String d){
        this.enddate = d;
    }

    public int getBudget() {return budget;}
    public void setBudget(int budget){
        this.budget = budget;
    }

    public String getCode() {return code;}
    public void setCode(String code){
        this.code = code;
    }
}

