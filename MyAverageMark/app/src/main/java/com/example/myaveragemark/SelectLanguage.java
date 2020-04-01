package com.example.myaveragemark;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class SelectLanguage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);

        TextView selectLangText = (TextView) findViewById(R.id.selectLangText);
        Spinner selectLangSpinner = (Spinner) findViewById(R.id.selectLangSpinner);
        Button exitButton = (Button) findViewById(R.id.exitButton);

        Typeface LucidaGrandeFont = Typeface.createFromAsset(getAssets(), "LucidaGrande.ttf");

        selectLangText.setTypeface(LucidaGrandeFont);
        exitButton.setTypeface(LucidaGrandeFont);

        LangArrayAdapter adapter = new LangArrayAdapter(
                this,
                R.array.languages,
                R.layout.spinner_item,
                LucidaGrandeFont);
        selectLangSpinner.setAdapter(adapter);
    }
}
