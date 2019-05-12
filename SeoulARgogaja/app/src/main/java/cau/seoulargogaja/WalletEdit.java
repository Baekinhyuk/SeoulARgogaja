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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cau.seoulargogaja.data.MainState;
import cau.seoulargogaja.data.WalletDAO;
import cau.seoulargogaja.data.WalletDTO;

import static android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT;

public class WalletEdit extends AppCompatActivity {

    private int id; //가계부 ID
    private String date;     // 날짜
    private int planlistid;    // 리스트 ID
    private String detail;     // 내역
    private int expend; // 금액
    private String memo; // 메모
    private int datatype; //날짜 , 내용 구별 0 = 날짜 , 1 = 내용
    private int main_image; //0 : 식사, 1 : 쇼핑, 2 : 교통, 3 : 관광, 4 : 숙박, 5: 기타
    private int sub_image; //0 : 카드, 1 : 현금
    private int order;

    private ImageView btnAdd, btnCancel, btnDelete; // add, cancel, delete 버튼
    private EditText wallet_add_detail, editmemo, wallet_add_expend; // 장소명, 메모
    private TextView wallet_add_date, wallet_edit_title;
    private ImageView wallet_add_money; //money
    private ImageView wallet_add_card; //card
    private ImageView wallet_add_dining,wallet_add_shopping,wallet_add_traffic,wallet_add_place,wallet_add_homw,wallet_add_etc; //main Image
    private SimpleDateFormat dateFormatter;
    private DatePickerDialog dialog;
    private Date sDate;
    private long a,b;
    private ArrayList<String> dates;
    private int date_length;
    WalletDTO dto;
    String result="";
    AlertDialog.Builder alertdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_edit);

        Intent intent = getIntent(); /*데이터 수신*/
        id = intent.getExtras().getInt("id"); /*int형*/
        date = intent.getExtras().getString("date"); /*String형*/
        planlistid = intent.getExtras().getInt("planlistid"); /*int형*/
        detail = intent.getExtras().getString("detail"); /*String형*/
        expend = intent.getExtras().getInt("expend"); /*int형*/
        memo = intent.getExtras().getString("memo"); /*String형*/
        datatype = intent.getExtras().getInt("datatype"); /*int형*/
        main_image = intent.getExtras().getInt("main_image"); /*int형*/
        sub_image = intent.getExtras().getInt("sub_image"); /*int형*/
        order= intent.getExtras().getInt("order"); /*int형*/

        final MainState mainState = new MainState();
        dates = mainState.getdates();

        final WalletDAO dao = new WalletDAO(this);
        dto = new WalletDTO();
        dto.setId(id);
        dto.setdate(date);
        dto.setplanlistid(planlistid);
        dto.setdetail(detail);
        dto.setexpend(expend);
        dto.setmemo(memo);
        dto.setdatatype(datatype);
        dto.setmain_image(main_image);
        dto.setsub_image(sub_image);
        dto.setOrder(order);

        wallet_edit_title = (TextView)findViewById(R.id.wallet_edit_title);
        wallet_edit_title.setText(wallet_edit_title.getText());
        wallet_edit_title.setPaintFlags(wallet_edit_title.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);


        btnAdd = (ImageView) findViewById(R.id.wallet_edit_add);
        btnCancel = (ImageView) findViewById(R.id.wallet_edit_cancel);
        btnDelete = (ImageView) findViewById(R.id.wallet_edit_delete);

        wallet_add_detail = (EditText) findViewById(R.id.wallet_edit_detail);
        editmemo = (EditText)findViewById(R.id.wallet_edit_memo);

        wallet_add_expend = (EditText)findViewById(R.id.wallet_edit_expend);
        final DecimalFormat decimalFormat = new DecimalFormat("#,###");


        wallet_add_date = (TextView)findViewById(R.id.wallet_edit_date);
        date_length = wallet_add_date.getText().toString().length();

        wallet_add_detail.setText(dto.getdetail());
        wallet_add_date.setText(dto.getdate());
        wallet_add_expend.setText(Integer.toString(dto.getexpend()));
        editmemo.setText(dto.getmemo());

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


        wallet_add_money = (ImageView) findViewById(R.id.wallet_edit_money);
        wallet_add_card = (ImageView) findViewById(R.id.wallet_edit_card);
        //기본 card로 설정
        //dto.setsub_image(0);
        wallet_add_money.setImageResource(R.drawable.ic_money_black);
        wallet_add_card.setImageResource(R.drawable.ic_payment_black_24dp);
        switch(dto.getsub_image()){
            case 0:
                wallet_add_card.setImageResource(R.drawable.ic_payment_red_24dp);
                break;
            default :
                wallet_add_money.setImageResource(R.drawable.ic_money_red);
        }

        wallet_add_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wallet_add_money.setImageResource(R.drawable.ic_money_black);
                wallet_add_card.setImageResource(R.drawable.ic_payment_red_24dp);
                dto.setsub_image(0);
            }
        });

        wallet_add_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wallet_add_card.setImageResource(R.drawable.ic_payment_black_24dp);
                wallet_add_money.setImageResource(R.drawable.ic_money_red);
                dto.setsub_image(1);
            }
        });

        //기본 etc로 설정
        //dto.setmain_image(5);

        wallet_add_dining = (ImageView) findViewById(R.id.wallet_edit_dining);
        wallet_add_shopping = (ImageView) findViewById(R.id.wallet_edit_shopping);
        wallet_add_traffic =(ImageView) findViewById(R.id.wallet_edit_traffic);
        wallet_add_place = (ImageView) findViewById(R.id.wallet_edit_place);
        wallet_add_homw = (ImageView) findViewById(R.id.wallet_edit_homw);
        wallet_add_etc = (ImageView) findViewById(R.id.wallet_edit_etc);

        wallet_add_dining.setImageResource(R.drawable.ic_local_dining_black_24dp);
        wallet_add_shopping.setImageResource(R.drawable.ic_shopping_cart_black_24dp);
        wallet_add_traffic.setImageResource(R.drawable.ic_directions_car_black_24dp);
        wallet_add_place.setImageResource(R.drawable.ic_crop_original_black_24dp);
        wallet_add_homw.setImageResource(R.drawable.ic_home_black_24dp);
        wallet_add_etc.setImageResource(R.mipmap.ic_etc_black);
        switch(dto.getmain_image()){
            case 0:
                wallet_add_dining.setImageResource(R.drawable.ic_local_dining_red_24dp);
                break;
            case 1:
                wallet_add_shopping.setImageResource(R.drawable.ic_shopping_cart_red_24dp);
                break;
            case 2:
                wallet_add_traffic.setImageResource(R.drawable.ic_directions_car_red_24dp);
                break;
            case 3:
                wallet_add_place.setImageResource(R.drawable.ic_crop_original_red_24dp);
                break;
            case 4:
                wallet_add_homw.setImageResource(R.drawable.ic_home_red_24dp);
                break;
            default :
                wallet_add_etc.setImageResource(R.mipmap.ic_etc_red);
        }

        wallet_add_dining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wallet_add_dining.setImageResource(R.drawable.ic_local_dining_red_24dp);
                wallet_add_shopping.setImageResource(R.drawable.ic_shopping_cart_black_24dp);
                wallet_add_traffic.setImageResource(R.drawable.ic_directions_car_black_24dp);
                wallet_add_place.setImageResource(R.drawable.ic_crop_original_black_24dp);
                wallet_add_homw.setImageResource(R.drawable.ic_home_black_24dp);
                wallet_add_etc.setImageResource(R.mipmap.ic_etc_black);
                dto.setmain_image(0);
            }
        });

        wallet_add_shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wallet_add_dining.setImageResource(R.drawable.ic_local_dining_black_24dp);
                wallet_add_shopping.setImageResource(R.drawable.ic_shopping_cart_red_24dp);
                wallet_add_traffic.setImageResource(R.drawable.ic_directions_car_black_24dp);
                wallet_add_place.setImageResource(R.drawable.ic_crop_original_black_24dp);
                wallet_add_homw.setImageResource(R.drawable.ic_home_black_24dp);
                wallet_add_etc.setImageResource(R.mipmap.ic_etc_black);
                dto.setmain_image(1);
            }
        });

        wallet_add_traffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wallet_add_dining.setImageResource(R.drawable.ic_local_dining_black_24dp);
                wallet_add_shopping.setImageResource(R.drawable.ic_shopping_cart_black_24dp);
                wallet_add_traffic.setImageResource(R.drawable.ic_directions_car_red_24dp);
                wallet_add_place.setImageResource(R.drawable.ic_crop_original_black_24dp);
                wallet_add_homw.setImageResource(R.drawable.ic_home_black_24dp);
                wallet_add_etc.setImageResource(R.mipmap.ic_etc_black);
                dto.setmain_image(2);
            }
        });

        wallet_add_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wallet_add_dining.setImageResource(R.drawable.ic_local_dining_black_24dp);
                wallet_add_shopping.setImageResource(R.drawable.ic_shopping_cart_black_24dp);
                wallet_add_traffic.setImageResource(R.drawable.ic_directions_car_black_24dp);
                wallet_add_place.setImageResource(R.drawable.ic_crop_original_red_24dp);
                wallet_add_homw.setImageResource(R.drawable.ic_home_black_24dp);
                wallet_add_etc.setImageResource(R.mipmap.ic_etc_black);
                dto.setmain_image(3);
            }
        });

        wallet_add_homw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wallet_add_dining.setImageResource(R.drawable.ic_local_dining_black_24dp);
                wallet_add_shopping.setImageResource(R.drawable.ic_shopping_cart_black_24dp);
                wallet_add_traffic.setImageResource(R.drawable.ic_directions_car_black_24dp);
                wallet_add_place.setImageResource(R.drawable.ic_crop_original_black_24dp);
                wallet_add_homw.setImageResource(R.drawable.ic_home_red_24dp);
                wallet_add_etc.setImageResource(R.mipmap.ic_etc_black);
                dto.setmain_image(4);
            }
        });

        wallet_add_etc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wallet_add_dining.setImageResource(R.drawable.ic_local_dining_black_24dp);
                wallet_add_shopping.setImageResource(R.drawable.ic_shopping_cart_black_24dp);
                wallet_add_traffic.setImageResource(R.drawable.ic_directions_car_black_24dp);
                wallet_add_place.setImageResource(R.drawable.ic_crop_original_black_24dp);
                wallet_add_homw.setImageResource(R.drawable.ic_home_black_24dp);
                wallet_add_etc.setImageResource(R.mipmap.ic_etc_red);
                dto.setmain_image(5);
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
                        dao.delete_wallet(dto);
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
                // 아이디 입력 확인
                if (wallet_add_date.getText().toString().length() == date_length) {
                    Toast.makeText(WalletEdit.this, "날짜가 선택되지 않았습니다 선택해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (wallet_add_expend.getText().toString().length() == 0) {
                    Toast.makeText(WalletEdit.this, "금액을 입력하세요", Toast.LENGTH_SHORT).show();
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
                //dto.setmain_image(0); 는 이미 위에서 자동으로 정의
                //dto.setsub_image(); 는 이미 위에서 자동으로 정의
                dto.setcolor_type(0);
                //Edit에서는 order를 넘겨주어야함
                dto.setOrder(order);
                dao.update_wallet(dto);
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
