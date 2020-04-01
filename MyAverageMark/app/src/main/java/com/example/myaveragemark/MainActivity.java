package com.example.myaveragemark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;


public class MainActivity extends AppCompatActivity implements RecognitionListener {

    /* Named searches allow to quickly reconfigure the decoder */
    private static final String NUMBER_SEARCH = "numbers";
    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private static final List<String> NUMBERS = new ArrayList<>(11);
    static {
        NUMBERS.add("ноль");
        NUMBERS.add("один");
        NUMBERS.add("два");
        NUMBERS.add("три");
        NUMBERS.add("четыре");
        NUMBERS.add("пять");
        NUMBERS.add("шесть");
        NUMBERS.add("семь");
        NUMBERS.add("восемь");
        NUMBERS.add("девять");
        NUMBERS.add("десять");
    }

    TextView marksView;

    private Float averageMark = 0.0f;
    List<Integer> marks = new ArrayList<>();

    private SpeechRecognizer mRecognizer;

    private final boolean COMPLETE_INIT = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if(!COMPLETE_INIT) {
            Intent selectLanguage = new Intent(".SelectLanguage");
            startActivity(selectLanguage);
            return;
        }

        setContentView(R.layout.activity_main);

        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }
        new SetupTask(this).execute();

        marksView = findViewById(R.id.Numbers);
        marksView.setText("Ваши оценки: ");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Recognizer initialization is a time-consuming and it involves IO,
                // so we execute it in async task
                new SetupTask(this).execute();
            } else {
                finish();
            }
        }
    }

    private static class SetupTask extends AsyncTask<Void, Void, Exception> {
        WeakReference<MainActivity> activityReference;
        SetupTask(MainActivity activity) {
            this.activityReference = new WeakReference<>(activity);
        }
        @Override
        protected Exception doInBackground(Void... params) {
            try {
                Assets assets = new Assets(activityReference.get());
                File assetDir = assets.syncAssets();
                activityReference.get().setupRecognizer(assetDir);
            } catch (IOException e) {
                return e;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Exception result) {
            if (result != null) {
                System.out.println("Failed to init recognizer " + result);
            } else {
                activityReference.get().switchSearch(NUMBER_SEARCH);
            }
        }
    }

    private void switchSearch(String searchName) {
        mRecognizer.stop();
        mRecognizer.startListening(searchName, 1000);
        marksView.setText("Вы можете говорить.");
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them

        File acm = new File(assetsDir, "zero_ru.cd_cont_4000");
        System.out.println(acm.getPath());

        mRecognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(acm)
                .setDictionary(new File(assetsDir, "Russian.dict"))
                .setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)
                .getRecognizer();
        mRecognizer.addListener(this);

        /* In your application you might not need to add all those searches.
          They are added here for demonstration. You can leave just one.
         */

        //numbers search
        File gram = new File(assetsDir, "Numbers.gram");
        mRecognizer.addGrammarSearch(NUMBER_SEARCH, gram);

    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onEndOfSpeech() {
        if (mRecognizer.getSearchName().equals(NUMBER_SEARCH)) {
            mRecognizer.stop();
        }
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        String text = hypothesis != null ? hypothesis.getHypstr() : null;
        text += " ";
        marksView.append(text);

        String[] words = text.split(" ");

        for(String word: words) {
            if(word.equals("все")) {
                mRecognizer.stop();
            } else if(NUMBERS.contains(word)) {
                Integer mark = NUMBERS.indexOf(word);
                System.out.println("Mark: " + mark);
                marks.add(mark);
                mRecognizer.startListening(NUMBER_SEARCH, 1000);
            }
            calcAverageMark();
            ((TextView) findViewById(R.id.averageMark)).setText(averageMark.toString());
        }
    }

    private void calcAverageMark() {
        Integer sum = 0;
        if(!marks.isEmpty()) {
            for(Integer mark: marks) {
                sum += mark;
            }
            averageMark = (float) sum / marks.size();
        }
    }

    @Override
    public void onError(Exception error) {

        System.out.println(error.getMessage());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mRecognizer != null) {
            mRecognizer.cancel();
            mRecognizer.shutdown();
        }
    }

    @Override
    public void onTimeout() {
    }
}
