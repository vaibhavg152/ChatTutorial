package com.example.vaibhav.chattutorial.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.vaibhav.chattutorial.R;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class SharedPrefManager {
    //constants
    private static final String TAG = "SharedPrefManager";
    private static final String SHARED_PREF_NAME = "FCMSPM";
    private static final String SHARED_PREF_NAME_USER = "userInfo";
    private static final String USER_KEY = "user_key";
    private static final String NUM_USER_KEY = "num_user";

    //variables
    private int numUsers = 0;
    private static Context mContext;
    private static SharedPrefManager mInstance;
    private static SharedPreferences preferencesConvo;
    private static SharedPreferences preferencesUSerInfo;
    private ArrayList<ConvoPreview> previewArrayList;

    public SharedPrefManager(Context context) {
        Log.d(TAG, "SharedPrefManager: ");

        mContext = context;
        preferencesConvo = mContext.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        preferencesUSerInfo = mContext.getSharedPreferences(SHARED_PREF_NAME_USER,Context.MODE_PRIVATE);
        previewArrayList = new ArrayList<>();
    }

    public static synchronized SharedPrefManager getInstance(Context context){
        Log.d(TAG, "getInstance: ");

        if (mInstance == null)
            mInstance = new SharedPrefManager(context);
        return mInstance;
    }

    public void addConversation(ConvoPreview preview){
        Log.d(TAG, "addConversation: ");

        if (previewArrayList.contains(preview)){
            Toast.makeText(mContext,"This person is already added to the conversations!",Toast.LENGTH_SHORT).show();
            return;
        }
        previewArrayList.add(preview);

        SharedPreferences.Editor editor = preferencesConvo.edit();
        editor.putInt(NUM_USER_KEY, preferencesConvo.getInt(NUM_USER_KEY,0)+1);
        editor.putString(USER_KEY + numUsers + "id",   preview.getUserId());
        editor.putString(USER_KEY + numUsers + "name", preview.getUserName());
        editor.putString(USER_KEY + numUsers + "msg",  preview.getLastMessage());
        editor.putString(USER_KEY + numUsers + "uri",  preview.getImageUri().toString());

        editor.apply();
        numUsers++;
    }

    public ConvoPreview getConversation(int sno){
        Log.d(TAG, "getConversation: ");

        if (sno > numUsers)
            throw new IndexOutOfBoundsException();
        String id = preferencesConvo.getString(USER_KEY + sno + "id","");
        String msg = preferencesConvo.getString(USER_KEY + sno + "msg","");
        String name = preferencesConvo.getString(USER_KEY + sno + "name","");
        String uri = preferencesConvo.getString(USER_KEY + sno + "uri", "");

        return new ConvoPreview(msg, name, id, Uri.parse(uri));
    }

    public ArrayList<ConvoPreview> getLastFewConersations(int numConvos){
        Log.d(TAG, "getLastFewConersations: "+numConvos);
        ArrayList<ConvoPreview> result = new ArrayList<>();

        for(int i = 1; i<=numConvos; i++){
            if (i>numUsers) break;
            result.add(getConversation(numUsers - i));
        }
        return result;
    }

    public ArrayList<ConvoPreview> getAllConversations(){
        Log.d(TAG, "getAllConversations: ");

        ArrayList<ConvoPreview> result = new ArrayList<>();
        for (int i = numUsers - 1; i >= 0; i--){
            result.add(getConversation(i));
        }
        return result;
    }

    public int getNumConversations() {
        return numUsers;
    }

    public void saveLoginInfo(FirebaseUser user) {
        Log.d(TAG, "saveLoginInfo: ");

        SharedPreferences.Editor editor = preferencesUSerInfo.edit();
        editor.putString("name",user.getDisplayName());
        editor.putString("id",user.getUid());
        editor.putString("image", (user.getPhotoUrl()==null)
                ? mContext.getResources().getString(R.string.defaultDPUri)
                : user.getPhotoUrl().toString() );

    }

    public void clearConvoInfo() {
        Log.d(TAG, "clearConvoInfo: ");
        preferencesConvo.edit().clear();
    }

    public void clearUserInfo() {
        Log.d(TAG, "clearUserInfo: ");
        preferencesUSerInfo.edit().clear();
    }
}
