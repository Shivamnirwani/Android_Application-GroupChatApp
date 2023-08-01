package com.example.groupchatapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class Group extends AppCompatActivity {
    JSONObject memberlist;
    String JWT;
    String chatname;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        chatname = getIntent().getStringExtra("message");
        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        actionBar.setTitle(chatname);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME |ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP);

        actionBar.setElevation(20);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("UTA Group Chat", 0); // 0 - for private mode
        JWT = pref.getString("JWT", null); // getting String
        if (JWT == null){
            switchtoLoginActivity();
            finish();
        }


        memberlist= null;

        try {
            memberlist = new JSONObject(
                    "{" +
                            "\"members\" : " +
                            "[" +
                            "{" +
                            "\"name\" : \"Chilli\"," +
                            "\"isAdmin\" : true,"+
                            "\"isSelf\" : true"+
                            "}," +
                            "{" +
                            "\"name\" : \"Potato\"," +
                            "\"isAdmin\" : false,"+
                            "\"isSelf\" : false"+
                            "}," +
                            "{" +
                            "\"name\" : \"Tomato\"," +
                            "\"isAdmin\" : false,"+
                            "isSelf : false"+
                            "}" +
                            "]" +
                            "}"
            );
        } catch (JSONException e) {
            Log.d("exception",e.toString());
        }
        getmembers(chatname);
        updateView(memberlist);
        Button AddButton = findViewById(R.id.addbutton);
        AddButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText email = findViewById(R.id.edit_gchat_message);
                Log.d("BUTTONS", "User tapped the Login Button on Register Activity");
                addmember(email.getText().toString());

            }
        });

         Button deletebutton = findViewById(R.id.deletebutton);
        deletebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteGroup();

            }
        });


        }
    private void deleteGroup(){
        JSONObject reqObject = new JSONObject();
                    try {
                        reqObject.put("groupName", chatname);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AndroidNetworking.post("https://1cec-12-184-115-59.ngrok-free.app/leaveGroup")
                            .addJSONObjectBody(reqObject)
                            .setTag("test")
                            .addHeaders("Authorization", JWT)
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsJSONObject( new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // do anything with response
                                    Log.d("f","f");
                                    try {
                                        if (response.getBoolean("success")) {
                                            Snackbar snack = Snackbar.make(findViewById(R.id.main),"Group Deleted",Snackbar.LENGTH_SHORT);
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
    private void addmember(String email){
        JSONObject reqObject = new JSONObject();
                    try {
                        reqObject.put("email", email);
                        reqObject.put("groupName", chatname);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AndroidNetworking.post("https://1cec-12-184-115-59.ngrok-free.app/addMember")
                            .addJSONObjectBody(reqObject)
                            .setTag("test")
                            .addHeaders("Authorization", JWT)
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsJSONObject( new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // do anything with response
                                    Log.d("f","f");
                                    try {
                                        if (response.getBoolean("success")) {
                                            Snackbar snack = Snackbar.make(findViewById(R.id.main),"Member Added",Snackbar.LENGTH_SHORT);
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

    private void getmembers(String chatname){
        AndroidNetworking.get("https://1cec-12-184-115-59.ngrok-free.app/members")
                .addHeaders("Authorization",JWT)
                .addQueryParameter("groupName",chatname)
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        Log.d("response",response.toString());
                        memberlist = response;
                        updateView(memberlist);
                    }
                    @Override
                    public void onError(ANError error) {
                        Log.d("error",error.toString());
                        Snackbar.make(findViewById(R.id.grouprecycler), "Error getting members", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
    }
private void updateView(JSONObject memberlist){
    
            RecyclerView grouprview = (RecyclerView) findViewById(R.id.grouprecycler);

        boolean isAdmin = false;
        JSONArray grouparray = null;
        try {
            isAdmin = memberlist.getBoolean("isAdmin");
            grouparray = memberlist.getJSONArray("members");
        } catch (JSONException e) {
            isAdmin= false;
            grouparray = new JSONArray();
        }
        if (!isAdmin){
            ((Button)findViewById(R.id.deletebutton)).setText("Leave Group");
            ((Button)findViewById(R.id.deletebutton)).setVisibility(View.VISIBLE);

            findViewById(R.id.layout_gchat_chatbox).setVisibility(View.GONE);
        } else{
            ((Button)findViewById(R.id.deletebutton)).setText("Delete Group");
            ((Button)findViewById(R.id.deletebutton)).setVisibility(View.VISIBLE);

            findViewById(R.id.layout_gchat_chatbox).setVisibility(View.VISIBLE);
        }

        GroupAdapter adapter = new GroupAdapter(grouparray, isAdmin);
            grouprview.setAdapter(adapter);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            grouprview.setLayoutManager(llm);

            if (isAdmin) {
                ItemClickSupport.addTo(grouprview).setOnItemClickListener(
                        new ItemClickSupport.OnItemClickListener() {
                            @Override
                            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                                // do stuff
                                Log.d("BUTTONS", "User tapped the " + position + " button on the Chat Activity");
                                TextView name = (TextView) v.findViewById(R.id.memberName);
                                deletemember(name.getText().toString());

                            }
                        }
                );
            }
}

    private void deletemember(String member) {

        new AlertDialog.Builder(this,R.style.AlertDialogStyle)
            .setTitle("Remove "+member+"?")
            .setMessage("")
            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    JSONObject reqObject = new JSONObject();
                    try {
                        reqObject.put("member", member);
                        reqObject.put("groupName", chatname);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AndroidNetworking.post("https://1cec-12-184-115-59.ngrok-free.app/removeMember")
                            .addJSONObjectBody(reqObject)
                            .setTag("test")
                            .addHeaders("Authorization", JWT)
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsJSONObject( new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // do anything with response
                                    Log.d("f","f");
                                    try {
                                        if (response.getBoolean("success")) {
                                            Snackbar snack = Snackbar.make(findViewById(R.id.main),"Member Removed",Snackbar.LENGTH_SHORT);
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
    @Override
    public boolean onSupportNavigateUp() {
        switchtoChatActivity(chatname);
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
    private void switchtoChatActivity(String chatname) {
        Intent switchActivityIntent = new Intent(this, Chat.class);
        switchActivityIntent.putExtra("message", chatname);
        startActivity(switchActivityIntent);
    }

        private void switchtoHomeActivity(){
        Intent switchActivityIntent = new Intent(this, Home.class);
        startActivity(switchActivityIntent);
    }
}