package com.example.vaibhav.chattutorial;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaibhav.chattutorial.util.ChatMessage;
import com.example.vaibhav.chattutorial.util.ChatsAppUser;
import com.example.vaibhav.chattutorial.util.ConvoPreviewAdapter;
import com.example.vaibhav.chattutorial.util.ExtStorageManager;
import com.example.vaibhav.chattutorial.util.SharedPrefManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class ConversationListActivity extends AppCompatActivity {
    //constants
    private static final String TAG = "ConvListActivity";
    private static final int REQUEST_CODE_SELECT_IMAGE = 999;

    //widgets
    private FloatingActionButton fabAddConvo;
    private ImageView imgMyProfPic;
    private ImageView imgSearchIcon;
    private TextView tvMyName;
    private ListView listConvos;
    private ImageButton btnOptions;

    //variables
    private ArrayList<ChatsAppUser> arrayListChatsAppUser;
    private ConvoPreviewAdapter adapter;
    private FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convo_list);
        Log.d(TAG, "onCreate: ");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        initialise();
        updateLastSeen();

        try {
            Bitmap bitmap = ExtStorageManager.getInstance(this).getImageBitmap(ExtStorageManager.MY_IMAGE,ExtStorageManager.PATH_DP);
            imgMyProfPic.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            setImage(imgMyProfPic,user.getPhotoUrl());
        }

        tvMyName.setText(user.getDisplayName());

        adapter = new ConvoPreviewAdapter(this,R.layout.listview_convo_preview, arrayListChatsAppUser);
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
                loadOptions(view);
            }
        });

        listConvos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) { openConversation(i); }
        });

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        arrayListChatsAppUser = SharedPrefManager.getInstance(this).getLastFewConersations(10);
        adapter = new ConvoPreviewAdapter(this,R.layout.listview_convo_preview, arrayListChatsAppUser);
        listConvos.setAdapter(adapter);
    }

    private void initialise(){
        Log.d(TAG, "initialise: ");

        fabAddConvo   = (FloatingActionButton) findViewById(R.id.fabAddConvo);
        imgMyProfPic  = (ImageView)   findViewById(R.id.imgMyProfPic);
        imgSearchIcon = (ImageView)   findViewById(R.id.imgBtnSearchList);
        tvMyName      = (TextView)    findViewById(R.id.txtMyName);
        listConvos    = (ListView)    findViewById(R.id.lvConvo);
        btnOptions    = (ImageButton) findViewById(R.id.btnOptions);

        user = FirebaseAuth.getInstance().getCurrentUser();
        arrayListChatsAppUser = SharedPrefManager.getInstance(this).getLastFewConersations(10);
        arrayListChatsAppUser.add(new ChatsAppUser("No new Messages here","Robert","abc",
                Uri.parse(getResources().getString(R.string.defaultDPUri))));
    }

    private void updateLastSeen() {
        Log.d(TAG, "updateLastSeen: ");

        Date date = new Date();
        String time = date.getHours()+":" + date.getMinutes() +" ("+ date.getDate() + " "+ ChatMessage.getMonthString(date.getMonth())+")";
        FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("lastSeen").setValue(time);
    }

    private void setImage(ImageView imgMyProfPic, Uri photoUrl) {
        Log.d(TAG, "setImage: ");
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new URL(photoUrl.toString()).openConnection().getInputStream());
            imgMyProfPic.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            imgMyProfPic.setImageDrawable(getDrawable(R.drawable.ic_person));
        }
    }

    private void openConversation(int position) {
        Log.d(TAG, "openConversation: ");

        ChatsAppUser preview = arrayListChatsAppUser.get(position);
        Intent intent = new Intent(ConversationListActivity.this,ChatActivity.class);
        intent.putExtra("name",preview.getUserName());
        intent.putExtra("id",preview.getUserId());
        intent.putExtra("image",preview.getImageUri().toString());

        startActivity(intent);
    }

    private void loadOptions(View view) {
        Log.d(TAG, "loadOptions: ");

        PopupMenu popupMenu = new PopupMenu(this,view,Gravity.AXIS_X_SHIFT);
        popupMenu.getMenuInflater().inflate(R.menu.menu_options_convolist,popupMenu.getMenu());
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Log.d(TAG, "onMenuItemClick: ");

                switch (menuItem.getItemId()){
                    case R.id.optDP :       changeDp(); break;
                    case R.id.optSignOut :  signOut();  break;
                }


                return false;
            }
        });
    }

    private void changeDp() {
        Log.d(TAG, "changeDp: ");

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,REQUEST_CODE_SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: ");
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK){
            if (data != null && data.getData() != null){
                Log.d(TAG, "onActivityResult: success");

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/dp/"+user.getUid());

                storageReference.putFile(data.getData()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uriImage) {

                                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setPhotoUri(uriImage).build();

                                user.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "onComplete: lets see");
                                            Toast.makeText(ConversationListActivity.this, "Profile picture updated", Toast.LENGTH_LONG).show();
                                            FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("image")
                                                    .setValue(uriImage.toString());
                                            imgMyProfPic.setImageURI(data.getData());

                                            try {
                                                ExtStorageManager.getInstance(ConversationListActivity.this).saveImage(data.getData(),10,
                                                        ExtStorageManager.MY_IMAGE, ExtStorageManager.PATH_DP);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                        else Toast.makeText(ConversationListActivity.this,"Failed",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });

                    }
                });

            }
        }
    }

    private void signOut() {
        Log.d(TAG, "signOut: ");

        FirebaseAuth.getInstance().signOut();
        SharedPrefManager.getInstance(ConversationListActivity.this).clearUserInfo();
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

        Intent intent = new Intent(ConversationListActivity.this,AllPeopleListActivity.class);
        startActivity(intent);
    }

}
