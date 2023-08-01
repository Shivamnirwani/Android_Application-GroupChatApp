package com.example.groupchatapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    EditText Email;
    EditText Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("UTA Group Chat", 0); // 0 - for private mode
        String JWT = pref.getString("JWT", null); // getting String
        if (JWT != null) {
            switchtoHomeActivity();
            finish();
        }
        SharedPreferences.Editor editor = pref.edit();


        Email = findViewById(R.id.EmailAddress);
        Password = findViewById(R.id.Password);


        Button LoginButton = findViewById(R.id.login_button);
        LoginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                login();


            }
        });
        Button RegisterButton = findViewById(R.id.register_button);
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTONS", "User tapped the Register button on the Login Activity");
                switchtoRegisterActivity();
                finish();
            }
        });
        Button ForgetButton = findViewById(R.id.forgot_button);
        ForgetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTONS", "User tapped the Forgot Button on the Login Activity");
            }
        });
    }

    private void switchtoRegisterActivity() {
        Intent switchActivityIntent = new Intent(this, Register.class);
        switchActivityIntent.putExtra("message", "From: " + MainActivity.class.getSimpleName());
        startActivity(switchActivityIntent);
    }

    private void switchtoHomeActivity() {
        Intent switchActivityIntent = new Intent(this, Home.class);
        switchActivityIntent.putExtra("message", "From: " + MainActivity.class.getSimpleName());
        startActivity(switchActivityIntent);
    }

    private boolean login() {


        JSONObject reqObject = new JSONObject();
        try {
            reqObject.put("email", Email.getText());
            reqObject.put("password", Password.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post("https://1cec-12-184-115-59.ngrok-free.app/login")
                .addJSONObjectBody(reqObject)
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("UTA Group Chat", 0);
                        try {
                            if (response.getBoolean("success")) {
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("JWT", response.getString("jwt")); // Storing string
                                editor.commit(); // commit changes
                                                        Snackbar snack = Snackbar.make(findViewById(R.id.main),"Login Successful",Snackbar.LENGTH_SHORT);
                        snack.show();
                                                    new Timer().schedule(
                    new TimerTask(){
                
                        @Override
                        public void run(){
                            
                        switchtoHomeActivity();   
                        finish();
                        }
                        
                    }, 1000);
                            }
                        } catch (JSONException e) {

                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        JSONObject response = null;
                        String err = null;
                        try {
                            response = new JSONObject(error.getErrorBody());
                            err = response.getString("status");

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        Log.d("fail", "login fail");
                        Snackbar snack = Snackbar.make(findViewById(R.id.main),err,Snackbar.LENGTH_SHORT);
                        snack.show();
                    }
                });

                                    new Timer().schedule(
                    new TimerTask(){
                
                        @Override
                        public void run(){
                            
                        startActivity(getIntent());   
                        finish();
                        }
                        
                    }, 1000);

        return true;
    }
}