package com.example.vaibhav.chattutorial;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaibhav.chattutorial.util.ChatMessage;
import com.example.vaibhav.chattutorial.util.ConvoPreview;
import com.example.vaibhav.chattutorial.util.ExtStorageManager;
import com.example.vaibhav.chattutorial.util.MessagesListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    //constants
    private static final String TAG = "ChatActivity";

    //widgets
    private ListView  lvMessages;
    private TextView  tvName;
    private TextView  tvActivity;
    private EditText  etInputMessage;
    private ImageView imgProfPic;
    private FloatingActionButton fabSend;

    //variables
    private DatabaseReference reference;
    private FirebaseUser user;
    private MessagesListAdapter adapter;
    private ConvoPreview person;
    private ArrayList<ChatMessage> messagesArrayList;
    private ArrayList<Integer> sentPosArrayList;
    private long numMessages = 0;
    private boolean isLoaded = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Log.d(TAG, "onCreate: ");

        Intent incomingIntent = getIntent();
        person = new ConvoPreview("",incomingIntent.getStringExtra("name"),
                incomingIntent.getStringExtra("id"),Uri.parse(incomingIntent.getStringExtra("image")));
        Log.d(TAG, "onCreate: id is "+person.getUserId());

        initialise();
        reference.child(user.getUid()).child("lastSeen").setValue("Online");
    }

    private void initialise() {
        Log.d(TAG, "initialise: ");

        //initialising widgets
        lvMessages     = (ListView)  findViewById(R.id.lvMessages);
        imgProfPic     = (ImageView) findViewById(R.id.imgTheirProfPic);
        tvName         = (TextView)  findViewById(R.id.txtTheirName);
        tvActivity     = (TextView)  findViewById(R.id.txtTheirLastActivity);
        etInputMessage = (EditText)  findViewById(R.id.etTypeMessage);
        fabSend        = (FloatingActionButton) findViewById(R.id.fabSendMsg);

        //initialising variables
        messagesArrayList = new ArrayList<>();
        sentPosArrayList  = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference().child("users");
        user = FirebaseAuth.getInstance().getCurrentUser();

        loadMessages();
        setImage(imgProfPic, person.getImageUri(),person.getUserId());
        tvName.setText(person.getUserName());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    tvActivity.setText(dataSnapshot.child(person.getUserId()).child("lastSeen").getValue(String.class));
                }catch (Exception e){
                    tvActivity.setText("Last Seen not found. :(");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMsg();
            }
        });

    }

    private void sendMsg() {
        Log.d(TAG, "sendMsg: ");

        String text = etInputMessage.getText().toString().trim();
        if (text.length() == 0){
            Toast.makeText(ChatActivity.this,"Please enter some message to send. :)",Toast.LENGTH_SHORT).show();
            return;
        }
        ChatMessage msg = new ChatMessage(user.getDisplayName(),text,true);

        if (isLoaded) {
            //storing record for sender
            Log.d(TAG, "sendMsg: doneSending");
            DatabaseReference curMsg = reference.child(user.getUid())
                    .child(this.getResources().getString(R.string.convo)).child(person.getUserId());
            curMsg.push().setValue(msg);

            //storing record for receiver
            Log.d(TAG, "sendMsg: doneReceiving "+person.getUserId());
            DatabaseReference curMsgReceived = reference.child(person.getUserId())
                    .child(this.getResources().getString(R.string.convo)).child(user.getUid());
            curMsgReceived.push().setValue(new ChatMessage(false,user.getUid(),text,false,msg.getMsgTime()));


        }else Toast.makeText(ChatActivity.this,"Please wait until the previous messages are loaded. :)",Toast.LENGTH_SHORT).show();
        etInputMessage.setText("");
    }

    private void loadMessages() {
        Log.d(TAG, "loadMessages: ");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: ");

                DataSnapshot dsConvo = dataSnapshot.child(user.getUid()).child(getResources().getString(R.string.convo))
                        .child(person.getUserId());
                numMessages = dsConvo.getChildrenCount();

                messagesArrayList.clear();
                for (DataSnapshot dsMsg : dsConvo.getChildren()) {

                    if (dsMsg.hasChild("msgText")) {
                        boolean isSent = dsMsg.child("sent").getValue(Boolean.class);

                        messagesArrayList.add(new ChatMessage(
                                dsMsg.child("isRead").getValue(Boolean.class),
                                person.getUserName(), dsMsg.child("msgText").getValue().toString(),
                                isSent, dsMsg.child("msgTime").getValue(String.class)));
                    }

                }
                if (messagesArrayList.isEmpty())
                    etInputMessage.setHint("Send a message to start the conversation! :)");
                adapter = new MessagesListAdapter(ChatActivity.this,R.layout.lv_messages_item_sent,
                        R.layout.lv_messages_item_recieved,messagesArrayList);
                lvMessages.setAdapter(adapter);
                isLoaded = true;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void setImage(ImageView imgMyProfPic, Uri photoUrl, String imageName) {
        Log.d(TAG, "setImage: ");

        try {
            Bitmap bitmap = ExtStorageManager.getInstance(this).getImageBitmap(imageName,ExtStorageManager.PATH_DP);
            imgProfPic.setImageBitmap(bitmap);
        }catch (IOException e) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(new URL(photoUrl.toString()).openConnection().getInputStream());
                imgMyProfPic.setImageBitmap(bitmap);
            } catch (Exception e2) {
                e2.printStackTrace();
                imgMyProfPic.setImageDrawable(getDrawable(R.drawable.ic_person));
            }
        }
    }

}
