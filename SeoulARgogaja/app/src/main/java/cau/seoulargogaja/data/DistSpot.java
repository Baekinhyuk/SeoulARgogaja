package cau.seoulargogaja.data;

public class DistSpot implements Comparable<DistSpot>{

    private double dist;
    private int id;
    private PoliceDTO dto;
    private HospitalDTO hdto;
    public DistSpot(double dist, int id, PoliceDTO dto){
        this.dist = dist;
        this.id = id;
        this.dto = dto;
    }

    public DistSpot(double dist, int id, HospitalDTO dto){
        this.dist = dist;
        this.id = id;
        this.hdto = dto;
    }

    public int compareTo(DistSpot other){
        return Double.compare(dist,  other.dist);
    }

    public double getDist(){
        return dist;
    }

    public int getId(){
        return id;
    }

    public PoliceDTO getDto() {return dto;}

    public HospitalDTO getHdto() {
        return hdto;
    }
}
