package com.petabyte.plate.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.petabyte.plate.R;

public class SearchAddressActivity extends AppCompatActivity {

    private WebView daum_webView;

    class MyJavaScriptInterface
    {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processDATA(String data) {
            Bundle extra = new Bundle();
            Intent intent = new Intent();
            extra.putString("data", data);
            intent.putExtras(extra);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_address);

        daum_webView = (WebView) findViewById(R.id.webView_searchAddress);
        daum_webView.getSettings().setJavaScriptEnabled(true);
        daum_webView.addJavascriptInterface(new MyJavaScriptInterface(), "Android");

        daum_webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                daum_webView.loadUrl("javascript:sample2_execDaumPostcode();");
            }
        });
        daum_webView.loadUrl("http://plate.dothome.co.kr/index.php");
    }
}
