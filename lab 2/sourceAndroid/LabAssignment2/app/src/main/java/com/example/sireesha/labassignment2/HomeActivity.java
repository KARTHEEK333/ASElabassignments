package com.example.sireesha.labassignment2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.FaceDetector;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//import com.google.android.gms.vision.Frame;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.DocumentSentimentResults;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener{
    private Button ButtonLogout;
    private Button ButtonAnalysis;
    private TextView textViewUserEmail;
    private FirebaseAuth firebaseAuth;
    private TextView txvResult;
    private TextView txtnegative;
    private TextView txtpositive;
    String sourceText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null)
        {
            // Go to Home Activity
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        FirebaseUser user = firebaseAuth.getCurrentUser();
        txtnegative = (TextView) findViewById(R.id.txtnegative);
        txtpositive = (TextView) findViewById(R.id.txtpositive);
        txvResult = (TextView) findViewById(R.id.txvResult);
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        String[] temp = user.getEmail().toString().split("@");
        textViewUserEmail.setText("Welcome "+ temp[0]);
        ButtonLogout = (Button) findViewById(R.id.Button2);
        ButtonAnalysis = (Button) findViewById(R.id.Button1);
        ButtonLogout.setOnClickListener(this);
        ButtonAnalysis.setOnClickListener(this);

    }
    public void getSpeechInput(View view) {
        // Clearing the previous data
        txvResult.setText(null);
        txtpositive.setText(null);
        txtnegative.setText(null);
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txvResult.setText(result.get(0));
                }
                break;
        }
    }
    public void analyzeText() {
        sourceText = txvResult.getText().toString();
        String getURL = "https://api.uclassify.com/v1/uClassify/Sentiment/classify/?readKey=9QRZ6ZWbVRpH&text="+sourceText;
        OkHttpClient client = new OkHttpClient();
        try {
            Request request = new Request.Builder()
                    .url(getURL)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final JSONObject jsonResult;
                    final String result = response.body().string();
                    try {
                        jsonResult = new JSONObject(result);
                        final String convertedText = jsonResult.getString("positive");
                        final String convertedText1 = jsonResult.getString("negative");

                        Log.d("okHttp", jsonResult.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                float a;
                                a=Float.parseFloat(convertedText);
                                a=a*100;
                                txtpositive.setText("Positive: "+String.valueOf(a)+"%");
                                a=Float.parseFloat(convertedText1);
                                a=a*100;
                                txtnegative.setText("Negative: "+String.valueOf(a)+"%");
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


        } catch (Exception ex) {
            txtpositive.setText(ex.getMessage());
            txtnegative.setText(ex.getMessage());

        }
    }
    @Override
    public void onClick(View view)
    {
        if (view == ButtonLogout)
        {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        if (view == ButtonAnalysis)
        {
            analyzeText();
        }
    }
}
