package com.app.instantcab;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class LoginActivity extends AppCompatActivity {

    private Button phoneBtn;
    private ImageView googleImg;
    private SessionManager sessionManager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phoneBtn = (Button) findViewById(R.id.btn1);
        googleImg = (ImageView) findViewById(R.id.googleBtn);
        sessionManager = new SessionManager(this);






        if (sessionManager.isLogin()) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }


        phoneLoginClick();

    }

    public void phoneLoginClick(){
        phoneBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, PhoneLoginActivity.class);
            startActivity(intent);
            finish();
        });

    }

}