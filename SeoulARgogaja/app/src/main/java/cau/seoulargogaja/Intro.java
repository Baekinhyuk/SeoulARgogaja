package cau.seoulargogaja;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cau.seoulargogaja.data.MainState;
import cau.seoulargogaja.data.PlanDAO;
import cau.seoulargogaja.data.PlanDTO;
import cau.seoulargogaja.data.PlanListDAO;
import cau.seoulargogaja.data.PlanListDTO;
import cau.seoulargogaja.data.SpotDAO;
import cau.seoulargogaja.data.SpotDTO;
import cau.seoulargogaja.data.SpotParser;

public class Intro extends AppCompatActivity{

    PlanDAO dao;
    PlanListDAO listdao;
    Activity activity = this;
    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Handler h= new Handler();
        dao = new PlanDAO(activity);
        listdao = new PlanListDAO(activity);
        h.postDelayed(new Runnable(){
            public void run(){
                //앱 최초 실행 시 db 생성
                SharedPreferences pref = activity.getSharedPreferences("isFirst", Activity.MODE_PRIVATE);
                boolean first = pref.getBoolean("isFirst", false);
                Log.d("tour~", Boolean.toString(first));
                if(first==false) {
                    Log.d("tour", "[first] 첫 실행...");
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("isFirst", true);
                    editor.commit();
                    dao.createTable();
                    listdao.createTable();
                    PlanListDTO plddto = new PlanListDTO();

                    // 주의!! planlist insert시에는 오늘 날짜 입력하도록
                    plddto.setStartDate(getTime());
                    plddto.setEnddate(getTime());
                    listdao.insert(plddto);

                    SpotParser parser = new SpotParser();
                    try {
                        parser.start();
                        parser.join(); // 서버 xml파일 파서

                        SpotDAO dao = new SpotDAO(activity); // db 생성
                        dao.createTable();
                        ArrayList<SpotDTO> spotlist;
                        spotlist = parser.getList();
                        dao.setData(spotlist);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                /* planlist test
                ArrayList<PlanListDTO> a = listdao.selectAll();
        try {
            PlanListDTO pldto = a.get(0);
            Log.d("과연 최초 생성 id", pldto.getId()+"");
            listdao.insert(new PlanListDTO());
            pldto = listdao.selectAll().get(1);
            Log.d("과연 두번째 id", pldto.getId()+"");
            listdao.delete(pldto.getId());
            listdao.insert(new PlanListDTO());
            a = listdao.selectAll();
            pldto = a.get(1);
            Log.d("삭제하고 생성한 두번째 id", pldto.getId()+"");
            pldto.setBudget(10000);
            listdao.update(pldto);
            a = listdao.selectAll();
            pldto = a.get(1);
            Log.d("제대로 update?", pldto.getBudget()+" id = "+pldto.getId());

        }catch(Exception e){
            Log.d("planlist", "추가안됫는데??");
        }
        */
                Intent i = new Intent (Intro.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        },1000);
    }
    protected void onPause(){
        super.onPause();
        finish();
    }

    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

}
