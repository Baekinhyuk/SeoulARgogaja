package cau.seoulargogaja;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import cau.seoulargogaja.data.PlanDAO;
import cau.seoulargogaja.data.PlanDTO;

public class PlanAdd extends AppCompatActivity {

    private ImageView btnAdd, btnCancel; // add, cancel 버튼
    private EditText editcontent, editmemo; // 장소명, 메모
    private long a,b;
    PlanDTO dto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_plus);
        final PlanDAO dao = new PlanDAO(this);
        dto = new PlanDTO();

        btnAdd = (ImageView) findViewById(R.id.edit_plan_add);
        btnCancel = (ImageView) findViewById(R.id.edit_plan_cancel);

        editcontent = (EditText) findViewById(R.id.edit_plan_content);
        editmemo = (EditText)findViewById(R.id.edit_plan_memo);

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
                dto.setdatatype(1);
                dto.setContent(String.valueOf(editcontent.getText()));
                dto.setdate("0월00일");
                dto.setcustomID(0);
                dto.setSpotID(0);
                dto.setplanlistid(0);
                dto.setOrder(0);
                dto.setmemo(String.valueOf(editmemo.getText()));
                dao.insert_plan(dto);
                finish();
            }
        });


    }

}
