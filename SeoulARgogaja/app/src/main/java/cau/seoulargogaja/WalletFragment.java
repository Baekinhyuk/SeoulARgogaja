package cau.seoulargogaja;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cau.seoulargogaja.adapter.WalletAdapter;
import cau.seoulargogaja.data.IdDAO;
import cau.seoulargogaja.data.MainState;
import cau.seoulargogaja.data.PlanListDAO;
import cau.seoulargogaja.data.PlanListDTO;
import cau.seoulargogaja.data.SpotDAO;
import cau.seoulargogaja.data.SpotDTO;
import cau.seoulargogaja.data.WalletDAO;
import cau.seoulargogaja.data.WalletDTO;

public class WalletFragment extends Fragment {

    InputMethodManager imm;
    public static ArrayList<WalletDTO> list = null;

    private Date sDate,eDate;
    WalletDAO dao;
    PlanListDAO listdao;
    MainState mainState;
    ArrayList<String> dates;
    private int row_count;
    ViewGroup rootView;
    Activity activity;
    WalletPageAdapter wallet_adapter;
    TabLayout tabLayout;
    IdDAO iddao;

    @Override
    public void onResume(){
        super.onResume();
        set_wallet(rootView);
        activity = getActivity();
        wallet_adapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = (ViewGroup)inflater.inflate(R.layout.fragment_wallet, container, false);
        ViewPager viewPager = (ViewPager)rootView.findViewById(R.id.viewpager);
        wallet_adapter = new WalletPageAdapter(getActivity(), getChildFragmentManager());
        viewPager.setAdapter(wallet_adapter);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        dao = new WalletDAO(this.getActivity());
        listdao = new PlanListDAO(this.getActivity());

        set_wallet(rootView);

        imm = (InputMethodManager)this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        return rootView;
    }

    private void set_wallet(ViewGroup rootView){
        listdao = new PlanListDAO(this.getActivity());
        iddao = new IdDAO(this.getActivity());
        PlanListDTO mainPlan = listdao.select_one(iddao.select());

        mainState = new MainState(mainPlan);

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

        list = dao.select_planlistid(mainState.getplanlistId(),dates);

        WalletDTO plus_plan = new WalletDTO();
        list.add(plus_plan);
        row_count = list.size()-1;//0부터 시작하니 마지막위치는 -1

        final CustomListView2 listView2 = (CustomListView2)rootView.findViewById(R.id.listView2);
        WalletAdapter adapter = new WalletAdapter(getActivity(), list,dao,mainState.getplanlistId(), new WalletAdapter.Listener() {
            @Override
            public void onGrab(int position, RelativeLayout row) {
                listView2.onGrab(position, row);
            }
        });

        listView2.setAdapter(adapter);
        listView2.setListener(new CustomListView2.Listener() {
            @Override
            public void swapElements(int indexOne, int indexTwo) {
                /*
                WalletDTO temp = list.get(indexOne);
                list.set(indexOne, list.get(indexTwo));
                list.set(indexTwo, temp);*/
                if(indexOne != row_count && indexTwo != row_count && indexOne != 0 && indexTwo != 0){
                    WalletDTO temp1 = list.get(indexOne);
                    WalletDTO temp2 = list.get(indexTwo);

                    dao.Change_two_order(temp1,temp2);

                    int temp_order1 = temp1.getOrder();
                    int temp_order2 = temp2.getOrder();
                    temp1.setOrder(temp_order2);
                    temp2.setOrder(temp_order1);

                    list.set(indexOne, temp2);
                    list.set(indexTwo, temp1);

                }
            }
        });

    }

    public void all_Date(Date sDate,Date eDate){
        dao = new WalletDAO(this.getActivity());
        listdao = new PlanListDAO(this.getActivity());
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
        mainState.setdates(dates);
        dao.insert_date(dates,mainState.getplanlistId());
        /*
        for (String date : dates) {
            System.out.println(date);
        }*/

    }
}