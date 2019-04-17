package cau.seoulargogaja;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cau.seoulargogaja.adapter.PlanAdapter;
import cau.seoulargogaja.data.PlanDAO;
import cau.seoulargogaja.data.PlanDTO;

import static android.app.AlertDialog.THEME_HOLO_LIGHT;

public class PlanFragment extends Fragment {

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2;
    private EditText editTitle;
    private TextView startDate,endDate;
    private ImageView startImage,endImage,addImage;
    // datePicker 사용
    private SimpleDateFormat dateFormatter;
    private DatePickerDialog dialog,dialog2;

    PlanDAO dao;
    InputMethodManager imm;
    ArrayList<PlanDTO> list;
    //TEST
    /*
    List<PlanDTO> list = Arrays.asList(
            new PlanDTO(1,"3월24일",0),
            new PlanDTO(2,"중앙대","3월24일",0,0, "중앙대학교",0,0),
            new PlanDTO(3,"중앙대2","3월24일",0,0, "중앙대학교2",0,0),
            new PlanDTO(4,"3월25일",0),
            new PlanDTO(5,"중앙대","3월25일",0,0, "중앙대학교",0,0),
            new PlanDTO(6,"중앙대2","3월25일",0,0, "중앙대학교2",0,0)
    );*/

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_plan, container, false);

        dao = new PlanDAO(this.getActivity());
        //앱 최초 실행 시 db 생성
        SharedPreferences pref = this.getActivity().getSharedPreferences("isFirst", Activity.MODE_PRIVATE);
        Log.d("tour", "[first] 첫 실행.......");
        boolean first = pref.getBoolean("isFirst", false);
        Log.d("tour~", Boolean.toString(first));
        if(first==false) {
            Log.d("tour", "[first] 첫 실행...");
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isFirst", true);
            editor.commit();
            dao.createTable();
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
                PlanDTO temp = list.get(indexOne);
                list.set(indexOne, list.get(indexTwo));
                list.set(indexTwo, temp);
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


        editTitle = (EditText) rootView.findViewById(R.id.plan_title);

        // 달력모양 입력 시 입력되는 형태
        startImage = (ImageView) rootView.findViewById(R.id.btn_calender);
        startDate = (TextView) rootView.findViewById(R.id.txt_calender);
        //startDate.setInputType(InputType.TYPE_NULL);
        dateFormatter = new SimpleDateFormat("MM-dd",Locale.KOREA);
        // 달력모양 눌렀을 때 datePicker 띄우기
        Calendar newCalendar = Calendar.getInstance();
        dialog = new DatePickerDialog(getActivity(), THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                startDate.setText(dateFormatter.format(newDate.getTime()));
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
        dialog2 = new DatePickerDialog(getActivity(), THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate2 = Calendar.getInstance();
                newDate2.set(year, monthOfYear, dayOfMonth);

                endDate.setText(dateFormatter.format(newDate2.getTime()));
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


