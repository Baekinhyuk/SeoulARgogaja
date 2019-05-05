package cau.seoulargogaja.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cau.seoulargogaja.R;
import cau.seoulargogaja.WalletAdd;
import cau.seoulargogaja.data.WalletDTO;

public class WalletAdapter  extends ArrayAdapter<WalletDTO> {

final int INVALID_ID = -1;
public interface Listener {
    void onGrab(int position, RelativeLayout row);
}

    final WalletAdapter.Listener listener;
    final Map<WalletDTO, Integer> mIdMap = new HashMap<>();

    public WalletAdapter(Context context, List<WalletDTO> list, WalletAdapter.Listener listener) {
        super(context, 0, list);
        this.listener = listener;
        for (int i = 0; i < list.size(); ++i) {
            mIdMap.put(list.get(i), i);
        }
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final Context context = getContext();
        final WalletDTO data = getItem(position);
        if(null == view) {
            if(data.getdatatype() == 1) {
                view = LayoutInflater.from(context).inflate(R.layout.fragment_wallet_item, null);
            }
            else if(data.getdatatype() == 0){
                view = LayoutInflater.from(context).inflate(R.layout.fragment_wallet_date, null);
            }
            else if(data.getdatatype() == 2){
                view = LayoutInflater.from(context).inflate(R.layout.fragment_plan_plus, null);
            }
        }

        try {
            if (data.getdatatype() == 1) {
                view = LayoutInflater.from(context).inflate(R.layout.fragment_wallet_item, null);
                TextView textView = (TextView) view.findViewById(R.id.wallet_detail);
                textView.setText(data.getdetail());
                textView.setPaintFlags(textView.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
                TextView textView2 = (TextView) view.findViewById(R.id.wallet_expend);
                textView2.setText(Integer.toString(data.getexpend()));
                TextView textView3 = (TextView) view.findViewById(R.id.wallet_memo);
                textView3.setText(data.getmemo());

                final RelativeLayout row2 = (RelativeLayout) view.findViewById(R.id.wallet_item_list1);
                view.findViewById(R.id.drag_image_wallet)
                        .setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                listener.onGrab(position, row2);
                                return false;
                            }
                        });
            }
            else if (data.getdatatype() == 0) {
                view = LayoutInflater.from(context).inflate(R.layout.fragment_wallet_date, null);
                TextView walletdate = (TextView) view.findViewById(R.id.wallet_date);

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
                walletdate.setText(last);

                TextView walletall = (TextView) view.findViewById(R.id.wallet_all);
                walletall.setText("총액");

            }
            else if(data.getdatatype() == 2){
                view = LayoutInflater.from(context).inflate(R.layout.fragment_plan_plus, null);
                ImageView addImage = (ImageView) view.findViewById(R.id.plus_plan_image);
                // add 버튼 누르면 plan 추가 화면으로 돌아감
                addImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, WalletAdd.class);
                        context.startActivity(intent);
                    }
                });
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= mIdMap.size()) {
            return INVALID_ID;
        }
        WalletDTO item = getItem(position);

        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }
}
