package cau.seoulargogaja;

public class TravelLocation {

    private String nick_name = "";
    private String nick_data = "";

    public TravelLocation(String nick_name, String nick_data) {
        this.nick_name = nick_name;
        this.nick_data = nick_data;
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
