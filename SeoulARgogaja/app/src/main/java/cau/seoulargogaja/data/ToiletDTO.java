package cau.seoulargogaja.data;

public class ToiletDTO {

    private String id;
    private String name;     //관광지명
    private String latitude; //위도 (37.xxxxxx)
    private String longitude;//경도 (128.xxxxxx)

    public ToiletDTO() {

    }

    public ToiletDTO(String id, String name, String latitude, String longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;

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


}
