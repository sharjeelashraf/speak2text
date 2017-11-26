package com.shareefullah.testtospeech;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;


public class MainActivity extends AppCompatActivity {
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView mVoiceInputTv;
    private InterstitialAd interAd;
int count = 0;
    private ImageButton mSpeakBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        interAd = new InterstitialAd(this);

        interAd.setAdUnitId(getString(R.string.interstitial_ad));
        //interAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        ///Create Ad
        requestad();

        interAd.setAdListener(new AdListener() {
            //public void onAdLoaded() {
            //displayInterstitial();
            //}
            public void onAdClosed() {
                // When user closes ad end this activity (go back to first activity)
                requestad();

            }
        });


        mVoiceInputTv = (TextView) findViewById(R.id.voiceInput);
        mSpeakBtn = (ImageButton) findViewById(R.id.btnSpeak);
        Button b2 = (Button) findViewById(R.id.copyButton2);
        Button b3 = (Button) findViewById(R.id.copyButton3);
        Button b4 = (Button) findViewById(R.id.copyButton4);
        mSpeakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startVoiceInput();
                count++;
                if (count>4)
                {
                    count =0;

                    displayInterstitial();
                }


            }
        });


        //copy
        b2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Code to Copy the content of Text View to the Clip board.
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("simple text", mVoiceInputTv.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Text copied to memory",
                        Toast.LENGTH_LONG).show();

                count++;

                if (count>4)
                {
                    count =0;

                    displayInterstitial();
                }
            }

        });

        b4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mVoiceInputTv.setText("");

                count++;

                if (count>4)
                {
                    count =0;

                    displayInterstitial();
                }
            }

        });

        //share
        b3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                shareit();

                count++;

                if (count>4)
                {
                    count =0;

                    displayInterstitial();
                }
            }

        });
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save state to the savedInstanceState
        savedInstanceState.putString("MyString", mVoiceInputTv.getText().toString());

    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.

        String myString = savedInstanceState.getString("MyString");
        mVoiceInputTv.setText(myString);
        interAd = new InterstitialAd(this);

        interAd.setAdUnitId(getString(R.string.interstitial_ad));
        //Google ad unit
        //interAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        ///Create Ad
        requestad();

        interAd.setAdListener(new AdListener() {
            //public void onAdLoaded() {
            //displayInterstitial();
            //}
            public void onAdClosed() {
                // When user closes ad end this activity (go back to first activity)
                requestad();

            }
        });




    }

    public void requestad()
    {
        NativeExpressAdView mAdView2 = (NativeExpressAdView)findViewById(R.id.adView2);
        AdView mAdView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder().build();

        interAd.loadAd(adRequest);
        mAdView.loadAd(adRequest);
        mAdView2.loadAd(adRequest);



        mAdView2.setVideoOptions(new VideoOptions.Builder().setStartMuted(true).build());


        VideoController vc = mAdView2.getVideoController();

        vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            public void onVideoEnd() {
                // Here apps can take action knowing video playback is finished
                // It's always a good idea to wait for playback to complete before
                // replacing or refreshing a native ad, for example.
                super.onVideoEnd();
            }
        });
    }

    private void shareit()
    {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = mVoiceInputTv.getText().toString();
        //sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public void displayInterstitial()
    {
        if(interAd.isLoaded())
        {
            interAd.show();
        }
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, "20000");
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, "10000");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Han Bhai! Kya likho gay?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mVoiceInputTv.setText(mVoiceInputTv.getText().toString() + '\n' + result.get(0));
                }
                break;
            }

        }
    }
}