package com.senter.demo.uhf.modelD2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.senter.demo.uhf.R;
import com.senter.demo.uhf.common.Activity2InventoryFocusCommonAbstract;
import com.senter.demo.uhf.util.DBHelper;
import com.senter.demo.uhf.util.PartialWakeLocker;
import com.senter.iot.support.openapi.uhf.UhfD2;
import com.senter.iot.support.openapi.uhf.UhfD2.UII;
import com.senter.iot.support.openapi.uhf.UhfD2.UmdErrorCode;
import com.senter.iot.support.openapi.uhf.UhfD2.UmdFrequencyPoint;
import com.senter.iot.support.openapi.uhf.UhfD2.UmdOnIso18k6cRealTimeInventory;
import com.senter.iot.support.openapi.uhf.UhfD2.UmdRssi;
import com.senter.support.openapi.StKeyManager;

public class Activity2InventoryFocus extends Activity2InventoryFocusCommonAbstract {
    
    private final PartialWakeLocker mPartialWakeLocker = new PartialWakeLocker(this, TAG);
    private final DBHelper mydb = new DBHelper(this, "KleitzElec.db", null, 1,this);
    private boolean finishOnStop = false;
    StKeyManager.ShortcutKeyMonitor monitorScan = StKeyManager.ShortcutKeyMonitor.isShortcutKeyAvailable(StKeyManager.ShortcutKey.Scan) ?
            StKeyManager.getInstanceOfShortcutKeyMonitor(StKeyManager.ShortcutKey.Scan) : null;
    StKeyManager.ShortcutKeyMonitor monitorScanHandle = StKeyManager.ShortcutKeyMonitor.isShortcutKeyAvailable(StKeyManager.ShortcutKey.Scan_Handle) ?
            StKeyManager.getInstanceOfShortcutKeyMonitor(StKeyManager.ShortcutKey.Scan_Handle) : null;
    private ProgressDialog waitingDialog = null;
    private boolean isScaning;
    private boolean goOnInventoring = true;
    public String uiiOfFocus;
    public String name="";
    public String room="";
    public String workplace="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        uiiOfFocus = intent.getStringExtra("uii"); //uii of the tag we want to focus on
        if (monitorScan != null) monitorScan.reset(this, listener, null);
        if (monitorScanHandle != null) monitorScanHandle.reset(this, listener, null);
        TextView uiiTV= (TextView) findViewById(R.id.FocusTagUii);
        uiiTV.setText(uiiOfFocus);
        Cursor cursor = mydb.selectATag(uiiOfFocus);
        if (cursor.moveToFirst() && cursor.getCount() != 0) {
            Button modifyBtn=(Button) findViewById(R.id.InventoryFocusAddModifyTag);
            modifyBtn.setText("Modifier les informations");
            name = cursor.getString(cursor.getColumnIndex("name"));
            TextView nameTV= (TextView) findViewById(R.id.FocusTagName);
            nameTV.setText(name);
            room = cursor.getString(cursor.getColumnIndex("room"));
            TextView roomTV= (TextView) findViewById(R.id.FocusTagRoom);
            roomTV.setText(room);
            workplace = cursor.getString(cursor.getColumnIndex("workplace"));
            TextView workplaceTV= (TextView) findViewById(R.id.FocusTagWorkplace);
            workplaceTV.setText(workplace);
        }

        uiOnInverntryButton();//immediately start scan

