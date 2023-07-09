package com.example.foodinventoryhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

public class UserGuide extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_guide);

        WebView view = (WebView) findViewById(R.id.webview);
        view.loadUrl("file:///android_asset/user_guide.html");
    }
}