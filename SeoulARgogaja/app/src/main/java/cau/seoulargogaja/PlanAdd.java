package cau.seoulargogaja;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cau.seoulargogaja.data.MainState;
import cau.seoulargogaja.data.PlanDAO;
import cau.seoulargogaja.data.PlanDTO;

import static android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT;

public class PlanAdd extends AppCompatActivity {

    private ImageView btnAdd, btnCancel; // add, cancel 버튼
    private EditText editcontent, editmemo; // 장소명, 메모
    private TextView editdate, edittitle, editlocation;
    private SimpleDateFormat dateFormatter;
    private DatePickerDialog dialog;
    private Date sDate;
    private String latitude,longitude;
    private ArrayList<String> dates;
    private int date_length;
    private boolean setlocation = false;
    PlanDTO dto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_plus);

        final MainState mainState = new MainState();
        dates = mainState.getdates();

        final PlanDAO dao = new PlanDAO(this);
        dto = new PlanDTO();

        edittitle = (TextView)findViewById(R.id.edit_plan_title);
        edittitle.setText(edittitle.getText());
        edittitle.setPaintFlags(edittitle.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);

        btnAdd = (ImageView) findViewById(R.id.edit_plan_add);
        btnCancel = (ImageView) findViewById(R.id.edit_plan_cancel);

        editcontent = (EditText) findViewById(R.id.edit_plan_content);
        editmemo = (EditText)findViewById(R.id.edit_plan_memo);

        editdate = (TextView)findViewById(R.id.edit_plan_date);
        date_length = editdate.getText().toString().length();

        editlocation = (TextView)findViewById(R.id.edit_plan_location);
        editlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog.show(); 여기서 맵 호출후 맵정보 다시 받아와야함
                Intent intent = new Intent(PlanAdd.this, PlanAddMap.class);
                startActivityForResult(intent,0);
            }
        });




        //startDate.setInputType(InputType.TYPE_NULL);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd",Locale.KOREA);
        // 달력모양 눌렀을 때 datePicker 띄우기
        Calendar newCalendar = Calendar.getInstance();
        dialog = new DatePickerDialog(this, THEME_DEVICE_DEFAULT_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                sDate = newDate.getTime();
                if(check_date(dateFormatter.format(sDate))){
                    editdate.setText(dateFormatter.format(sDate));
                }
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        editdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });



        // 취소 버튼 누르면 이전 화면으로 돌아감
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // SQL문 실행
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 아이디 입력 확인
                if (editdate.getText().toString().length() == date_length) {
                    Toast.makeText(PlanAdd.this, "날짜가 선택되지 않았습니다 선택해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!setlocation){
                    Toast.makeText(PlanAdd.this, "여행 장소가 지정되지 않았습니다 선택해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                dto.setdatatype(1);
                dto.setContent(String.valueOf(editcontent.getText()));
                dto.setdate(editdate.getText().toString());
                dto.setStamp(0);
                dto.setLatitude(latitude);
                dto.setLongitude(longitude);
                dto.setSpotID(0);
                dto.setplanlistid(mainState.getplanlistId());
                dto.setOrder(0);
                dto.setmemo(String.valueOf(editmemo.getText()));
                dao.insert_plan(dto);
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    latitude = data.getStringExtra("latitude");
                    longitude = data.getStringExtra("longitude");
                    editlocation = (TextView)findViewById(R.id.edit_plan_location);
                    editlocation.setText("여행위치가 지정되었습니다.");
                    setlocation = true;
                    break;
            }
        }
    }

    private boolean check_date(String Date){
        for(int i =0;i<dates.size();i++){
            if(dates.get(i).compareTo(Date)==0){
                return true;
            }
        }
        Toast.makeText(this, "일정 사이에 존재하지 않는 날짜 입니다 날짜를 다시 선택해주세요", Toast.LENGTH_SHORT).show();
        return false;

    }


}
