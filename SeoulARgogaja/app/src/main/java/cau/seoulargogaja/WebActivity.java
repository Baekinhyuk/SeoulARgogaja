package cau.seoulargogaja;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

public class WebActivity extends AppCompatActivity {



    private WebView webview;    //웹뷰 객체


    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);


        String url = "";     //링크주소 담을 변수
        Intent intent = getIntent();
        if(intent != null) {
            url = intent.getStringExtra("url");     //인텐트로 넘어온 링크값 저장
        }
        else
            Toast.makeText(WebActivity.this, "링크값이 넘어오지 않았습니다.", Toast.LENGTH_SHORT).show();

        // 웹뷰 객체 참조
        webview = (WebView) findViewById(R.id.webview);

        // 웹뷰 설정 정보
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true); //자바스크립트 허용



        webview.loadUrl(url);   //웹 실행

    }


}