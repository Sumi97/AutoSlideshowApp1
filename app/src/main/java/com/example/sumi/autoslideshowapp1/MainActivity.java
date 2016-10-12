package com.example.sumi.autoslideshowapp1;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    Cursor cursor;
    Timer timer;
    Button button1;
    Button button2;

    android.os.Handler handler = new android.os.Handler();

    public void showPicture() { //indexからIDを取得し、そのIDから画像のURIを取得する

        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        Long id = cursor.getLong(fieldIndex);
        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
        ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
        imageVIew.setImageURI(imageUri);

    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(this);

        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(this);

        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(this);


        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                }
                break;
            default:
                break;
        }
    }

    private void getContentsInfo() {

        // 画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );

            if(cursor.moveToFirst()) {

                showPicture();
            }

    }

    public void onClick(View v) {

        if (v.getId() == R.id.button1) {

            goNext(); //進むボタンの処理

        } else if (v.getId() == R.id.button2) {

            goPrev(); //戻るボタンの処理

        } else if (v.getId() == R.id.button3) {

            startPause(); //再生停止ボタンの処理

            if (timer == null) {

                Button start = (Button) v; //再生ボタン
                start.setText("再生");

                //ボタン不可
                button1.setEnabled(true);
                button2.setEnabled(true);

            } else {

                Button pause = (Button) v;
                pause.setText("停止"); //停止ボタン

                //ボタン可
                button1.setEnabled(false);
                button2.setEnabled(false);

            }

        }


    }



    public void goNext() {

        Log.d("debug", "goNext()");

        if (cursor == null || cursor.getCount() <= 0) {

            Toast.makeText(this, "データがありません。", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cursor.isLast() == false) { //最後ではないとき

            cursor.moveToNext(); //進む

        } else { //最後のとき

            cursor.moveToFirst(); //最初へ戻る

        }

        showPicture();

    }

    public void goPrev() {

        Log.d("debug", "goPrev()");

        if (cursor == null || cursor.getCount() <= 0) {

            Toast.makeText(this, "データがありません。", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cursor.isFirst() == false) { //最初ではないとき

            cursor.moveToPrevious(); //戻る

        } else { //最初の時

            cursor.moveToLast(); //最後へ進む

        }

        showPicture();


    }

    public void startPause() {

        if (cursor == null || cursor.getCount() <= 0) {

            Toast.makeText(this, "データがありません。", Toast.LENGTH_SHORT).show();
            return;
        }

        if (timer == null) {

            TimerTask task = new TimerTask() {

                @Override
                public void run() {
                 Runnable runnable = new Runnable() {

                     @Override
                     public void run() {
                         goNext();
                     }
                 };
                    handler.post(runnable);
                }
            };

            timer = new Timer();
            timer.schedule(task, 0, 2000);

        } else if (timer != null) {

            timer.cancel();
            timer = null;
        }

        Log.d("debug", "startPause()");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        cursor.close();
    }

}

