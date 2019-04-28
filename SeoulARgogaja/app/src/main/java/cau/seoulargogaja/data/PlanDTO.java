package cau.seoulargogaja.data;

public class PlanDTO {
    private int id; //일정 ID
    private String content; // 내용
    private String date;     // 날짜
    private int spotID;     // spotID
    private int customID; // customID
    private String memo; // 메모
    private int order; // 순서
    private int datatype; //날짜 , 내용 구별 0 = 날짜 , 1 = 내용, 2 = + 모양
    private int planlistid;    // 리스트 ID

    //ListView 날짜에 대해 + 모양 (datatype = 2)
    public PlanDTO() {
        datatype = 2;
    }

    //ListView 날짜에 대해 저장 (datatype = 0)
    public PlanDTO(int id, String date, int order, int planlistid) {
        this.id = id;
        this.date = date;
        this.order = order;
        this.planlistid = planlistid;
        datatype = 0;
    }

    //ListView 날짜에 대해 저장 (datatype = 0)
    public PlanDTO(String date, int planlistid) {
        this.date = date;
        this.planlistid = planlistid;
        datatype = 0;
    }

    //ListView 내용에 대해 저장 (datatype = 1)
    public PlanDTO(int id,String content, String date, int spotID,int customID, String memo,int order,int planlistid) {
        this.id = id;
        this.content = content;
        this.date = date;
        this.planlistid = planlistid;
        this.order = order;
        this.spotID = spotID;
        this.customID = customID;
        this.memo = memo;
        datatype = 1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getdate() {
        return date;
    }

    public void setdate(String date) {
        this.date = date;
    }

    public int getplanlistid() {
        return planlistid;
    }

    public void setplanlistid(int planlistid) {
        this.planlistid = planlistid;
    }

    public int getspotID() {
        return spotID;
    }

    public void setSpotID(int spotID) {
        this.spotID = spotID;
    }

    public int getcustomID() {
        return customID;
    }

    public void setcustomID(int customID) {
        this.customID = customID;
    }

    public String getmemo() {
        return memo;
    }

    public void setmemo(String memo) {
        this.memo = memo;
    }

    public int getdatatype() {
        return datatype;
    }

    public void setdatatype(int datatype) {
        this.datatype = datatype;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }


}
