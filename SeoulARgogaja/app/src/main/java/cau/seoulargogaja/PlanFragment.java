package cau.seoulargogaja;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.design.widget.FloatingActionButton;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.app.AlertDialog.THEME_HOLO_LIGHT;

public class PlanFragment extends Fragment{

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2;
    private EditText editTitle;
    private TextView startDate,endDate;
    private ImageView startImage,endImage;
    // datePicker 사용
    private SimpleDateFormat dateFormatter;
    private DatePickerDialog dialog,dialog2;

    //TEST
    List<TravelLocation> list = Arrays.asList(
            new TravelLocation("3월24일"),
            new TravelLocation("여행지1", "여행지정보1"),
            new TravelLocation("여행지2", "여행지정보2"),
            new TravelLocation("3월25일"),
            new TravelLocation("여행지5", "여행지정보5"),
            new TravelLocation("여행지6", "여행지정보6"),
            new TravelLocation("3월26일"),
            new TravelLocation("여행지10", "여행지정보10"),
            new TravelLocation("여행지20", "여행지정보20")
    );


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_plan, container, false);


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
                TravelLocation temp = list.get(indexOne);
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
