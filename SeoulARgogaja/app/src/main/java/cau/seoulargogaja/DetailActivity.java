package cau.seoulargogaja;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



import cau.seoulargogaja.data.ServerGetImage;
import cau.seoulargogaja.data.SpotDTO;

public class DetailActivity extends AppCompatActivity {

    TextView nameTextView,themeTextView,areaTextView,noteTextView,timeTextView,phoneTextView,trafficTextView;


    ImageView imageView;

    String name,theme,area,latitude,longitude,address,phone,web,description;
    int id;
    SpotDTO add_dto;
    double x,y;


    Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);


        nameTextView= (TextView)findViewById(R.id.textName);
        themeTextView= (TextView)findViewById(R.id.textTheme);
        areaTextView= (TextView)findViewById(R.id.textArea);
        noteTextView= (TextView)findViewById(R.id.textNote);
        phoneTextView= (TextView)findViewById(R.id.textPhone);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        theme = intent.getStringExtra("theme");
        area = intent.getStringExtra("area");
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");
        address = intent.getStringExtra("address");
        phone = intent.getStringExtra("phone");
        web = intent.getStringExtra("web");
        description = intent.getStringExtra("description");

        String sid = intent.getStringExtra("id");
        id = Integer.parseInt(sid);
        Log.d("id", id+" detailactivity");
        nameTextView.setText(name); //받아온 이름값을 등록한다.
        themeTextView.setText(theme);  // 테마
        areaTextView.setText(address); // 주소
        phoneTextView.setText(phone);
        noteTextView.setText(description);

        x = Double.parseDouble(latitude);
        y = Double.parseDouble(longitude);


        // String imageUrl = "https://cauteam202.com/image/"+id+".jpg";

        new ServerGetImage((ImageView)findViewById(R.id.detail)).execute(area);

        }



    public void onButton_Call(View v) {         //Call버튼

        String str = phoneTextView.getText().toString();

        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+str));
        startActivity(intent);
    }



    // 여기에 일정 추가하는 코드 작성해야함
    public void onAddButtonClick(View view) {   //추가

        Toast.makeText(this, "추가한 관광지에 추가되었습니다.", Toast.LENGTH_SHORT).show();
    }


    public void onButton_naver(View v) {        //네이버 검색

        Intent intent = new Intent(getApplicationContext(),WebActivity.class);
        String url = "https://m.search.naver.com/search.naver?query="+nameTextView.getText();
        intent.putExtra("url",url);
        startActivityForResult(intent,1500);


    }

   /* public void onClickPlus(View v) {    //추가 버튼
        Toast.makeText(this, "관광지가 추가되었습니다. 마이리스트로 이동하시겠습니까?", Toast.LENGTH_SHORT).show();
        TourDAO dao = new TourDAO(this,sysLanguage);
        dao.updatePoke(Integer.parseInt(add_dto.getNumber()),TourDAO.TOUR_ADD);
    }*/





}

