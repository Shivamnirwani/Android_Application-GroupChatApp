package com.example.groupchatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class HomeAdapter  extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private JSONArray homeList;

    public HomeAdapter(JSONArray homelist) {
        homeList = homelist;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView groupName;
        public TextView message;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            groupName = (TextView) itemView.findViewById(R.id.groupName);
            message = (TextView) itemView.findViewById(R.id.lastmessage);
        }
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.home_element, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(HomeAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        JSONObject group = null;
        try {
            group = homeList.getJSONObject(position);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // Set item views based on your views and data model
        TextView groupName = holder.groupName;
        try {
            groupName.setText(group.getString("name"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        TextView message = holder.message;
        try {
            message.setText(group.getString("message"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return homeList.length();
    }


}