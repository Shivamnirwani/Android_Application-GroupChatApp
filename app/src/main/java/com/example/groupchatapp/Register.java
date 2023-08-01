package com.example.groupchatapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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

public class Register extends AppCompatActivity {
    
    EditText FirstName;
    EditText LastName;
    EditText Email;
    EditText Password;
    EditText ConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        String message = getIntent().getStringExtra("message");

        FirstName = findViewById(R.id.FirstName);
        LastName = findViewById(R.id.LastName);
        Email = findViewById(R.id.EmailAddress);
        Password = findViewById(R.id.Password);
        ConfirmPassword = findViewById(R.id.ConfirmPassword);





        Button LoginButton = findViewById(R.id.login_button);
        LoginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTONS", "User tapped the Login Button on Register Activity");
                switchtoLoginActivity();
                finish();
            }
        });
        Button RegisterButton = findViewById(R.id.register_button);
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTONS", "User tapped the RegisterButton on Register Activity");
                register();
            }
        });
    }


    private boolean register (){
        JSONObject reqObject = new JSONObject();
        try {
            reqObject.put("fname", FirstName.getText().toString());
            reqObject.put("lname", LastName.getText().toString());
            reqObject.put("email", Email.getText().toString());
            reqObject.put("password", Password.getText().toString());
            reqObject.put("cpassword", ConfirmPassword.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post("https://1cec-12-184-115-59.ngrok-free.app/register")
                .addJSONObjectBody(reqObject)
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    // This is the success method
                    @Override
                    public void onResponse(JSONObject response) {
                        Snackbar snack = Snackbar.make(findViewById(R.id.main),"Successfully Registered",Snackbar.LENGTH_SHORT);
                        snack.show();
                        Log.d("Register", "onResponse: " + response.toString());


                                                            new Timer().schedule(
                    new TimerTask(){
                
                        @Override
                        public void run(){
                            
                        switchtoLoginActivity();
                        finish();
                        }
                        
                    }, 1000);
                    }
                    // This is the error method it will tell you why the post request failed
                    @Override
                    public void onError(ANError error) {
                        JSONObject response = null;
                        String err = null;
                        try {
                            response = new JSONObject(error.getErrorBody());
                            err = response.getString("status");

                        } catch (JSONException e) {
                            err = "Registration Error";
                        }
                        Log.d("fail", "register fail");
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
    private void switchtoLoginActivity() {
        Intent switchActivityIntent = new Intent(this, MainActivity.class);
        startActivity(switchActivityIntent);
    }
}