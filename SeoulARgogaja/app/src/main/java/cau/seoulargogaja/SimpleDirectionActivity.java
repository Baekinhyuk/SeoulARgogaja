package cau.seoulargogaja;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.core.constants.Constants.PRECISION_6;

public class SimpleDirectionActivity extends AppCompatActivity {
    // private MapView mapView;
    private static final String TAG = "DirectionsActivity";
    private MapView mapView;
    private MapboxMap map;
    private DirectionsRoute currentRoute;
    private MapboxDirections client;
    double destination_long; // longitude
    double destination_lati; // latitude

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_direction);

        // 맵박스 사용하기 위한 접근 토큰 지정
        Mapbox.getInstance(this, getString(R.string.access_token));

        // 아래 함수로 통해 목적지 주소값을 위도 경도 값으로 변경
        //getPointFromGeoCoder("서울특별시 송파구 방이동 112-1");

        // 내 gps 위치 (임의지정)
        final Point origin = Point.fromLngLat(126.947721, 37.503200);

        // 도착지 gps 위치
        final Point destination = Point.fromLngLat(126.962779, 37.508807);

        Button btn = (Button) findViewById(R.id.btn);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //거리
                //Toast.makeText(MainActivity.this, "Distance"+currentRoute.distance(), Toast.LENGTH_SHORT).show();
                if ( Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions( SimpleDirectionActivity.this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                            0 );
                }
                else {
                    startLocationService();
                }
            }
        };

        // Setup the MapView
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;

                // 카메라 위치 고정(내 gps 위치로 임의지정)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        // 카메라는 반대의 값으로 적어줄 것
                        // 뒤에 숫자 15은 카메라 확대 배수이다( 15가 적당 )
                        new LatLng(37.500342, 126.867769), 15));
            }
        });
        Log.d("현재위치 받안오냐", "101");
        btn.setOnClickListener(listener);
        Log.d("현재위치 받안오냐", "103");
    }

    public void startLocationService() {
        long mintime = 1000;
        float mindistance = 0;
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GPSListener gpsListener = new GPSListener();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("현재위치 받안오냐","114");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, mintime, mindistance, gpsListener);
    }

    private class GPSListener implements LocationListener {

        public void onLocationChanged(Location location) {
            final double latitude = location.getLatitude();
            final double longitude = location.getLongitude();
            final Point start = Point.fromLngLat(longitude, latitude);
            final Point end = Point.fromLngLat(126.947721, 37.503200);
            final Point origin = Point.fromLngLat(126.957621, 37.52300);

            Log.d("현재위치 받안오냐","latitude =" + latitude + "longitude =" + longitude );

            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(MapboxMap mapboxMap) {
                    map = mapboxMap;

                    // 카메라 위치 고정(내 gps 위치로 임의지정)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            // 카메라는 반대의 값으로 적어줄 것
                            // 뒤에 숫자 15은 카메라 확대 배수이다( 15가 적당 )
                            new LatLng(latitude, longitude), 15));

                    // Add origin and destination to the map
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            // 타이틀은 상호명 건물명, snippet은 설명 그에 대한 설명이다
                            // 출발지
                            .title("출발지")
                            .snippet("소우2"));
                    map.addMarker(new MarkerOptions()
                            // 목적지
                            .position(new LatLng(37.503200, 126.947721))
                            .title("중간")
                            .snippet("가비2"));
                    // Get route from API
                    getRoute(start, end);
                }
            });
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    }

    private void getRoute(Point origin, Point destination) {
        client = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_CYCLING)
                .accessToken(getString(R.string.access_token))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {

            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                System.out.println(call.request().url().toString());

                // You can get the generic HTTP info about the response
                if (response.body() == null) {
                    return;
                } else if (response.body().routes().size() < 1) {
                    return;
                }
                // Print some info about the route
                currentRoute = response.body().routes().get(0);
                // Draw the route on the map
                drawRoute(currentRoute);
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {

            }
        });
    }

    private void drawRoute(DirectionsRoute route) {
        // Convert LineString coordinates into LatLng[]
        LineString lineString = LineString.fromPolyline(route.geometry(), PRECISION_6);
        List<Point> coordinates = lineString.coordinates();
        LatLng[] points = new LatLng[coordinates.size()];
        for (int i = 0; i < coordinates.size(); i++) {
            points[i] = new LatLng(
                    coordinates.get(i).latitude(),
                    coordinates.get(i).longitude());
        }

        // Draw Points on MapView

        map.addPolyline(new PolylineOptions()
                .add(points)
                .color(Color.parseColor("#009688"))
                .width(5));
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel the directions API request
        if (client != null) {
            client.cancelCall();
        }
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    // 목적지 주소값을 통해 목적지 위도 경도를 얻어오는 구문

    public void getPointFromGeoCoder(String addr) {
        String destinationAddr = addr;
        Geocoder geocoder = new Geocoder(this);
        List<Address> listAddress = null;
        try {
            listAddress = geocoder.getFromLocationName(destinationAddr, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        destination_long = listAddress.get(0).getLongitude();
        destination_lati = listAddress.get(0).getLatitude();
        System.out.println(addr + "'s Destination x, y = " + destination_long + ", " + destination_lati);
    }
}