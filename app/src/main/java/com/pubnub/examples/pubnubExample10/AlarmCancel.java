package com.pubnub.examples.pubnubExample10;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Vibrator;
import java.net.URI;
import java.net.URL;


public class AlarmCancel extends Activity {

    Button stopButton;
    EditText pinCode;
    MediaPlayer mediaPlayer;
    public AlarmCancel() {
        // Required empty public constructor
    }

        @Override
        public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.alarm_cancel);
            stopButton = (Button)findViewById(R.id.Stopbutton);
            pinCode  = (EditText)findViewById(R.id.pinText);
            final SharedPreferences prefs = getSharedPreferences("PIN_CODE", 0);
            final String pinCodeConfirmation =  prefs.getString("PIN_CODE","");
            Toast.makeText(getApplicationContext(), "Saved Code:!!."+pinCodeConfirmation, Toast.LENGTH_SHORT).show();
               startAlarm();
            // handle events
            stopButton.setOnClickListener(
                    new View.OnClickListener() {
                        public void onClick(View view) {
                            if(pinCode.getText().toString().equals(pinCodeConfirmation)){
                                Toast.makeText(getApplicationContext(), "Goodbye!!.", Toast.LENGTH_SHORT).show();
                                mediaPlayer.stop();
                                finish();}
                            else{
                                Toast.makeText(getApplicationContext(), "Enter a valid pin to stop.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
 }


    public void startAlarm(){

        mediaPlayer = MediaPlayer.create(this, R.drawable.hangouts_video_call);
        mediaPlayer.start();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
       finish();
    }
}
