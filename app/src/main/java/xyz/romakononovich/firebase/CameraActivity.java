package xyz.romakononovich.firebase;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CameraActivity extends AppCompatActivity {
    private final static String TAG = CameraActivity.class.getSimpleName();
    private static final int REQUEST_PHOTO_PERMISSIONS_CODE = 204;
    private static final int REQUEST_VIDEO_PERMISSIONS_CODE = 205;
    private final static int CODE = 203;
    private TextView textView;
    private ImageView imageView;
    private Button btn_left;
    private Button btn_right;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_camera);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textView = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.imageView);
        btn_left = (Button) findViewById(R.id.button_left);
        btn_right = (Button) findViewById(R.id.button_right);

        btn_left.setText("Take photo");
        btn_right.setText("Rec video");

        btn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT<=Build.VERSION_CODES.KITKAT||checkMyPermission()) {
                    takePhoto();
                } else {
                    requestMyPermission(REQUEST_PHOTO_PERMISSIONS_CODE);

                }

            }
        });
        btn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT<=Build.VERSION_CODES.KITKAT||checkMyPermission()) {
                    recVideo();
                } else {
                    requestMyPermission(REQUEST_VIDEO_PERMISSIONS_CODE);

                }

            }


        });

    }

    private void requestMyPermission(int code) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA}, code);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkMyPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                ==PackageManager.PERMISSION_GRANTED
                &&ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==PackageManager.PERMISSION_GRANTED;
    }

    private void recVideo() {
        int i=0;
    }

    private void takePhoto() {
        int i=0;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PHOTO_PERMISSIONS_CODE && grantResults.length > 0) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();

            } else {
                new AlertDialog.Builder(this).setMessage(R.string.alert_no_camera_permission).
                        setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();

            }
        } else if (requestCode == REQUEST_VIDEO_PERMISSIONS_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                recVideo();

            } else {
                new AlertDialog.Builder(this).setMessage(R.string.alert_no_camera_permission).
                        setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
        }
    }

}
