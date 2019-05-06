package cau.seoulargogaja.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cau.seoulargogaja.R;
import cau.seoulargogaja.data.ServerGetImage;
import cau.seoulargogaja.data.SpotDTO;

public class SearchPhotoAdapter extends BaseAdapter {

    private SearchPhotoAdapter.ViewHolder holder;
    private LayoutInflater inflater;

    private List<SpotDTO> tourList;

    Context context;

    static class ViewHolder {
        ImageView image;

    }

    public SearchPhotoAdapter(Context context, List<SpotDTO> tourList) {
        this.tourList = tourList;
        this.context = context;
        this.inflater = LayoutInflater.from(context);

    }





    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;

        final SpotDTO tour = getItem(position);
        if (rowView == null) {
            holder = new SearchPhotoAdapter.ViewHolder();

            rowView = inflater.inflate(R.layout.search_photo_row, parent, false);

            holder.image = (ImageView) rowView.findViewById(R.id.search_item_title);

            rowView.setTag(holder);
        } else {
            holder = (SearchPhotoAdapter.ViewHolder) rowView.getTag();
        }
        String imageUrl = "https://cauteam202.com/image/"+tour.getId()+".jpg";
        new ServerGetImage(holder.image).execute(imageUrl);




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
