package com.senter.demo.uhf.common;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.widget.Button;
import android.database.Cursor;

import com.senter.demo.common.misc.Accompaniment;
import com.senter.demo.uhf.R;
import com.senter.demo.uhf.util.DBHelper;
import com.senter.demo.uhf.util.DataTransfer;
import com.senter.iot.support.openapi.uhf.UhfD2;
import com.senter.iot.support.openapi.uhf.UhfD2.UII;
public abstract class Activity1InventoryCommonAbstract extends Activity_Abstract {
    public static final String TAG = "Activity0CmnAbstract";
    private final DBHelper mydb = new DBHelper(this, "KleitzElec.db", null, 1,this);

    private final Accompaniment accompaniment = Accompaniment.newInstanceOfResource(Activity1InventoryCommonAbstract.this, R.raw.tag_inventoried);
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
        setContentView(R.layout.activity1inventory);
        HandlerThread htHandlerThread = new HandlerThread("");
        htHandlerThread.start();
        accompainimentsHandler = new Handler(htHandlerThread.getLooper());
        views = new Views();
        //mydb.insertWorkplace("testworkplacez1");//a suppr
        //mydb.insertRoom("testRoom");//a suppr
    }
    
    @Override
    protected void onDestroy() {
        if (accompainimentsHandler != null) {
            accompainimentsHandler.getLooper().quit();
        }
        accompaniment.release();
        super.onDestroy();
    }

    protected final void addNewUiiMassageToListview(UII paramUII, UhfD2.UmdRssi paramUmdRssi) {
        int rssiint = paramUmdRssi.getRssi();
        trigTagAccompainiment();
        String uii =DataTransfer.xGetString(paramUII.getBytes());
        uii = uii.substring(0, uii.length() - 1);
        Cursor cursor = mydb.selectATag(uii);
        if (!cursor.moveToFirst() || cursor.getCount() == 0) {
            this.views.recordsBoard.addMassageNewTagInventoryDetected(uii, "", Integer.toString(Integer.valueOf(rssiint).intValue()), "", "");
            return;
        }
        String name = cursor.getString(cursor.getColumnIndex("name"));
        String room = cursor.getString(cursor.getColumnIndex("room"));
        String workplace = cursor.getString(cursor.getColumnIndex("workplace"));
        this.views.recordsBoard.addMassageNewTagInventoryDetected(uii, name, Integer.toString(Integer.valueOf(rssiint).intValue()), room, workplace);
    }

    protected final void addNewUiiMassageToListview(String msg) {
        trigTagAccompainiment();
        views.recordsBoard.addMassage(msg);
    }

    protected abstract void uiOnInverntryButton();
    
    private void trigTagAccompainiment() {
        accompainimentsHandler.post(accompainimentRunnable);
    }

    public Views views;
    
    protected final Views getViews() {
        return views;
    }
    
    protected final class Views {
        private final Button btnInventoryOrStop = findViewById(R.id.idE20InventoryMain_llInvestory_btnStart);
        public final RecordsBoard recordsBoard = new RecordsBoard(Activity1InventoryCommonAbstract.this, findViewById(R.id.idE20InventoryActivity_inShow));
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
