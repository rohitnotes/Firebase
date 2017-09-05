package xyz.romakononovich.firebase.loaders;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import xyz.romakononovich.firebase.Constants;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by romank on 03.08.17.
 */

public class AvatarLoader extends AsyncTaskLoader<String> {
    private static final String TAG = AvatarLoader.class.getSimpleName();
    private volatile boolean isFinished;
    private String filePath;
    public AvatarLoader(Context context) {
        super(context);
    }

    @Override
    public String loadInBackground() {
        Log.d(TAG, "loadInBackground: "+ Thread.currentThread().getName());
        FirebaseStorage storage= FirebaseStorage.getInstance();
        String storageFileName = "image/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/avatar.jpg";
        StorageReference storageReference = storage.getReference();
        final StorageReference avatarImagesRef = storageReference.child(storageFileName);
        avatarImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                File[] files = ContextCompat.getExternalCacheDirs(getContext());
                Log.d(TAG,"External files size: "+files.length);
                for (int i=0; i<files.length;i++) {
                    Log.d(TAG,"Path: "+files[i].getAbsolutePath());
                }
                if (files.length>0) {
                    File storageDir = files[0];
                    File image = new File(storageDir,"avatar.jpg");
                    //image = File.createTempFile("avatar",".jpg",storageDir);
                    downloadImageFromStorage(image, avatarImagesRef);
                }
            }

            private void downloadImageFromStorage(final File image, StorageReference avatarImagesRef) {
                Log.d(TAG, "downloadImageFromStorage: starting");
                avatarImagesRef.getFile(image).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG,"onSuccess download");
                        filePath = image.getAbsolutePath();
                        getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,MODE_PRIVATE).edit().putString(Constants.KEY_URI,filePath).apply();
                       isFinished = true;
                        //imageView.setImageURI(Uri.fromFile(image));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: "+e.getMessage());
                        isFinished = true;
                    }
                });
            }
        });
        while (!isFinished) {
        }
        return  filePath;
    }
}
