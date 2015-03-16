package com.pubnub.examples.pubnubExample10;

import java.io.IOException;
import java.security.PublicKey;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class MainActivity extends FragmentActivity {

    //Pubnub pubnub;
    GoogleCloudMessaging gcm;
    public SharedPreferences prefs;
    Context context;
    public static String SENDER_ID ="914467891927";
    public static String REG_ID;
    private static final String APP_VERSION = "3.6.1";

     String PUBLISH_KEY = "pub-c-ae38895f-098b-4f2e-97b3-2b7f07bc28f2";
     String SUBSCRIBE_KEY = "sub-c-22258e30-bf6d-11e4-b42d-02ee2ddab7fe";
    String CIPHER_KEY = "";
    String SECRET_KEY = "";
    String ORIGIN = "pubsub";
    String AUTH_KEY;
    String UUID;
    Boolean SSL = false;
    Button mButton;
    ImageButton subcribeButton,UnsubscribeButton, StateImgButton;
    EditText pin;
    Intent mainPanelIntent;
     Pubnub pubnub;
     RelativeLayout relativeLayout;
     static final String TAG = "Register Activity";
     String successFlag = "";
     TextView txtview;

    String PIN_CODE = null;
    private  void notifyUser(Object message) {
        try {
            if (message instanceof JSONObject) {
                final JSONObject obj = (JSONObject) message;
                this.runOnUiThread(new Runnable() {
                    public void run() {

                        Log.i("Received msg : ", String.valueOf(obj));
                    }
                });

            } else if (message instanceof String) {
                final String obj = (String) message;
                this.runOnUiThread(new Runnable() {
                    public void run() {

                        Intent startAlarm = new Intent(getApplicationContext(),AlarmCancel.class);
                        startAlarm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(startAlarm);
                        Log.i("Received msg : ", obj.toString());
                    }
                });

            } else if (message instanceof JSONArray) {
                final JSONArray obj = (JSONArray) message;
                this.runOnUiThread(new Runnable() {
                    public void run() {

                        Log.i("Received msg : ", obj.toString());
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(
                "PHOME SECURITY SYSTEM", Context.MODE_PRIVATE);

        final SharedPreferences ComfirmationPrefs;
        ComfirmationPrefs = getSharedPreferences("PIN_CODE", 0);

          init();
         setContentView(R.layout.panellayout);
         txtview = (TextView) findViewById(R.id.stateLabel);

        subcribeButton = (ImageButton)findViewById(R.id.imageLockButton);
        UnsubscribeButton = (ImageButton)findViewById(R.id.imageUnlockButton);
        StateImgButton = (ImageButton)findViewById(R.id.StateButton);

        if(REG_ID !=null)
            gcmRegister();
            subscribe();
        this.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                pubnub.disconnectAndResubscribe();

            }

        }, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        pin = (EditText)findViewById(R.id.pinText);
        mButton = (Button)findViewById(R.id.Sendbutton);

         subcribeButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                     String lockState =  ComfirmationPrefs.getString("PIN_CODE","");

                         startFragmentActivity();

                        subscribe();
                        StateImgButton.setImageResource(R.drawable.lock);
                        txtview.setText("LOCKED");

                    }
                });

        UnsubscribeButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        String lockState =  ComfirmationPrefs.getString("PIN_CODE","");
                         if(lockState == null){
                             Toast.makeText(getApplicationContext(), "The system is unlock",
                             Toast.LENGTH_LONG).show();
                            }
                        else{
                        startFragmentActivity();

                              unsubscribe();
                              StateImgButton.setImageResource(R.drawable.unlock);
                              txtview.setText("UNLOCKED");
                              final SharedPreferences.Editor editor = prefs.edit();
                              editor.putString("PIN_CODE",null);
                              editor.commit();
                         }
                    }
                });

    }
    public void startFragmentActivity(){
        Intent fragmentIntent = new Intent(this,Accessibility.class);
        fragmentIntent.putExtra("PIN_CODE",2);
        startActivityForResult(fragmentIntent,2);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        PIN_CODE = data.getStringExtra("PIN_CODE");

    }
    private void setAuthKey() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Auth Key");
        builder.setMessage("Enter Auth Key");
        final EditText edAuthKey = new EditText(this);
        edAuthKey.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(edAuthKey);
        builder.setPositiveButton("Set",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        AUTH_KEY = edAuthKey.getEditableText().toString();
                        saveCredentials();
                        init();
                    }

                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void init() {

        Map<String, String> map = getCredentials();

     pubnub = new Pubnub(
                PUBLISH_KEY,
                SUBSCRIBE_KEY,
                SECRET_KEY,
                CIPHER_KEY,
                SSL
        );
        pubnub.setCacheBusting(false);
        pubnub.setOrigin(ORIGIN);
        pubnub.setAuthKey(AUTH_KEY);


    }

    private void saveCredentials() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("PUBLISH_KEY", PUBLISH_KEY);
        editor.putString("SUBSCRIBE_KEY", SUBSCRIBE_KEY);
        editor.putString("SECRET_KEY", SECRET_KEY);
        editor.putString("AUTH_KEY", AUTH_KEY);
        editor.putString("CIPHER_KEY", CIPHER_KEY);
        editor.putString("ORIGIN", ORIGIN);
        editor.putString("UUID", UUID);
        editor.putString("SSL", SSL.toString());
        editor.putString("SENDER_ID", SENDER_ID);
        editor.commit();
    }

    private Map<String, String> getCredentials() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("PUBLISH_KEY", prefs.getString("PUBLISH_KEY", "demo"));
        map.put("SUBSCRIBE_KEY", prefs.getString("SUBSCRIBE_KEY", "demo"));
        map.put("SECRET_KEY", prefs.getString("SECRET_KEY", "demo"));
        map.put("CIPHER_KEY", prefs.getString("CIPHER_KEY", ""));
        map.put("AUTH_KEY", prefs.getString("AUTH_KEY", null));
        map.put("ORIGIN", prefs.getString("ORIGIN", "pubsub"));
        map.put("UUID", prefs.getString("UUID", null));
        map.put("SSL", prefs.getString("SSL", "false"));
        map.put("SENDER_ID", prefs.getString("SENDER_ID", null));
        return map;
    }

    private void gcmRemoveAllChannels() {
        if (TextUtils.isEmpty(REG_ID)) {
            Toast.makeText(getApplicationContext(),
                    "GCM Registration id not set. Register to GCM and try again.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        pubnub.removeAllPushNotificationsForDeviceRegistrationId(REG_ID, new Callback() {
            @Override
            public void successCallback(String channel,
                                        Object message) {
                notifyUser("GCM REMOVE ALL : " + message);
            }

            @Override
            public void errorCallback(String channel,
                                      PubnubError error) {
                notifyUser("GCM REMOVE ALL : " + error);
            }
        });

    }

    private void gcmRemoveChannel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove Channel from GCM");
        builder.setMessage("Enter Channel Name");
        final EditText edChannelName = new EditText(this);
        edChannelName.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(edChannelName);
        builder.setPositiveButton("Remove",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (TextUtils.isEmpty(REG_ID)) {
                            Toast.makeText(getApplicationContext(),
                                    "GCM Registration id not set. Register to GCM and try again.",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        String channel = edChannelName.getText().toString();
                        pubnub.disablePushNotificationsOnChannel(channel, REG_ID, new Callback() {
                            @Override
                            public void successCallback(String channel,
                                                        Object message) {
                                notifyUser("GCM REMOVE : " + message);
                            }

                            @Override
                            public void errorCallback(String channel,
                                                      PubnubError error) {
                                notifyUser("GCM REMOVE : " + error);
                            }
                        });
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void gcmAddChannel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Channel to GCM");
        builder.setMessage("Enter Channel Name");
        final EditText edChannelName = new EditText(this);
        edChannelName.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(edChannelName);
        builder.setPositiveButton("Add",
                new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (TextUtils.isEmpty(REG_ID)) {
                      Toast.makeText(getApplicationContext(),
                              "GCM Registration id not set. Register to GCM and try again.",
                              Toast.LENGTH_LONG).show();
                      return ;
                }
                String channel = edChannelName.getText().toString();
                pubnub.enablePushNotificationsOnChannel(channel, REG_ID, new Callback() {
                    @Override
                    public void successCallback(String channel,
                    Object message) {
                        notifyUser("GCM ADD : " + message);
                    }
                    @Override
                    public void errorCallback(String channel,
                    PubnubError error) {
                        notifyUser("GCM ADD : " + error);
                    }
                });
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    private void gcmUnregister() {
        //TODO add unregister code
    }


    private String gcmRegister() {

        context = getApplicationContext();
        gcm = GoogleCloudMessaging.getInstance(this);

        if (TextUtils.isEmpty(SENDER_ID)) {
            Toast.makeText(getApplicationContext(),
                    "GCM Sender ID not set.",
                    Toast.LENGTH_LONG).show();
            return null;
        }

        REG_ID = getRegistrationId(context);

        if (TextUtils.isEmpty(REG_ID)) {

            registerInBackground();

            Log.d("RegisterActivity",
                    "registerGCM - successfully registered with GCM server - regId: "
                            + REG_ID);
        } else {
            Toast.makeText(getApplicationContext(),
                    "RegId already available. RegId: " + REG_ID,
                    Toast.LENGTH_LONG).show();
        }
        return REG_ID;
    }

    private String getRegistrationId(Context context) {
        String registrationId = prefs.getString("REG_ID", "");
        if (registrationId.length() <= 0) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            Log.d("RegisterActivity",
                    "I never expected this! Going down, going down!" + e);
            throw new RuntimeException(e);
        }
    }

    private void registerInBackground() {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    REG_ID = gcm.register(SENDER_ID);
                    Log.d("RegisterActivity", "registerInBackground - regId: "
                            + REG_ID);
                    msg = "Device registered, registration ID=" + REG_ID;

                    storeRegistrationId(context, REG_ID);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.d("RegisterActivity", "Error: " + msg);
                }
                Log.d("RegisterActivity", "AsyncTask completed: " + msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(getApplicationContext(),
                        "Registered with GCM Server." + msg, Toast.LENGTH_LONG)
                        .show();
            }
        }.execute(null, null, null);
    }

    private void storeRegistrationId(Context context, String regId) {
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("REG_ID", regId);
        editor.putInt(APP_VERSION, appVersion);
        editor.commit();
    }

    private void setHeartbeatInterval() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Presence Heartbeat Interval");
        builder.setMessage("Enter heartbeat value in seconds");
        final EditText edTimeout = new EditText(this);
        edTimeout.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(edTimeout);
        builder.setPositiveButton("Done",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pubnub.setHeartbeatInterval(Integer.parseInt(edTimeout.getText().toString()));
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void setHeartbeat() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Presence Heartbeat");
        builder.setMessage("Enter heartbeat value in seconds");
        final EditText edTimeout = new EditText(this);
        edTimeout.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(edTimeout);
        builder.setPositiveButton("Done",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pubnub.setHeartbeat(Integer.parseInt(edTimeout.getText().toString()));
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void toggleCacheBusting() {
        pubnub.setCacheBusting(pubnub.getCacheBusting() ? false : true);
        notifyUser("CACHE BUSTING : " + pubnub.getCacheBusting());

    }

    private void setOrigin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Origin");
        builder.setMessage("Enter Origin");
        final EditText edTimetoken = new EditText(this);
        builder.setView(edTimetoken);
        builder.setPositiveButton("Done",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ORIGIN = edTimetoken.getText().toString();
                        saveCredentials();
                        pubnub.setOrigin(ORIGIN);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }



   public  void subscribe() {

                        try {
                            pubnub.subscribe(SUBSCRIBE_KEY, new Callback() {
                                @Override
                                public void connectCallback(String channel,
                                                            Object message) {

                              }

                                @Override
                                public void disconnectCallback(String channel,Object message) {

                                }

                                @Override
                                public void reconnectCallback(String channel,  Object message) {

                }

                                @Override
                                public void successCallback(String channel,
                                                            Object message) {
                                    notifyUser("SUBSCRIBE : " + channel + " : "
                                            + message.getClass() + " : "
                                            + message.toString());
           }

                                @Override
                                public void errorCallback(String channel,
                                                          PubnubError error) {
                             }
                            });

                        } catch (Exception e) {

                        }
    }


    private void _publish(final String channel) {

                        Callback publishCallback = new Callback() {
                            @Override
                            public void successCallback(String channel,
                                                        Object message) {
                                notifyUser("PUBLISH : " + message);
                            }

                            @Override
                            public void errorCallback(String channel,
                                                      PubnubError error) {
                                notifyUser("PUBLISH : " + error);
                            }
                        };

                        String message = "hello android";

                        try {
                            Integer i = Integer.parseInt(message);
                            pubnub.publish(channel, i, publishCallback);
                            return;
                        } catch (Exception e) {
                        }

                        try {
                            Double d = Double.parseDouble(message);
                            pubnub.publish(channel, d, publishCallback);
                            return;
                        } catch (Exception e) {
                        }


                        try {
                            JSONArray js = new JSONArray(message);
                            pubnub.publish(channel, js, publishCallback);
                            return;
                        } catch (Exception e) {
                        }

                        try {
                            JSONObject js = new JSONObject(message);
                            pubnub.publish(channel, js, publishCallback);
                            return;
                        } catch (Exception e) {
                        }

                        pubnub.publish(channel, message, publishCallback);
                    }

    private void publish() {

                        _publish(PUBLISH_KEY);
    }

    private void _channelGroupRemoveChannel(final String groupName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove channel from Group");
        builder.setMessage("Enter channel name");
        final EditText etChannel = new EditText(this);
        builder.setView(etChannel);
        builder.setPositiveButton("Add",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Callback cb = new Callback() {
                            @Override
                            public void successCallback(String channel,
                                                        Object message) {
                                notifyUser("REMOVE CHANNEL : " + message);
                            }

                            @Override
                            public void errorCallback(String channel,
                                                      PubnubError error) {
                                notifyUser("REMOVE CHANNEL : " + error);
                            }
                        };

                        String channel = etChannel.getText().toString();


                            pubnub.channelGroupRemoveChannel(groupName, channel, cb);
                    }

                });
        AlertDialog alert = builder.create();
        alert.show();
    }



    public void unsubscribe() {
                     pubnub.unsubscribe(SUBSCRIBE_KEY);
}



    private void disconnectAndResubscribe() {
        pubnub.disconnectAndResubscribe();

    }

    private void setSubscribeTimeout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Subscribe Timeout");
        builder.setMessage("Enter timeout value in milliseconds");
        final EditText edTimeout = new EditText(this);
        edTimeout.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(edTimeout);
        builder.setPositiveButton("Done",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pubnub.setSubscribeTimeout(Integer.parseInt(edTimeout
                                .getText().toString()));
                    }

                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void setRetryInterval() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Retry Interval");
        builder.setMessage("Enter retry interval in milliseconds");
        final EditText edInterval = new EditText(this);
        edInterval.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(edInterval);
        builder.setPositiveButton("Done",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pubnub.setRetryInterval(Integer.parseInt(edInterval
                                .getText().toString()));
                    }

                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void setWindowInterval() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Window Interval");
        builder.setMessage("Enter Window interval in milliseconds");
        final EditText edInterval = new EditText(this);
        edInterval.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(edInterval);
        builder.setPositiveButton("Done",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pubnub.setWindowInterval(Integer.parseInt(edInterval
                                .getText().toString()));
                    }

                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void toggleResumeOnReconnect() {
        pubnub.setResumeOnReconnect(pubnub.isResumeOnReconnect() ? false : true);
        notifyUser("RESUME ON RECONNECT : " + pubnub.isResumeOnReconnect());
    }

    private void setMaxRetries() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Max Retries");
        builder.setMessage("Enter Max Retries");
        final EditText edRetries = new EditText(this);
        edRetries.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(edRetries);
        builder.setPositiveButton("Done",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pubnub.setMaxRetries(Integer.parseInt(edRetries
                                .getText().toString()));
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void disconnectAndResubscribeWithTimetoken() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Disconnect and Resubscribe with timetoken");
        builder.setMessage("Enter Timetoken");
        final EditText edTimetoken = new EditText(this);
        builder.setView(edTimetoken);
        builder.setPositiveButton("Done",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pubnub.disconnectAndResubscribeWithTimetoken(
                                edTimetoken.getText().toString());
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
