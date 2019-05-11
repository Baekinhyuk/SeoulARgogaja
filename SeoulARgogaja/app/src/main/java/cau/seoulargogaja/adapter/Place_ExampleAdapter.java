package cau.seoulargogaja.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cau.seoulargogaja.DetailActivity;
import cau.seoulargogaja.R;
import cau.seoulargogaja.data.ServerGetImage;
import cau.seoulargogaja.data.SpotDTO;

public class Place_ExampleAdapter extends RecyclerView.Adapter<Place_ExampleAdapter.ExampleViewHolder> {

    private Context mContext;
    private ArrayList<SpotDTO> mExampleList;

    public Place_ExampleAdapter(Context context, ArrayList<SpotDTO> exampleList) {
        mContext = context;
        mExampleList = exampleList;
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.place_example_item, parent, false);
        return new ExampleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, final int position) {
        SpotDTO tour = mExampleList.get(position);
        String imageUrl = tour.getArea();
        String contentid = tour.getId();
        String creatorName = tour.getName();
        String address = tour.getAddress();

        holder.mTextViewCreator.setText(creatorName);
        holder.mTextViewAddress.setText(address);
        new ServerGetImage(holder.mImageView).execute(imageUrl);

        //Picasso.with(mContext).load(imageUrl).fit().centerInside().into(holder.mImageView);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpotDTO ctour = mExampleList.get(position);
                /*Utils.toast(getApplication(), "Show Detail : " + tour.getName());*/
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("id", ctour.getId());
                intent.putExtra("name", ctour.getName());
                intent.putExtra("theme", ctour.getTheme());
                intent.putExtra("area", ctour.getArea());
                intent.putExtra("latitude", ctour.getLatitude());
                intent.putExtra("longitude", ctour.getLongitude());
                intent.putExtra("address", ctour.getAddress());
                intent.putExtra("phone", ctour.getPhone());
                intent.putExtra("web", ctour.getWeb());
                intent.putExtra("description", ctour.getDescription());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextViewCreator;
        public TextView mTextViewAddress;
        public View layout;

        public ExampleViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image_view);
            mTextViewCreator = itemView.findViewById(R.id.text_view_creator);
            mTextViewAddress = itemView.findViewById(R.id.text_view_address);

//            mTextViewLat = itemView.findViewById(R.id.text_view_lat);
//            mTextViewLat = itemView.findViewById(R.id.text_view_lng);


            layout = itemView;

        }
    }
}
