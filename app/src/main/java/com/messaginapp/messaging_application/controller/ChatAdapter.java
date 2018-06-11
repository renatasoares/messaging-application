package com.messaginapp.messaging_application.controller;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.messaginapp.messaging_application.R;
import com.messaginapp.messaging_application.model.Chat;

import java.util.List;

/**
 * Created by renata on 21/05/18.
 */

public class ChatAdapter extends ArrayAdapter<Chat> {

    public ChatAdapter(Context context, int resource, List<Chat> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_chat, parent, false);
        }

        TextView chatTextView = (TextView) convertView.findViewById(R.id.chatTitle);

        Chat chat = getItem(position);
        chatTextView.setText(chat.getRoomName());

        return convertView;
    }
}

