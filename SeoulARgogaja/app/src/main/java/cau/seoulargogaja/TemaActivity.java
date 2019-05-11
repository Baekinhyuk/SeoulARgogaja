package cau.seoulargogaja;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import cau.seoulargogaja.adapter.ViewPagerAdapter;

public class TemaActivity extends AppCompatActivity {

    private TabLayout tabLayout;

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tema);

        tabLayout = findViewById(R.id.tab);

        viewPager = findViewById(R.id.viewpager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new Place_fragment1(),"역사");
        adapter.AddFragment(new Place_fragment2(), "쇼핑");
        adapter.AddFragment(new Place_fragment3(), "맛집");
        adapter.AddFragment(new Place_fragment4(), "카페");
        adapter.AddFragment(new Place_fragment5(), "번화가");
        adapter.AddFragment(new Place_fragment6(), "랜드마크");
        //adapter setup
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


//        viewPager.setAdapter(adapter);
//
        TabLayout tab = findViewById(R.id.tab);
//        //공간이 부족하면 스크롤 해서 넘어갈 수 있도록 해주는 기능
        tab.setTabMode(TabLayout.MODE_SCROLLABLE);
//
//        tabLayout.setupWithViewPager(viewPager);
    }


}
