package cau.seoulargogaja.adapter;

import android.app.Activity;
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

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

public class PlanAdapter extends ArrayAdapter<PlanDTO> {

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
                                context.startActivity(new Intent(context, SimpleDirectionActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
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

                view.findViewById(R.id.optimize)
                        .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "???", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else if (data.getdatatype() == 0) {
                view = LayoutInflater.from(context).inflate(R.layout.fragment_plan_date, null);
                TextView plandate = (TextView) view.findViewById(R.id.plan_date);

                String day = data.getdate();
                String a_year = day.substring(0,4);
                String b_month = day.substring(5,7);
                String c_day = day.substring(8,10);
                String sum_day = a_year + b_month + c_day;

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

    //순서 string 출력
    public String[] greedy(double array[][]){
        int n = array.length;
        double best;
        int temp = 0;
        int i,k=0;
        boolean[] check = new boolean[n]; // 이동 노드 체크
        String[] optimize = new String[n]; // 최적화후 출력 값
        optimize[0] = "0"; // 시작점 고정

        for(i=0;i<n;i++) check[i]=false; // 이동 경로 확인

        check[0] = true; // 첫번째 노드가 시작점

        //grid 알고리즘
        for(i=0;i<n-1;i++){
            best = 999999999;
            for(int j=0;j<n;j++){
                if(check[j] == true) continue;
                else {
                    if(best > array[k][j] && array[k][j] != 0)  {
                        best = array[k][j];
                        Log.d("best :"," "+best);
                        temp=j;
                    }
                }
            }
            k=temp;
            check[k] = true;
            optimize[i+1] = Integer.toString(k);
        }
        return optimize;
    }
}
