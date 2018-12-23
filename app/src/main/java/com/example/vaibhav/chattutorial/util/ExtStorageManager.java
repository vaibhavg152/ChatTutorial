package com.example.vaibhav.chattutorial.util;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class ExtStorageManager {
    //constants
    private static final String TAG = "ExtStorageManager";
    public static final String PATH_MAIN = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()+"/.ChatsApp";
    public static final String PATH_DP = PATH_MAIN + "/ProfilePics";
    public static final String MY_IMAGE = "my_photo";

    //variables
    private Context context;
    private static ExtStorageManager mInstance;

    public ExtStorageManager(Context context) {
        this.context = context;
    }

    public static synchronized ExtStorageManager getInstance(Context context){
        if (mInstance == null)
            mInstance = new ExtStorageManager(context);
        return mInstance;
    }

    public Bitmap getImageBitmap(String imageName, String filePath) throws IOException {
        Log.d(TAG, "getImageBitmap: "+imageName);

        File myDir = new File(filePath);
        if (!myDir.exists())
            throw new IOException("Could not find the directory in given path " + filePath);

        if (Build.VERSION.SDK_INT >= 23) {
            Log.d(TAG, "getImageBitmap: more build");
            if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "getImageBitmap: Not granted");
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }

        for(File file: myDir.listFiles()) {
            Log.d(TAG, "getImageBitmap: "+file.getAbsolutePath());
            if (file.getAbsolutePath().matches("(.*)"+imageName+"(.*)"))
                return BitmapFactory.decodeFile(file.toString());
        }

        throw new IOException("image not found for this person "+imageName);
    }

    public void saveImage(Bitmap bitmap, int QUALITY, String image_Name, String filePath) {
        Log.d(TAG, "saveImage: ");

        FileOutputStream stream;

        File myDir = new File(filePath);
        if (!myDir.exists()) myDir.mkdirs();
        File file = new File(myDir,image_Name+".png");
        try {
            Log.d(TAG, "saveImage: fone");
            stream = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG,QUALITY,stream);
            stream.close();

            toastMessage("Image Saved Successfully!");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            toastMessage("Error Couldnt Save the photo");
            if (Build.VERSION.SDK_INT >= 23){
                Log.d(TAG, "saveImage: moreBuild");
                if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "saveImage: Not granted");
                    ActivityCompat.requestPermissions((Activity) context,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }
            }
            else Log.d(TAG, "saveImage: lessBuild");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void saveImage(Uri imageUri, int QUALITY, String image_Name, String filePath) throws IOException {
        Log.d(TAG, "saveImage: ");

        FileOutputStream stream;

        File myDir = new File(filePath);
        if (!myDir.exists()) myDir.mkdirs();
        File file = new File(myDir,image_Name+".jpg");
        try {
            stream = new FileOutputStream(file);

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(),imageUri);
            bitmap.compress(Bitmap.CompressFormat.JPEG,QUALITY,stream);
            stream.close();

            toastMessage("Image Saved Successfully!");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            if (Build.VERSION.SDK_INT >= 23){
                Log.d(TAG, "saveImage: moreBuild");
                if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "saveImage: Not granted");
                    ActivityCompat.requestPermissions((Activity) context,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }
            }
            else Log.d(TAG, "saveImage: lessBuild");

        } catch (IOException e) {
            e.printStackTrace();
            toastMessage("Error Couldnt Save the photo");
            throw e;
        }

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cR.getType(uri));
    }

    private void SaveTextFile(File file, String data) {

        Log.d(TAG, "SaveTextFile: ");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            toastMessage("Error! File Not found :(");
            return;
        }

        try {
            try {
                fos.write(data.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                toastMessage("Error! :(");
                return;
            }
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                toastMessage("Error! :(");
                return;
            } catch (NullPointerException e) {
                e.printStackTrace();
                toastMessage("Error! :(");
                return;
            }
        }
    }

    private void toastMessage(String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }
}
