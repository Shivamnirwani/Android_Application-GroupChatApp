package com.example.groupchatapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

public class Chat extends AppCompatActivity {
    String JWT;
    JSONObject chatlist;
    String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        groupName = getIntent().getStringExtra("message");
        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        actionBar.setTitle(groupName);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME |ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_USE_LOGO);
        actionBar.setElevation(20);


        SharedPreferences pref = getApplicationContext().getSharedPreferences("UTA Group Chat", 0); // 0 - for private mode
        JWT = pref.getString("JWT", null); // getting String
        if (JWT == null){
            switchtoLoginActivity();
            finish();
        }

        chatlist= new JSONObject();
        getchat(groupName);

        try {
            chatlist = new JSONObject(
                    "{" +
                        "\"chats\" : " +
                            "[" +
                                "{" +
                                    "\"name\" : \"Chilli\"," +
                                    "\"message\" : \"Hi\","+
                                    "\"isSelf\" : true"+
                                "}," +
                                "{" +
                                    "\"name\" : \"Potato\"," +
                                    "\"message\" : \"Hello\","+
                                    "\"isSelf\" : false"+
                                "}," +
                                "{" +
                                "\"name\" : \"Kaushal\"," +
                                "\"message\" : \"Hi\","+
                                "\"isSelf\" : true"+
                                "}," +
                                "{" +
                                    "\"name\" : \"Tomato\"," +
                                    "\"message\" : \"big message big message big message big message big message\","+
                                    "isSelf : false"+
                                "}" +
                            "]" +
                    "}"
            );


        } catch (JSONException e) {
            Log.d("exception",e.toString());
        }
        updateView(chatlist);


        Button sendButton = findViewById(R.id.sendbutton);

        EditText messageHolder = findViewById(R.id.edit_gchat_message);
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendMessage(String.valueOf(messageHolder.getText()));
            }
        });

    }
    private void sendMessage(String message){

        JSONObject reqObject = new JSONObject();
                        try {
                            reqObject.put("message", message);
                            reqObject.put("groupName", groupName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

        AndroidNetworking.post("https://1cec-12-184-115-59.ngrok-free.app/sendMessage")
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
                                                Snackbar snack = Snackbar.make(findViewById(R.id.main),"Message Sent",Snackbar.LENGTH_SHORT);
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
    private void updateView(JSONObject chatlist) {
        Log.d("in updateView", "d");
            RecyclerView chatrview = (RecyclerView) findViewById(R.id.chatrecycler);
            JSONArray chatarray = new JSONArray();
            try {
                chatarray = chatlist.getJSONArray("chats");
            }catch (Exception e){

            }
            ChatAdapter adapter = new ChatAdapter(chatarray);
            chatrview.setAdapter(adapter);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setReverseLayout(true);
            chatrview.setLayoutManager(llm);
            ItemClickSupport.addTo(chatrview).setOnItemLongClickListener(
                    new ItemClickSupport.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                            // do stuff
                            TextView message = v.findViewById(R.id.selfmessage);
                            String messageText = message.getText().toString();
                            if (messageText.equals("")){
                                return false;
                            }
                            Log.d("BUTTONS", "User long tapped the "+position+" button on the Chat Activity");
                            deleteChat(messageText);
//                            finish();
                            return true;
                        }
                    }
            );
    }
    private void getchat(String groupName) {
        Log.d("here","here");
        AndroidNetworking.get("https://1cec-12-184-115-59.ngrok-free.app/chat")
                 .addHeaders("Authorization", JWT)
                 .addQueryParameter("groupName", groupName)
                 .setTag("test")
                 .setPriority(Priority.LOW)
                 .build()
                 .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
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


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.manage:
                switchtoGroupActivity();
                return true;
            case R.id.media:
                imageChooser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void imageChooser()
    {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        launchSomeActivity.launch(i);
    }

    ActivityResultLauncher<Intent> launchSomeActivity
            = registerForActivityResult(
            new ActivityResultContracts
                    .StartActivityForResult(),
            result -> {
                if (result.getResultCode()
                        == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // do your operation from here....
                    if (data != null
                            && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        Log.d("uri",selectedImageUri.toString());
                        upload(selectedImageUri);


                    }
                }
            });
    private void switchtoGroupActivity() {
        Intent switchActivityIntent = new Intent(this, Group.class);
        switchActivityIntent.putExtra("message", groupName);
        startActivity(switchActivityIntent);
//        finish();
    }

    private void upload(Uri image){
        File f = new File(image.toString());
        try {
            f = getFile(getApplicationContext(), image);
        } catch (IOException e) {
            Log.d("io","io");
            }
        AndroidNetworking.upload("https://1cec-12-184-115-59.ngrok-free.app/upload")
                .addMultipartFile("file",f)
                .addMultipartParameter("chat",groupName)
                .addMultipartParameter("JWT",JWT)
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {
                            if( response.getBoolean("success")){
                                Snackbar snack = Snackbar.make(findViewById(R.id.main),"Media Uploaded",Snackbar.LENGTH_SHORT);
                                snack.show();


                            }
                        } catch (JSONException e) {
                            Log.d("dd","af");
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.d("FS",error.toString());
                    }
                });

                    new Timer().schedule(
                    new TimerTask(){
                
                        @Override
                        public void run(){
                            
                        startActivity(getIntent());   
                        finish();
                        }
                        
                    }, 2000);

    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        switchtoHomeActivity();
        return true;
    }

    private void switchtoHomeActivity(){
        Intent switchActivityIntent = new Intent(this, Home.class);
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
    private void deleteChat(String messageText){


        new AlertDialog.Builder(this,R.style.AlertDialogStyle)
            .setTitle("Delete Message")
            .setMessage("")
            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    JSONObject reqObject = new JSONObject();
                    try {
                        reqObject.put("message", messageText);
                        reqObject.put("groupName", groupName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AndroidNetworking.post("https://1cec-12-184-115-59.ngrok-free.app/deleteMessage")
                            .addJSONObjectBody(reqObject)
                            .setTag("test")
                            .addHeaders("Authorization", JWT)
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // do anything with response
                                    Log.d("f","f");
                                    try {
                                        if (response.getBoolean("success")) {
                                            Snackbar snack = Snackbar.make(findViewById(R.id.main),"Message Deleted",Snackbar.LENGTH_SHORT);
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
    public static File getFile(Context context, Uri uri) throws IOException {
        File destinationFilename = new File(context.getFilesDir().getPath() + File.separatorChar + queryName(context, uri));
        try (InputStream ins = context.getContentResolver().openInputStream(uri)) {
            createFileFromStream(ins, destinationFilename);
        } catch (Exception ex) {
            Log.e("Save File", ex.getMessage());
            ex.printStackTrace();
        }
        return destinationFilename;
    }

    public static void createFileFromStream(InputStream ins, File destination) {
        try (OutputStream os = new FileOutputStream(destination)) {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = ins.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
        } catch (Exception ex) {
            Log.e("Save File", ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static String queryName(Context context, Uri uri) {
        Cursor returnCursor =
                context.getContentResolver().query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }
}