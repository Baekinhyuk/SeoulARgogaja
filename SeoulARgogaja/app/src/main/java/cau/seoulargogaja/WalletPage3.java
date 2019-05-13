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

import cau.seoulargogaja.data.IdDAO;
import cau.seoulargogaja.data.MainState;
import cau.seoulargogaja.data.PlanListDAO;
import cau.seoulargogaja.data.PlanListDTO;
import cau.seoulargogaja.data.WalletDAO;

public class WalletPage3 extends Fragment
{
    private TextView budget_t,money_t,card_t;
    ViewGroup rootView;
    AlertDialog.Builder alertdialog;
    PlanListDAO listdao;
    WalletDAO dao;
    Activity activity;
    MainState mainState;
    ArrayList<String> dates;
    private Date sDate,eDate;
    int[] result_sum;
    int budget;
    int money;
    int card;
    IdDAO iddao;

    public WalletPage3()
    {
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        listdao = new PlanListDAO(this.getActivity());
        iddao = new IdDAO(this.getActivity());
        PlanListDTO mainPlan = listdao.select_one(iddao.select());
        dao = new WalletDAO(this.getActivity());
        mainState = new MainState(mainPlan);
    }

    @Override
    public void onResume(){
        super.onResume();
        set_wallet_page3(rootView);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = (ViewGroup)inflater.inflate(R.layout.wallet_page3, container, false);
        set_wallet_page3(rootView);
        return rootView;
    }

    private void set_wallet_page3(ViewGroup rootView){

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
        result_sum = dao.sum_expend_cm(mainState.getplanlistId(),dates);
        card = result_sum[0];
        money = result_sum[1];
        budget = card+money;

        budget_t = (TextView)rootView.findViewById(R.id.wallet_edit_expend);
        money_t = (TextView)rootView.findViewById(R.id.wallet_page3_money);
        card_t = (TextView)rootView.findViewById(R.id.wallet_page3_card);

        NumberFormat nFormat = NumberFormat.getCurrencyInstance();
        String a = nFormat.format(budget);
        budget_t.setText(a);

        a = nFormat.format(card);
        card_t.setText(a);

        a = nFormat.format(money);
        money_t.setText(a);

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