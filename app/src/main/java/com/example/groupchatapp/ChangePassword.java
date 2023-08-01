package com.example.groupchatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;

public class ChangePassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);




        Button UpdateButton = findViewById(R.id.reset_button);
        UpdateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTONS", "User tapped the Update button on the User Activity");
                Snackbar snack = Snackbar.make(findViewById(R.id.main),"Password Changed",Snackbar.LENGTH_SHORT);
                snack.show();
            }
        });
    }
}