package com.shubham.imshubham1_reddit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class SingleFeed extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_feed);

        Bundle bn = getIntent().getExtras();
        String url = bn.getString("url");

        webView = findViewById(R.id.web);
        webView.loadUrl("https://www.reddit.com"+url);
    }
}
