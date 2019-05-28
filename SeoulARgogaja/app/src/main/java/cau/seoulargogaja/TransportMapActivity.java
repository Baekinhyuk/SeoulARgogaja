package cau.seoulargogaja;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TransportMapActivity extends AppCompatActivity {
    private ODsayService oDsayService;
    int path;
    String transport;
    TextView tv_text;

    private GoogleMap gmap;
    private MapView mapView2;
    double s_lati;
    double s_long;
    double e_lati;
    double e_long;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_map);

        Intent intent  = getIntent();
        path = intent.getIntExtra("subPath",0);
        String start_latitude = intent.getStringExtra("start_latitude");
        String start_longitude = intent.getStringExtra("start_longitude");
        String end_latitude = intent.getStringExtra("end_latitude");
        String end_longitude = intent.getStringExtra("end_longitude");

        tv_text = (TextView)findViewById(R.id.tv_text);

        s_lati = Double.parseDouble(start_latitude);
        s_long = Double.parseDouble(start_longitude);
        e_lati = Double.parseDouble(end_latitude);
        e_long = Double.parseDouble(end_longitude);

        LatLng start_position = new LatLng(s_lati,s_long);
        LatLng end_position = new LatLng(e_lati,e_long);

        // Setup the MapView
        mapView2 = findViewById(R.id.mapView2);
        mapView2.onCreate(savedInstanceState);
        mapView2.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gmap = googleMap;

                gmap.addMarker(new MarkerOptions().position(start_position).title("출발지"));
                gmap.addMarker(new MarkerOptions().position(end_position).title("도착지"));

                gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(s_lati, s_long),15));
            }
        });

        oDsayService = ODsayService.init(this, getString(R.string.odsay_key));
        oDsayService.setReadTimeout(5000);
        oDsayService.setConnectionTimeout(5000);

        oDsayService.requestSearchPubTransPath(start_longitude, start_latitude
                , end_longitude, end_latitude, "0", "0", "0", onResultCallbackListener);

    }

    private OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener(){

        @Override
        public void onSuccess(ODsayData oDsayData, API api) {
            try {
                JSONArray subPath = oDsayData.getJson().getJSONObject("result").getJSONArray("path").getJSONObject(path).getJSONArray("subPath");
                transport = path+1+") ";
                LatLng before = new LatLng(s_lati,s_long);

                for (int i = 0; i < subPath.length(); i++){
                    JSONObject SubPathObject = subPath.getJSONObject(i);

                    if (SubPathObject.getString("trafficType").equals("1")){
                        JSONArray laneArray = SubPathObject.getJSONArray("lane");// 노선 들어있는 어레이
                        JSONObject stopList = SubPathObject.getJSONObject("passStopList"); // 정류장 들어있는 오브젝트
                        JSONArray stationList = stopList.getJSONArray("stations"); // 거쳐야할 정류장 리스트어레이.

                        /*하위의 JsonArray[lane] 에 타야할 지하철 번호가 나옴
                         * 하위의 JsonObject passStopList -> JsonArray[Jsonobject] stations ->에 해당 이동수단의 정류장  */
                        String startname = SubPathObject.getString("startName") + "역 "; // 승차역 이름
                        String subway_no = "("+subPath.getJSONObject(i).getJSONArray("lane").getJSONObject(0).getString("subwayCode")+"호선) 승차";

                        String lanewayname = startname + subway_no + " -> ";

                        for (int v = 0; v < stationList.length(); v++) { // 정류장 리스트 뽑기
                            //여기서 맵을 그려버리자
                            double c_long =  Double.parseDouble(stationList.getJSONObject(v).getString("x")); //longitude
                            double c_lati =  Double.parseDouble(stationList.getJSONObject(v).getString("y")); //longitude
                            //   stationList.getJSONObject(v).getString("y"); // latitude
                            LatLng current = new LatLng(c_lati,c_long);
                            gmap.addPolyline(new PolylineOptions().add(before,current).width(8).color(Color.BLUE));
                            before = new LatLng(c_lati,c_long);
                        }

                        String endname = SubPathObject.getString("endName"); // 하차역 이름
                        int endid = SubPathObject.getInt("endID");

                        if(endid != 426){
                            endname = endname + "역 하차 ";
                        }
                        else endname = endname + " 하차 "; // 하차역 이름

                        transport += lanewayname + endname;


                    }
                    else if (SubPathObject.getString("trafficType").equals("2")) { // 버스 일때
                        JSONArray laneArray = SubPathObject.getJSONArray("lane");// 노선 들어있는 어레이
                        JSONObject stopList = SubPathObject.getJSONObject("passStopList"); // 정류장 들어있는 오브젝트
                        JSONArray stationList = stopList.getJSONArray("stations"); // 거쳐야할 정류장 리스트어레이.

                        String startname = SubPathObject.getString("startName") + " 정류장"; // 승차역 이름
                        String bus_no = "("+subPath.getJSONObject(i).getJSONArray("lane").getJSONObject(0).getString("busNo")+"번) 승차";

                        String lanewayname = startname + bus_no +" -> ";

                        for (int v = 0; v < stationList.length(); v++) { // 정류장 리스트 뽑기
                            //   여기서 맵을 그려버리자
                            double c_long =  Double.parseDouble(stationList.getJSONObject(v).getString("x")); //longitude
                            double c_lati =  Double.parseDouble(stationList.getJSONObject(v).getString("y")); //longitude
                            //   stationList.getJSONObject(v).getString("y"); // latitude
                            LatLng current = new LatLng(c_lati,c_long);
                            gmap.addPolyline(new PolylineOptions().add(before,current).width(8).color(Color.GREEN));
                            before = new LatLng(c_lati,c_long);
                        }

                        String endname = SubPathObject.getString("endName") + "정류장 하차 "; // 하차역 이름
                        transport +=lanewayname + endname;
                    } else if(SubPathObject.getString("trafficType").equals("3")){
                        continue;
                    }

                    if(i<subPath.length()-2)
                        transport += "-> ";

                    tv_text.setText(transport);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(int i, String s, API api) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mapView2.onResume();
    }
}