package cau.seoulargogaja;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SearchFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_search, container, false);

        Button btn1 = (Button)rootView.findViewById(R.id.button1);
        //이벤트
        btn1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });
        Button btn2 = (Button)rootView.findViewById(R.id.button2);
        //이벤트
        btn1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });
        Button btn3 = (Button)rootView.findViewById(R.id.button3);
        //이벤트
        btn1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    /*
    public void onButtonClick1(View v) {    //상세 검색
        Intent intent = new Intent(getActivity(), SearchActivity.class);

        startActivity(intent);
    }

    public void onButtonClick2(View v) {    //상세 검색
        Intent intent = new Intent(getActivity(), SearchActivity.class);

        startActivity(intent);
    }

    public void onButtonClick3(View v) {    //상세 검색
        Intent intent = new Intent(getActivity(), SearchActivity.class);

        startActivity(intent);
    }*/
}