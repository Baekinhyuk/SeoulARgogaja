package cau.seoulargogaja;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cau.seoulargogaja.adapter.Place_ExampleAdapter;
import cau.seoulargogaja.data.SpotDAO;
import cau.seoulargogaja.data.SpotDTO;

public class Place_fragment6 extends Fragment {

    private RecyclerView mRecyclerView;
    private Place_ExampleAdapter mExampleAdapter;
    //private ArrayList<> mExampleList;
    ArrayList<SpotDTO> searchedList = new ArrayList<>();
    List<SpotDTO> tourList;


    public Place_fragment6() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.place_fragment1, null);
        tourList = getTourList();
        mRecyclerView = v.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        submitQuery(6);


        return v;
    }


    private void submitQuery(int i) {
        searchedList = search(i);
        Log.d("theme",searchedList.get(0).getName());

        mExampleAdapter = new Place_ExampleAdapter(getActivity(), searchedList);

        mRecyclerView.setAdapter(mExampleAdapter);



    }

    public ArrayList<SpotDTO> getTourList() {
        SpotDAO dao = new SpotDAO(getActivity());
        return dao.selectAll();
    }


    public ArrayList<SpotDTO> search(int i) {
        ArrayList<SpotDTO> result = new ArrayList<>();


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
