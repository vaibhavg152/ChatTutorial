package com.example.vaibhav.chattutorial;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.vaibhav.chattutorial.util.ConvoPreview;
import com.example.vaibhav.chattutorial.util.ConvoPreviewAdapter;
import com.example.vaibhav.chattutorial.util.SharedPrefManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class ConversationList extends AppCompatActivity {
    //constants
    private static final String TAG = "ConversationList";

    //widgets
    private FloatingActionButton fabAddConvo;
    private ImageView imgMyProfPic;
    private ImageView imgSearchIcon;
    private TextView tvMyName;
    private ListView listConvos;
    private Button btnOptions;

    //variables
    private ArrayList<ConvoPreview> arrayListConvoPreview;
    private ConvoPreviewAdapter adapter;
    private FirebaseUser user;
    private Uri uriImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convo_list);
        Log.d(TAG, "onCreate: ");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        initialise();

        setImage(imgMyProfPic,user.getPhotoUrl());

        tvMyName.setText(user.getDisplayName());

        adapter = new ConvoPreviewAdapter(this,R.layout.listview_convo_preview, arrayListConvoPreview);
        listConvos.setAdapter(adapter);

        fabAddConvo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addConversation();
            }
        });

        imgSearchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });

        btnOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadOptions();
            }
        });

        listConvos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) { openConversation(i); }
        });

    }

    private void initialise(){
        Log.d(TAG, "initialise: ");

        fabAddConvo   = (FloatingActionButton) findViewById(R.id.fabAddConvo);
        imgMyProfPic  = (ImageView) findViewById(R.id.imgMyProfPic);
        imgSearchIcon = (ImageView) findViewById(R.id.imgBtnSearchList);
        tvMyName      = (TextView)  findViewById(R.id.txtMyName);
        listConvos    = (ListView)  findViewById(R.id.lvConvo);
        btnOptions    = (Button)    findViewById(R.id.btnOptions);

        user = FirebaseAuth.getInstance().getCurrentUser();
        arrayListConvoPreview = SharedPrefManager.getInstance(this).getLastFewConersations(10);
        arrayListConvoPreview.add(new ConvoPreview("No new Messages here","Robert","abc",
                Uri.parse(getResources().getString(R.string.defaultDPUri))));
    }

    private void setImage(ImageView imgMyProfPic, Uri photoUrl) {
        Log.d(TAG, "setImage: ");
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(new URL(photoUrl.toString()).openConnection().getInputStream());
//            bitmap.compress(Bitmap.CompressFormat.PNG,10,stream);
//            byte[] bytes = stream.toByteArray();
//            bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            imgMyProfPic.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            imgMyProfPic.setImageDrawable(getDrawable(R.drawable.ic_person));
        }
    }

    private void openConversation(int position) {
        Log.d(TAG, "openConversation: ");

        ConvoPreview preview = arrayListConvoPreview.get(position);
        Intent intent = new Intent(ConversationList.this,ChatActivity.class);
        intent.putExtra("name",preview.getUserName());
        intent.putExtra("id",preview.getUserId());
        intent.putExtra("image",preview.getImageUri().toString());

        startActivity(intent);
    }

    private void loadOptions() {
        Log.d(TAG, "loadOptions: ");

        startActivity(new Intent(ConversationList.this,OptionsActivity.class));
        finish();
    }

    private void search() {
        Log.d(TAG, "search: ");

        final EditText etSearchBar = (EditText) findViewById(R.id.etSearchConvo);
        etSearchBar.setVisibility(View.VISIBLE);

        etSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, "onTextChanged: ");

                adapter.getFilter().filter(charSequence);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void addConversation() {
        Log.d(TAG, "addConversation: ");

        Intent intent = new Intent(ConversationList.this,AllPeopleList.class);
        startActivity(intent);
        finish();
    }

}
