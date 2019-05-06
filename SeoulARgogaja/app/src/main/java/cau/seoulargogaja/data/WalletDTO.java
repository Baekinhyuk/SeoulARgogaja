package cau.seoulargogaja.data;

public class WalletDTO {

    private int id; //가계부 ID
    private String date;     // 날짜
    private int planlistid;    // 리스트 ID
    private String detail;     // 내역
    private int expend; // 금액
    private String memo; // 메모
    private int datatype; //날짜 , 내용 구별 0 = 날짜 , 1 = 내용

    private int main_image; //0 : 식사, 1 : 쇼핑, 2 : 교통, 3 : 관광, 4 : 숙박, 5: 기타
    private int sub_image; //0 : 카드, 1 : 현금
    private int color_type; //0. 노랑 #FFFF33 1. 주황 #FF9933 2. 초록 #66FF66 3. 파랑 #66CCFF 4. 분홍 #FF99FF

    private int order;

    //ListView 날짜에 대해 + 모양 (datatype = 2)
    public WalletDTO() {
        datatype = 2;
    }

    //ListView 날짜에 대해 저장 (datatype = 0)
    public WalletDTO(String date, int planlistid) {
        this.date = date;
        this.planlistid = planlistid;
        expend = 0; //각 날짜별 금액합산하기위해 날짜정보들어간것은 0원으로 인식
        datatype = 0;
    }

    //ListView 날짜에 대해 저장 (datatype = 0)
    public WalletDTO(int id, String date, int order,int planlistid) {
        this.id = id;
        this.date = date;
        this.order = order;
        this.planlistid = planlistid;
        datatype = 0;
    }

    //ListView 내용에 대해 저장 (datatype = 1)
    public WalletDTO(int id, String date, int planlistid,String detail, int expend, String memo) {
        this.id = id;
        this.date = date;
        this.planlistid = planlistid;
        this.detail = detail;
        this.expend = expend;
        this.memo = memo;
        datatype = 1;
    }

    //ListView 내용에 대해 저장 (datatype = 1)
    public WalletDTO(int id, String date, int planlistid,String detail, int expend, String memo,int main_image,int sub_image,int color_type,int order) {
        this.id = id;
        this.date = date;
        this.planlistid = planlistid;
        this.detail = detail;
        this.expend = expend;
        this.memo = memo;
        datatype = 1;
        this.main_image = main_image;
        this.sub_image = sub_image;
        this.color_type = color_type;
        this.order = order;
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

    public int getplanlistid() {
        return planlistid;
    }

    public void setplanlistid(int planlistid) {
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

    public int getmain_image() {
        return main_image;
    }

    public void setmain_image(int main_image) {
        this.main_image = main_image;
    }

    public int getsub_image() {
        return sub_image;
    }

    public void setsub_image(int sub_image) {
        this.sub_image = sub_image;
    }

    public int getcolor_type() {
        return color_type;
    }

    public void setcolor_type(int color_type) {
        this.color_type = color_type;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

}
