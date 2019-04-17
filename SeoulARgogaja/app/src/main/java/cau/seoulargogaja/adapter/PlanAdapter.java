package cau.seoulargogaja.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Build;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cau.seoulargogaja.R;
import cau.seoulargogaja.SimpleDirectionActivity;
import cau.seoulargogaja.data.PlanDTO;

public class PlanAdapter extends ArrayAdapter<PlanDTO> {

    final int INVALID_ID = -1;
    public interface Listener {
        void onGrab(int position, RelativeLayout row);
    }

    final Listener listener;
    final Map<PlanDTO, Integer> mIdMap = new HashMap<>();

    public PlanAdapter(Context context, List<PlanDTO> list, Listener listener) {
        super(context, 0, list);
        this.listener = listener;
        for (int i = 0; i < list.size(); ++i) {
            mIdMap.put(list.get(i), i);
        }
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final Context context = getContext();

        final PlanDTO data = getItem(position);
        if(null == view) {
            if(data.getdatatype() == 1) {
                view = LayoutInflater.from(context).inflate(R.layout.fragment_plan_item, null);
            }
            else if(data.getdatatype() == 0){
                view = LayoutInflater.from(context).inflate(R.layout.fragment_plan_date, null);
            }
        }

        try {
            if (data.getdatatype() == 1) {
                view = LayoutInflater.from(context).inflate(R.layout.fragment_plan_item, null);
                TextView textView = (TextView) view.findViewById(R.id.nick_name);
                textView.setText(data.getContent());

                final RelativeLayout row = (RelativeLayout) view.findViewById(R.id.item_list1);
                view.findViewById(R.id.vote_image)
                        .setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                context.startActivity(new Intent(context, SimpleDirectionActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                return false;
                            }
                        });
            }
            if (data.getdatatype() == 0) {
                view = LayoutInflater.from(context).inflate(R.layout.fragment_plan_date, null);
                TextView plandate = (TextView) view.findViewById(R.id.plan_date);
                plandate.setText(data.getdate());
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        final RelativeLayout row = (RelativeLayout) view.findViewById(R.id.item_list1);
        view.findViewById(R.id.drag_image)
                .setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        listener.onGrab(position, row);
                        return false;
                    }
                });

        return view;
    }


    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= mIdMap.size()) {
            return INVALID_ID;
        }
        PlanDTO item = getItem(position);

        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }
}
