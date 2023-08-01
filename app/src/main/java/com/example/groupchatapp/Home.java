package com.example.groupchatapp;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Home extends AppCompatActivity {
    JSONArray list;

    String JWT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        String message = getIntent().getStringExtra("message");
        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        actionBar.setTitle(" UTA Group Chat");
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME |ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_USE_LOGO);
        actionBar.setLogo(R.drawable.logoactionbar);
        actionBar.setElevation(20);
//        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#5D2940")));

        SharedPreferences pref = getApplicationContext().getSharedPreferences("UTA Group Chat", 0); // 0 - for private mode
        JWT = pref.getString("JWT", null); // getting String
        if (JWT == null){
            switchtoLoginActivity();
            finish();
        }
        Log.d("JWT", JWT);


//        list = Arrays.asList("Group 1", "Poop Flingers", "Potato Farmers", "Casual Vegans", "Dallas Mavericks", "Slumdog Millionaires", "Plant Parents", "D", "Tomato Receipes", "Totally unsafe and Unwarranted");
         list = new JSONArray();
         getHome();



        updateView(list);




    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    private void updateView(JSONArray list){
        RecyclerView homerview = (RecyclerView) findViewById(R.id.homerecycler);
        HomeAdapter adapter = new HomeAdapter(list);
        homerview.setAdapter(adapter);
        homerview.setLayoutManager(new LinearLayoutManager(this));
        ItemClickSupport.addTo(homerview).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        // do stuff
                        Log.d("BUTTONS", "User tapped the "+position+" button on the Home Activity");
                        TextView group = v.findViewById(R.id.groupName);
                        String groupName = (String) group.getText();
                        Log.d("sd",groupName);
                        switchtoChatActivity(groupName);
                                                finish();
                    }
                }
        );
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                switchtoLoginActivity();
                return true;
            case R.id.account:
                switchtoUserActivity();
                return true;
            case R.id.personal:

            case R.id.newgroup:
                createChat();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
    finish();
    return true;
    }

    private void createChat(){
        final EditText txtUrl = new EditText(this);
        txtUrl.setTextColor(ContextCompat.getColor(this, R.color.Silver));
        txtUrl.setHintTextColor(ContextCompat.getColor(this, R.color.Silver));
// Set the default text to a link of the Queen
        txtUrl.setHint("Enter Name");
        txtUrl.setPadding(10, 10,10,10);

        new AlertDialog.Builder(this,R.style.AlertDialogStyle)
                .setTitle("Create Chat")
                .setMessage("")
                .setView(txtUrl)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newname = txtUrl.getText().toString();
                        Log.d("newname", newname);
                        JSONObject reqObject = new JSONObject();
                        try {
                            reqObject.put("newname", newname);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        AndroidNetworking.post("https://1cec-12-184-115-59.ngrok-free.app/createChat")
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
                                                Snackbar snack = Snackbar.make(findViewById(R.id.main),"Group Created",Snackbar.LENGTH_SHORT);
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
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();


    }
    private void getHome()  {

        AndroidNetworking.get("https://1cec-12-184-115-59.ngrok-free.app/allChats")
                 .addHeaders("Authorization", JWT)
                 .setTag("test")
                 .setPriority(Priority.LOW)
                 .build()
                 .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                      // do anything with response
                        Log.d("res", response.toString());
                      updateView(response);
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

    private void switchtoChatActivity(String chatname) {
        Intent switchActivityIntent = new Intent(this, Chat.class);
        switchActivityIntent.putExtra("message", chatname);
        startActivity(switchActivityIntent);
    }
    private void switchtoUserActivity() {
        Intent switchActivityIntent = new Intent(this, User.class);
        startActivity(switchActivityIntent);
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
}