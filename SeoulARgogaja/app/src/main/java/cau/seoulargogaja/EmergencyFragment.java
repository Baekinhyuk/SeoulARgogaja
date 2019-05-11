package cau.seoulargogaja;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import cau.seoulargogaja.adapter.HospitalAdapter;
import cau.seoulargogaja.adapter.PoliceAdapter;

import cau.seoulargogaja.adapter.SingerAdapter;
import cau.seoulargogaja.data.DistSpot;
import cau.seoulargogaja.data.HospitalDAO;
import cau.seoulargogaja.data.HospitalDTO;
import cau.seoulargogaja.data.PoliceDAO;
import cau.seoulargogaja.data.PoliceDTO;
import cau.seoulargogaja.data.SpotDAO;
import cau.seoulargogaja.data.SpotDTO;
import cau.seoulargogaja.data.ToiletDAO;
import cau.seoulargogaja.data.ToiletDTO;

public class EmergencyFragment extends Fragment implements OnMapReadyCallback {

    double latitude, longitude;

    private FragmentActivity myContext;

    private GoogleMap map;
    LocationManager manger;
    MyLocationListener listener;
    ArrayList<DistSpot> ds = new ArrayList<DistSpot>();
    ArrayList<DistSpot> hds = new ArrayList<DistSpot>();
    ArrayList<DistSpot> tds = new ArrayList<DistSpot>();


    int option = 0; //0 = 처음 1 = 경찰서 2 = 응급실 3 = 화장실
    boolean isShow;
    ListView listView;
    PoliceAdapter adapter;
    PoliceDAO dao;
    HospitalDAO hdao;
    ToiletDAO tdao;
    ArrayList<PoliceDTO> list;
    ArrayList<PoliceDTO> smallList = new ArrayList<PoliceDTO>();
    ArrayList<HospitalDTO> hlist;
    ArrayList<HospitalDTO> hsmallList = new ArrayList<HospitalDTO>();
    ArrayList<ToiletDTO> tlist;
    ArrayList<ToiletDTO> tsmallList = new ArrayList<ToiletDTO>();


