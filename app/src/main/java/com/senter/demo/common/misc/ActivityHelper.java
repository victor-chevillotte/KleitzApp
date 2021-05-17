package com.senter.demo.common.misc;

import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityHelper<TypeOfActivity extends Activity> {
    private final TypeOfActivity ownersActivity;

    public ActivityHelper(TypeOfActivity activity) {
        ownersActivity = activity;
    }

    public final void showToastLong(final String text) {
        ownersActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ownersActivity, text, Toast.LENGTH_LONG).show();
            }
        });
    }

    public <TypeOfTargetActivity extends Activity> void startActivity(Class<TypeOfTargetActivity> t) {
        ownersActivity.startActivity(new Intent(ownersActivity, t).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

}