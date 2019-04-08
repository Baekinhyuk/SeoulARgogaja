package cau.seoulargogaja;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cau.seoulargogaja.adapter.WalletAdapter;
import cau.seoulargogaja.data.SpotDAO;
import cau.seoulargogaja.data.SpotDTO;
import cau.seoulargogaja.data.WalletDAO;
import cau.seoulargogaja.data.WalletDTO;

public class WalletFragment extends Fragment {

    InputMethodManager imm;
    public static ArrayList<WalletDTO> list = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_wallet, container, false);

        list = new ArrayList<WalletDTO>();
        list.add(new WalletDTO(1,"3월24일","ID"));
        list.add(new WalletDTO(2, "3월24일","ID","숙소비",50000,"Memo"));
        list.add(new WalletDTO(3, "3월24일","ID","식당",10000,"Memo"));
        list.add(new WalletDTO(4,"3월25일","ID"));
        list.add(new WalletDTO(5, "3월25일","ID","숙소비",20000,"Memo"));
        list.add(new WalletDTO(6, "3월25일","ID","식당",30000,"Memo"));

        final CustomListView2 listView = (CustomListView2)rootView.findViewById(R.id.listView2);
        WalletAdapter adapter = new WalletAdapter(getActivity(), list, new WalletAdapter.Listener() {
            @Override
            public void onGrab(int position, RelativeLayout row) {
                listView.onGrab(position, row);
            }
        });

        listView.setAdapter(adapter);
        listView.setListener(new CustomListView2.Listener() {
            @Override
            public void swapElements(int indexOne, int indexTwo) {
                WalletDTO temp = list.get(indexOne);
                list.set(indexOne, list.get(indexTwo));
                list.set(indexTwo, temp);
            }
        });

        //앱 최초 실행 시 db 생성
        SharedPreferences pref = this.getActivity().getSharedPreferences("isFirst", Activity.MODE_PRIVATE);
        boolean first = pref.getBoolean("isFirst", false);
        if(first==false) {
            Log.d("tour", "[first] 첫 실행...");
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isFirst", true);
            editor.commit();

            WalletDAO dao = new WalletDAO(getActivity());
            dao.createTable();

            dao.setData(list);

        }
        imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);





        return rootView;
    }
}