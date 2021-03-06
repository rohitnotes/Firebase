package xyz.romakononovich.firebase;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import xyz.romakononovich.firebase.adapter.MessageAdapter;
import xyz.romakononovich.firebase.models.Message;
import xyz.romakononovich.firebase.models.Profiles;

public class MainActivity extends AppCompatActivity implements ChildEventListener {
    private DatabaseReference databaseReference;
    private DatabaseReference userDataBaseReference;
    private DatabaseReference userProfileDataBaseReference;
    private DatabaseReference profileDataBaseReference;
    private MessageAdapter adapter;
    private RecyclerView rv;
    private MessageDataSource messageDataSource;
    private DateFormat dateFormat = new SimpleDateFormat("dd MMM HH:mm", Locale.getDefault());
    private String id;
    private Toolbar mActionBarToolbar;
    private ImageView mImageView;
    private static List<Message> list = new ArrayList<>();
    private static final int REQUEST = 1;
    SharedPreferences preferences;
    private FirebaseStorage storage;
    private String email;
    Profiles profiles;
    private String TAG = MainActivity.class.getSimpleName();


    //ArrayList<Message> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = this.getSharedPreferences("xyz.romakononovich.firebase", MODE_PRIVATE);
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        rv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(linearLayoutManager);
        rv.setHasFixedSize(true);
        messageDataSource = new MessageDataSource(getApplicationContext());
        databaseReference = FirebaseDatabase.getInstance().getReference("message");
        profileDataBaseReference = FirebaseDatabase.getInstance().getReference("profiles");
        if(getIntent().getExtras()!=null) {
            email = getIntent().getExtras().getString("email");
        }



        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        mImageView = (ImageView) findViewById(R.id.toolbar_img);
        setSupportActionBar(mActionBarToolbar);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST);
            }
        });



        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            userDataBaseReference = databaseReference.child(getUserName());
           userProfileDataBaseReference =  profileDataBaseReference.child(getUserName());
            userDataBaseReference.keepSynced(true);
        if(email!=null) {
            addProfile(email);
        }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap img = null;
        if(requestCode == REQUEST && resultCode == RESULT_OK) {
            Uri selectedImg = data.getData();
           firebase(selectedImg);
            try {
                img = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImg);
             } catch (IOException e) {
                e.printStackTrace();
            }
            mImageView.setImageBitmap(img);

        }

    }

    private void firebase(Uri selectedImg) {
        storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReference();
        StorageReference avatarRef = storageReference.child("/image/"+selectedImg.getLastPathSegment());
        UploadTask uploadTask = avatarRef.putFile(selectedImg);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                taskSnapshot.getDownloadUrl();
               // @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
             //   profiles.setUri(taskSnapshot.getDownloadUrl());
               // userProfileDataBaseReference.setValue(profiles);
               profileDataBaseReference.child(profileDataBaseReference.getKey()).setValue(profiles);
            }
        });



        //profiles.setUri(uri);
        Log.d("TAG",profiles.toString());
        userProfileDataBaseReference.setValue(profiles);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(messageDataSource!=null) {
            list = messageDataSource.getAllMessage();
        }
        for (Message m: list) {
            Log.d("TAG", m.toString());
        }

    }

    private void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_dialog);
        final EditText editMessge = (EditText) dialog.findViewById(R.id.et_message);

        dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMessage(editMessge.getText().toString());
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void addProfile (String email) {
        Profiles profiles = new Profiles();
        profiles.setName(email.substring(0,email.indexOf("@")));
        profileDataBaseReference.child(email.replace(".", "_")).setValue(profiles);
    }
    private void addMessage( String message) {
        Message messageObject = new Message();
        messageObject.setMessage(message);
        messageObject.setTime(dateFormat.format(new Date()));
        id = String.valueOf(System.currentTimeMillis());
        messageObject.setId(id);
        userDataBaseReference.child(id).setValue(messageObject);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(this, SignUpActivity.class));
        } else {
            Log.d(TAG,"UID: "+ firebaseUser.getUid());
            startActivity(new Intent(this, CameraActivity.class));
//            userDataBaseReference.addChildEventListener(this);
//            messageDataSource.open();
//            list.clear();
//            list.addAll(messageDataSource.getAllMessage());
//            adapter = new MessageAdapter(list);
//            rv.setAdapter(adapter);
//            adapter.notifyDataSetChanged();
//            userProfileDataBaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    profiles = dataSnapshot.getValue(Profiles.class);
//                    Log.d("TAG",profiles.toString());
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(userDataBaseReference!=null) {
            userDataBaseReference.removeEventListener(this);
            messageDataSource.close();
        }
        preferences.edit().putString("img","").apply();
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        Message message = dataSnapshot.getValue(Message.class);
        if(!list.contains(message)) {
            showSnackBar();
            messageDataSource.addMessage(message);
            list.add(message);
            adapter.addItem(message);
        }
        //message.setId(Long.parseLong(dataSnapshot.getKey()));

        //messageDataSource.addMessage(message);
        adapter.notifyDataSetChanged();
        Log.d("TAG",dataSnapshot.toString());
    }

    private void showSnackBar() {
        Snackbar.make(this.getCurrentFocus(),"Беседа обновлена...",Snackbar.LENGTH_SHORT).show();
    }


    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }


    public String getUserName() {
        return FirebaseAuth.getInstance()
                .getCurrentUser().getEmail().replace(".", "_");
    }
}
