package cau.seoulargogaja;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cau.seoulargogaja.data.MainState;
import cau.seoulargogaja.data.PlanListDAO;
import cau.seoulargogaja.data.PlanListDTO;
import cau.seoulargogaja.data.WalletDAO;

public class WalletPage2 extends Fragment
{
    private TextView budget,budget2;
    ViewGroup rootView;
    AlertDialog.Builder alertdialog;
    PlanListDAO listdao;
    WalletDAO dao;
    Activity activity;
    MainState mainState;
    ArrayList<String> dates;
    private Date sDate,eDate;
    int result_sum;
    int budget_save;

    public WalletPage2()
    {
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ArrayList<PlanListDTO> planlist = getPlanList();
        PlanListDTO mainPlan = planlist.get(0);
        listdao = new PlanListDAO(this.getActivity());
        dao = new WalletDAO(this.getActivity());
        mainState = new MainState(mainPlan);
    }

    @Override
    public void onResume(){
        super.onResume();
        set_wallet_page2(rootView);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = (ViewGroup)inflater.inflate(R.layout.wallet_page2, container, false);
        set_wallet_page2(rootView);
        return rootView;
    }

    private void set_wallet_page2(ViewGroup rootView){

        try {
            String from = mainState.getStartDate();
            sDate = new SimpleDateFormat("yyyy-MM-dd").parse(from);
            from = mainState.getEndDate();
            eDate = new SimpleDateFormat("yyyy-MM-dd").parse(from);
            dates = new ArrayList<String>();
            all_Date(sDate,eDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        result_sum = dao.sum_expend_all(mainState.getplanlistId(),dates);

        budget = (TextView)rootView.findViewById(R.id.wallet_page2_start);
        budget2 = (TextView)rootView.findViewById(R.id.wallet_page2_end);
        budget_save = mainState.getMainDto().getBudget();
        NumberFormat nFormat = NumberFormat.getCurrencyInstance();
        String a = nFormat.format(budget_save);
        budget.setText(a);

        int skajwl = mainState.getMainDto().getBudget() - result_sum;
        String b = nFormat.format(skajwl);
        budget2.setText(b);

        ProgressBar progress = (ProgressBar)rootView.findViewById(R.id.progress) ;
        progress.setProgress(result_sum);
        progress.setMax(budget_save);

        budget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertdialog = new AlertDialog.Builder(getActivity());
                alertdialog.setMessage("예산 총액");

                // EditText 삽입하기
                final EditText et = new EditText(getActivity());
                et.setText(Integer.toString(budget_save));
                alertdialog.setView(et);

                // 확인버튼
                alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String value = et.getText().toString();
                        String valuec = nFormat.format(Integer.parseInt(value));
                        budget.setText(valuec);
                        mainState.getMainDto().setBudget(Integer.parseInt(value));
                        budget_save = Integer.parseInt(value);
                        listdao = new PlanListDAO(activity);
                        listdao.update_budget(mainState.getMainDto());
                        //progress.setMax(Integer.parseInt(value));
                        onResume();
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


    }


    public ArrayList<PlanListDTO> getPlanList() {
        PlanListDAO dao = new PlanListDAO(this.getActivity());
        return dao.selectAll();
    }

    public void all_Date(Date sDate,Date eDate){
        final String DATE_PATTERN = "yyyy-MM-dd";
        Date currentDate = sDate;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        while (currentDate.compareTo(eDate) <= 0) {
            dates.add(sdf.format(currentDate));
            Calendar c = Calendar.getInstance();
            c.setTime(currentDate);
            c.add(Calendar.DAY_OF_MONTH, 1);
            currentDate = c.getTime();
        }
    }
}