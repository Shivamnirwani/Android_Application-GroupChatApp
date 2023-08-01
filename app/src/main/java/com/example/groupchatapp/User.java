package com.example.groupchatapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class User extends AppCompatActivity {
    String JWT;
    EditText fName;
    EditText lName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        String message = getIntent().getStringExtra("message");
        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        actionBar.setTitle("User Profile");
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME |ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP);


        SharedPreferences pref = getApplicationContext().getSharedPreferences("UTA Group Chat", 0); // 0 - for private mode
        JWT = pref.getString("JWT", null); // getting String
        if (JWT == null){
            switchtoLoginActivity();
            finish();
        }
        fName = findViewById(R.id.FirstName);
        lName = findViewById(R.id.LastName);

        getProfile();
        Button UpdateButton = findViewById(R.id.change_button);
        UpdateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTONS", "User tapped the Update button on the User Activity");

                updateProfile(lName.getText().toString(), fName.getText().toString());
            }
        });
        Button ChangeButton = findViewById(R.id.reset_button);
        ChangeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTONS", "User tapped the Update button on the User Activity");

                switchtoPasswordActivity();
            }
        });


    }

    private void getProfile(){
        AndroidNetworking.get("https://1cec-12-184-115-59.ngrok-free.app/getProfile")
                 .addHeaders("Authorization", JWT)
                 .setTag("test")
                 .setPriority(Priority.LOW)
                 .build()
                 .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                      // do anything with response
                        Log.d("res", response.toString());
                        try {
                            if (response.getBoolean("success")) {
                                String first = response.getString("firstname");
                                String last = response.getString("lastname");
                                fName.setText(first);
                                lName.setText(last);


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                            err = "Error Please Try again";
                        }
                        Log.d("fail", "login fail");
                        Snackbar snack = Snackbar.make(findViewById(R.id.main),err,Snackbar.LENGTH_SHORT);
                        snack.show();
                    }
                });
    }

    private void updateProfile(String lName, String fName) {
        JSONObject reqObject = new JSONObject();
                        try {
                            reqObject.put("lname", lName);
                            reqObject.put("fname", fName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

        AndroidNetworking.post("https://1cec-12-184-115-59.ngrok-free.app/updateProfile")
                                .addJSONObjectBody(reqObject)
                                .setTag("test")
                                .addHeaders("Authorization", JWT)
                                .setPriority(Priority.MEDIUM)
                                .build()
                                .getAsJSONObject(new JSONObjectRequestListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        // do anything with response
                                        SharedPreferences pref = getApplicationContext().getSharedPreferences("UTA Group Chat", 0);
                                        try {
                                            if (response.getBoolean("success")) {
                                                Snackbar snack = Snackbar.make(findViewById(R.id.main),"Profile Updated",Snackbar.LENGTH_SHORT);
                                                snack.show();

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
                                            err = "Error Please Try again";
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
                    
        
    }
    
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void switchtoLoginActivity() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("UTA Group Chat", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();


        Intent switchActivityIntent = new Intent(this, MainActivity.class);
        startActivity(switchActivityIntent);
        finish();
    }
    private void switchtoPasswordActivity() {
        Intent switchActivityIntent = new Intent(this, ChangePassword.class);
        startActivity(switchActivityIntent);
    }
}