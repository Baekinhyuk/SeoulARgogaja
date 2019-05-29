package cau.seoulargogaja;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class SimpleDirectionActivity extends AppCompatActivity {
    public Handler handler;
    LoadFragment fragment1;
    TransportFragment fragment2;
    LocationManager manager;

    String start_content;
    double start_latitude;
    double start_longitude;

    String end_content;
    double end_latitude;
    double end_longitude;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_direction);

        TextView start = (TextView) findViewById(R.id.start);
        TextView destination = (TextView)findViewById(R.id.destination);

        Intent intent = getIntent(); /*데이터 수신*/
        int position = intent.getExtras().getInt("data2_type");
        Log.d("position"," "+position);

        if(position == 1){
            start_latitude = Double.parseDouble(intent.getExtras().getString("s_latitude")); /*String형*/
            start_longitude = Double.parseDouble(intent.getExtras().getString("s_longitude")); /*String형*/

            end_latitude = Double.parseDouble(intent.getExtras().getString("e_latitude")); /*String형*/
            end_longitude = Double.parseDouble(intent.getExtras().getString("e_longitude")); /*String형*/

            start_content = intent.getExtras().getString("s_content");
            end_content = intent.getExtras().getString("e_content");

            start.setText(start_content);
            destination.setText(end_content);

            Log.d("s_lati",""+start_latitude);
            Log.d("s_long",""+start_longitude);
            Log.d("e_lati",""+end_latitude);
            Log.d("e_long",""+end_longitude);
        }
        else{
            end_latitude = Double.parseDouble(intent.getExtras().getString("e_latitude")); /*String형*/
            end_longitude = Double.parseDouble(intent.getExtras().getString("e_longitude")); /*String형*/
            start_content = "현재위치";
            end_content = intent.getExtras().getString("e_content");

            start.setText(start_content);
            destination.setText(end_content);

            if ( Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                        0 );
            }
            else {
                startLocationService();
            }
        }

        handler = new Handler();


        //프래그먼트는 뷰와 다르게 context를 매개변수로 넣어줄 필요가 없다.
        fragment1 = new LoadFragment();
        fragment2 = new TransportFragment();

        ImageView load = (ImageView)findViewById(R.id.load);
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putDouble("start_lati", start_latitude);
                bundle.putDouble("start_logi", start_longitude);
                bundle.putDouble("end_lati", end_latitude);
                bundle.putDouble("end_logi", end_longitude);
                fragment1.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1).commit();

            }
        });

        // 클릭시 bus fragment로 이동
        ImageView trans = (ImageView)findViewById(R.id.trans);
        trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(0);
                handler.removeMessages(0);

                Bundle bundle = new Bundle();
                bundle.putString("start_lati", String.valueOf(start_latitude));
                bundle.putString("start_logi", String.valueOf(start_longitude));
                bundle.putString("end_lati", String.valueOf(end_latitude));
                bundle.putString("end_logi", String.valueOf(end_longitude));
                fragment2.setArguments(bundle);

                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment2).commit();
            }
        });
    }

    public void startLocationService() {
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("current","116");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Log.d("current","126");
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, gpsListener);
        Log.d("current","128");
    }

    private final LocationListener gpsListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            start_latitude = location.getLatitude();
            start_longitude = location.getLongitude();
            Log.d("s_latitude : "+start_latitude,"s_longitude : "+start_longitude); //여기서는 받아온다
            stopLocationService();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("error","1");

        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d("error","2");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("error","3");
        }
    };

    public void stopLocationService(){
        manager.removeUpdates(gpsListener);
    }

}