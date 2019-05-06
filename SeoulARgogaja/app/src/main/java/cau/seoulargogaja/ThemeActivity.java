package cau.seoulargogaja;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cau.seoulargogaja.adapter.SearchListAdapter;
import cau.seoulargogaja.adapter.SearchPhotoAdapter;
import cau.seoulargogaja.data.SpotDAO;
import cau.seoulargogaja.data.SpotDTO;

public class ThemeActivity extends AppCompatActivity {

    Button button1;
    Button button2;
    Button button3;
    Button button4;
    Button button5;
    Button button6;

    SearchPhotoAdapter searchPhotoAdapter;
    ListView searchListView;
    List<SpotDTO> searchedList = new ArrayList<>();
    List<SpotDTO> tourList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);
        tourList = getTourList();
        button1 = findViewById(R.id.shopping_btn);
        button2 = findViewById(R.id.hisotry_btn);
        button3 = findViewById(R.id.cafe_btn);
        button4 = findViewById(R.id.food_btn);
        button5 = findViewById(R.id.road_btn);
        button6 = findViewById(R.id.landmark_btn);
        searchListView = (ListView) findViewById(R.id.search_list);

        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                submitQuery(1);
            }
        });

        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                submitQuery(2);
            }
        });


        button3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                submitQuery(3);
            }
        });

        button4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                submitQuery(4);
            }
        });

        button5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                submitQuery(5);
            }
        });

        button6.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                submitQuery(6);
            }
        });

    }

    private void submitQuery(int i) {
        searchedList = search(i);
        Log.d("theme",searchedList.get(0).getName());

        searchPhotoAdapter = new SearchPhotoAdapter(getApplicationContext(), searchedList);
        searchListView.setAdapter(searchPhotoAdapter);
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                SpotDTO tour = searchedList.get(position);
                /*Utils.toast(getApplication(), "Show Detail : " + tour.getName());*/
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
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

                startActivity(intent);
            }
        });

    }

    public List<SpotDTO> getTourList() {
        SpotDAO dao = new SpotDAO(this);
        return dao.selectAll();
    }


    public List<SpotDTO> search(int i) {
        List<SpotDTO> result = new ArrayList<>();


//        Utils.log("# SEARCH BY KEYWORD: " + query + " in " + tourList.size());
        for (SpotDTO tour : tourList) {

            if(i == 1 && tour.getTheme().equals("쇼핑")){
                result.add(tour);
            }else if(i == 2 && tour.getTheme().equals("역사")){
                result.add(tour);
            }else if(i == 3 && tour.getTheme().equals("카페")){
                result.add(tour);
            }else if(i == 4 && tour.getTheme().equals("식당")){
                result.add(tour);
            }else if(i == 5 && tour.getTheme().equals("번화가")){
                result.add(tour);
            }else if(i == 6 && tour.getTheme().equals("랜드마크")){
                result.add(tour);
            }

        }
        Log.d("THEME","# SEARCH RESULT " + result.size() + "개");
        return result;
    }
}
