package com.senter.demo.uhf.common;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.senter.demo.uhf.App;
import com.senter.iot.support.openapi.uhf.UhfD2;

public abstract class Activity_Abstract extends Activity {
    protected final Activity_Abstract mActivityAbstract;
    
    public Activity_Abstract() {
        mActivityAbstract = this;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                UhfD2.getInstance().iso18k6cSetAccessEpcMatch(UhfD2.UmdEpcMatchSetting.newInstanceOfDisable());
                break;
            
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    public final void showToast(final String string) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mActivityAbstract, string, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
