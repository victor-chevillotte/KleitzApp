package com.senter.demo.uhf.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.ArrayList;

/**
 * @author C'dine
 * A simple cursor adapter. Only variation is that it displays alternate rows
 *  in alternate colors.
 */
public class AlternateRowAdapter extends SimpleAdapter{

    private final int[] colors = new int[] { Color.parseColor("#2d355a75"),Color.TRANSPARENT};//};//1b224375
    public AlternateRowAdapter(Context context, ArrayList array, int layout,
                                     String[] from, int[] to) {
        super(context, array, layout, from, to);
    }
    /**
     * Display rows in alternating colors
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        int colorPos = position % colors.length;
        view.setBackgroundColor(colors[colorPos]);
        return view;
    }
}