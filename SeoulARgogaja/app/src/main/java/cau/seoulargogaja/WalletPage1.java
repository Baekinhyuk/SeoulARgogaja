package cau.seoulargogaja;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cau.seoulargogaja.data.IdDAO;
import cau.seoulargogaja.data.MainState;
import cau.seoulargogaja.data.PlanListDAO;
import cau.seoulargogaja.data.PlanListDTO;

public class WalletPage1 extends Fragment
{
    ViewGroup rootView;
    MainState mainState;
    IdDAO iddao;
    PlanListDAO listdao;

    public WalletPage1()
    {
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume(){
        super.onResume();
        iddao = new IdDAO(this.getActivity());
        listdao = new PlanListDAO(this.getActivity());
        PlanListDTO mainPlan = listdao.select_one(iddao.select());
        mainState = new MainState(mainPlan);
        TextView textView = (TextView) rootView.findViewById(R.id.wallet_date_start);
        textView.setText(mainState.getStartDate());
        TextView textView2 = (TextView) rootView.findViewById(R.id.wallet_date_end);
        textView2.setText(mainState.getEndDate());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = (ViewGroup)inflater.inflate(R.layout.wallet_page1, container, false);
        return rootView;
    }

}