        findViewById(R.id.InventoryFocusAddModifyTag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Switch to add/modify tag");
                goOnInventoring=false;
                boolean newTag=true;
                if(!name.equals("")){
                    newTag=false;
                }
                onIso6C_7AddTag(newTag);
            }
        });
    }

    private void onIso6C_7AddTag(Boolean newTag) {
        Intent myIntent = new Intent(this, Activity3AddTag.class);
        myIntent.putExtra("uii", uiiOfFocus);
        myIntent.putExtra("name", name);
        myIntent.putExtra("room", room);
        myIntent.putExtra("workplace", workplace);
        myIntent.putExtra("newTag", newTag);
        this.startActivity(myIntent);
    }

    @Override
    protected void uiOnInverntryButton() {
        if (worker.isInventroing()) {
            System.out.println("stop inventory");
            worker.stopInventory();
        } else {
            mPartialWakeLocker.wakeLockAcquire();
            getViews().setStateStarted();
            worker.startInventory();
        }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK: {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (worker.isInventroing()) {
                        finishOnStop = true;
                        waitDialogShow();
                        worker.stopInventory();
                    } else {
                        UhfD2.getInstance().iso18k6cSetAccessEpcMatch(UhfD2.UmdEpcMatchSetting.newInstanceOfDisable());
                        waitDialogDismiss();
                        finish();
                    }
                    return true;
                }
                break;
            }
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    private final ContinuousInventoryListener workerLisener = new ContinuousInventoryListener() {
        
        @Override
        public void onTagInventory(UII uii, UmdFrequencyPoint frequencyPoint, Integer antennaId, UmdRssi rssi) {
            addNewUiiMassageToListviewFocus(uii,uiiOfFocus, rssi);
        }
        
        @Override
        public void onFinished() {
            if (finishOnStop) {
                UhfD2.getInstance().iso18k6cSetAccessEpcMatch(UhfD2.UmdEpcMatchSetting.newInstanceOfDisable());
                waitDialogDismiss();
                finish();
            } else {
                mPartialWakeLocker.wakeLockRelease();
                getViews().setStateStoped();
            }
        }
    };
    private final ContinuousInventoryWorker worker = new ContinuousInventoryWorker(workerLisener);
    
    /**
     * show waiting dialog
     */
    private void waitDialogShow() {
        if (waitingDialog != null && waitingDialog.isShowing()) {
            return;
        }
        
        waitingDialog = new ProgressDialog(mActivityAbstract);
        waitingDialog.setCancelable(false);
        waitingDialog.setMessage(getResources().getText(R.string.strWait4Close));
        waitingDialog.show();
    }
    
    /**
     * dismiss waiting diaglog
     */
    private void waitDialogDismiss() {
        
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (waitingDialog != null && waitingDialog.isShowing()) {
                    waitingDialog.dismiss();
                }
            }
        });
    }
    
    private class ContinuousInventoryWorker {
        /**
         * go on inventoring after one inventory cycle finished.
         */

        private ContinuousInventoryListener mListener = null;
        
        private boolean isInventoring = false;
        
        /**
         * @param listener must no be null
         */
        public ContinuousInventoryWorker(ContinuousInventoryListener listener) {
            if (listener == null) {
                throw new NullPointerException();
            }
            mListener = listener;
        }
        
        public void startInventory() {
            goOnInventoring = true;
            isInventoring = true;
            
            UhfD2.getInstance().iso18k6cRealTimeInventory(1, new UmdOnIso18k6cRealTimeInventory() {
                
                @Override
                public void onFinishedWithError(UmdErrorCode error) {
                    onFinishedOnce();
                }
                
                @Override
                public void onFinishedSuccessfully(Integer antennaId, int readRate, int totalRead) {
                    onFinishedOnce();
                }
                
                private void onFinishedOnce() {
                    errorDetectorDialog.removeMessages(0);
                    
                    if (goOnInventoring) {
                        startInventory();
                    } else {
                        isInventoring = false;
                        mListener.onFinished();
                    }
                }
                
                @Override
                public void onTagInventory(UII uii, UmdFrequencyPoint frequencyPoint, Integer antennaId, UmdRssi rssi) {
                    errorDetectorDialog.removeMessages(0);
                    errorDetectorDialog.sendEmptyMessageDelayed(0, 1000 * 6);
                    mListener.onTagInventory(uii, frequencyPoint, antennaId, rssi);
                }
            });
            
            errorDetectorDialog.removeMessages(0);
            errorDetectorDialog.sendEmptyMessageDelayed(0, 1000 * 6);
        }
        
        public void stopInventory() {
            goOnInventoring = false;
        }
        
        public boolean isInventroing() {
            return isInventoring;
        }
    }
    
    private interface ContinuousInventoryListener {
        /**
         * will be called on finished completely
         */
        void onFinished();
        
        void onTagInventory(UII uii, UmdFrequencyPoint frequencyPoint, Integer antennaId, UmdRssi rssi);
    }
    
    
    /**
     * start a inventory process with tag buffer,the result is tags inventoried。
     */

    StKeyManager.ShortcutKeyMonitor.ShortcutKeyListener listener = new StKeyManager.ShortcutKeyMonitor.ShortcutKeyListener() {
        
        public void onKeyDown(int keycode, int repeatCount, StKeyManager.ShortcutKeyMonitor.ShortcutKeyEvent event) {
            if (isScaning == false) {
                uiOnInverntryButton();
                isScaning = true;
            }
        }
        
        public void onKeyUp(int keycode, int repeatCount, StKeyManager.ShortcutKeyMonitor.ShortcutKeyEvent event) {
            isScaning = false;
        }
        
    };
    
    @Override
    protected void onResume() {
        super.onResume();
        if (monitorScan != null) {
            monitorScan.startMonitor();
        }
        if (monitorScanHandle != null) {
            monitorScanHandle.startMonitor();
        }
        
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (monitorScan != null) {
            monitorScan.stopMonitor();
        }
        if (monitorScanHandle != null) {
            monitorScanHandle.stopMonitor();
        }
        if (isFinishing()) {
            mPartialWakeLocker.wakeLockRelease();
        }
    }
    
    @Override
    protected void onDestroy() {
        mPartialWakeLocker.wakeLockRelease();
        super.onDestroy();
    }
    
    Handler errorDetectorDialog = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    waitDialogDismiss();
                    
                    AlertDialog.Builder adb = new AlertDialog.Builder(Activity2InventoryFocus.this);
                    adb.setCancelable(false);
                    adb.setTitle(R.string.strWarning);
                    adb.setMessage(R.string.strSomethingWrongHasBeenDetected_ExitThisAppAndRetryPlease);
                    adb.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UhfD2.getInstance().uninit();
                            Runtime.getRuntime().exit(0);
                        }
                    });
                    adb.show();
                }
            });
        }
    };
}
