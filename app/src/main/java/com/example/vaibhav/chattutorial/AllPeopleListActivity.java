package com.example.vaibhav.chattutorial;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vaibhav.chattutorial.util.ChatsAppUser;
import com.example.vaibhav.chattutorial.util.ExtStorageManager;
import com.example.vaibhav.chattutorial.util.SharedPrefManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

public class AllPeopleListActivity extends AppCompatActivity {
    //constants
    private static final String TAG = "AllPeopleListActivity";

    //widgets
    private ListView lvAllPeople;
    private EditText etSearch;
    private Button   btnDone;

    //variables
    private DatabaseReference reference;
    private StorageReference storageReference;
    private ArrayList<String> arrayListPeople, arrayListId, arrayListName, arrayListEmail, arrayListImg;
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_list);
        Log.d(TAG, "onCreate: ");

        initialise();
    }

    private void initialise() {
        Log.d(TAG, "initialise: ");

        arrayListPeople = new ArrayList<>();
        arrayListName   = new ArrayList<>();
        arrayListId     = new ArrayList<>();
        arrayListEmail  = new ArrayList<>();
        arrayListImg    = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        lvAllPeople = (ListView) findViewById(R.id.lvPeople);
        etSearch    = (EditText) findViewById(R.id.etSearchPeople);
        btnDone     = (Button)   findViewById(R.id.btnDoneAddingConvos);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });


        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: ");

                for (DataSnapshot ds :dataSnapshot.child("users").getChildren()) {
                    String userId = ds.child("id").getValue(String.class);
                    String name = ds.child("name").getValue(String.class);
                    String email = ds.child("email").getValue(String.class);
                    String uriDP = ds.child("image").getValue(String.class);
                    if (uriDP == null || uriDP.length() == 0)
                        uriDP = getResources().getString(R.string.defaultDPUri);

                    boolean isAlreadyPresent = false;
                    for (ChatsAppUser user : SharedPrefManager.getInstance(AllPeopleListActivity.this).getAllConversations()) {
                        if (user.getUserId().equals(userId)) {
                            isAlreadyPresent = true;
                            break;
                        }
                    }

                    if (isAlreadyPresent) continue;

                    arrayListName.add(name);
                    arrayListEmail.add(email);
                    arrayListId.add(userId);
                    arrayListImg.add(uriDP);

                    arrayListPeople.add(name + " : " + email);
                    adapter = new ArrayAdapter(AllPeopleListActivity.this, android.R.layout.simple_list_item_1, arrayListPeople);
                    lvAllPeople.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        lvAllPeople.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) { addConvoToSharedPref(position); }
        });
    }

    private void addConvoToSharedPref(int position) {
        Log.d(TAG, "addConvoToSharedPref: ");

        ChatsAppUser preview = new ChatsAppUser(arrayListEmail.get(position),arrayListName.get(position),
                arrayListId.get(position),
                Uri.parse(arrayListImg.get(position)));
        Log.d(TAG, "addConvoToSharedPref: "+preview.getUserId()+":"+arrayListId.get(position));

        SharedPrefManager.getInstance(AllPeopleListActivity.this).addConversation(preview);
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(preview.getImageUri().toString());
        storageReference.getFile(
                new File(ExtStorageManager.PATH_DP+"/"+preview.getUserId()));

        Toast.makeText(AllPeopleListActivity.this,"Conversation with "+arrayListName.get(position)+
                " added to the list on conversations page. Go back to start conversation",
                Toast.LENGTH_SHORT).show();

        arrayListPeople.remove(position);
        arrayListName.remove(position);
        arrayListEmail.remove(position);
        arrayListImg.remove(position);
        arrayListId.remove(position);

        adapter = new ArrayAdapter(AllPeopleListActivity.this,android.R.layout.simple_list_item_1,arrayListPeople);
        lvAllPeople.setAdapter(adapter);

        btnDone.setVisibility(View.VISIBLE);
    }

    private void goBack() {
        Log.d(TAG, "goBack: ");

        startActivity(new Intent(AllPeopleListActivity.this,ConversationListActivity.class));
        finish();
    }
}
