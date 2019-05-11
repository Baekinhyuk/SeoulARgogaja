package cau.seoulargogaja;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONException;


public class BusFragment extends Fragment {
    private Button bt_api_call;
    private TextView tv_data;
    private Context context;
    private ODsayService oDsayService;
    String start_latitude;
    String start_longitude;
    String end_latitude;
    String end_longitude;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.fragment_bus, container, false);

        if(getArguments() != null){
            start_latitude = getArguments().getString("start_lati");
            start_longitude = getArguments().getString("start_logi");
            end_latitude = getArguments().getString("end_lati");
            end_longitude = getArguments().getString("end_logi");
        }

        context = getActivity();
        bt_api_call = (Button) view.findViewById(R.id.bt_api_call);
        tv_data = (TextView) view.findViewById(R.id.tv_data);

        oDsayService = ODsayService.init(getActivity(), getString(R.string.odsay_key));
        oDsayService.setReadTimeout(5000);
        oDsayService.setConnectionTimeout(5000);

        bt_api_call.setOnClickListener(onClickListener);

        return view;
    }

    private OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
        @Override
        public void onSuccess(ODsayData oDsayData, API api) {
            try {
                if(api == API.SEARCH_PUB_TRANS_PATH) {
                    String start_station = oDsayData.getJson().getJSONObject("result").getJSONArray("path").getJSONObject(0).getJSONObject("info").getString("firstStartStation");
                    String end_station = oDsayData.getJson().getJSONObject("result").getJSONArray("path").getJSONObject(0).getJSONObject("info").getString("lastEndStation");
                    tv_data.setText("출발 : "+start_station+"\n"+"도착 : "+end_station);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(int i, String errorMessage, API api) {
            tv_data.setText("API : " + api.name() + "\n" + errorMessage);
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            oDsayService.requestSearchPubTransPath(start_longitude, start_latitude
                    , end_longitude, end_latitude, "0", "0", "0", onResultCallbackListener);
        }
    };

}