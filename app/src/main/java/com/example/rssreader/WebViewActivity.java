package com.example.rssreader;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;

    private void findElems()
    {
        webView = findViewById(R.id.wv_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        findElems();

        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl(getIntent().getStringExtra(ContextContainer.LOAD_DATA_WAY));
    }

    private class MyWebViewClient extends WebViewClient {
        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
           // view.loadUrl(request.getUrl().toString());
            //return true;
            return false;
        }
    }
}