    private static final String[] LOCATION_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_emergency, container, false);



    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!canAccessLocation()) {
            requestPermissions(LOCATION_PERMS, 1340);
        }

        dao = new PoliceDAO(this.getActivity());
        list = dao.selectAll();
        hdao = new HospitalDAO(this.getActivity());
        hlist = hdao.selectAll();
        tdao = new ToiletDAO(this.getActivity());
        tlist = tdao.selectAll();


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        manger = (LocationManager) myContext.getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        listView = (ListView) myContext.findViewById(R.id.searchListView);   //리스트뷰는 껍데기
        adapter = new PoliceAdapter(myContext.getApplicationContext()); //어댑터 생성

        mapFragment.getMapAsync(this);

        Button btn1 = (Button)view.findViewById(R.id.police);
        //이벤트
        btn1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(option != 1) {
                    clearList();
                    option = 1;
                    onMapReady(map);
                }
            }
        });

        Button btn2 = (Button)view.findViewById(R.id.hospital);

        btn2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(option != 2) {
                    clearList();
                    option = 2;
                    onMapReady(map);
                }
            }
        });

        Button btn3 = (Button)view.findViewById(R.id.toilet);

        btn3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(option != 3) {
                    clearList();
                    option = 3;
                    onMapReady(map);
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();

        requestMyLocation();


    }

    @Override
    public void onPause() {
        super.onPause();


        if (ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(false);

        if (manger != null) {
            manger.removeUpdates(listener);
        }


    }

    public void requestMyLocation() {

        long minTime = 10000; //10초마다 업데이트
        //long minTime = 10000; //10초마다 업데이트
        float minDistance = 0; //항상 업데이트(최소거리 0)


        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        manger.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, listener);

        manger.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, listener);

        Location lastLocation = manger.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        if (lastLocation != null) {
            latitude = lastLocation.getLatitude();
            longitude = lastLocation.getLongitude();

            Toast.makeText(this.getActivity(), "가장 최근의 내위치 : " + latitude + "/" + longitude, Toast.LENGTH_SHORT).show();

        }
    }

    private boolean canAccessLocation() {
        return (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    private boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED == myContext.checkSelfPermission(perm));
    }

    private void showCurrentMap(Double latitude, Double longitude) {
        LatLng curPoint = new LatLng(latitude, longitude);   //지도상의 하나의 위치값

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);  //일반 지도 유형

       /* String[] addrName = { "노량진2동 주민센터", "KBS별관", "공덕역", "신촌역", "경복궁" };
        String[] addrXY = { "37.5082030,126.9373670", "37.5188158,126.9292650", "37.5435594,126.9519426",
                "37.5552192,126.9368460", "37.5788408,126.9770162" };

        StringTokenizer token;*/
        if (isShow) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 13)); //현재 위치를 지도의 중심으로 표시  (13이면 약 5KM 반경)
            onMapReady(map);
        }
        isShow = false;

        Log.d("hun", "내위치 : " + latitude + "/" + longitude);


    }

    @Override
    public void onMapReady(GoogleMap googlemap) {        //거리내에 있는 리스트를 맵과 리스트뷰에 각각 장착
        map = googlemap;
        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LatLng current = new LatLng(latitude, longitude);
        map.addMarker(new MarkerOptions().position(current).title("현재위치").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(current, 13.0f)); //현재 위치


        if(option == 0) {

            //list = LoadActivity.list;   //데이터집합을 받음.

            Log.d("toilet", "search 크기 : " + tlist.size());
            //Log.d("hospital", "search 크기 : " + hlist.size());


            for (int i = 0, j = 0; i < list.size(); i++) {
                PoliceDTO dto = list.get(i);
                double x = Double.parseDouble(dto.getLatitude());
                double y = Double.parseDouble(dto.getLongitude());


                double vector = VectorCalc.distance(latitude, longitude, x, y, "k");
                Log.d("hun", dto.getName() + "거리 : " + vector);
                ds.add(new DistSpot(vector, i, dto));
            }

            for (int i = 0, j = 0; i < hlist.size(); i++) {
                HospitalDTO dto = hlist.get(i);
                double x = Double.parseDouble(dto.getLatitude());
                double y = Double.parseDouble(dto.getLongitude());


                double vector = VectorCalc.distance(latitude, longitude, x, y, "k");
                Log.d("hun", dto.getName() + "거리 : " + vector);
                hds.add(new DistSpot(vector, i, dto));
            }

            for (int i = 0, j = 0; i < tlist.size(); i++) {
                ToiletDTO dto = tlist.get(i);
                double x = Double.parseDouble(dto.getLatitude());
                double y = Double.parseDouble(dto.getLongitude());


                double vector = VectorCalc.distance(latitude, longitude, x, y, "k");
                Log.d("hun", dto.getName() + "거리 : " + vector);
                tds.add(new DistSpot(vector, i, dto));
            }

            ds.sort(Comparator.naturalOrder());
            hds.sort(Comparator.naturalOrder());
            tds.sort(Comparator.naturalOrder());

            for (int i = 0; i < 5; i++) { // 5개만

                MarkerOptions marker = new MarkerOptions();
                ToiletDTO dto = tlist.get(tds.get(i).getId());
                double x = Double.parseDouble(dto.getLatitude());
                double y = Double.parseDouble(dto.getLongitude());

                marker.position(new LatLng(x, y));
                //37.7958188,127.7760805
                marker.title(dto.getName());    //이름
                marker.draggable(true);
                //marker.icon(BitmapDescriptorFactory.fromResource(imageArray[j++]));

                map.addMarker(marker);
                adapter.addItem(tds.get(i));
                tsmallList.add(dto);

            }
            option = 3;
        }else if(option == 1) {
            for (int i = 0; i < 5; i++) { // 5개만

                MarkerOptions marker = new MarkerOptions();
                PoliceDTO dto = list.get(ds.get(i).getId());
                double x = Double.parseDouble(dto.getLatitude());
                double y = Double.parseDouble(dto.getLongitude());

                marker.position(new LatLng(x, y));
                //37.7958188,127.7760805
                marker.title(dto.getName());    //이름
                marker.draggable(true);
                //marker.icon(BitmapDescriptorFactory.fromResource(imageArray[j++]));

                map.addMarker(marker);
                adapter.addItem(ds.get(i));
                smallList.add(dto);

            }
        }else if(option == 2){
            for (int i = 0; i < 5; i++) { // 5개만

                MarkerOptions marker = new MarkerOptions();
                HospitalDTO dto = hlist.get(hds.get(i).getId());
                double x = Double.parseDouble(dto.getLatitude());
                double y = Double.parseDouble(dto.getLongitude());

                marker.position(new LatLng(x, y));
                //37.7958188,127.7760805
                marker.title(dto.getName());    //이름
                marker.draggable(true);
                //marker.icon(BitmapDescriptorFactory.fromResource(imageArray[j++]));

                map.addMarker(marker);
                adapter.addItem(hds.get(i));
                hsmallList.add(dto);

            }

        }else if(option == 3){
            for (int i = 0; i < 5; i++) { // 5개만

                MarkerOptions marker = new MarkerOptions();
                ToiletDTO dto = tlist.get(tds.get(i).getId());
                double x = Double.parseDouble(dto.getLatitude());
                double y = Double.parseDouble(dto.getLongitude());

                marker.position(new LatLng(x, y));
                //37.7958188,127.7760805
                marker.title(dto.getName());    //이름
                marker.draggable(true);
                //marker.icon(BitmapDescriptorFactory.fromResource(imageArray[j++]));

                map.addMarker(marker);
                adapter.addItem(tds.get(i));
                tsmallList.add(dto);

            }

        }



        listView.setAdapter(adapter);


        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {        //marker 이벤트처리
            @Override
            public void onInfoWindowClick(Marker marker) {
                // 길찾기 및 전화걸기 화면
                /*
                Intent intent = new Intent(myContext.getApplicationContext(), DetailActivity.class);
                String name = marker.getTitle();
                SpotDTO tour = new SpotDTO();
                for (int i = 0, j = 0; i < smallList.size(); i++) {
                    tour = smallList.get(i);
                    if(tour.getName().equals(name)){
                        break;
                    }
                }
                intent.putExtra("id", tour.getId());
                intent.putExtra("name", tour.getName());
                intent.putExtra("theme", tour.getTheme());
                intent.putExtra("area", tour.getArea());
                intent.putExtra("latitude", tour.getLatitude());
                intent.putExtra("longitude", tour.getLongitude());
                intent.putExtra("address", tour.getAddress());
                intent.putExtra("phone", tour.getPhone());
                intent.putExtra("web", tour.getWeb());
                intent.putExtra("description", tour.getDescription());

                startActivityForResult(intent, 2001);
                */
            }
        });


        listView.setOnItemClickListener(new ListView.OnItemClickListener() {    //리스트뷰 이벤트처리
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(ListActivity.this, "눌렀따", Toast.LENGTH_SHORT).show();
                // 여기에 누르면 위치 이동
                // 길찾기 및 전화걸기 화면
                /*
                Intent intent = new Intent(myContext.getApplicationContext(), DetailActivity.class);

                SpotDTO tour = (SpotDTO) parent.getAdapter().getItem(position);
                intent.putExtra("id", tour.getId());
                intent.putExtra("name", tour.getName());
                intent.putExtra("theme", tour.getTheme());
                intent.putExtra("area", tour.getArea());
                intent.putExtra("latitude", tour.getLatitude());
                intent.putExtra("longitude", tour.getLongitude());
                intent.putExtra("address", tour.getAddress());
                intent.putExtra("phone", tour.getPhone());
                intent.putExtra("web", tour.getWeb());
                intent.putExtra("description", tour.getDescription());

                startActivityForResult(intent, 2001);
                */
            }


        });


    }

    public void clearList() {
        map.clear();
        adapter.clearData();
    /*    map.addMarker(marker);
        adapter.addItem(dto);
        listView.setAdapter(adapter);*/
    }

class MyLocationListener implements LocationListener {  //
    @Override
    public void onLocationChanged(Location location) {
        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();


        showCurrentMap(latitude, longitude);

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
}



}


