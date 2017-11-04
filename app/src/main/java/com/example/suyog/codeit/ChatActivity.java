package com.example.suyog.codeit;

import android.content.Intent;
import android.os.AsyncTask;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ai.api.AIListener;
import  ai.api.*;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Metadata;
import ai.api.model.Result;
import ai.api.model.Status;
import ai.api.ui.AIButton;

public class ChatActivity extends AppCompatActivity implements AIListener {

    ListView mListView;
    EditText mEditText;
    List<ChatModel> list_chat = new ArrayList<>();
    ImageButton mSendbtn;
    ImageButton mRecord;
    public static TextToSpeech t1;

    private Gson gson = GsonFactory.getDefaultFactory().getGson();
    public static final String TAG="codeit";
    SpeechRecognizer mSpeechRecognizer;
    AIDataService aiDataService=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        mRecord = (ImageButton) findViewById(R.id.micButton);
        mListView = (ListView)findViewById(R.id.list_of_message);
        mEditText = (EditText)findViewById(R.id.user_message);
        mSendbtn = (ImageButton)findViewById(R.id.fab);


        final AIConfiguration config = new AIConfiguration("d402c697427b47769a6568402acf5b46",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiDataService = new AIDataService(config);

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });


        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);





        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {


                                                     @Override
                                                     public void onReadyForSpeech(Bundle bundle) {

                                                     }

                                                     @Override
                                                     public void onBeginningOfSpeech() {

                                                     }

                                                     @Override
                                                     public void onRmsChanged(float v) {

                                                     }

                                                     @Override
                                                     public void onBufferReceived(byte[] bytes) {

                                                     }

                                                     @Override
                                                     public void onEndOfSpeech() {

                                                     }

                                                     @Override
                                                     public void onError(int i) {

                                                     }

                                                     @Override
                                                     public void onResults(Bundle bundle) {
                                                         String text = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);
                                                         ChatModel model1 = new ChatModel(text,true); // user send message
                                                         list_chat.add(model1);
                                                         CustomAdapter adapter = new CustomAdapter(list_chat,getApplicationContext());
                                                         mListView.setAdapter(adapter);

                                                     }

                                                     @Override
                                                     public void onPartialResults(Bundle bundle) {

                                                     }

                                                     @Override
                                                     public void onEvent(int i, Bundle bundle) {

                                                     }
                                                 });




        mRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                        getApplicationContext().getPackageName());
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,0);
                mSpeechRecognizer.startListening(intent);
                Log.d(TAG,"speech start");
            }
        });

        mSendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mEditText.getText().toString();
                if(TextUtils.isEmpty(text)){
                    Toast.makeText(ChatActivity.this, "Enter Your Text", Toast.LENGTH_SHORT).show();
                }
                else {
                    ChatModel model1 = new ChatModel(text, true); // user send message
                    list_chat.add(model1);
                    CustomAdapter adapter = new CustomAdapter(list_chat, getApplicationContext());
                    mListView.setAdapter(adapter);
                    //remove user message
                    mEditText.setText("");
                    responseFormAssistant(text);
                }
            }
        });
    }

    void responseFormAssistant(String message){
        final AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery(message);

        new AsyncTask<AIRequest, Void, AIResponse>() {
            @Override
            protected AIResponse doInBackground(AIRequest... requests) {
                final AIRequest request = requests[0];
                try {
                    final AIResponse response = aiDataService.request(aiRequest);
                    return response;
                } catch (AIServiceException e) {
                }
                return null;
            }
            @Override
            protected void onPostExecute(AIResponse aiResponse) {
                if (aiResponse != null) {


                    Result result = aiResponse.getResult();
                    //String message = result.getFulfillment().getSpeech().trim();
                    String message=Operations.doOperations(result);
                    //ChatActivity.t1.speak(message, TextToSpeech.QUEUE_FLUSH, null);
                    ChatModel model1 = new ChatModel(message, false); // user send message
                    list_chat.add(model1);
                    CustomAdapter customAdapter=new CustomAdapter(list_chat,getApplicationContext());
                    mListView.setAdapter(customAdapter);




                }
            }
        }.execute(aiRequest);
    }


    @Override
    public void onResult(AIResponse result) {
        String userMessage = result.getResult().getResolvedQuery();
        mEditText.setText(userMessage);
    }

    @Override
    public void onError(AIError error) {

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }
}

