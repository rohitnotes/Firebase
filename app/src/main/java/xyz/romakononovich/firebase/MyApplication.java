package xyz.romakononovich.firebase;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by romank on 20.06.17.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
