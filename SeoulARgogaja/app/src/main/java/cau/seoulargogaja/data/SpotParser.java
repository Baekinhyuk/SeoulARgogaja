package cau.seoulargogaja.data;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class SpotParser extends Thread {

    private String url;

    private ArrayList<String> id, name, theme,area, latitude, longitude, address, phone, web, description;
    private ArrayList<SpotDTO> list;


    public SpotParser() {
        url = "https://cauteam202.com/spots.xml";
    }

    @Override
    public void run() {

        id = new ArrayList<String>();
        name = new ArrayList<String>();
        theme = new ArrayList<String>();
        area = new ArrayList<String>();
        latitude = new ArrayList<String>();
        longitude = new ArrayList<String>();
        address = new ArrayList<String>();
        phone = new ArrayList<String>();
        web = new ArrayList<String>();
        description= new ArrayList<String>();

        list = new ArrayList<SpotDTO>();


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
                            theme.add(max);
                        } else if (inTitle4) {
                            String max = parser.getText();
                            area.add(max);
                        } else if (inTitle5) {
                            String max = parser.getText();
                            latitude.add(max);      //x

                        } else if (inTitle6) {
                            String max = parser.getText();
                            longitude.add(max);

                        } else if (inTitle7) {
                            String max = parser.getText();
                            address.add(max);

                        } else if (inTitle8) {
                            String max = parser.getText();
                            phone.add(max);

                        } else if (inTitle9) {
                            String max = parser.getText();
                            web.add(max);

                        } else if (inTitle10) {
                            String max = parser.getText();
                            description.add(max);
                        }

                        break;

                    case XmlPullParser.START_TAG: // 먼저
                        tag = parser.getName();


                        if (tag.compareTo("id") == 0) {
                            inTitle1 = true;
                        } else if (tag.compareTo("name") == 0) {
                            inTitle2 = true;
                        } else if (tag.compareTo("theme") == 0) {
                            inTitle3 = true;
                        } else if (tag.compareTo("area") == 0) {
                            inTitle4 = true;
                        } else if (tag.compareTo("latitude") == 0) {
                            inTitle5 = true;
                        } else if (tag.compareTo("longitude") == 0) {
                            inTitle6 = true;
                        } else if (tag.compareTo("address") == 0) {
                            inTitle7 = true;
                        } else if (tag.compareTo("phone") == 0) {
                            inTitle8 = true;
                        } else if (tag.compareTo("web") == 0) {
                            inTitle9 = true;
                        } else if (tag.compareTo("description") == 0) {
                            inTitle10 = true;
                        }

                        break;

                    case XmlPullParser.END_TAG: // 나중에
                        tag = parser.getName();
                        if (tag.compareTo("id") == 0) {
                            inTitle1 = false;
                        } else if (tag.compareTo("name") == 0) {
                            inTitle2 = false;
                        } else if (tag.compareTo("theme") == 0) {
                            inTitle3 = false;
                        } else if (tag.compareTo("area") == 0) {
                            inTitle4 = false;
                        } else if (tag.compareTo("latitude") == 0) {
                            inTitle5 = false;
                        } else if (tag.compareTo("longitude") == 0) {
                            inTitle6 = false;
                        } else if (tag.compareTo("address") == 0) {
                            inTitle7 = false;
                        } else if (tag.compareTo("phone") == 0) {
                            inTitle8 = false;
                        } else if (tag.compareTo("web") == 0) {
                            inTitle9 = false;
                        } else if (tag.compareTo("description") == 0){
                            inTitle10 = false;
                        }

                        break;


                }
                parserEvent = parser.next();

            }
        } catch (Exception e) {
            Log.e("tour", "Error in network call", e);
        }

        Log.d("tour", "크기 1: " + id.size());
        Log.d("tour", "크기 2: " + name.size());
        Log.d("tour", "크기 3: " + theme.size());
        Log.d("tour", "크기 4: " + area.size());
        Log.d("tour", "크기 5: " + latitude.size());
        Log.d("tour", "크기 6: " + longitude.size());
        Log.d("tour", "크기 7: " + address.size());
        Log.d("tour", "크기 8: " + phone.size());
        Log.d("tour", "크기 9: " + web.size());
        Log.d("tour", "크기 10: " + description.size());

        try {
            for (int i = 0; i < id.size(); i++) {  //사이즈 바꿔져야함!

                list.add(new SpotDTO(id.get(i), name.get(i), theme.get(i), area.get(i),latitude.get(i), longitude.get(i), address.get(i),phone.get(i), web.get(i), description.get(i)));
            }

        } catch (Exception e) {
            Log.e("tour", "array null ", e);
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
