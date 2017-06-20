package xyz.romakononovich.firebase;

import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ChildEventListener {
    private DatabaseReference databaseReference;
    private DatabaseReference userDataBaseReference;
    private DatabaseReference messageDataBaseReference;
    private MessageAdapter adapter;
    private RecyclerView rv;
    private MessageDataSource messageDataSource;
    private DateFormat dateFormat = new SimpleDateFormat("dd MMM HH:mm", Locale.getDefault());
    private String id;
    private static List<Message> list = new ArrayList<>();

    //ArrayList<Message> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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


        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            userDataBaseReference = databaseReference.child(getUserName());
        }

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
        //final EditText editTitle = (EditText) dialog.findViewById(R.id.et_title);
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

    private void addMessage( String message) {
        Message messageObject = new Message();
        //messageObject.setTitle(title);
        messageObject.setMessage(message);
        messageObject.setTime(dateFormat.format(new Date()));
        id = String.valueOf(System.currentTimeMillis());
        messageObject.setId(id);
        userDataBaseReference.child(id).setValue(messageObject);
        //adapter.addItem(messageObject);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(this, SignUpActivity.class));
        } else {
            userDataBaseReference.addChildEventListener(this);
            messageDataSource.open();
            list.clear();
            list.addAll(messageDataSource.getAllMessage());
            adapter = new MessageAdapter(list);
            rv.setAdapter(adapter);
            adapter.notifyDataSetChanged();


        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(userDataBaseReference!=null) {
            userDataBaseReference.removeEventListener(this);
            messageDataSource.close();
        }
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
