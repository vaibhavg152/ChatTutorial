package com.example.vaibhav.chattutorial.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vaibhav.chattutorial.R;

import java.util.ArrayList;

public class MessagesListAdapter extends ArrayAdapter<ChatMessage> {
    //constants
    private static final String TAG = "MessagesListAdapter";
    private Context context;
    private int resourceIdSend, resourceIdReceive;
    private ArrayList<ChatMessage> msgArrayList;

    public MessagesListAdapter(@NonNull Context context, int resourceSend, int resourceReceive,
                               @NonNull ArrayList<ChatMessage> objects) {
        super(context, resourceSend, objects);
        this.context = context;
        resourceIdSend = resourceSend;
        resourceIdReceive = resourceReceive;
        msgArrayList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.d(TAG, "getView: ");

        int resourceId = ( (msgArrayList.get(position).isSent()) ? resourceIdSend : resourceIdReceive);

        convertView = LayoutInflater.from(context).inflate(resourceId,parent,false);
        TextView  tvTime    = (TextView)  convertView.findViewById(R.id.txtTimeMessageChat);
        TextView  txtMessage = (TextView)  convertView.findViewById(R.id.txtMessageChat);

        ChatMessage message = msgArrayList.get(position);

        txtMessage.setText(message.getMsgText());
        tvTime.setText(message.getMsgTime());

        return convertView;
    }

}
