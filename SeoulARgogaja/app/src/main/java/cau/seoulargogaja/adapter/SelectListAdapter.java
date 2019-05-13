package cau.seoulargogaja.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cau.seoulargogaja.PlanEdit;
import cau.seoulargogaja.R;
import cau.seoulargogaja.data.IdDAO;
import cau.seoulargogaja.data.MainState;
import cau.seoulargogaja.data.PlanListDTO;

import static android.util.Log.println;

public class SelectListAdapter extends BaseAdapter {

    private SelectListAdapter.ViewHolder holder;
    private LayoutInflater inflater;

    private ArrayList<PlanListDTO> planlist;
    MainState mainState;
    IdDAO iddao;
    Context context;

    static class ViewHolder {
        TextView title;
        TextView startdate;
        TextView enddate;

    }

    public SelectListAdapter(Context context, ArrayList<PlanListDTO> planlist) {
        this.planlist = planlist;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        Log.d("레코드 selectlist#" + Integer.toString(planlist.size()),"입력");
    }





    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;

        final PlanListDTO dto = getItem(position);
        if (rowView == null) {
            holder = new SelectListAdapter.ViewHolder();

            rowView = inflater.inflate(R.layout.select_list_item, parent, false);

            holder.title = (TextView) rowView.findViewById(R.id.select_title);
            holder.startdate = (TextView) rowView.findViewById(R.id.select_startdate);
            holder.enddate = (TextView) rowView.findViewById(R.id.select_enddate);

            rowView.setTag(holder);
        } else {
            holder = (SelectListAdapter.ViewHolder) rowView.getTag();
        }
        holder.title.setText(dto.getName());
        holder.startdate.setText(dto.getStartDate());
        holder.enddate.setText(dto.getEndDate());
        Log.d("레코드 selectlist#" + dto.getId(),"이름 : "+dto.getName());
        //holder.title.setText(tour.getName().trim());


        return rowView;
    }

    @Override
    public int getCount() {
        return this.planlist.size();
    }

    @Override
    public PlanListDTO getItem(int position) {
        return planlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
