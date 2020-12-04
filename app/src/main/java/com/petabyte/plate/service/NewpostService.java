package com.petabyte.plate.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.petabyte.plate.R;
import com.petabyte.plate.ui.activity.MainActivity;

public class NewpostService extends Service {
    NotificationManager NotiMng;
    FirebaseDatabase db;
    DatabaseReference ref;
    boolean FLAG_INIT = false;
    long postCnt = 0;
    int loadCnt = 0;
    //ServiceThread thread;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("ji1dev", "Service onCreated");

        // get notification service and make handler object
        NotiMng = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // make foreground notification object and start foreground service
        initForegroundService();

        // if device OS version is Oreo or newer, make notification channel (android policy)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            @SuppressLint("WrongConstant")
            NotificationChannel notificationChannel = new NotificationChannel(
                    "newpost_noti", "새 PLATE POST 알림", NotificationManager.IMPORTANCE_MAX );
            assert NotiMng != null;
            NotiMng.createNotificationChannel(notificationChannel);
        }

        // get number of PLATE POSTs
        ref = db.getInstance().getReference().child("Home/Plate Post");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postCnt = snapshot.getChildrenCount();
                Log.d("ji1dev", "NUM OF POST >> "+postCnt);
                FLAG_INIT = true;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // child event listener (when new post add ed to db)
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("ji1dev", "child >> "+snapshot.getValue());
                if(FLAG_INIT){
                    Log.d("ji1dev", "child >> "+snapshot.getValue());
                    sendNotification();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    private void sendNotification() {
        //NotificationManager mNotiMng = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notiIntent = new Intent(NewpostService.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                NewpostService.this, 0, notiIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // make new post notification object
        NotificationCompat.Builder npNoti = new NotificationCompat.Builder(NewpostService.this, "newpost_noti")
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo))
                .setSmallIcon(R.drawable.ic_icon)
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(NewpostService.this, R.color.colorPrimary))
                .setContentTitle("새로운 PLATE POST 도착")
                .setContentText("PLATE가 준비한 포스트를 확인해보세요!");

        // activate notification
        Notification notification = npNoti.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotiMng.notify(2, notification);
    }

    // codes that running on background
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ji1dev", "Service onStartComand");
        //newpostHandler handler = new newpostHandler();
        // run thread to separate internet connection feature (using Firebase DB)
        //thread = new ServiceThread(handler);
        //thread.start();
        return START_STICKY;
    }

    private void initForegroundService() {

        // make foreground service notification object
        NotificationCompat.Builder fgNoti = new NotificationCompat.Builder(NewpostService.this, "foreground_noti")
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo))
                .setSmallIcon(R.drawable.ic_icon)
                .setContentTitle("PLATE앱이 실행중이에요")
                .setContentText("이 알림을 보지 않으려면 설정 > 알림 > 실행 중 알림을 꺼주세요!");

        // if device OS version is Oreo or newer, make notification channel (android policy)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "foreground_noti", "실행 중 알림", NotificationManager.IMPORTANCE_NONE);
            channel.setVibrationPattern(new long[]{0});
            channel.enableVibration(true);
            NotiMng.createNotificationChannel(channel);
        }

        // start foreground service with notification
        Notification notification = fgNoti.build();
        startForeground(1, notification);
    }

    // when service destroyed, re
    public void onDestroy() {
        Log.d("ji1dev", "Service onDestroy");
        /*
        newpostHandler handler = new newpostHandler();
        thread = new ServiceThread(handler);
        thread.start();
         */
    }

    /*
    class newpostHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {

            Log.d("ji1dev", "newposthandler.handleMessage() is working now");
            NotificationManager mNotiMng = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            Intent intent = new Intent(NewpostService.this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    NewpostService.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

            // if device OS version is Oreo or newer, make notification channel (android policy)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                @SuppressLint("WrongConstant")
                NotificationChannel notificationChannel = new NotificationChannel(
                        "newpost_noti", "새 PLATE POST 알림", NotificationManager.IMPORTANCE_MAX );

                assert mNotiMng != null;
                mNotiMng.createNotificationChannel(notificationChannel);
            }

            // make new post notification object
            NotificationCompat.Builder npNoti = new NotificationCompat.Builder(NewpostService.this, "newpost_noti")
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo))
                    .setSmallIcon(R.drawable.ic_icon)
                    .setContentIntent(pendingIntent)
                    .setColor(ContextCompat.getColor(NewpostService.this, R.color.colorPrimary))
                    .setContentTitle("새 PLATE POST가 도착")
                    .setContentText("PLATE가 준비한 포스트를 확인해보세요!");

            // activate notification
            mNotiMng.notify(2, npNoti.build());
            Toast.makeText(NewpostService.this, "TEST MSG", Toast.LENGTH_LONG).show();

        }
    };*/
}
