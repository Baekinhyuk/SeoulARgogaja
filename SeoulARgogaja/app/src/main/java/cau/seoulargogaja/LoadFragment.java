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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import com.mapbox.mapboxsdk.maps.SupportMapFragment;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class LoadFragment extends Fragment {



    // private MapView mapView;
    private static final String TAG = "DirectionsActivity";
    private MapView mapView;
    private MapboxMap map;
    private DirectionsRoute currentRoute;
    private MapboxDirections client;
    double destination_long; // longitude
    double destination_lati; // latitude
    double start_latitude;
    double start_longitude;
    double end_latitude;
    double end_longitude;

    LocationManager manager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (View)inflater.inflate(R.layout.fragment_load, container, false);

        if(getArguments() != null){
            start_latitude = getArguments().getDouble("start_lati");
            start_longitude = getArguments().getDouble("start_logi");
            end_latitude = getArguments().getDouble("end_lati");
            end_longitude = getArguments().getDouble("end_logi");
        }


        // 맵박스 사용하기 위한 접근 토큰 지정
        Mapbox mapbox = Mapbox.getInstance(getActivity(), getString(R.string.access_token));

        // 아래 함수로 통해 목적지 주소값을 위도 경도 값으로 변경
        //getPointFromGeoCoder("서울특별시 송파구 방이동 112-1");

        Button btn = (Button) view.findViewById(R.id.btn);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //거리
                //Toast.makeText(MainActivity.this, "Distance"+currentRoute.distance(), Toast.LENGTH_SHORT).show();
                if ( Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions( getActivity(), new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                            0 );
                }
                else {
                    startLocationService();
                }
            }
        };

        // Setup the MapView
        mapView = view.findViewById(R.id.mapView);
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
        btn.setOnClickListener(listener);
        return view;
    }

    public void startLocationService() {
        manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            final double latitude = location.getLatitude();
            final double longitude = location.getLongitude();

            final Point start = Point.fromLngLat(start_longitude, start_latitude);
            final Point end = Point.fromLngLat(end_longitude, end_latitude);

            Log.d("latitude"," : "+latitude);
            Log.d("longitude"," : "+longitude);

            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(MapboxMap mapboxMap) {
                    map = mapboxMap;

                    // 카메라 위치 고정(내 gps 위치로 임의지정)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            // 카메라는 반대의 값으로 적어줄 것
                            // 뒤에 숫자 15은 카메라 확대 배수이다( 15가 적당 )
                            new LatLng(start_latitude, start_longitude), 15));

                    // Add origin and destination to the map
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(start_latitude, start_longitude))
                            // 타이틀은 상호명 건물명, snippet은 설명 그에 대한 설명이다
                            // 출발지
                            .title("출발지")
                            .snippet("소우2"));
                    map.addMarker(new MarkerOptions()
                            // 목적지
                            .position(new LatLng(end_latitude, end_longitude))
                            .title("중간")
                            .snippet("가비2"));
                    // Get route from API
                    getRoute(start, end);
                }
            });
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
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
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
        Geocoder geocoder = new Geocoder(getActivity());
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