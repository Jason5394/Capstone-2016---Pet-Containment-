package com.example.jason.petcontainment;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Array;
import java.util.ArrayList;

/**
 * Created by jason on 3/27/2016.
 */
public class SocketService extends Service {
    public static final String MY_ACTION = "MY_ACTION";
    public static final String filename = "saved_region.txt";
    //ruwireless public static final String address = "172.31.144.15";
    public static final int port = 12345;
   // public static final String address = "192.168.1.221";
    public static final String address = "192.168.43.70";
    @Override
    //Not used.
    public IBinder onBind(Intent arg0){
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        System.out.println("Socket System has started.");
        MyThread myThread = new MyThread();
        myThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    public class MyThread extends Thread{
        @Override
        public void run(){
            /*
            for (int i = 0; i < 10; ++i){
                System.out.println("testing: "+i);
            }
            for(int i=0; i<10; i++){
                try {
                    Thread.sleep(5000);
                    Intent intent = new Intent();
                    intent.setAction(MY_ACTION);
                    intent.putExtra("string_name", "hello world!");
                    sendBroadcast(intent);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            stopSelf();
            */
            System.out.println("Service coords:");
            ArrayList<String> stringCoords = Helper.readFile(filename, getApplicationContext());
            if (stringCoords == null || stringCoords.size() < 3 ){
                //Something went wrong, service will exit
                System.out.println("Something went wrong in Socket Service.");
                stopSelf();
            }
            Point[] polygon = new Point[stringCoords.size()];
            int vertices = 0;
            for (int i = 0; i < stringCoords.size(); ++i) {
                String[] strsplit = stringCoords.get(i).split("[;]");
                double lat = Double.parseDouble(strsplit[0]);
                double lng = Double.parseDouble(strsplit[1]);
                polygon[i] = new Point(lat, lng);
                ++vertices;
            }

            //Socket Communication with RPi
            try (   Socket sock = new Socket(address, port);
                    InputStreamReader isr = new InputStreamReader(sock.getInputStream());
                    BufferedReader in = new BufferedReader(isr);
                    PrintWriter out = new PrintWriter(sock.getOutputStream(), true);){
                String outOfBounds = "0";
                int count = 0;
                System.out.println("got here");
                while(true){
                    Point dog;
                    //read latitude;longitude\n line
                    System.out.println("got here 2");
                    String result = in.readLine();
                    System.out.println("got here 3");
                    System.out.println(result);
                    if (result == null)
                        break;
                    Intent intent = new Intent();
                    intent.setAction(MY_ACTION);
                    intent.putExtra("coordinates", result);
                    sendBroadcast(intent);

                    //parse dog Point
                    String[] split = result.split("[;]");
                    dog = new Point(Double.parseDouble(split[0]), Double.parseDouble(split[1]));

                    //writing 1 or 0
                    if (PointInPolygon.isInside(polygon, vertices, dog)) {
                        outOfBounds = "0";
                    }
                    else {
                        outOfBounds = "1";
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(SocketService.this)
                                .setSmallIcon(R.drawable.dog_notif)
                                .setContentTitle("Dog is out of bounds!")
                                .setContentText("Your dog is out of bounds! Click here to go to " +
                                        "app's homepage to track it.")
                                .setAutoCancel(true);
                        Intent resultIntent = new Intent(SocketService.this, MainActivity.class);
                        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                                SocketService.this, 0, resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                        mBuilder.setContentIntent(resultPendingIntent);
                        int notifyId = 001;
                        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        mNotifyMgr.notify(notifyId, mBuilder.build());
                    }
                    out.write(outOfBounds);
                    out.flush();
                    System.out.println(outOfBounds);
                }
            } catch (UnknownHostException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            System.out.println("Exited loop.");
            stopSelf();
        }
    }
}
