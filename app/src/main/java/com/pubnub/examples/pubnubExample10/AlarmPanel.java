package com.pubnub.examples.pubnubExample10;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class AlarmPanel extends Activity {
    Button mButton;
    Intent mainPanelIntent;
    ImageButton lockButton, unLockButton;
     Pubnub pubnub;
//    public static String SENDER_ID = "914467891927";
//    public static String REG_ID;
//    private static final String APP_VERSION = "3.6.1";

    //  pub-c-ae38895f-098b-4f2e-97b3-2b7f07bc28f2
    // sub-c-22258e30-bf6d-11e4-b42d-02ee2ddab7fe

    //pc's keyset
     //pub-c-ef34a7ed-52a5-4e3b-8d12-39a96d416b02
    //Subscribe Key sub-c-930dd836-acd1-11e4-815e-0619f8945a4f


    SharedPreferences prefs;

    //MainActivity mainActivity = new MainActivity();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panelmain);
        lockButton = (ImageButton) findViewById(R.id.imageLockButton);
        unLockButton = (ImageButton) findViewById(R.id.imageUnlockButton);

         lockButton.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View view) {

                        //.pubnubFromMainActivity myPubnubFromAct = new MainActivity.pubnubFromMainActivity();
                        try {
                            //mainActivity.subscribe();//"sub-c-22258e30-bf6d-11e4-b42d-02ee2ddab7fe", new Callback() {
//                                @Override
//                                public void connectCallback(String channel,
//                                                            Object message) {
//                                    Toast.makeText(getApplicationContext(), "SUBSCRIBE : CONNECT on channel:"
//                                            + channel
//                                            + " : "
//                                            + message.getClass()
//                                            + " : "
//                                            + message.toString(), Toast.LENGTH_SHORT).show();
//                                }
//
//                                @Override
//                                public void disconnectCallback(String channel,
//                                                               Object message) {
//                                    Toast.makeText(getApplicationContext(), "SUBSCRIBE : DISCONNECT on channel:"
//                                            + channel
//                                            + " : "
//                                            + message.getClass()
//                                            + " : "
//                                            + message.toString(), Toast.LENGTH_LONG).show();
//                                }
//
//                                @Override
//                                public void reconnectCallback(String channel,
//                                                              Object message) {
//                                    Toast.makeText(getApplicationContext(), "SUBSCRIBE : RECONNECT on channel:"
//                                            + channel
//                                            + " : "
//                                            + message.getClass()
//                                            + " : "
//                                            + message.toString(), Toast.LENGTH_LONG);
//                                }
//
//                                @Override
//                                public void successCallback(String channel,
//                                                            Object message) {
//                                    Toast.makeText(getApplicationContext(), "SUBSCRIBE : " + channel + " : "
//                                            + message.getClass() + " : "
//                                            + message.toString(), Toast.LENGTH_LONG);
//                                }
//
//                                @Override
//                                public void errorCallback(String channel,
//                                                          PubnubError error) {
//                                    Toast.makeText(getApplicationContext(), "SUBSCRIBE : ERROR on channel "
//                                            + channel + " : "
//                                            + error.toString(), Toast.LENGTH_LONG);
//                                }
//                            });

                        } catch (Exception e) {

                        }
                    }
});

        unLockButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                      //mainActivity.unsubscribe("sub-c-22258e30-bf6d-11e4-b42d-02ee2ddab7fe");
                       }
                });

    }


}
