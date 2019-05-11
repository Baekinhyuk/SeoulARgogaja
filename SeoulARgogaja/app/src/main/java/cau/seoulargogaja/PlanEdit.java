package cau.seoulargogaja;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cau.seoulargogaja.data.MainState;
import cau.seoulargogaja.data.PlanDAO;
import cau.seoulargogaja.data.PlanDTO;

import static android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT;

public class PlanEdit extends AppCompatActivity {

    private int id; //일정 ID
    private String content; // 내용
    private String date;     // 날짜
    private int spotID;     // spotID
    private int customID; // customID
    private String memo; // 메모
    private int order; // 순서
    private int datatype; //날짜 , 내용 구별 0 = 날짜 , 1 = 내용, 2 = + 모양
    private int planlistid;    // 리스트 ID

    private ImageView btnAdd, btnCancel, btnDelete; // add, cancel, delete 버튼
    private EditText editcontent, editmemo; // 장소명, 메모
    private TextView editdate, edittitle;
    private SimpleDateFormat dateFormatter;
    private DatePickerDialog dialog;
    private Date sDate;
    private long a, b;
    private ArrayList<String> dates;
    private int date_length;
    PlanDTO dto;

    AlertDialog.Builder alertdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_edit);

        Intent intent = getIntent(); /*데이터 수신*/
        id = intent.getExtras().getInt("id"); /*int형*/
        content = intent.getExtras().getString("content"); /*String형*/
        date = intent.getExtras().getString("date"); /*String형*/
        spotID = intent.getExtras().getInt("spotID"); /*int형*/
        customID = intent.getExtras().getInt("customID"); /*int형*/
        memo = intent.getExtras().getString("memo"); /*String형*/
        order = intent.getExtras().getInt("order"); /*int형*/
        datatype = intent.getExtras().getInt("datatype"); /*int형*/
        planlistid = intent.getExtras().getInt("planlistid"); /*int형*/


        final MainState mainState = new MainState();
        dates = mainState.getdates();

        final PlanDAO dao = new PlanDAO(this);
        dto = new PlanDTO();
        dto.setId(id);
        dto.setContent(content);
        dto.setdate(date);
        dto.setSpotID(spotID);
        dto.setcustomID(customID);
        dto.setmemo(memo);
        dto.setOrder(order);
        dto.setdatatype(datatype);
        dto.setplanlistid(planlistid);

        edittitle = (TextView) findViewById(R.id.edit_edit_title);
        edittitle.setText(edittitle.getText());

        edittitle.setPaintFlags(edittitle.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);

        btnAdd = (ImageView) findViewById(R.id.edit_edit_add);
        btnCancel = (ImageView) findViewById(R.id.edit_edit_cancel);
        btnDelete = (ImageView) findViewById(R.id.edit_edit_delete);

        editcontent = (EditText) findViewById(R.id.edit_edit_content);
        editmemo = (EditText) findViewById(R.id.edit_edit_memo);

        editdate = (TextView) findViewById(R.id.edit_edit_date);

        editcontent.setText(content);
        editdate.setText(date);
        editmemo.setText(memo);


        // 취소 버튼 누르면 이전 화면으로 돌아감
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        alertdialog = new AlertDialog.Builder(this);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertdialog.setMessage("정말 삭제하시겠습니까?");
                // 확인버튼
                alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dao.delete_plan(dto);
                        finish();
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

        // SQL문 실행
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dto.setdatatype(datatype);
                dto.setContent(String.valueOf(editcontent.getText()));
                dto.setdate(editdate.getText().toString());
                dto.setcustomID(customID);
                dto.setSpotID(spotID);
                dto.setplanlistid(mainState.getplanlistId());
                dto.setOrder(order);
                dto.setmemo(String.valueOf(editmemo.getText()));
                dao.update_plan(dto);
                finish();
            }
        });
    }
}
