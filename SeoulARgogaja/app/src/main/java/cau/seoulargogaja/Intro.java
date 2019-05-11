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

import cau.seoulargogaja.data.HospitalDAO;
import cau.seoulargogaja.data.HospitalDTO;
import cau.seoulargogaja.data.HospitalParser;
import cau.seoulargogaja.data.MainState;
import cau.seoulargogaja.data.PlanDAO;
import cau.seoulargogaja.data.PlanDTO;
import cau.seoulargogaja.data.PlanListDAO;
import cau.seoulargogaja.data.PlanListDTO;
import cau.seoulargogaja.data.PoliceDAO;
import cau.seoulargogaja.data.PoliceDTO;
import cau.seoulargogaja.data.PoliceParser;
import cau.seoulargogaja.data.SpotDAO;
import cau.seoulargogaja.data.SpotDTO;
import cau.seoulargogaja.data.SpotParser;
import cau.seoulargogaja.data.ToiletDAO;
import cau.seoulargogaja.data.ToiletDTO;
import cau.seoulargogaja.data.ToiletParser;
import cau.seoulargogaja.data.WalletDAO;

public class Intro extends AppCompatActivity{

    PlanDAO dao;
    PlanListDAO listdao;
    WalletDAO walletDAO;
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
        walletDAO = new WalletDAO(activity);
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
                    walletDAO.createTable();
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

                    PoliceParser Pparser = new PoliceParser();
                    try {
                        Pparser.start();
                        Pparser.join(); // 서버 xml파일 파서

                        PoliceDAO dao = new PoliceDAO(activity); // db 생성
                        dao.createTable();
                        ArrayList<PoliceDTO> policelist;
                        policelist = Pparser.getList();
                        dao.setData(policelist);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }

                    HospitalParser Hparser = new HospitalParser();
                    try {
                        Hparser.start();
                        Hparser.join(); // 서버 xml파일 파서

                        HospitalDAO dao = new HospitalDAO(activity); // db 생성
                        dao.createTable();
                        ArrayList<HospitalDTO> hospitallist;
                        hospitallist = Hparser.getList();
                        dao.setData(hospitallist);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }

                    ToiletParser toiletParser = new ToiletParser();
                    try {
                        toiletParser.start();
                        toiletParser.join(); // 서버 xml파일 파서

                        ToiletDAO dao = new ToiletDAO(activity); // db 생성
                        dao.createTable();
                        ArrayList<ToiletDTO> toiletlist;
                        toiletlist = toiletParser.getList();
                        dao.setData(toiletlist);
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
