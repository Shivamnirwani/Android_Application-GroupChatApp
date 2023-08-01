package com.example.groupchatapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private JSONArray chatmessage;

    public ChatAdapter(JSONArray chatMessage) {
        chatmessage = chatMessage;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView selffirstName;
        public TextView selfmessage;
        public TextView elsefirstName;
        public TextView elsemessage;
        public LinearLayout selfchat;
        public LinearLayout elsechat;

        public LinearLayout imagechat;
        public TextView imageName;
        public ImageView imageview1;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            imagechat = (LinearLayout) itemView.findViewById(R.id.imagechat);
            imageName = (TextView) itemView.findViewById(R.id.imageName);
            selfchat = (LinearLayout) itemView.findViewById(R.id.selfchat);
            elsechat = (LinearLayout) itemView.findViewById(R.id.elsechat);
            selffirstName = (TextView) itemView.findViewById(R.id.selffirstName);
            selfmessage = (TextView) itemView.findViewById(R.id.selfmessage);
            elsefirstName = (TextView) itemView.findViewById(R.id.elsefirstName);
            elsemessage = (TextView) itemView.findViewById(R.id.elsemessage);
            imageview1 = (ImageView) itemView.findViewById(R.id.imagepic);
//            imageview1.setImageBitmap()
        }
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.chat_element, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ChatAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        JSONObject chat;
        try {
            chat= chatmessage.getJSONObject(position);
            if (chat.getBoolean("isFile")){
                holder.imagechat.setVisibility(View.VISIBLE);

                holder.imageName.setText(chat.getString("name"));
                if ( chat.getBoolean("isSelf")) {
                    holder.imageName.setText("You");
                    holder.selfmessage.setText(chat.getString("message"));
                    holder.selffirstName.setText(chat.getString("name"));

                }
                String imageUri = "https://1cec-12-184-115-59.ngrok-free.app/download/"+chat.getString("message");

                Picasso.with(holder.itemView.getContext()).load(imageUri).into(holder.imageview1);
            }else{
            if ( chat.getBoolean("isSelf")){
                holder.selfchat.setVisibility(View.VISIBLE);
                holder.selffirstName.setText(chat.getString("name"));
                holder.selfmessage.setText(chat.getString("message"));
            } else{
                holder.elsechat.setVisibility(View.VISIBLE);
                holder.elsefirstName.setText(chat.getString("name"));
                holder.elsemessage.setText(chat.getString("message"));
            }}
        } catch (JSONException e) {
            Log.d("s","exceptions");
            holder.elsefirstName.setText("Deleted User");

        }


    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return chatmessage.length();
    }


}