package com.example.vaibhav.chattutorial.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaibhav.chattutorial.R;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class ConvoPreviewAdapter extends ArrayAdapter<ChatsAppUser> {
    private static final String TAG = "ConvoPreviewAdapter";

    private Context context;
    private int resourceId;
    private ArrayList<ChatsAppUser> previewList;

    public ConvoPreviewAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ChatsAppUser> objects) {
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

        final ChatsAppUser preview = previewList.get(position);
        try {
            Bitmap bitmap = ExtStorageManager.getInstance(context).getImageBitmap(preview.getUserId(),ExtStorageManager.PATH_DP);
            imgProfPic.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(new URL(preview.getImageUri().toString()).openConnection().getInputStream());
                imgProfPic.setImageBitmap(bitmap);
            } catch (IOException e1) {
                e1.printStackTrace();
                imgProfPic.setImageDrawable(context.getDrawable(R.drawable.icons_happy_48));
            }
        }

        txtMessage.setText(preview.getLastMessage());
        txtName.setText(preview.getUserName());
        imgProfPic.setClickable(true);
        imgProfPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage(context,preview);
            }
        });

        return convertView;
    }

    private void openImage(Context context,ChatsAppUser user) {
        Log.d(TAG, "openImage: ");

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_view_image);

        ImageView imgProfPic = (ImageView) dialog.findViewById(R.id.imgProfPic);
        TextView  txtName    = (TextView)  dialog.findViewById(R.id.txtPersonName);
        txtName.setText(user.getUserName());
        setImage(imgProfPic,user);

        dialog.show();

    }

    private void setImage(ImageView imgProfPic, ChatsAppUser user) {
        Log.d(TAG, "setImage: ");
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new URL(user.getImageUri().toString()).openConnection().getInputStream());
            imgProfPic.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context,"Could not load this image",Toast.LENGTH_LONG).show();
            imgProfPic.setImageDrawable(context.getDrawable(R.drawable.icons_sad));
        }
    }

}
