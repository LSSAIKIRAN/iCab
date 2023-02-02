package com.app.instantcab;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class NoInternetActivity extends AppCompatActivity {

    private Button retryBtn;
    private ImageView imageView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        retryBtn = (Button) findViewById(R.id.button3);
        imageView = (ImageView) findViewById(R.id.imageView3);
        Glide.with(this).load(R.drawable.nowifi).into(imageView);

        retryBtn.setOnClickListener(v -> {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            if (activeNetwork != null){

                Intent intent = new Intent( NoInternetActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}