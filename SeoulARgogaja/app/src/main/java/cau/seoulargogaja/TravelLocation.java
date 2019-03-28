package cau.seoulargogaja;

public class TravelLocation {

    // 아이템 타입을 구분하기 위한 type 변수. 0 = 날짜 , 1 = 여행정보, 2 = + (날짜추가) Default값 = 1;
    private int type;

    private String date = "";
    private String nick_name = "";
    private String nick_data = "";
    // 1 내용추가
    public TravelLocation(String nick_name, String nick_data) {
        this.nick_name = nick_name;
        this.nick_data = nick_data;
        this.type = 1;
    }
    // 0 날짜 추가
    public TravelLocation(String date) {
        this.date = date;
        this.type = 0;
    }
    // + 추가
    public TravelLocation() {
        this.type = 2;
    }

    public int getType() {
        return this.type ;
    }

    public String getdate() {
        return this.date ;
    }
    public void setdate(String date) {
        this.date = date ;
    }


    public String getnick_name() {
        return this.nick_name ;
    }
    public String getnick_data() {
        return this.nick_data ;
    }

    public void setnick_name(String name) { nick_name = name ; }
    public void setnick_data(String data) {
        nick_data = data ;
    }
}
