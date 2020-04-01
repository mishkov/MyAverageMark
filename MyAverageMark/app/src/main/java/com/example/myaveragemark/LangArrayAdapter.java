package com.example.myaveragemark;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.ArrayRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import java.util.Arrays;


public class LangArrayAdapter extends ArrayAdapter {

    private Typeface mFont;

    public LangArrayAdapter(Context context,
                            int textArrayResId,
                            int textViewResId,
                            Typeface font) {
        super(context, textViewResId, 0, Arrays.asList(context.getResources().getTextArray(textArrayResId)));
        mFont = font;
    }

    public TextView getView(int position, View convertView, ViewGroup parent) {
        TextView v = (TextView) super.getView(position, convertView, parent);
        v.setTypeface(mFont);
        v.setGravity(Gravity.CENTER_VERTICAL);
        return v;
    }

    public TextView getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView v = (TextView) super.getView(position, convertView, parent);
        v.setTypeface(mFont);
        v.setGravity(Gravity.CENTER_VERTICAL);
        return v;
    }

}
