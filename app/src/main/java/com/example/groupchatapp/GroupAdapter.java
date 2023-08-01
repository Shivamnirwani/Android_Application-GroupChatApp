
package com.example.groupchatapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private JSONArray members;
    private boolean isAdmin;

    public GroupAdapter(JSONArray members, boolean isAdmin) {
        this.members = members;
        this.isAdmin = isAdmin;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView memberName;
        public Button removeMember;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            removeMember = (Button) itemView.findViewById(R.id.removeMember);
            memberName = (TextView) itemView.findViewById(R.id.memberName);
            
        }
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.group_element, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(GroupAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        JSONObject member;
        try {
            member= members.getJSONObject(position);
            if (!isAdmin) {
                holder.removeMember.setVisibility(View.GONE);
            }
            holder.memberName.setText(member.getString("name"));
        } catch (JSONException e) {
            Log.d("s","exceptions");
        }


    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return members.length();
    }


}