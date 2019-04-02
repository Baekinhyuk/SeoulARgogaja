package cau.seoulargogaja.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cau.seoulargogaja.R;
import cau.seoulargogaja.data.SpotDTO;

public class SearchListAdapter extends BaseAdapter {

    private ViewHolder holder;
    private LayoutInflater inflater;

    private List<SpotDTO> tourList;

    Context context;

    static class ViewHolder {
        TextView title;
        TextView subTitle;

    }

    public SearchListAdapter(Context context, List<SpotDTO> tourList) {
        this.tourList = tourList;
        this.context = context;
        this.inflater = LayoutInflater.from(context);

    }





    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;

        final SpotDTO tour = getItem(position);
        if (rowView == null) {
            holder = new ViewHolder();

            rowView = inflater.inflate(R.layout.search_list_row, parent, false);

            holder.title = (TextView) rowView.findViewById(R.id.search_item_title);
            holder.subTitle = (TextView) rowView.findViewById(R.id.search_item_sub_title);

            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        holder.title.setText(tour.getName().trim());
        holder.subTitle.setText(tour.getTheme().trim());



        return rowView;
    }

    @Override
    public int getCount() {
        return this.tourList.size();
    }

    @Override
    public SpotDTO getItem(int position) {
        return tourList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
