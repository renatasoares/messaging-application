package com.messaginapp.messaging_application.controller;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.messaginapp.messaging_application.R;
import com.messaginapp.messaging_application.model.Acception;
import com.messaginapp.messaging_application.model.AppMessage;

import java.util.List;

public class AcceptionAdapter extends ArrayAdapter<Acception> {

    public AcceptionAdapter(Context context, int resource, List<Acception> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_invitation, parent, false);
        }

        TextView solicitationTextView = (TextView) convertView.findViewById(R.id.newChatInviter);

        Acception solicitation = getItem(position);
        solicitationTextView.setText(solicitation.getIdentifierSender());

        return convertView;
    }

}




