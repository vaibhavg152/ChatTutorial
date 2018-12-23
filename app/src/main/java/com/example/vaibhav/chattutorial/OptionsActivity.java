package com.example.vaibhav.chattutorial;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class OptionsActivity extends AppCompatActivity {
    //constants
    private static final String TAG = "OptionsActivity";
    private static final int REQUEST_CODE_SELECT_IMAGE = 999;

    //widgets
    private Button btnSignOut,btnChangeDp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        Log.d(TAG, "onCreate: ");

        btnChangeDp = (Button) findViewById(R.id.btnChangeProfilePic);
        btnSignOut  = (Button) findViewById(R.id.btnOptions);

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
        btnChangeDp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeDp();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                                        if (task.isSuccessful()){
                                            Toast.makeText(OptionsActivity.this,"Profile picture updated",Toast.LENGTH_SHORT).show();
                                            FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("image")
                                                    .setValue(uriImage.toString());
                                        }
                                        else Toast.makeText(OptionsActivity.this,"Failed",Toast.LENGTH_SHORT).show();
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
        SharedPrefManager.getInstance(OptionsActivity.this).clearUserInfo();
        finish();
    }
}
