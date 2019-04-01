package cau.seoulargogaja;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Build;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanAdapter extends ArrayAdapter<TravelLocation> {

    final int INVALID_ID = -1;
    public interface Listener {
        void onGrab(int position, RelativeLayout row);
    }

    final Listener listener;
    final Map<TravelLocation, Integer> mIdMap = new HashMap<>();

    public PlanAdapter(Context context, List<TravelLocation> list, Listener listener) {
        super(context, 0, list);
        this.listener = listener;
        for (int i = 0; i < list.size(); ++i) {
            mIdMap.put(list.get(i), i);
        }
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        Context context = getContext();
        final TravelLocation data = getItem(position);
        if(null == view) {
            if(data.getType() == 1) {
                view = LayoutInflater.from(context).inflate(R.layout.fragment_plan_item, null);
            }
            else if(data.getType() == 0){
                view = LayoutInflater.from(context).inflate(R.layout.fragment_plan_date, null);
            }
        }

        try {
            if (data.getType() == 1) {
                view = LayoutInflater.from(context).inflate(R.layout.fragment_plan_item, null);
                TextView textView = (TextView) view.findViewById(R.id.nick_name);
                textView.setText(data.getnick_name());

                TextView timerange = (TextView) view.findViewById(R.id.nick_data);
                timerange.setText(data.getnick_data());
            }
            if (data.getType() == 0) {
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
        TravelLocation item = getItem(position);

        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }
}
