package cau.seoulargogaja.data;

public class HospitalDTO {

    private String id;
    private String name;     //관광지명
    private String latitude; //위도 (37.xxxxxx)
    private String longitude;//경도 (128.xxxxxx)
    private String address;  //주소
    private String phone;    //전화번호

    public HospitalDTO() {

    }

    public HospitalDTO(String id, String name, String latitude, String longitude ,String address,String phone) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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



}
