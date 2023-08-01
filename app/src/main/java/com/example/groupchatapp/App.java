package com.example.groupchatapp;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Parse SDK stuff goes here
        AndroidNetworking.initialize(getApplicationContext());
    }
}
