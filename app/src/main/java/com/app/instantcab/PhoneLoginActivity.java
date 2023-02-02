package com.app.instantcab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.instantcab.Responses.ApiClient;
import com.app.instantcab.Responses.ApiInterface;
import com.app.instantcab.Responses.Users;
import com.chaos.view.PinView;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsApi;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hbb20.CountryCodePicker;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneLoginActivity extends AppCompatActivity {

    private CountryCodePicker ccp;
    private EditText phoneEdit;
    private PinView pinView;
    private LinearLayout layout;
    private TextView OTP;
    private String selected_country_code = "+91";
    private static final int CREDENTIAL_PICKER_REQUEST = 120;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken resentToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private FirebaseAuth auth;

    ApiInterface apiInterface;
    private String device_token;
    private SessionManager sessionManager;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        device_token = String.valueOf(FirebaseMessaging.getInstance().getToken());



        sessionManager = new SessionManager(this);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        phoneEdit = (EditText) findViewById(R.id.phoneEdit);
        pinView = (PinView) findViewById(R.id.firstPinView);
        layout = (LinearLayout) findViewById(R.id.phoneLayout);
        OTP = (TextView) findViewById(R.id.OTP);
        auth = FirebaseAuth.getInstance();
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);


        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                selected_country_code = ccp.getSelectedCountryCodeWithPlus();
            }
        });


        phoneEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().length() == 10) {
                    layout.setVisibility(View.GONE);
                    pinView.setVisibility(View.VISIBLE);
                    OTP.setVisibility(View.VISIBLE);
                    sendOtp();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().length() == 6) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,pinView.getText().toString().trim());
                    signInWithAuthCredential(credential);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();


        PendingIntent intent = Credentials.getClient(PhoneLoginActivity.this).getHintPickerIntent(hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(), CREDENTIAL_PICKER_REQUEST, null, 0, 0, 0, new Bundle());
        } catch (IntentSender.SendIntentException e) {

            e.printStackTrace();
        }


        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                String code = phoneAuthCredential.getSmsCode();
                if (code !=null){
                    pinView.setText(code);

                    signInWithAuthCredential(phoneAuthCredential);
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                Toast.makeText(PhoneLoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                layout.setVisibility(View.VISIBLE);
                pinView.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);

                verificationId = verificationId;
                resentToken = token;

                Toast.makeText(PhoneLoginActivity.this, "OTP sent Successfully", Toast.LENGTH_SHORT).show();
                layout.setVisibility(View.GONE);
                pinView.setVisibility(View.VISIBLE);
            }
        };

    }


    private void sendOtp(){
        String phoneNumber = selected_country_code+phoneEdit.getText().toString();
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setPhoneNumber(phoneNumber)
                .setActivity(PhoneLoginActivity.this)
                .setCallbacks(callbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == RESULT_OK)
        {

            Credential credentials = data.getParcelableExtra(Credential.EXTRA_KEY);

            phoneEdit .setText(credentials.getId().substring(3));
        }
        else if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == CredentialsApi.ACTIVITY_RESULT_NO_HINTS_AVAILABLE)
        {

            Toast.makeText(PhoneLoginActivity.this, "No phone numbers found", Toast.LENGTH_LONG).show();
        }
    }

    private void signInWithAuthCredential(PhoneAuthCredential Credential) {

        auth.signInWithCredential(Credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    Call<Users> call = apiInterface.login_register(phoneEdit.getText().toString(),"8894569874123654456698489",device_token);
                    call.enqueue(new Callback<Users>() {
                        @Override
                        public void onResponse(Call<Users> call, Response<Users> response) {
                            if (response.isSuccessful()){
                                String status = response.body().getResponse();
                                if (status.equals("already")){

                                    sessionManager.createSession(device_token, "user",phoneEdit.getText().toString(),selected_country_code);
                                    Intent intent = new Intent(PhoneLoginActivity.this, HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                } else if (status.equals("new")){
                                    sessionManager.createSession(device_token, "user",phoneEdit.getText().toString(),selected_country_code);
                                    Intent intent = new Intent(PhoneLoginActivity.this, EditUserProfileActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                                else if (status.equals("failed")){
                                    Toast.makeText(PhoneLoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(PhoneLoginActivity.this, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    Toast.makeText(PhoneLoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(PhoneLoginActivity.this, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                            else {
                                Toast.makeText(PhoneLoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Users> call, Throwable t) {
                            Toast.makeText(PhoneLoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(PhoneLoginActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });


                }
                else {
                    Toast.makeText(PhoneLoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(PhoneLoginActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PhoneLoginActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}