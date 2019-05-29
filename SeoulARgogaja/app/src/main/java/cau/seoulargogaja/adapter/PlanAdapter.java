package cau.seoulargogaja.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.os.Build;
import android.widget.Toast;

import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cau.seoulargogaja.ARActivity;
import cau.seoulargogaja.PlanAdd;
import cau.seoulargogaja.PlanEdit;
import cau.seoulargogaja.R;
import cau.seoulargogaja.SimpleDirectionActivity;
import cau.seoulargogaja.data.PlanDAO;
import cau.seoulargogaja.data.PlanDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlanAdapter extends ArrayAdapter<PlanDTO> {
    private DirectionsRoute currentRoute;
    private MapboxDirections client;

    final int INVALID_ID = -1;
    public interface Listener {
        void onGrab(int position, RelativeLayout row);
    }

    final Listener listener;
    final Map<PlanDTO, Integer> mIdMap = new HashMap<>();
    final Context mContext;


    public PlanAdapter(Context context, List<PlanDTO> list, Listener listener) {
        super(context, 0, list);
        mContext = context;
        this.listener = listener;
        for (int i = 0; i < list.size(); ++i) {
            mIdMap.put(list.get(i), i);
        }
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final Context context = getContext();
        final PlanDTO data = getItem(position);

        if(null == view) {
            if(data.getdatatype() == 1) {
                view = LayoutInflater.from(context).inflate(R.layout.fragment_plan_item, null);
            }
            else if(data.getdatatype() == 0){
                view = LayoutInflater.from(context).inflate(R.layout.fragment_plan_date, null);
            }
            else if(data.getdatatype() == 2){
                view = LayoutInflater.from(context).inflate(R.layout.fragment_plan_plus, null);
            }
        }

        try {
            if (data.getdatatype() == 1) {
                view = LayoutInflater.from(context).inflate(R.layout.fragment_plan_item, null);

                ImageView ar_image = (ImageView) view.findViewById(R.id.ar_image);
                if(data.getStamp() ==1){
                    //change image AR스탬프
                    Log.d("PlanAdapter","stamp");
                    ar_image.setImageResource(R.drawable.stampcheck);
                }

                ar_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent ar_intent = new Intent(context, ARActivity.class);
                        ar_intent.putExtra("latitude", data.getLatitude());
                        ar_intent.putExtra("longitude", data.getLongitude());
                        ar_intent.putExtra("content",data.getContent());
                        ((Activity) mContext).startActivityForResult(ar_intent, position);

                    }
                });

                TextView textView = (TextView) view.findViewById(R.id.nick_name);
                TextView textView_memo = (TextView) view.findViewById(R.id.item_memo);

                textView.setText(data.getContent());
                textView.setPaintFlags(textView.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);

                LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.plan_item_linear);
                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //context.startActivity(new Intent(context, SimpleDirectionActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        Intent intent = new Intent(context, PlanEdit.class); // intent 되는 activty에 알맞은 data 출력

                        intent.putExtra("id", data.getId());
                        intent.putExtra("content", data.getContent());
                        intent.putExtra("date", data.getdate());
                        intent.putExtra("spotID", data.getspotID());
                        intent.putExtra("stamp", data.getStamp());
                        intent.putExtra("latitude", data.getLatitude());
                        intent.putExtra("longitude", data.getLongitude());
                        intent.putExtra("memo", data.getmemo());
                        intent.putExtra("order", data.getOrder());
                        intent.putExtra("datatype", data.getdatatype());
                        intent.putExtra("planlistid", data.getplanlistid());

                        context.startActivity(intent);
                    }
                });

                textView_memo.setText(data.getmemo());
                view.findViewById(R.id.vote_image)
                        .setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                Intent intent = new Intent(context, SimpleDirectionActivity.class);

                                if(position != 0){
                                    final PlanDTO data2 = getItem(position-1);
                                    if(data2.getdatatype() == 1){
                                        intent.putExtra("data2_type", data2.getdatatype());
                                        intent.putExtra("s_latitude", data2.getLatitude());
                                        intent.putExtra("s_longitude", data2.getLongitude());
                                        intent.putExtra("s_content",data2.getContent());

                                        intent.putExtra("e_latitude", data.getLatitude());
                                        intent.putExtra("e_longitude", data.getLongitude());
                                        intent.putExtra("e_content",data.getContent());

                                    }
                                    else{
                                        intent.putExtra("position", position);
                                        intent.putExtra("e_latitude", data.getLatitude());
                                        intent.putExtra("e_longitude", data.getLongitude());
                                        intent.putExtra("e_content",data.getContent());
                                    }
                                }

                                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                return false;
                            }
                        });

                final RelativeLayout row = (RelativeLayout) view.findViewById(R.id.item_list1);
                view.findViewById(R.id.drag_image)
                        .setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                listener.onGrab(position, row);
                                return false;
                            }
                        });
            }
            else if (data.getdatatype() == 0) {
                view = LayoutInflater.from(context).inflate(R.layout.fragment_plan_date, null);
                TextView plandate = (TextView) view.findViewById(R.id.plan_date);

                PlanDAO planDAO = new PlanDAO((Activity)mContext);
                String day = data.getdate();
                String a_year = day.substring(0,4);
                String b_month = day.substring(5,7);
                String c_day = day.substring(8,10);
                String sum_day = a_year + b_month + c_day;

                //test
                planDAO.day_plan(data.getplanlistid(),day);
                planDAO.select_stamp(data.getplanlistid());

                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
                Date date = null;
                try {
                    date = dateFormatter.parse(sum_day);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                String day_kr = "요일";
                switch(calendar.get(Calendar.DAY_OF_WEEK)){
                    case 1:
                        day_kr = "일";
                        break;
                    case 2:
                        day_kr = "월";
                        break;
                    case 3 :
                        day_kr = "화";
                        break;
                    case 4 :
                        day_kr = "수";
                        break;
                    case 5 :
                        day_kr = "목";
                        break;
                    case 6 :
                        day_kr = "금";
                        break;
                    case 7 :
                        day_kr = "토";
                        break;
                    default :
                        System.out.println("그 외의 숫자");
                }
                if(b_month.substring(0,1).equals("0")){
                    b_month = b_month.substring(1,2);
                }
                String last = b_month +"월"+ c_day +"일("+ day_kr+")";
                plandate.setText(last);

                view.findViewById(R.id.optimize)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder alertdialog = new AlertDialog.Builder((Activity)mContext);
                                alertdialog.setMessage("최적화시키겠습니까?");

                                // 확인버튼
                                alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        optimize_(data.getplanlistid(),day,planDAO);
                                    }
                                });
                                // 취소버튼
                                alertdialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                // 메인 다이얼로그 생성
                                AlertDialog alert = alertdialog.create();
                                alert.show();
                            }
                        });
            }
            else if(data.getdatatype() == 2){
                view = LayoutInflater.from(context).inflate(R.layout.fragment_plan_plus, null);
                ImageView addImage = (ImageView) view.findViewById(R.id.plus_plan_image);
                // add 버튼 누르면 plan 추가 화면으로 돌아감
                addImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PlanAdd.class);
                        context.startActivity(intent);
                    }
                });
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("PlanAdapter", "onActivityResult");
        if(resultCode == 1){ // 1 == result 정상
            Log.d("PlanAdapter", "정상 requestcode = "+requestCode);
            PlanDTO item = getItem(requestCode); // requestCode == listview position
            item.setStamp(1);
            PlanDAO dao = new PlanDAO((Activity)mContext);
            dao.update_plan(item);
        }
        Log.d("PlanAdapter", "비정상?");

    }

    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= mIdMap.size()) {
            return INVALID_ID;
        }
        PlanDTO item = getItem(position);

        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }

    public void optimize_(int planlist_id,String day,PlanDAO planDAO){
        ArrayList<PlanDTO> list;
        list = planDAO.day_plan(planlist_id,day);
        int n = list.size();
        double[] latitude = new double[n];
        double[] longitude = new double[n];
        double[][] distance = new double[n][n];
        Distance dist = new Distance();
        dist.setDistance(distance);
        for(int i =0;i<n;i++){
            latitude[i] = Double.parseDouble(list.get(i).getLatitude());
            longitude[i] = Double.parseDouble(list.get(i).getLongitude());
        }
        get_distace(latitude,longitude,dist);
        /*
        try {
            Thread workingThread = new Thread() {
                public void run() {
                    get_distace(latitude,longitude,dist);
                }
            };
            workingThread.start();
            workingThread.join();
        }
        catch (Exception e) {
            Log.i("get_distace", "get_distace was failed.");
        }*/
        Handler h= new Handler();
        ProgressDialog mProgressDialog = ProgressDialog.show((Activity)mContext,"", "잠시만 기다려 주세요.",true);

        h.postDelayed(new Runnable(){
            public void run(){
                String[] opt_order = new String[n]; // 최적화후 출력 값
                int[] opt_real_order = new int[n]; // 최적화후 출력 값
                opt_order =orderfunction(dist.distance);

                for(int i =0;i<n;i++){
                    Log.d("opt_order","result"+Integer.toString(i)+" result : "+opt_order[i]);
                    opt_real_order[i] = list.get(Integer.parseInt(opt_order[i])-1).getOrder();
                    Log.d("opt_order","result"+Integer.toString(i)+" result_real_order : "+opt_real_order[i]);
                }

                for(int i =0;i<n;i++){
                    PlanDTO planDTO = list.get(i);
                    planDTO.setOrder(opt_real_order[i]);
                    planDAO.update_plan(planDTO);
                }
                mProgressDialog.dismiss();
                ((Activity) mContext).recreate();
            }
        },6000);
        //opt_order =orderfunction(dist.distance);

    }

    //거리 구하기 주어진 좌표값들로 거리 구하기 저장된 latitude, longitude를 기반으로 거리 계산
    public void get_distace(double[] latitude, double[] longitude,Distance dist){
        Point start;
        Point end ;

        for(int i=0;i<latitude.length;i++){
            for(int j=0;j<latitude.length;j++){
                start = Point.fromLngLat(longitude[i],latitude[i]);
                end = Point.fromLngLat(longitude[j],latitude[j]);
                getRoute(start, end,i,j,dist);
            }
        }
    }

    private void getRoute(Point origin, Point destination, int i, int j,Distance dist) {
        final int a =i;
        final int b =j;

        client = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_CYCLING)
                .accessToken("pk.eyJ1IjoiY2tuY2tuMjAwNiIsImEiOiJjanYxc2Jqd2owZXBnM3pxY280bGN2bjZyIn0.YwgAxVf8SLbzu6V4Epi4KA")
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

                //거리
                Log.d("success","Distance : "+currentRoute.distance());

                //실제 거리값 저장
                dist.distance[a][b] = currentRoute.distance();
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {

            }
        });
    }

    //최적화 함수 각 노드마다 계산한 거리를 double형 이중 array로 받아서 가장 짧은 순서로 return
    private String[] orderfunction(double[][] array) {

        int n = array.length;
        double best;
        int temp = 0;
        int i, j, k = 0;
        boolean[] check = new boolean[n]; // 이동 노드 체크
        String[] optimize = new String[n]; // 최적화후 출력 값
        double sum = 0;
        int[] order = new int[n];
        int[] temp_order = new int[n];

        order[0] = 0;
        optimize[0] = "0"; // 시작점 고정


        for (i = 0; i < n; i++) check[i] = false; // 이동 경로 확인

        check[0] = true; // 첫번째 노드가 시작점

        //그리디
        for (i = 1; i < n; i++) {
            best = 999999999;
            for (j = 0; j < n; j++) {
                if (check[j] == true) continue;
                else {
                    if (best > array[k][j] && array[k][j] != 0) {
                        best = array[k][j];
                        Log.d("best :", " " + best);
                        temp = j;
                    }
                }
                Log.d("distinguish","\n");
            }
            k = temp;
            sum += best;
            check[k] = true;
            order[i] = k;
        }


        //2opt
        int temp_sum = 0;
        int count =0;

        for(int p=0;p<order.length;p++){
            temp_order[p] = order[p];
        }

        while (true) {
            for (i = 1; i < n; i++) {
                for (j = i + 1; j < n; j++) {
                    temp = temp_order[i];
                    temp_order[i] = temp_order[j];
                    temp_order[j] = temp;

                    for (int o = 0; o < n - 1; o++)
                        temp_sum += array[temp_order[o]][temp_order[o + 1]];

                    Log.d("temp_sum : "," "+temp_sum +" vs sum : "+sum);

                    if (temp_sum < sum) {
                        for(int p=0;p<order.length;p++){
                            order[p] = temp_order[p];
                        }
                        sum = temp_sum;
                        count = 1;
                        break;
                    }
                    else {
                        for(int p=0;p<order.length;p++){
                            temp_order[p] = order[p];
                        }
                        temp_sum = 0;
                    }
                }
            }

            if(count != 1)
                break;
            else count = 0;
        }


        for(i=0;i<n;i++) {
            optimize[i] = Integer.toString(order[i] + 1);
        }

        return optimize;
    }

    class Distance{
        double[][] distance;

        public void setDistance(double[][] distance){
            this.distance = distance;
        }
    }

}