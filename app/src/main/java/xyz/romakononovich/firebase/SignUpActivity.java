package xyz.romakononovich.firebase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import xyz.romakononovich.firebase.models.Profiles;

public class SignUpActivity extends AppCompatActivity {
    private EditText login;
    private EditText password;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth = FirebaseAuth.getInstance();
        login = (EditText) findViewById(R.id.login_et);
        password = (EditText) findViewById(R.id.password_et);
        findViewById(R.id.signUp_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void login(final String email, String pass) {
        auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final DatabaseReference profileDataBaseReference = FirebaseDatabase.getInstance().getReference("profiles");
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            profileDataBaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Profiles profiles = dataSnapshot.getValue(Profiles.class);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void signUp() {
        final String email = login.getText().toString().trim();
        final String pass = password.getText().toString().trim();


        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    Profiles profiles = new Profiles();
                    DatabaseReference profileDataBaseReference = FirebaseDatabase.getInstance().getReference("profiles");
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    profileDataBaseReference.child(uid).setValue(profiles);
                    Context context = getApplicationContext();
                    Intent intent = new Intent(context,MainActivity.class);
                    intent.putExtra("email",email);

                    startActivity(intent);
                    finish();
                } else {
                    login(email, pass);
                    //Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

