package cau.seoulargogaja;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class PlanAddMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Double latitude;
    private Double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.plan_map);
        mapFragment.getMapAsync(this);

        Button button1 = (Button) findViewById(R.id.plan_map_close) ;
        button1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                data.putExtra("latitude",Double.toString(latitude));
                data.putExtra("longitude",Double.toString(longitude));
                setResult(RESULT_OK,data);
                finish();
            }
        });

    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        // 맵 터치 이벤트 구현 //
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng point) {
                MarkerOptions mOptions = new MarkerOptions();
                // 마커 타이틀
                mOptions.title("마커 좌표");
                latitude = point.latitude; // 위도
                longitude = point.longitude; // 경도
                // 마커의 스니펫(간단한 텍스트) 설정
                mOptions.snippet(latitude.toString() + ", " + longitude.toString());
                // LatLng: 위도 경도 쌍을 나타냄
                mOptions.position(new LatLng(latitude, longitude));
                // 마커(핀) 추가
                mMap.clear();
                googleMap.addMarker(mOptions);
                LatLng startpoint = new LatLng(latitude, longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(startpoint));
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
                mMap.animateCamera(zoom);
            }
        });


        Intent intent = getIntent(); /*데이터 수신*/
        try{
            String lat = intent.getExtras().getString("latitude"); /*String형*/
            String lon = intent.getExtras().getString("longitude"); /*String형*/
            latitude = Double.parseDouble(lat);
            longitude = Double.parseDouble(lon);
            LatLng startpoint = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(startpoint).title("현재 지정 위치"));

            mMap.moveCamera(CameraUpdateFactory.newLatLng(startpoint));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
            mMap.animateCamera(zoom);

        }
        catch(Exception e){
            // Add a marker in Sydney and move the camera
            LatLng startpoint = new LatLng(37.5050923, 126.9549072);
            mMap.addMarker(new MarkerOptions().position(startpoint).title("중앙대학교"));

            mMap.moveCamera(CameraUpdateFactory.newLatLng(startpoint));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
            mMap.animateCamera(zoom);

        }
    }

}
