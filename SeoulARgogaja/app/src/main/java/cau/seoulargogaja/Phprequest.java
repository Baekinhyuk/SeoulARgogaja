package cau.seoulargogaja;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;


public class Phprequest {
    public static final String BASE_URL = "https://cauteam202.com/";
    private URL url;
    static private int result_planlistid;

    public Phprequest(){
    }

    public int getResult_planlistid(){
        return result_planlistid;
    }

    public Phprequest(String url) throws MalformedURLException { this.url = new URL(url); }

    private String readStream(InputStream in) throws IOException {
        StringBuilder jsonHtml = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
        String line = null;

        while((line = reader.readLine()) != null)
            jsonHtml.append(line);

        reader.close();
        return jsonHtml.toString();
    }

    public void regist_planlist(final int ID, final String name,final String startdate,final String enddate,final int budget,final String code) {
        try {
            String postData = "ID=" + ID + "&" + "name=" + name + "&" + "startdate=" + startdate+"&" + "enddate=" + enddate+ "&" + "budget=" + budget+ "&" + "code=" + code;
            Thread workingThread = new Thread() {
                public void run() {
                    String result = accept(postData);
                    result_planlistid = Integer.parseInt(result);
                    //Log.d("phprequest_regis : ",Integer.toString(planlistid));
                }
            };
            workingThread.start();
            workingThread.join();
        }
        catch (Exception e) {
            Log.i("PHPRequest", "request was failed.");
        }
    }

    public void regist_plan(final int ID, final String content,final String date,final int spotID,final int stamp,final String latitude,final String longitude,final String memo,final int order,final int datatype,final int planlistid) {
        try {
            String postData = "ID=" + ID + "&" + "content=" + content + "&" + "date=" + date+"&" + "spotID=" + spotID+ "&" + "stamp=" + stamp+ "&" + "latitude=" + latitude+ "&" + "longitude=" + longitude+ "&" + "memo=" + memo+ "&" + "order=" + order+ "&" + "datatype=" + datatype+ "&" + "planlistid=" + result_planlistid;
            Thread workingThread = new Thread() {
                public void run() {
                    accept(postData);
                }
            };
            workingThread.start();
            workingThread.join();
        }
        catch (Exception e) {
            Log.i("PHPRequest", "request was failed.");
        }
    }

    public void regist_wallet(final int ID, final String date,final int planlistid,final String detail,final int expend,final String memo,final int datatype,final int main_image,final int sub_image,final int color_type,final int order) {
        try {
            String postData = "ID=" + ID + "&" + "date=" + date + "&" + "planlistid=" + result_planlistid+"&" + "detail=" + detail+ "&" + "expend=" + expend+ "&" + "memo=" + memo+ "&" + "datatype=" + datatype+ "&" + "main_image=" + main_image+ "&" + "sub_image=" + sub_image+ "&" + "color_type=" + color_type+ "&" + "order=" + order;
            Thread workingThread = new Thread() {
                public void run() {
                    accept(postData);
                }
            };
            workingThread.start();
            workingThread.join();
        }
        catch (Exception e) {
            Log.i("PHPRequest", "request was failed.");
        }
    }

    private String accept(final String postData){
        try {
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);

            OutputStream outputStream = conn.getOutputStream();

            outputStream.write(postData.getBytes("utf-8"));
            outputStream.flush();
            outputStream.close();
            String result = readStream(conn.getInputStream());
            conn.disconnect();
            return result;
        }
        catch (Exception e) {
            Log.i("PHPRequest", "request was failed.");
            e.printStackTrace();
            return null;
        }
    }

}