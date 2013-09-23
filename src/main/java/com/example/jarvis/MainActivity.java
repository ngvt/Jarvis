package com.example.jarvis;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener{
    TextView tv;
    Button speakBtn, listenBtn;
    TextToSpeech tts;
    EditText txtBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("jludden", "Initialized");
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        tv = (TextView)findViewById(R.id.textView1);
        speakBtn = (Button)findViewById(R.id.speakBtn);
        speakBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                speakButtonPressed(v);
            }
        });

        //speakBtn.setOnClickListener(this);

        listenBtn = (Button)findViewById(R.id.listenBtn);
        listenBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                listenButtonPressed(v);
            }
        });


        Log.d("jludden","doing stuff");


        //Intent installIntent = new Intent();
        //installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
        //startActivity(installIntent);

        tts = new TextToSpeech(this, this);
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener(){
            @Override
            public void onDone(String utteranceId)
            {
                doneSpeaking(utteranceId);
            }

            @Override
            public void onError(String utteranceId)
            {
            }

            @Override
            public void onStart(String utteranceId)
            {
                startedSpeaking(utteranceId);
            }
        });
        tv.setText("Speak stuff!");


        txtBox = (EditText) findViewById(R.id.textBox1);


        Log.d("jludden","Reached end of main");
    }



    /**
     * Button is clicked
     * @param view
     */
    public void speakButtonPressed(View view){
        tv.setText("speaking...");

        String text=txtBox.getText().toString();
        if(text==null || text.length() < 1)
            text = "i am robot";
        tts.speak(text, TextToSpeech.QUEUE_ADD, null);

    }

    /**
     * Button is clicked
     * @param view
     */
    public void listenButtonPressed(View view){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say the magic word");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 100);
        startActivityForResult(intent,1234);
    }

    @Override
    protected void
    onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 1234)
        {
            if (resultCode == RESULT_OK)
            {
                ArrayList<String> matches = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if (matches.size() == 0)
                {
                    tts.speak("Heard nothing", TextToSpeech.QUEUE_FLUSH, null);
                }
                else
                {
                    String mostLikelyThingHeard = matches.get(0);
                    String magicWord = "epic";
                    if (mostLikelyThingHeard.equals(magicWord))
                    {
                        tts.speak("You said the magic word!", TextToSpeech.QUEUE_FLUSH, null);
                    }
                    else
                    {
                        tts.speak("The magic word is not " + mostLikelyThingHeard + " try again", TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
                tv.setText("heard: " + matches);
            }
            else
            {
                Log.d("jludden", "speech recognizer result NOT ok");
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.d("TTS", "This Language is not supported");
            } else {
                speakBtn.setEnabled(true);
            }
        } else {
            Log.d("TTS", "Initilization Failed! status:"+status);
            Log.d("TTS", "Initilization Failed! err==:"+TextToSpeech.ERROR);
        }
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void startedSpeaking(String s) {
        speakBtn.setActivated(false);
        tv.setText("Speaking: "+txtBox.getText().toString());
    }

    public void doneSpeaking(String s) {
        speakBtn.setActivated(true);
        tv.setText("say something!");
    }
}

