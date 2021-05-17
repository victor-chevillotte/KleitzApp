package com.senter.demo.uhf.common;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.senter.demo.common.misc.Accompaniment;
import com.senter.demo.uhf.R;
import com.senter.demo.uhf.util.DBHelper;
import com.senter.demo.uhf.util.DataTransfer;
import com.senter.iot.support.openapi.uhf.UhfD2;
import com.senter.iot.support.openapi.uhf.UhfD2.UII;

public abstract class Activity2InventoryFocusCommonAbstract extends Activity_Abstract {
    public static final String TAG = "Activity0CmnFcsAbstract";
    private final DBHelper mydb = new DBHelper(this, "KleitzElec.db", null, 1,this);
    private int nbrOfDetections=0;
    private final Accompaniment accompaniment = Accompaniment.newInstanceOfResource(Activity2InventoryFocusCommonAbstract.this, R.raw.tag_inventoried);
    private Handler accompainimentsHandler;
    
    private final Runnable accompainimentRunnable = new Runnable() {
        @Override
        public void run() {
            accompaniment.start();
            accompainimentsHandler.removeCallbacks(this);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2inventoryfocus);
        HandlerThread htHandlerThread = new HandlerThread("");
        htHandlerThread.start();
        nbrOfDetections=0;
        accompainimentsHandler = new Handler(htHandlerThread.getLooper());
        views = new Views();
    }
    
    @Override
    protected void onDestroy() {
        if (accompainimentsHandler != null) {
            accompainimentsHandler.getLooper().quit();
        }
        accompaniment.release();
        super.onDestroy();
    }

    protected final void addNewUiiMassageToListviewFocus(UII paramUII, String uiiOfFocus, UhfD2.UmdRssi paramUmdRssi) {
        int rssiint = paramUmdRssi.getRssi();
        String uii =DataTransfer.xGetString(paramUII.getBytes());
        uii = uii.substring(0, uii.length() - 1);
        if(!uii.equals(uiiOfFocus)) {
            return;
        }
        trigTagAccompainiment();
        Cursor cursor = mydb.selectATag(uii);
        if (!cursor.moveToFirst() || cursor.getCount() == 0) {
            addMassageNewTagInventoryDetectedFocus(uii, "", Integer.toString(Integer.valueOf(rssiint).intValue()), "", "");
            return;
        }
        String name = cursor.getString(cursor.getColumnIndex("name"));
        String room = cursor.getString(cursor.getColumnIndex("room"));
        String workplace = cursor.getString(cursor.getColumnIndex("workplace"));
        addMassageNewTagInventoryDetectedFocus(uii, name, Integer.toString(Integer.valueOf(rssiint).intValue()), room, workplace);
    }


    private void addMassageNewTagInventoryDetectedFocus(final String uii, final String name, final String rssi, final String room, final String workplace){
        nbrOfDetections++;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView nameTv= (TextView) findViewById(R.id.FocusTagName);
                nameTv.setText(uii);
                TextView nameTV= (TextView) findViewById(R.id.FocusTagName);
                nameTV.setText(name);
                TextView roomTV= (TextView) findViewById(R.id.FocusTagRoom);
                roomTV.setText(room);
                TextView workplaceTV= (TextView) findViewById(R.id.FocusTagWorkplace);
                workplaceTV.setText(workplace);
                TextView rssiTV= (TextView) findViewById(R.id.FocusTagSgnlPwr);
                rssiTV.setText(rssi);
                TextView nbreDetectTV= (TextView) findViewById(R.id.FocusTagNbDetect);
                nbreDetectTV.setText(String.valueOf(nbrOfDetections));
                ProgressBar pb =(ProgressBar) findViewById(R.id.progressBar);
                pb.setProgress(Integer.valueOf(rssi));
                int distance= 90-Integer.valueOf(rssi);
                if (distance<=0){
                    distance=0;
                }
                TextView distanceTV= (TextView) findViewById(R.id.FocusTagDistance);
                distanceTV.setText(String.valueOf(distance)+" cm");
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable,interval);
            }
        });
    }

    private final int interval = 500; // 2 Seconds
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable(){
        public void run() {
            TextView rssiTV= (TextView) findViewById(R.id.FocusTagSgnlPwr);
            rssiTV.setText("0");
            ProgressBar pb =(ProgressBar) findViewById(R.id.progressBar);
            pb.setProgress(0);
            TextView distanceTV= (TextView) findViewById(R.id.FocusTagDistance);
            distanceTV.setText("/ cm");
        }
    };

    protected abstract void uiOnInverntryButton();

    private void trigTagAccompainiment() {
        accompainimentsHandler.post(accompainimentRunnable);
    }
    
    private Views views;
    
    protected final Views getViews() {
        return views;
    }
    
    protected final class Views {
        private final Button btnInventoryOrStop = findViewById(R.id.idE20InventoryMain_llInvestory_btnStart);
        private final View.OnClickListener mBtnInventoryClickLisener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uiOnInverntryButton();
            }
        };

        public Views() {
            btnInventoryOrStop.setOnClickListener(mBtnInventoryClickLisener);
        }

        public final void setStateStoped() {
            runOnUiThread(new Runnable() {
                
                @Override
                public void run() {
                    views.btnInventoryOrStop.setText(R.string.Inventory);
                }
            });
        }

        public final void setStateStarted() {
            runOnUiThread(new Runnable() {
                
                @Override
                public void run() {
                    views.btnInventoryOrStop.setText(R.string.Stop);
                }
            });
        }
    }


}
