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
import org.json.JSONObject;


public class TransportFragment extends Fragment {
    private TextView tv_data;
    private Context context;
    private ODsayService oDsayService;
    String start_latitude;
    String start_longitude;
    String end_latitude;
    String end_longitude;

    JSONObject obj;
    String totalTime=null, pathType, sectionTime3;
    int w;
    String busNo, trafficType, bussub = "", subwayCode, sectionTime, distance;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.fragment_transport, container, false);

        if(getArguments() != null){
            start_latitude = getArguments().getString("start_lati");
            start_longitude = getArguments().getString("start_logi");
            end_latitude = getArguments().getString("end_lati");
            end_longitude = getArguments().getString("end_logi");
        }

        context = getActivity();
        tv_data = (TextView) view.findViewById(R.id.tv_data);

        oDsayService = ODsayService.init(getActivity(), getString(R.string.odsay_key));
        oDsayService.setReadTimeout(5000);
        oDsayService.setConnectionTimeout(5000);

        oDsayService.requestSearchPubTransPath(start_longitude, start_latitude
                , end_longitude, end_latitude, "0", "0", "0", onResultCallbackListener);

        return view;
    }

    private OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
        @Override
        public void onSuccess(ODsayData oDsayData, API api) {
            try {
                if(api == API.SEARCH_PUB_TRANS_PATH) {
                    int k = oDsayData.getJson().getJSONObject("result").getJSONArray("path").length();
                    int sum=0;

/*

                    for (int l = 0; l < k; l++){
                        obj = oDsayData.getJson().getJSONObject("result").getJSONArray("path").getJSONObject(l);

                        totalTime = obj.getJSONObject("info").getString("totalTime");
                        w = obj.getJSONArray("subPath").length();
                        int sectionTime2 = 0;

                        for (int q = 0; q<w; q++) {
                            trafficType = obj.getJSONArray("subPath").getJSONObject(q).getString("trafficType");
                            sectionTime = obj.getJSONArray("subPath").getJSONObject(q).getString("sectionTime");

                            if (trafficType.equals("1")) {
                                subwayCode = obj.getJSONArray("subPath").getJSONObject(q).getJSONArray("lane").getJSONObject(0).getString("subwayCode");
                                sectionTime2 += Integer.parseInt(sectionTime);
                                if (subwayCode.equals("101")){
                                    bussub += "공항철도 ";
                                }else{
                                    bussub += subwayCode+"호선 ";
                                }

                            } else if (trafficType.equals("2")) {
                                busNo = obj.getJSONArray("subPath").getJSONObject(q).getJSONArray("lane").getJSONObject(0).getString("busNo");
                                bussub += busNo + "번 ";
                                sectionTime2 += Integer.parseInt(sectionTime);

                            } else if (trafficType.equals("3")) {
                                distance = obj.getJSONArray("subPath").getJSONObject(q).getString("distance");
                                sum+=Integer.parseInt(distance);
                                sectionTime2 += Integer.parseInt(sectionTime)*1.5;

                            } else {
                            }
                            tv_data.setText(bussub+"total time : "+totalTime+"\n");
                        }

                        sectionTime3 = Integer.toString(sectionTime2);

                    }*/
                    String start_station = oDsayData.getJson().getJSONObject("result").getJSONArray("path").getJSONObject(0).getJSONObject("info").getString("firstStartStation");
                    String end_station = oDsayData.getJson().getJSONObject("result").getJSONArray("path").getJSONObject(0).getJSONObject("info").getString("lastEndStation");
                    String total_time = oDsayData.getJson().getJSONObject("result").getJSONArray("path").getJSONObject(0).getJSONObject("info").getString("totalTime");
                    tv_data.setText("출발 : "+start_station+"\n도착 : "+end_station+"\n총 시간 : "+total_time+"분");
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

}