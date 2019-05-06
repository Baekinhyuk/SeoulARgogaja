package cau.seoulargogaja.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import cau.seoulargogaja.data.DistSpot;

public class HospitalAdapter extends BaseAdapter {   //자동으로 호촐되는 4가지 오버라이딩 함수들 //어뎁터는 데이터도 관리하고 뷰도 생성한다는 중요함
    Context context;

    public HospitalAdapter(Context context) {
        this.context = context;
    }

    ArrayList<DistSpot> items = new ArrayList<DistSpot>(); //우리가 만든 dto타입을 지정


    //아이템삭제해주는거
    public void clearData() {
        items.clear();
    }

    public void addItem(DistSpot item) {
        items.add(item);
    }


    @Override
    public int getCount() { //데이터가 몇개인지 넣어준다.
        return items.size();
    }

    @Override
    public Object getItem(int position) {   //각각 하나하나씩 아이템값
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) { //뷰를 여기서 만든다.
        //view가  엄청많으면 특히 이미지 메모리가 문제가됨 그래서 재사용 방법으로
        //convertView를 사용하여 메모리를 효율적사용
        SingerItemView view = null;
        if(convertView == null) {
            view = new SingerItemView(context);
        } else {
            view = (SingerItemView)convertView;
        }

        DistSpot curItem = items.get(position);

        view.setName(curItem.getDto().getName());
        view.setAddr(""+Math.round((curItem.getDist()*1000))+"m");

        return view;
    }
}