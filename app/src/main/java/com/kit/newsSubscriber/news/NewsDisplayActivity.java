package com.kit.newsSubscriber.news;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.newsSubscriber.R;

/**
 * Created by Jawor
 * Time: 2018/4/29
 */

public class NewsDisplayActivity extends AppCompatActivity {
    // 用来接收url地址
    private String newsUrl;
    //创建时获取布局

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_news);
        //创建时获取地址
        newsUrl = getIntent().getStringExtra("news_url");
        /*
         * webview
         * 允许js
         * 设置委托
         * 载入网址
         */
        WebView webView = findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(newsUrl);
    }

}
