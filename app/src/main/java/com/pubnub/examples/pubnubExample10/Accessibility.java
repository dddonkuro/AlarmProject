package com.pubnub.examples.pubnubExample10;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Dari on 2/13/2015.
 */
public class Accessibility extends Activity {
    Button mButton;
    Intent mainPanelIntent;
   // TextView _view;
    ViewGroup root;
    private int xDelta;
    private int yDelta;
    EditText pin;
    public static SharedPreferences prefs;
   @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
        setContentView(R.layout.accesspin);
      pin = (EditText)findViewById(R.id.pinText);
        mButton = (Button)findViewById(R.id.Sendbutton);
       prefs = getSharedPreferences("PIN_CODE", Activity.MODE_PRIVATE);
         mButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                      if(!(pin.getText().toString().matches("[0-9]{4}"))){
                        Toast.makeText(getApplicationContext(), "Enter a valid pin to continue.", Toast.LENGTH_SHORT).show();}
                     else{
                          SavePinCode();
                          Intent intent = new Intent();
                          intent.putExtra("PIN_CODE",pin.getText().toString());
                          setResult(2,intent);
                          finish();

                      }
                    }
                });

        }
public void SavePinCode(){

    String pinCode = prefs.getString("PIN_CODE","");
   // if(pinCode == null){
   final SharedPreferences.Editor editor = prefs.edit();
     editor.putString("PIN_CODE",pin.getText().toString());
    Toast.makeText(getApplicationContext(), "You entered: "+pin.getText().toString(), Toast.LENGTH_SHORT).show();
    editor.commit();

    //}
}
}

