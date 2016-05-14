package com.example.hai.awaresys.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hai.awaresys.R;
import com.example.hai.awaresys.dao.*;
import com.example.hai.awaresys.model.Emotion;
import com.example.hai.awaresys.model.Skype;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")


/**
 * This class is the home page of the application, in this page, the navigation of the features is given.
 * @author Hai Yu <yuhair@iastate.edu>
 * @version 1.0
 * @since 2013-12-17
 */
public class MainActivity extends Activity implements OnItemClickListener {

    protected static final String TAG = "mainActivity";
    Context mContext = null;
    private SQLiteDatabase db;
    private String texts[] = null;
    private Integer images[] = null;
    private static final int SERVERPORT = 9999;
    private String serverIp = null;
    private ServerSocket serverSocket;
    private TextView ip;
    private Button connect, disconnect;
    private InputStream readData;
    private AwareMessage awareMessage;
    private Thread serverThread = null;
    public static final String WARNMESSAGE = "Dear care taker, the subject is experiencing high level frustration.\n";


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        DBOpenHelper helper = new DBOpenHelper(this);
        db = helper.getWritableDatabase();
        db.close();
        images = new Integer[]
                {R.drawable.connect,R.drawable.data,R.drawable.contacts, R.drawable.skypes, R.drawable.music, R.drawable.videos};
        texts = new String[]
                {"Connect","Datalog","Contact", "Skype", "Music", "Video"};

        // fill out GridView
        GridView gridView = (GridView) findViewById(R.id.homeGrid);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, fillMap(),
                R.layout.layout_gridview_item, new String[]
                {"imageView", "imageTitle"}, new int[]
                {R.id.imageView, R.id.imageTitle});
        gridView.setAdapter(simpleAdapter);
        gridView.setOnItemClickListener((OnItemClickListener) this);


    }

    private void buildDialog(){
        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        View dialogView= LayoutInflater.from(MainActivity.this).inflate(R.layout.main_connection, null);
        ip = (TextView) dialogView.findViewById(R.id.ipAddress);
        serverIp = getLocalIpAddress();
        String state = serverThread == null ? "OFF" : "ON";
        ip.setText(state + " - " + serverIp);
        connect = (Button) dialogView.findViewById(R.id.connectButton);
        disconnect = (Button) dialogView.findViewById(R.id.disconnectButton);
        awareMessage = new AwareMessage(MainActivity.this);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serverThread == null) {
                    serverThread = new Thread(new ServerThread());
                    serverThread.start();
                }
                ip.setText("ON" + " - " + serverIp);
                //alertDialog.dismiss();
            }
        });

        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (serverSocket!= null && !serverSocket.isClosed())
                        serverSocket.close();
                    if (serverThread != null) {
                        Thread temp = serverThread;
                        serverThread = null;
                        temp.interrupt();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ip.setText("OFF" + " - " + serverIp);
                //alertDialog.dismiss();
            }
        });
        alertDialog.setView(dialogView);
        alertDialog.show();

    }

    @SuppressWarnings("deprecation")
    public String getLocalIpAddress() {
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        wm.getConnectionInfo().getIpAddress();
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (serverSocket!= null && !serverSocket.isClosed())
                serverSocket.close();
            if (serverThread != null) {
                Thread temp = serverThread;
                serverThread = null;
                temp.interrupt();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int idx, long arg3) {
        // TODO Auto-generated method stub
        switch (idx) {
            case 0:
                buildDialog();
                break;
            case 1:
                startActivity(new Intent(this, LineChartActivity.class));
                break;
            case 2:
                startActivity(new Intent(this, ContactListActivity.class));
                break;
            case 3:
                startActivity(new Intent(this, PhotoGalleryActivity.class));
                break;
            case 4:
                startActivity(new Intent(this, MusicPlayerActivity.class));
                break;
            case 5:
                startActivity(new Intent(this, VideoActivity.class));
                break;
            default:
                break;
        }
    }

    public List<Map<String, Object>> fillMap() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (int i = 0, j = texts.length; i < j; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("imageView", images[i]);
            map.put("imageTitle", texts[i]);
            list.add(map);
        }
        return list;
    }

    private void storeEmotion(Emotion emotion) {
        EmotionDao emotionDao = new EmotionDao(getApplicationContext());
        emotionDao.add(emotion);
    }

    /**
     * return the current time from the computer system
     *
     * @return
     */
    private String[] getTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = df.format(new Date());
        String[] res = dateTime.split(" ");
        return res;
    }

    private Handler handlerWarn = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            awareMessage.sendEmails(WARNMESSAGE);
            awareMessage.sendMessages(WARNMESSAGE);
        }
    };

    private Handler handlerEntertainment = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            selectEnt();
        }
    };

    private void selectEnt() {
        final CharSequence[] items = {"Skype", "Music", "Video", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Options");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Skype")) {
                    startActivity(new Intent(MainActivity.this, PhotoGalleryActivity.class));
                } else if (items[item].equals("Music")) {
                    startActivity(new Intent(MainActivity.this, MusicPlayerActivity.class));
                } else if (items[item].equals("Video")) {
                    startActivity(new Intent(MainActivity.this, VideoActivity.class));
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    class ServerThread implements Runnable {

        public void run() {
            Socket socket = null;
            BufferedReader input;
            try {
                Log.d(TAG, "xxxxxxxxxxxxxxxxxxx");
                serverSocket = new ServerSocket(SERVERPORT);
                Log.d(TAG, serverSocket.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {

                socket = serverSocket.accept();
                Log.d(TAG, "accept");
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (!Thread.currentThread().isInterrupted()) {
                    try {

                        String read = input.readLine();
                        if (read != null) {
                            String[] parts = read.split(" ");
                            String boredomValue = parts[1];
                            String frustrationValue = parts[3];
                            String date = getTime()[0];
                            String time = getTime()[1];
                            Emotion frus = new Emotion("", date, time, "Frustration",frustrationValue);
                            Emotion bore = new Emotion("", date, time, "Boredom", boredomValue);
                            storeEmotion(frus);
                            storeEmotion(bore);
                            handlerEntertainment.sendEmptyMessage(0);
                            if(parts[2].equals("Warn:")){
                                handlerWarn.sendEmptyMessage(0);
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


}
