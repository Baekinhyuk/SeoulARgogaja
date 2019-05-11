package cau.seoulargogaja.data;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;
import java.util.ArrayList;

public class PoliceParser extends Thread {

    private String url;

    private ArrayList<String> id, name, latitude, longitude, address, phone;
    private ArrayList<PoliceDTO> list;


    public PoliceParser() {
        url = "https://cauteam202.com/police.xml";
    }

    @Override
    public void run() {

        id = new ArrayList<String>();
        name = new ArrayList<String>();
        latitude = new ArrayList<String>();
        longitude = new ArrayList<String>();
        address = new ArrayList<String>();
        phone = new ArrayList<String>();

        list = new ArrayList<PoliceDTO>();


        try {

            URL text = new URL(url); // 파싱하고자하는 URL

            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();

            XmlPullParser parser = parserCreator.newPullParser(); // XMLPullParser 사용

            parser.setInput(text.openStream(), null);   // 파싱하기위해서 스트림을 열어야한다.

            int parserEvent = parser.getEventType();  // 파싱할 데이터의 타입을 알려준다.
            String tag;


            boolean inTitle1 = false, inTitle2 = false, inTitle3 = false, inTitle4 = false, inTitle5 = false, inTitle6 = false, inTitle7 = false, inTitle8 = false,inTitle9 = false, inTitle10 = false;
            while (parserEvent != XmlPullParser.END_DOCUMENT) { // xml 파일의 문서 끝인가?
                switch (parserEvent) {

                    case XmlPullParser.TEXT:
                        tag = parser.getName();
                        if (inTitle1) {
                            String max = parser.getText();
                            id.add(max);
                        } else if (inTitle2) {
                            String max = parser.getText();
                            name.add(max);
                        } else if (inTitle3) {
                            String max = parser.getText();
                            latitude.add(max);      //x
                        } else if (inTitle4) {
                            String max = parser.getText();
                            longitude.add(max);

                        } else if (inTitle5) {
                            String max = parser.getText();
                            address.add(max);

                        } else if (inTitle6) {
                            String max = parser.getText();
                            phone.add(max);
                        }



                        break;

                    case XmlPullParser.START_TAG: // 먼저
                        tag = parser.getName();


                        if (tag.compareTo("id") == 0) {
                            inTitle1 = true;
                        } else if (tag.compareTo("name") == 0) {
                            inTitle2 = true;
                        } else if (tag.compareTo("latitude") == 0) {
                            inTitle3 = true;
                        } else if (tag.compareTo("longitude") == 0) {
                            inTitle4 = true;
                        } else if (tag.compareTo("address") == 0) {
                            inTitle5 = true;
                        } else if (tag.compareTo("phone") == 0) {
                            inTitle6 = true;
                        }
                        break;

                    case XmlPullParser.END_TAG: // 나중에
                        tag = parser.getName();
                        if (tag.compareTo("id") == 0) {
                            inTitle1 = false;
                        } else if (tag.compareTo("name") == 0) {
                            inTitle2 = false;
                        } else if (tag.compareTo("latitude") == 0) {
                            inTitle3 = false;
                        } else if (tag.compareTo("longitude") == 0) {
                            inTitle4 = false;
                        } else if (tag.compareTo("address") == 0) {
                            inTitle5 = false;
                        } else if (tag.compareTo("phone") == 0) {
                            inTitle6 = false;
                        }

                        break;


                }
                parserEvent = parser.next();

            }
        } catch (Exception e) {
            Log.e("Police", "Error in network call", e);
        }

        Log.d("police", "크기 1: " + id.size());

        try {
            for (int i = 0; i < id.size(); i++) {  //사이즈 바꿔져야함!

                list.add(new PoliceDTO(id.get(i), name.get(i) ,latitude.get(i), longitude.get(i), address.get(i),phone.get(i)));
            }

        } catch (Exception e) {
            Log.e("police", "array null ", e);
        }
    }

    public ArrayList getList() {
        if(list == null) {
            Log.d("tour","ArrayList의 값이 null입니다.");
        } else {
            Log.d("tour","파싱완료");
        }
        return list;
    }

}
