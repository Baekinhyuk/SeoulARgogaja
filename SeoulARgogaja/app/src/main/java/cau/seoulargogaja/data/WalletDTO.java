package cau.seoulargogaja.data;

public class WalletDTO {

    private int id; //가계부 ID
    private String date;     // 날짜
    private String planlistid;    // 리스트 ID
    private String detail;     // 내역
    private int expend; // 금액
    private String memo; // 메모
    private int datatype; //날짜 , 내용 구별 0 = 날짜 , 1 = 내용

    public WalletDTO() {

    }

    //ListView 날짜에 대해 저장 (datatype = 0)
    public WalletDTO(int id, String date, String planlistid) {
        this.id = id;
        this.date = date;
        this.planlistid = planlistid;
        datatype = 0;
    }

    //ListView 내용에 대해 저장 (datatype = 1)
    public WalletDTO(int id, String date, String planlistid,String detail, int expend, String memo) {
        this.id = id;
        this.date = date;
        this.planlistid = planlistid;
        this.detail = detail;
        this.expend = expend;
        this.memo = memo;
        datatype = 1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getdate() {
        return date;
    }

    public void setdate(String date) {
        this.date = date;
    }

    public String getplanlistid() {
        return planlistid;
    }

    public void setplanlistid(String planlistid) {
        this.planlistid = planlistid;
    }

    public String getdetail() {
        return detail;
    }

    public void setdetail(String detail) {
        this.detail = detail;
    }

    public int getexpend() {
        return expend;
    }

    public void setexpend(int expend) {
        this.expend = expend;
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


}
