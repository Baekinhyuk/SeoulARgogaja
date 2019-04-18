package cau.seoulargogaja;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.design.widget.FloatingActionButton;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cau.seoulargogaja.adapter.PlanAdapter;
import cau.seoulargogaja.data.PlanDAO;
import cau.seoulargogaja.data.PlanDTO;
import cau.seoulargogaja.data.SpotDAO;
import cau.seoulargogaja.data.SpotDTO;
import cau.seoulargogaja.data.SpotParser;

import static android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT;

public class PlanFragment extends Fragment {


    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2;
    private TextView editTitle;
    private TextView startDate,endDate;
    private ImageView startImage,endImage,addImage;
    // datePicker 사용
    private SimpleDateFormat dateFormatter;
    private DatePickerDialog dialog,dialog2;


    private Date sDate,eDate;
    private int Date_diff_all;
    private int Date_diff_month;
    private int Date_diff_date;

    PlanDAO dao;
    InputMethodManager imm;
    ArrayList<PlanDTO> list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_plan, container, false);

        dao = new PlanDAO(this.getActivity());
        //앱 최초 실행 시 db 생성
        SharedPreferences pref = this.getActivity().getSharedPreferences("isFirst", Activity.MODE_PRIVATE);
        boolean first = pref.getBoolean("isFirst", false);
        Log.d("tour~", Boolean.toString(first));
        if(first==false) {
            Log.d("tour", "[first] 첫 실행...");
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isFirst", true);
            editor.commit();
            dao.createTable();

            SpotParser parser = new SpotParser();
            try {
                parser.start();
                parser.join(); // 서버 xml파일 파서

                SpotDAO dao = new SpotDAO(this.getActivity()); // db 생성
                dao.createTable();
                ArrayList<SpotDTO> spotlist;
                spotlist = parser.getList();
                dao.setData(spotlist);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        imm = (InputMethodManager)this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        list = dao.select_planlistid(0);

        final CustomListView listView = (CustomListView)rootView.findViewById(R.id.listView1);
        PlanAdapter adapter = new PlanAdapter(getActivity(), list, new PlanAdapter.Listener() {
            @Override
            public void onGrab(int position, RelativeLayout row) {
                listView.onGrab(position, row);
            }
        });

        listView.setAdapter(adapter);
        listView.setListener(new CustomListView.Listener() {
            @Override
            public void swapElements(int indexOne, int indexTwo) {

                PlanDTO temp1 = list.get(indexOne);
                PlanDTO temp2 = list.get(indexTwo);

                dao.Change_two_order(temp1,temp2);

                int temp_order1 = temp1.getOrder();
                int temp_order2 = temp2.getOrder();
                temp1.setOrder(temp_order2);
                temp2.setOrder(temp_order1);

                /*
                list.set(indexOne, list.get(indexTwo));
                list.set(indexTwo, temp);*/
                list.set(indexOne, temp2);
                list.set(indexTwo, temp1);

            }
        });


        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab1 = (FloatingActionButton) rootView.findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) rootView.findViewById(R.id.fab2);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anim();
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anim();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anim();
            }
        });


        addImage = (ImageView) rootView.findViewById(R.id.add_plan_image);
        // add 버튼 누르면 plan 추가 화면으로 돌아감
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PlanAdd.class);
                startActivity(intent);
            }
        });


        editTitle = (TextView) rootView.findViewById(R.id.plan_title);

        editTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());

                ad.setTitle("제목을 입력해 주세요");       // 제목 설정

                // EditText 삽입하기
                final EditText et = new EditText(getActivity());
                et.setText(editTitle.getText());
                ad.setView(et);

                // 확인 버튼 설정
                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String value = et.getText().toString();
                        editTitle.setText(value);
                        dialog.dismiss();     //닫기
                        // Event
                    }
                });
                // 취소 버튼 설정
                ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                        // Event
                    }
                });

                // 창 띄우기
                ad.show();
            }
        });





        // 달력모양 입력 시 입력되는 형태
        startImage = (ImageView) rootView.findViewById(R.id.btn_calender);
        startDate = (TextView) rootView.findViewById(R.id.txt_calender);
        //startDate.setInputType(InputType.TYPE_NULL);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd",Locale.KOREA);
        // 달력모양 눌렀을 때 datePicker 띄우기
        Calendar newCalendar = Calendar.getInstance();
        dialog = new DatePickerDialog(getActivity(), THEME_DEVICE_DEFAULT_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                sDate = newDate.getTime();
                if(check_Date_diff(sDate,eDate)){
                    startDate.setText(dateFormatter.format(sDate));
                }
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        startImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        // 달력모양 입력 시 입력되는 형태
        endImage = (ImageView) rootView.findViewById(R.id.btn_calender2);
        endDate = (TextView) rootView.findViewById(R.id.txt_calender2);
        //endDate.setInputType(InputType.TYPE_NULL);
        // 달력모양 눌렀을 때 datePicker 띄우기
        Calendar newCalendar2 = Calendar.getInstance();
        dialog2 = new DatePickerDialog(getActivity(), THEME_DEVICE_DEFAULT_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate2 = Calendar.getInstance();
                newDate2.set(year, monthOfYear, dayOfMonth);
                eDate = newDate2.getTime();
                if(check_Date_diff(sDate,eDate)){
                    endDate.setText(dateFormatter.format(eDate));
                }
            }
        }, newCalendar2.get(Calendar.YEAR), newCalendar2.get(Calendar.MONTH), newCalendar2.get(Calendar.DAY_OF_MONTH));
        endImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.show();
            }
        });




        return rootView;
    }

    public boolean check_Date_diff(Date sDate,Date eDate){
        if(sDate != null && eDate != null){
            if(eDate.compareTo(sDate) < 0){
                Toast.makeText(getActivity(), "날짜가 안 맞습니다. 날짜를 확인해 주세요", Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), Integer.toString(eDate.compareTo(sDate)), Toast.LENGTH_SHORT).show();
                return false;
            }
            Date_diff_all = Date_diff(sDate,eDate);
            Date_diff(sDate,Date_diff_all);

            return true;
        }
        return true;
    }

    public int Date_diff(Date sDate,Date eDate){
        try{
            //SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
            long calDate = sDate.getTime() - eDate.getTime();

            // Date.getTime() 은 해당날짜를 기준으로1970년 00:00:00 부터 몇 초가 흘렀는지를 반환해준다.
            // 이제 24*60*60*1000(각 시간값에 따른 차이점) 을 나눠주면 일수가 나온다.
            long calDateDays = calDate / ( 24*60*60*1000);

            calDateDays = Math.abs(calDateDays);
            Toast.makeText(getActivity(), "두 날짜의 날짜 차이:"+(int)calDateDays, Toast.LENGTH_SHORT).show();
            return (int)calDateDays;
        }
        catch(Exception e)
        {
            return -1;
            // 예외 처리
        }
    }

    public void Date_diff(Date sDate,int Date_diff_all){
        try{
            String diff_M;
            SimpleDateFormat dateFormat_mm = new SimpleDateFormat("MM", java.util.Locale.getDefault());
            diff_M = dateFormat_mm.format(sDate);
            Date_diff_month = Integer.parseInt(diff_M);
            Toast.makeText(getActivity(), "첫달의 월 : "+Date_diff_month, Toast.LENGTH_SHORT).show();

            String diff_d;
            SimpleDateFormat dateFormat_dd = new SimpleDateFormat("dd", java.util.Locale.getDefault());
            diff_d = dateFormat_dd.format(sDate);
            Date_diff_date = Integer.parseInt(diff_d);
            Toast.makeText(getActivity(), "첫달의 일 : "+Date_diff_date, Toast.LENGTH_SHORT).show();

        }
        catch(Exception e)
        {
            // 예외 처리
        }
    }

    public void anim() {

        if (isFabOpen) {
            fab.setImageResource(R.drawable.ic_add_black_24dp);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
        } else {
            fab.setImageResource(R.drawable.ic_close_black_24dp);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
        }
    }
}


