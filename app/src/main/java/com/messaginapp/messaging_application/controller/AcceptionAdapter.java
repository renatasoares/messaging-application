package com.messaginapp.messaging_application.controller;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.messaginapp.messaging_application.R;
import com.messaginapp.messaging_application.model.Acception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AcceptionAdapter extends ArrayAdapter<Acception> {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

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
        solicitationTextView.setText(solicitation.getRoomName());

        ImageButton acceptButton = (ImageButton) convertView.findViewById(R.id.acceptButton);
        ImageButton declineButton = (ImageButton) convertView.findViewById(R.id.declineButton);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("solicitations");

        acceptButton.setTag(position);
        declineButton.setTag(position);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (int) view.getTag();
                Acception item = getItem(position);
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/" + item.getId() + "/response", true);
                databaseReference.updateChildren(childUpdates);
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (int) view.getTag();
                Acception item = getItem(position);
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/" + item.getId() + "/response", false);
                databaseReference.updateChildren(childUpdates);
            }
        });

        return convertView;
    }

}




