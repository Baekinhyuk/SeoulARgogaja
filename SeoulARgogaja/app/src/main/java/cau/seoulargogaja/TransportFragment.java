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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


public class TransportFragment extends Fragment {
    private TextView tv_data;
    private TextView startingpoint;
    private TextView destination;
    private TextView path;

    private Context context;
    private ODsayService oDsayService;
    String start_latitude;
    String start_longitude;
    String end_latitude;
    String end_longitude;

    TextView tv_text;
    String transport = "";

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
        // start_latitude = "37.654620";
        //start_longitude = "127.060530";

        context = getActivity();
        tv_text = (TextView) view.findViewById(R.id.tv_text);
        startingpoint = (TextView) view.findViewById(R.id.start);
        destination = (TextView) view.findViewById(R.id.destination);


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
                int k = oDsayData.getJson().getJSONObject("result").getJSONArray("path").length();

                for(int j=0; j<k; j++) {
                    transport +=j+1+")";
                    JSONArray subPath = oDsayData.getJson().getJSONObject("result").getJSONArray("path").getJSONObject(j).getJSONArray("subPath");

                    for (int i = 0; i < subPath.length(); i++) {
                        JSONObject SubPathObject = subPath.getJSONObject(i);

                        if (SubPathObject.getString("trafficType").equals("1")) { // 지하철 일때
                            JSONArray laneArray = SubPathObject.getJSONArray("lane");// 노선 들어있는 어레이
                            JSONObject stopList = SubPathObject.getJSONObject("passStopList"); // 정류장 들어있는 오브젝트
                            JSONArray stationList = stopList.getJSONArray("stations"); // 거쳐야할 정류장 리스트어레이.

                            /*하위의 JsonArray[lane] 에 타야할 지하철 번호가 나옴
                             * 하위의 JsonObject passStopList -> JsonArray[Jsonobject] stations ->에 해당 이동수단의 정류장  */
                            String startname = SubPathObject.getString("startName") + "역 "; // 승차역 이름
                            String subway_no = "("+subPath.getJSONObject(i).getJSONArray("lane").getJSONObject(0).getString("subwayCode")+"호선) 승차";

                            String lanewayname = startname + subway_no + " -> ";

                            String stationString = "";
                            for (int v = 0; v < stationList.length(); v++) { // 정류장 리스트 뽑기
                                stationString = stationString + stationList.getJSONObject(v).getString("stationName").toString() + "ᚔ"; // 정류장 번호
                            }


                            String endname = SubPathObject.getString("endName"); // 하차역 이름
                            int endid = SubPathObject.getInt("endID");

                            if(endid != 426){
                                endname = endname + "역 하차 ";
                            }
                            else endname = endname + " 하차 "; // 하차역 이름

                            transport += lanewayname + endname;

                        } else if (SubPathObject.getString("trafficType").equals("2")) { // 버스 일때
                            JSONArray laneArray = SubPathObject.getJSONArray("lane");// 노선 들어있는 어레이
                            JSONObject stopList = SubPathObject.getJSONObject("passStopList"); // 정류장 들어있는 오브젝트
                            JSONArray stationList = stopList.getJSONArray("stations"); // 거쳐야할 정류장 리스트어레이.

                            String startname = SubPathObject.getString("startName") + " 정류장"; // 승차역 이름
                            String bus_no = "("+subPath.getJSONObject(i).getJSONArray("lane").getJSONObject(0).getString("busNo")+"번) 승차";


                            String lanewayname = startname + bus_no +" -> ";

                            String stationString = "";
                            for (int v = 0; v < stationList.length(); v++) { // 정류장 리스트 뽑기
                                stationString = stationString + stationList.getJSONObject(v).getString("stationName").toString() + "ᚔ"; // 정류장 번호
                            }

                            String endname = SubPathObject.getString("endName") + "정류장 하차 "; // 하차역 이름
                            transport +=lanewayname + endname;

                        } else if (SubPathObject.getString("trafficType").equals("3")) { // 도보 일때
                            String footdistance = SubPathObject.getString("distance") + "m 도보 이동";
                            double distance = SubPathObject.getDouble("distance");

                            if(distance == 0 ) {
                                continue;
                            }
                            else
                                transport += footdistance +" ";
                        }

                        if(i<subPath.length()-1)
                            transport += "-> ";
                    }
                    transport += "\n";
                }

                tv_text.setText(transport);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(int i, String errorMessage, API api) {
            tv_text.setText("API : " + api.name() + "\n" + errorMessage);
        }
    };

}