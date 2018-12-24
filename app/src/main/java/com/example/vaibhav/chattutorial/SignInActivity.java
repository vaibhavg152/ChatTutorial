package com.example.vaibhav.chattutorial;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.vaibhav.chattutorial.util.ExtStorageManager;
import com.example.vaibhav.chattutorial.util.SharedPrefManager;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.util.Arrays;

public class SignInActivity extends AppCompatActivity {
    //constants
    private static final String TAG = "SignInActivity";
    private static final int REQUEST_CODE_SIGN_IN = 145;

    //variables
    private AuthUI myAuthUI;
    private FirebaseAuth myAuth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Log.d(TAG, "onCreate: ");

        initialize();
    }

    private void initialize() {
        Log.d(TAG, "initialize: ");

        myAuthUI = AuthUI.getInstance();
        myAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("users");

        if (myAuth.getCurrentUser() == null) {
            Log.d(TAG, "initialize: null");

            SharedPrefManager.getInstance(SignInActivity.this).clearUserInfo();
            SharedPrefManager.getInstance(SignInActivity.this).clearConvoInfo();
            ExtStorageManager.getInstance(SignInActivity.this).removeMyDp();

            Intent signInIntent = myAuthUI.createSignInIntentBuilder()
                    .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build()))
                    .build();
            startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
        }
        else {
            FirebaseUser user = myAuth.getCurrentUser();
            SharedPrefManager.getInstance(SignInActivity.this).saveLoginInfo(user);
            startActivity(new Intent(SignInActivity.this, ConversationListActivity.class));
            finish();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");

        if (requestCode == REQUEST_CODE_SIGN_IN){
            Log.d(TAG, "onActivityResult: request code is same");

            if (resultCode == RESULT_OK){
                Log.d(TAG, "onActivityResult: result is ok");

                Toast.makeText(SignInActivity.this,"Welcome",Toast.LENGTH_SHORT).show();

                FirebaseUser user = myAuth.getCurrentUser();
                String userId = user.getUid();
                reference.child(userId).child("email").setValue(user.getEmail());
                reference.child(userId).child("name" ).setValue(user.getDisplayName());
                reference.child(userId).child("id"   ).setValue(userId);
                reference.child(userId).child("image").setValue( (user.getPhotoUrl()==null)
                        ? getResources().getString(R.string.defaultDPUri)
                        : user.getPhotoUrl().toString() );

                FirebaseStorage.getInstance().getReference().child("images/dp/").child(userId).
                        getFile(new File(ExtStorageManager.PATH_DP+"/"+ExtStorageManager.MY_IMAGE));
                SharedPrefManager.getInstance(SignInActivity.this).saveLoginInfo(user);
                startActivity(new Intent(SignInActivity.this, ConversationListActivity.class));
                finish();
            }
            else Log.d(TAG, "onActivityResult: "+resultCode);
        }
    }
}
