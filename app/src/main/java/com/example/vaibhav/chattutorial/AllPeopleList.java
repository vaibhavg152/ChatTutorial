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

import com.example.vaibhav.chattutorial.util.ConvoPreview;
import com.example.vaibhav.chattutorial.util.SharedPrefManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllPeopleList extends AppCompatActivity {
    //constants
    private static final String TAG = "AllPeopleList";

    //widgets
    private ListView lvAllPeople;
    private EditText etSearch;
    private Button   btnDone;

    //variables
    private DatabaseReference reference;
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
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: ");

                for (DataSnapshot ds :dataSnapshot.child("users").getChildren()){
                    String userId = ds.child("id"   ).getValue(String.class);
                    String name   = ds.child("name" ).getValue(String.class);
                    String email  = ds.child("email").getValue(String.class);
                    String uriDP  = ds.child("image").getValue(String.class);
                    if (uriDP == null || uriDP.length() == 0)
                        uriDP = getResources().getString(R.string.defaultDPUri);

                    arrayListName.add(name);
                    arrayListEmail.add(email);
                    arrayListId.add(userId);
                    arrayListImg.add(uriDP);

                    arrayListPeople.add(name+" : "+email);
                    adapter = new ArrayAdapter(AllPeopleList.this,android.R.layout.simple_list_item_1,arrayListPeople);
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

        ConvoPreview preview = new ConvoPreview(arrayListEmail.get(position),arrayListName.get(position),
                arrayListId.get(position),
                Uri.parse(arrayListImg.get(position)));
        Log.d(TAG, "addConvoToSharedPref: "+preview.getUserId()+":"+arrayListId.get(position));

        SharedPrefManager.getInstance(AllPeopleList.this).addConversation(preview);
        Toast.makeText(AllPeopleList.this,"Conversation with "+arrayListName.get(position)+
                " added to the list on conversations page. Go back to start conversation",
                Toast.LENGTH_SHORT).show();

        arrayListPeople.remove(position);
        arrayListName.remove(position);
        arrayListEmail.remove(position);
        arrayListImg.remove(position);
        arrayListId.remove(position);

        adapter = new ArrayAdapter(AllPeopleList.this,android.R.layout.simple_list_item_1,arrayListPeople);
        lvAllPeople.setAdapter(adapter);

        btnDone.setVisibility(View.VISIBLE);
    }

    private void goBack() {
        Log.d(TAG, "goBack: ");

        startActivity(new Intent(AllPeopleList.this,ConversationList.class));
        finish();
    }
}