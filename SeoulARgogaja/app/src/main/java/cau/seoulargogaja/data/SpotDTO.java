package cau.seoulargogaja.data;

import android.util.Log;

public class SpotDTO {

    private String id;
    private String name;     //관광지명
    private String theme;    //테마
    private String area;     //구역
    private String latitude; //위도 (37.xxxxxx)
    private String longitude;//경도 (128.xxxxxx)
    private String address;  //주소
    private String phone;    //전화번호
    private String web;      //웹사이트 주소
    private String description; // 설명

    public SpotDTO() {

    }

    public SpotDTO(String id, String name, String theme,String area, String latitude, String longitude ,String address,String phone, String web, String description) {
        this.id = id;
        this.name = name;
        this.theme = theme;
        this.area = area;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.phone = phone;
        this.web = web;
        this.description = description;
    }


    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String x) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String y) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }




    public void print() {
        String str = id+"/"+ name+"/"+ theme+"/"+area+"/"+ latitude+"/"+ longitude+"/"+ description+"/"+ phone+"/"+ web;
        Log.d("tour","print() : "+str);
    }
}
