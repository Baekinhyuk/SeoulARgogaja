package cau.seoulargogaja;

import android.app.DatePickerDialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cau.seoulargogaja.data.MainState;
import cau.seoulargogaja.data.PlanDAO;
import cau.seoulargogaja.data.PlanDTO;
import cau.seoulargogaja.data.WalletDAO;
import cau.seoulargogaja.data.WalletDTO;

import static android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT;

public class WalletAdd extends AppCompatActivity {

    private ImageView btnAdd, btnCancel; // add, cancel 버튼
    private EditText wallet_add_detail, editmemo, wallet_add_expend; // 장소명, 메모
    private TextView wallet_add_date, wallet_add_title;
    private ImageView wallet_add_money; //money
    private ImageView wallet_add_card; //card
    private SimpleDateFormat dateFormatter;
    private DatePickerDialog dialog;
    private Date sDate;
    private long a,b;
    private ArrayList<String> dates;
    private int date_length;
    WalletDTO dto;
    String result="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_add);

        final MainState mainState = new MainState();
        dates = mainState.getdates();

        final WalletDAO dao = new WalletDAO(this);
        dto = new WalletDTO();

        wallet_add_title = (TextView)findViewById(R.id.wallet_add_title);
        wallet_add_title.setText(wallet_add_title.getText());
        wallet_add_title.setPaintFlags(wallet_add_title.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);


        btnAdd = (ImageView) findViewById(R.id.wallet_add_add);
        btnCancel = (ImageView) findViewById(R.id.wallet_add_cancel);


        wallet_add_detail = (EditText) findViewById(R.id.wallet_add_detail);
        editmemo = (EditText)findViewById(R.id.wallet_add_memo);

        wallet_add_expend = (EditText)findViewById(R.id.wallet_add_expend);
        final DecimalFormat decimalFormat = new DecimalFormat("#,###");


        /*형식바꾸는거오류나서 일단보류
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence.toString()) && !charSequence.toString().equals(result)){
                    dto.setexpend(Integer.parseInt(result));
                    result = decimalFormat.format(Double.parseDouble(charSequence.toString().replaceAll(",","")));
                    wallet_add_expend.setText(result);
                    wallet_add_expend.setSelection(result.length());
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }

        };
        wallet_add_expend.addTextChangedListener(watcher);*/


        wallet_add_date = (TextView)findViewById(R.id.wallet_add_date);
        date_length = wallet_add_date.getText().toString().length();

        //startDate.setInputType(InputType.TYPE_NULL);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        // 달력모양 눌렀을 때 datePicker 띄우기
        Calendar newCalendar = Calendar.getInstance();
        dialog = new DatePickerDialog(this, THEME_DEVICE_DEFAULT_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                sDate = newDate.getTime();
                if(check_date(dateFormatter.format(sDate))){
                    wallet_add_date.setText(dateFormatter.format(sDate));
                }
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        wallet_add_date.setOnClickListener(new View.OnClickListener() {
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


        wallet_add_money = (ImageView) findViewById(R.id.wallet_add_money);
        wallet_add_card = (ImageView) findViewById(R.id.wallet_add_card);
        dto.setsub_image(0);

        wallet_add_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wallet_add_money.setImageResource(R.drawable.ic_monetization_on_black_24dp);
                wallet_add_card.setImageResource(R.drawable.ic_payment_red_24dp);
                //wallet_add_money.invalidate();
                //wallet_add_card.invalidate();
                dto.setsub_image(0);
            }
        });

        wallet_add_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wallet_add_card.setImageResource(R.drawable.ic_payment_black_24dp);
                wallet_add_money.setImageResource(R.drawable.ic_monetization_on_red_24dp);
                //wallet_add_money.invalidate();
                //wallet_add_card.invalidate();
                dto.setsub_image(1);
            }
        });





        // SQL문 실행
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 아이디 입력 확인
                if (wallet_add_date.getText().toString().length() == date_length) {
                    Toast.makeText(WalletAdd.this, "날짜가 선택되지 않았습니다 선택해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (wallet_add_expend.getText().toString().length() == 0) {
                    Toast.makeText(WalletAdd.this, "금액을 입력하세요", Toast.LENGTH_SHORT).show();
                    wallet_add_expend.requestFocus();
                    return;
                }

                dto.setdate(wallet_add_date.getText().toString());
                dto.setplanlistid(mainState.getplanlistId());
                dto.setdetail(String.valueOf(wallet_add_detail.getText()));
                //dto.setexpend(expend_value);
                String value= wallet_add_expend.getText().toString();
                int finalValue=Integer.parseInt(value);
                dto.setexpend(finalValue);
                dto.setmemo(String.valueOf(editmemo.getText()));
                dto.setdatatype(1);
                dto.setmain_image(0);
                //dto.setsub_image(); 는 이미 위에서 자동으로 정의
                dto.setcolor_type(0);
                //자동으로 order가 변경이 되므로 임의로 0으로 입력
                dto.setOrder(0);
                dao.test_sql_order(mainState.getplanlistId());
                dao.insert_wallet_item(dto);
                dao.test_sql_order(mainState.getplanlistId());
                finish();
            }
        });


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
