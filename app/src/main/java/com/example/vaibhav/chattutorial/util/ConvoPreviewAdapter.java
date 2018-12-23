package com.example.vaibhav.chattutorial.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vaibhav.chattutorial.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class ConvoPreviewAdapter extends ArrayAdapter<ConvoPreview> {
    private static final String TAG = "ConvoPreviewAdapter";

    private Context context;
    private int resourceId;
    private ArrayList<ConvoPreview> previewList;

    public ConvoPreviewAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ConvoPreview> objects) {
        super(context, resource, objects);

        this.context = context;
        resourceId = resource;
        previewList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        convertView = LayoutInflater.from(context).inflate(resourceId,parent,false);
        ImageView imgProfPic = (ImageView) convertView.findViewById(R.id.imgProfPic);
        TextView  txtName    = (TextView)  convertView.findViewById(R.id.txtPersonName);
        TextView  txtMessage = (TextView)  convertView.findViewById(R.id.txtNewMsg);

        ConvoPreview preview = previewList.get(position);
        try {
            Bitmap bitmap = ExtStorageManager.getInstance(context).getImageBitmap(preview.getUserId(),ExtStorageManager.PATH_DP);
            imgProfPic.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            setImage(imgProfPic,preview.getImageUri());
        }

        txtMessage.setText(preview.getLastMessage());
        txtName.setText(preview.getUserName());

        return convertView;
    }

    private void setImage(ImageView imgMyProfPic, Uri photoUrl) {
        Log.d(TAG, "setImage: ");
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new URL(photoUrl.toString()).openConnection().getInputStream());
            imgMyProfPic.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}
