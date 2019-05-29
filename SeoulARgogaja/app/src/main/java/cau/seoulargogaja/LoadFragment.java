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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.model.PolylineOptions;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
//import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    double start_latitude;
    double start_longitude;
    double end_latitude;
    double end_longitude;

    private GoogleMap gmap;


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

        Log.d("l_s_lati",""+start_latitude);
        Log.d("l_s_long",""+start_longitude);
        Log.d("l_e_lati",""+end_latitude);
        Log.d("l_e_long",""+end_longitude);


        final Point start = Point.fromLngLat(start_longitude, start_latitude);
        final Point end = Point.fromLngLat(end_longitude, end_latitude);

        // 맵박스 사용하기 위한 접근 토큰 지정
        Mapbox mapbox = Mapbox.getInstance(getActivity(), getString(R.string.access_token));

        // 아래 함수로 통해 목적지 주소값을 위도 경도 값으로 변경
        //getPointFromGeoCoder("서울특별시 송파구 방이동 112-1");

        // Setup the MapView
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gmap = googleMap;
                MarkerOptions makerOptions = new MarkerOptions();
                makerOptions.position(new LatLng(start_latitude, start_longitude)).title("시작점");
                gmap.addMarker(makerOptions);
                makerOptions.position(new LatLng(end_latitude, end_longitude)).title("도착점");
                gmap.addMarker(makerOptions);

                gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(start_latitude, start_longitude),15));
                getRoute(start, end);
            }
        });

        return view;
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
        gmap.addPolyline(new PolylineOptions().add(points).width(8).color(Color.BLACK));


        /*map.addPolyline(new PolylineOptions()
                .add(points)
                .color(Color.parseColor("#009688"))
                .width(5));*/
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

}