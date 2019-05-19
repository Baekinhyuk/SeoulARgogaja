package cau.seoulargogaja;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import cau.seoulargogaja.data.IdDAO;
import cau.seoulargogaja.data.MainState;
import cau.seoulargogaja.data.PlanDAO;
import cau.seoulargogaja.data.PlanDTO;
import cau.seoulargogaja.data.PlanListDAO;
import cau.seoulargogaja.data.PlanListDTO;
import cau.seoulargogaja.data.WalletDAO;
import cau.seoulargogaja.data.WalletDTO;

public class QRreadActivity extends AppCompatActivity {
    //view Objects
    private Button buttonScan;
    private TextView textViewName, textViewAddress, textViewResult;

    //qr code scanner object
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrread);

        //View Objects
        buttonScan = (Button) findViewById(R.id.buttonScan);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewAddress = (TextView) findViewById(R.id.textViewAddress);
        textViewResult = (TextView)  findViewById(R.id.textViewResult);

        //intializing scan object
        qrScan = new IntentIntegrator(this);

        //button onClick
        buttonScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //scan option
                qrScan.setPrompt("Scanning...");
                //qrScan.setOrientationLocked(false);
                qrScan.initiateScan();
            }
        });
    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            //qrcode 가 없으면
            if (result.getContents() == null) {
                Toast.makeText(QRreadActivity.this, "취소!", Toast.LENGTH_SHORT).show();
            } else {
                //qrcode 결과가 있으면
                Toast.makeText(QRreadActivity.this, "스캔완료!", Toast.LENGTH_SHORT).show();
                Toast.makeText(QRreadActivity.this, result.getContents(), Toast.LENGTH_SHORT).show();
                try {
                    Phprequest request = new Phprequest();
                    String jsonresult = request.readURL(new URL(result.getContents().trim()));
                    Toast.makeText(QRreadActivity.this, jsonresult, Toast.LENGTH_SHORT).show();
                    write_data(jsonresult);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void write_data(String result) {

        MainState mainState;
        PlanListDAO listdao = new PlanListDAO(this);
        IdDAO iddao = new IdDAO(this);
        PlanDAO plandao = new PlanDAO(this);
        WalletDAO walletdao = new WalletDAO(this);
        int planlistid_json = 0;

        String TAG_JSON_PLANLIST="planlist";
        String PLANLIST_name = "name";
        String PLANLIST_startdate = "startdate";
        String PLANLIST_enddate = "enddate";
        String PLANLIST_budget = "budget";
        String PLANLIST_code = "code";

        String TAG_JSON_PLAN="plan";
        String PLAN_content = "content";
        String PLAN_date = "date";
        String PLAN_spotID = "spotID";
        String PLAN_stamp = "stamp";
        String PLAN_latitude = "latitude";
        String PLAN_longitude = "longitude";
        String PLAN_memo = "memo";
        String PLAN_order_ = "order_";
        String PLAN_datatype = "datatype";

        String TAG_JSON_WALLET="wallet";
        String WALLET_date = "date";
        String WALLET_detail = "detail";
        String WALLET_expend = "expend";
        String WALLET_memo = "memo";
        String WALLET_datatype = "datatype";
        String WALLET_main_image = "main_image";
        String WALLET_sub_image = "sub_image";
        String WALLET_color_type = "color_type";
        String WALLET_order_ = "order_";


        try {

            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray_planlist = jsonObject.getJSONArray(TAG_JSON_PLANLIST);
            JSONArray jsonArray_plan = jsonObject.getJSONArray(TAG_JSON_PLAN);
            JSONArray jsonArray_wallet = jsonObject.getJSONArray(TAG_JSON_WALLET);

            for(int i=0;i<jsonArray_planlist.length();i++){

                JSONObject item = jsonArray_planlist.getJSONObject(i);

                String pl_name = item.getString(PLANLIST_name);
                String pl_startdate = item.getString(PLANLIST_startdate);
                String pl_enddate = item.getString(PLANLIST_enddate);
                String pl_budget = item.getString(PLANLIST_budget);
                String pl_code = item.getString(PLANLIST_code);

                PlanListDTO newlist = new PlanListDTO();
                newlist.setName(pl_name);
                newlist.setStartDate(pl_startdate);
                newlist.setEnddate(pl_enddate);
                newlist.setBudget(Integer.parseInt(pl_budget));
                listdao.insert(newlist);
                int newlistid = listdao.last_id();
                newlist.setId(newlistid);
                iddao.update(newlistid);
                mainState = new MainState(newlist);
                planlistid_json = mainState.getplanlistId();
            }

            for(int i=0;i<jsonArray_plan.length();i++){

                JSONObject item = jsonArray_plan.getJSONObject(i);

                String p_content = item.getString(PLAN_content);
                String p_date = item.getString(PLAN_date);
                String p_spotID = item.getString(PLAN_spotID);
                String p_stamp = item.getString(PLAN_stamp);
                String p_latitude = item.getString(PLAN_latitude);
                String p_longitude = item.getString(PLAN_longitude);
                String p_memo = item.getString(PLAN_memo);
                String p_order_ = item.getString(PLAN_order_);
                String p_datatype = item.getString(PLAN_datatype);

                PlanDTO plan = new PlanDTO();
                plan.setContent(p_content);
                plan.setdate(p_date);
                plan.setSpotID(Integer.parseInt(p_spotID));
                plan.setStamp(Integer.parseInt(p_stamp));
                plan.setLatitude(p_latitude);
                plan.setLongitude(p_longitude);
                plan.setmemo(p_memo);
                plan.setOrder(Integer.parseInt(p_order_));
                plan.setdatatype(Integer.parseInt(p_datatype));
                plan.setplanlistid(planlistid_json);
                plandao.insert(plan);
            }


            for(int i=0;i<jsonArray_wallet.length();i++){

                JSONObject item = jsonArray_wallet.getJSONObject(i);

                String w_date = item.getString(WALLET_date);
                String w_detail = item.getString(WALLET_detail);
                String w_expend = item.getString(WALLET_expend);
                String w_memo = item.getString(WALLET_memo);
                String w_datatype = item.getString(WALLET_datatype);
                String w_main_image = item.getString(WALLET_main_image);
                String w_sub_image = item.getString(WALLET_sub_image);
                String w_color_type = item.getString(WALLET_color_type);
                String w_order_ = item.getString(WALLET_order_);


                WalletDTO wallet = new WalletDTO();
                wallet.setdate(w_date);
                wallet.setdetail(w_detail);
                wallet.setexpend(Integer.parseInt(w_expend));
                wallet.setmemo(w_memo);
                wallet.setdatatype(Integer.parseInt(w_datatype));
                wallet.setmain_image(Integer.parseInt(w_main_image));
                wallet.setsub_image(Integer.parseInt(w_sub_image));
                wallet.setcolor_type(Integer.parseInt(w_color_type));
                wallet.setOrder(Integer.parseInt(w_order_));
                wallet.setplanlistid(planlistid_json);
                walletdao.insert(wallet);
            }
            Toast.makeText(QRreadActivity.this, "성공적으로 여행일정을 받아왔습니다.", Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
